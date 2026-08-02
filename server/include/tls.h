/** tls.h — OpenSSL TLS wrapper for BAREIRON server
 *
 * Provides TLS encryption on top of the existing socket layer.
 * Uses self-signed certificate generated on first run if none exists.
 * Requires: -lssl -lcrypto
 */

#ifndef TLS_H
#define TLS_H

#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/x509v3.h>
#include <sys/socket.h>

/** Initialize OpenSSL library and TLS context.
 *  Loads or generates server certificate + key.
 *  Call once at server startup.
 *  Returns 0 on success, -1 on failure.
 */
int tls_init(const char* cert_path, const char* key_path);

/** Wrap an accepted client socket with TLS.
 *  Returns a new file descriptor (-1 on failure).
 *  Internally stores SSL* in a lookup table keyed by fd.
 */
int tls_accept(int client_fd);

/** Read from TLS socket (like recv). */
ssize_t tls_recv(int tls_fd, void* buf, size_t len);

/** Write to TLS socket (like send). */
ssize_t tls_send(int tls_fd, const void* buf, size_t len);

/** Graceful TLS shutdown, then close underlying fd.
 *  Returns 0 on success.
 */
int tls_close(int tls_fd);

/** Cleanup OpenSSL state. Call at server shutdown. */
void tls_cleanup(void);

/** Return human-readable TLS error string for a fd. */
const char* tls_error(int tls_fd);

/** Check if a fd is a TLS wrapper (not a raw socket). */
int tls_is_tls_fd(int fd);

/** Maximum TLS sockets we can track. */
#define MAX_TLS_FDS 32

#endif
