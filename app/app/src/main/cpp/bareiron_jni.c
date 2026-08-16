#include <jni.h>
#include <unistd.h>
#include "globals.h"

extern int bareiron_android_server_main(int argc, char** argv);

volatile int bareiron_android_stop_requested = 0;
static volatile int bareiron_android_running = 0;

static void close_known_player_sockets(void) {
    for (int i = 0; i < MAX_PLAYERS; i++) {
        if (player_data[i].client_fd >= 0) {
            close(player_data[i].client_fd);
            player_data[i].client_fd = -1;
        }
    }
    client_count = 0;
}

JNIEXPORT jint JNICALL
Java_com_bareiron_game_NativeBareiron_nativeRun(JNIEnv* env, jclass clazz, jstring data_dir) {
    (void)clazz;
    if (bareiron_android_running) return -2;

    const char* path = (*env)->GetStringUTFChars(env, data_dir, NULL);
    if (path == NULL) return -3;

    if (chdir(path) != 0) {
        (*env)->ReleaseStringUTFChars(env, data_dir, path);
        return -4;
    }
    (*env)->ReleaseStringUTFChars(env, data_dir, path);

    bareiron_android_stop_requested = 0;
    bareiron_android_running = 1;

    char arg0[] = "bareiron";
    char* argv[] = {arg0, NULL};
    int result = bareiron_android_server_main(1, argv);

    close_known_player_sockets();
    bareiron_android_running = 0;
    bareiron_android_stop_requested = 0;
    return result;
}

JNIEXPORT void JNICALL
Java_com_bareiron_game_NativeBareiron_nativeRequestStop(JNIEnv* env, jclass clazz) {
    (void)env;
    (void)clazz;
    bareiron_android_stop_requested = 1;
}

JNIEXPORT jboolean JNICALL
Java_com_bareiron_game_NativeBareiron_nativeIsRunning(JNIEnv* env, jclass clazz) {
    (void)env;
    (void)clazz;
    return bareiron_android_running ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_bareiron_game_NativeBareiron_nativePlayerCount(JNIEnv* env, jclass clazz) {
    (void)env;
    (void)clazz;
    return client_count;
}
