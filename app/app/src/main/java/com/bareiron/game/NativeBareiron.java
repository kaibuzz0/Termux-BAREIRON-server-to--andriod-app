package com.bareiron.game;

/** JNI facade for the BAREIRON C server embedded in the APK. */
public final class NativeBareiron {
    static {
        System.loadLibrary("bareiron_android");
    }

    private NativeBareiron() {}

    /** Blocks until the native server exits. Call from a worker thread only. */
    public static int run(String dataDirectory) {
        return nativeRun(dataDirectory);
    }

    public static void requestStop() {
        nativeRequestStop();
    }

    public static boolean isRunning() {
        return nativeIsRunning();
    }

    public static int getPlayerCount() {
        return nativePlayerCount();
    }

    private static native int nativeRun(String dataDirectory);
    private static native void nativeRequestStop();
    private static native boolean nativeIsRunning();
    private static native int nativePlayerCount();
}
