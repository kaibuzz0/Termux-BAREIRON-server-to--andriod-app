#include "tls.h"
#include <sys/socket.h>
#include <unistd.h>

/*
 * First Android milestone intentionally runs BAREIRON in its existing plain
 * LAN mode. The desktop/Termux OpenSSL implementation remains unchanged.
 * These functions satisfy the server transport abstraction on Android while
 * use_tls stays disabled.
 */
int tls_init(const char* cert_path, const char* key_path) {
    (void)cert_path;
    (void)key_path;
    return -1;
}

int tls_accept(int client_fd) {
    return client_fd;
}

ssize_t tls_recv(int tls_fd, void* buf, size_t len) {
    return recv(tls_fd, buf, len, 0);
}

ssize_t tls_send(int tls_fd, const void* buf, size_t len) {
    return send(tls_fd, buf, len, 0);
}

int tls_close(int tls_fd) {
    return close(tls_fd);
}

void tls_cleanup(void) {
}

const char* tls_error(int tls_fd) {
    (void)tls_fd;
    return "TLS is not enabled in the Android LAN host build";
}

int tls_is_tls_fd(int fd) {
    (void)fd;
    return 0;
}
