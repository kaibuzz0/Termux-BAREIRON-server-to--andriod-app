#ifndef TLS_H
#define TLS_H

#include <stddef.h>
#include <sys/types.h>

int tls_init(const char* cert_path, const char* key_path);
int tls_accept(int client_fd);
ssize_t tls_recv(int tls_fd, void* buf, size_t len);
ssize_t tls_send(int tls_fd, const void* buf, size_t len);
int tls_close(int tls_fd);
void tls_cleanup(void);
const char* tls_error(int tls_fd);
int tls_is_tls_fd(int fd);

#define MAX_TLS_FDS 32

#endif
