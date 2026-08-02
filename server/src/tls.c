// tls.c — OpenSSL TLS wrapper for BAREIRON server
// Provides transparent TLS encryption without touching the main loop logic.
// Requires linking with -lssl -lcrypto

#include "tls.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>

static SSL_CTX* ctx = NULL;

// Mapping from app-facing fd to internal SSL object
static struct {
    int active;
    int real_fd;
    SSL* ssl;
} ssl_table[MAX_TLS_FDS];

// ── Internal helpers ──────────────────────────────────────────

static int find_free_tls_fd(void) {
    for (int i = 0; i < MAX_TLS_FDS; i++) {
        if (!ssl_table[i].active) return i;
    }
    return -1;
}

static int tls_fd_to_real(int tls_fd) {
    if (tls_fd < 0 || tls_fd >= MAX_TLS_FDS || !ssl_table[tls_fd].active)
        return -1;
    return ssl_table[tls_fd].real_fd;
}

// ── Self-signed certificate generation ──────────────────────────

static int tls_generate_selfsigned(const char* cert_path, const char* key_path) {
    EVP_PKEY* pkey = EVP_PKEY_new();
    if (!pkey) return -1;

    RSA* rsa = RSA_new();
    if (!rsa) { EVP_PKEY_free(pkey); return -1; }

    BIGNUM* bn = BN_new();
    BN_set_word(bn, RSA_F4);
    if (RSA_generate_key_ex(rsa, 2048, bn, NULL) != 1) {
        BN_free(bn); RSA_free(rsa); EVP_PKEY_free(pkey); return -1;
    }
    BN_free(bn);

    EVP_PKEY_assign_RSA(pkey, rsa);

    X509* x509 = X509_new();
    if (!x509) { EVP_PKEY_free(pkey); return -1; }

    ASN1_INTEGER_set(X509_get_serialNumber(x509), 1);
    X509_gmtime_adj(X509_get_notBefore(x509), 0);
    X509_gmtime_adj(X509_get_notAfter(x509), 31536000L); // 1 year

    X509_set_pubkey(x509, pkey);

    X509_NAME* name = X509_get_subject_name(x509);
    X509_NAME_add_entry_by_txt(name, "C",  MBSTRING_ASC, (const unsigned char*)"US", -1, -1, 0);
    X509_NAME_add_entry_by_txt(name, "O",  MBSTRING_ASC, (const unsigned char*)"Bareiron", -1, -1, 0);
    X509_NAME_add_entry_by_txt(name, "CN", MBSTRING_ASC, (const unsigned char*)"bareiron.local", -1, -1, 0);

    X509_set_issuer_name(x509, name);
    X509_sign(x509, pkey, EVP_sha256());

    // Write cert
    FILE* f = fopen(cert_path, "wb");
    if (!f) { X509_free(x509); EVP_PKEY_free(pkey); return -1; }
    PEM_write_X509(f, x509);
    fclose(f);

    // Write key
    f = fopen(key_path, "wb");
    if (!f) { X509_free(x509); EVP_PKEY_free(pkey); return -1; }
    PEM_write_PrivateKey(f, pkey, NULL, NULL, 0, NULL, NULL);
    fclose(f);

    X509_free(x509);
    EVP_PKEY_free(pkey);

    printf("[TLS] Generated self-signed certificate: %s\n", cert_path);
    return 0;
}

// ── Public API ──────────────────────────────────────────────────

int tls_init(const char* cert_path, const char* key_path) {
    // Generate cert if missing
    if (access(cert_path, F_OK) != 0 || access(key_path, F_OK) != 0) {
        if (tls_generate_selfsigned(cert_path, key_path) != 0) {
            fprintf(stderr, "[TLS] Failed to generate self-signed certificate\n");
            return -1;
        }
    }

    SSL_library_init();
    SSL_load_error_strings();
    OpenSSL_add_all_algorithms();

    const SSL_METHOD* method = TLS_server_method();
    ctx = SSL_CTX_new(method);
    if (!ctx) {
        fprintf(stderr, "[TLS] SSL_CTX_new failed\n");
        return -1;
    }

    // Modern TLS settings
    SSL_CTX_set_min_proto_version(ctx, TLS1_2_VERSION);
    SSL_CTX_set_options(ctx, SSL_OP_NO_SSLv2 | SSL_OP_NO_SSLv3 | SSL_OP_NO_TLSv1 | SSL_OP_NO_TLSv1_1);

    if (SSL_CTX_use_certificate_file(ctx, cert_path, SSL_FILETYPE_PEM) <= 0) {
        fprintf(stderr, "[TLS] Failed to load certificate\n");
        ERR_print_errors_fp(stderr);
        return -1;
    }

    if (SSL_CTX_use_PrivateKey_file(ctx, key_path, SSL_FILETYPE_PEM) <= 0) {
        fprintf(stderr, "[TLS] Failed to load private key\n");
        ERR_print_errors_fp(stderr);
        return -1;
    }

    // Verify key matches cert
    if (SSL_CTX_check_private_key(ctx) != 1) {
        fprintf(stderr, "[TLS] Private key does not match certificate\n");
        return -1;
    }

    memset(ssl_table, 0, sizeof(ssl_table));
    printf("[TLS] Initialized. Cert: %s\n", cert_path);
    return 0;
}

int tls_accept(int client_fd) {
    int tls_fd = find_free_tls_fd();
    if (tls_fd < 0) {
        fprintf(stderr, "[TLS] No free TLS slots\n");
        return -1;
    }

    SSL* ssl = SSL_new(ctx);
    if (!ssl) {
        fprintf(stderr, "[TLS] SSL_new failed\n");
        return -1;
    }

    SSL_set_fd(ssl, client_fd);

    int ret = SSL_accept(ssl);
    if (ret <= 0) {
        int err = SSL_get_error(ssl, ret);
        if (err == SSL_ERROR_WANT_READ || err == SSL_ERROR_WANT_WRITE) {
            // Non-blocking retry will happen in the main loop
            // For now, store and retry on next iteration
        } else {
            fprintf(stderr, "[TLS] SSL_accept failed (err=%d)\n", err);
            SSL_free(ssl);
            return -1;
        }
    }

    ssl_table[tls_fd].active = 1;
    ssl_table[tls_fd].real_fd = client_fd;
    ssl_table[tls_fd].ssl = ssl;

    return tls_fd;
}

ssize_t tls_recv(int tls_fd, void* buf, size_t len) {
    if (tls_fd < 0 || tls_fd >= MAX_TLS_FDS || !ssl_table[tls_fd].active) {
        errno = EBADF;
        return -1;
    }
    SSL* ssl = ssl_table[tls_fd].ssl;
    int ret = SSL_read(ssl, buf, (int)len);
    if (ret < 0) {
        int err = SSL_get_error(ssl, ret);
        if (err == SSL_ERROR_WANT_READ || err == SSL_ERROR_WANT_WRITE) {
            errno = EAGAIN;
            return -1;
        }
    }
    return ret;
}

ssize_t tls_send(int tls_fd, const void* buf, size_t len) {
    if (tls_fd < 0 || tls_fd >= MAX_TLS_FDS || !ssl_table[tls_fd].active) {
        errno = EBADF;
        return -1;
    }
    SSL* ssl = ssl_table[tls_fd].ssl;
    int ret = SSL_write(ssl, buf, (int)len);
    if (ret < 0) {
        int err = SSL_get_error(ssl, ret);
        if (err == SSL_ERROR_WANT_READ || err == SSL_ERROR_WANT_WRITE) {
            errno = EAGAIN;
            return -1;
        }
    }
    return ret;
}

int tls_close(int tls_fd) {
    if (tls_fd < 0 || tls_fd >= MAX_TLS_FDS || !ssl_table[tls_fd].active) {
        return -1;
    }
    SSL* ssl = ssl_table[tls_fd].ssl;
    int real_fd = ssl_table[tls_fd].real_fd;

    if (ssl) {
        SSL_shutdown(ssl);
        SSL_free(ssl);
    }
    if (real_fd >= 0) close(real_fd);

    ssl_table[tls_fd].active = 0;
    ssl_table[tls_fd].ssl = NULL;
    ssl_table[tls_fd].real_fd = -1;
    return 0;
}

void tls_cleanup(void) {
    for (int i = 0; i < MAX_TLS_FDS; i++) {
        if (ssl_table[i].active) {
            tls_close(i);
        }
    }
    if (ctx) {
        SSL_CTX_free(ctx);
        ctx = NULL;
    }
    EVP_cleanup();
    ERR_free_strings();
}

const char* tls_error(int tls_fd) {
    static char buf[256];
    if (tls_fd < 0 || tls_fd >= MAX_TLS_FDS || !ssl_table[tls_fd].active) {
        return "Invalid TLS fd";
    }
    long err = SSL_get_error(ssl_table[tls_fd].ssl, -1);
    snprintf(buf, sizeof(buf), "SSL error %ld", err);
    return buf;
}

/** Return the real underlying fd for a TLS wrapper fd.
 *  Useful for select() or poll() before calling tls_accept/recv.
 */
int tls_get_real_fd(int tls_fd) {
    return tls_fd_to_real(tls_fd);
}

/** Check if a fd is a TLS wrapper (not a raw socket). */
int tls_is_tls_fd(int fd) {
    return (fd >= 0 && fd < MAX_TLS_FDS && ssl_table[fd].active);
}
