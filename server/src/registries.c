// registries.c — COMPREHENSIVE STUB for TLS integration test
// Generated from source code analysis. Full version from build_registries.js.

#include <stdint.h>
#include <stddef.h>

// Block-to-item and item-to-block lookup (identity mapping as fallback)
uint16_t B_to_I[256];
uint16_t I_to_B[256];
uint16_t block_palette[256];
uint16_t network_block_palette[256];

// Binary blobs (minimal stubs — client may disconnect but TLS handshake works)
uint8_t registries_bin[1] = {0x00};
size_t registries_bin_size = 0;
uint8_t tags_bin[1] = {0x00};
size_t tags_bin_size = 0;

// Initialize lookup tables with identity mapping
__attribute__((constructor))
static void init_lookups(void) {
    for (int i = 0; i < 256; i++) {
        B_to_I[i] = i;
        I_to_B[i] = i;
        block_palette[i] = i;
        network_block_palette[i] = i;
    }
}
