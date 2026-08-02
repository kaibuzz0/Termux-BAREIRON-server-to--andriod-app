// SinglePlayerManager.java — Download, cache, and run bareiron locally
package com.bareiron.game;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.*;

/**
 * Manages the local bareiron server for Single Player mode.
 *
 * Strategy (simplest):
 *   1. Look for bareiron in app private files
 *   2. If missing, look in known Termux paths
 *   3. If still missing, download from GitHub releases (~250KB)
 *   4. chmod +x, start with ProcessBuilder
 *   5. Poll 127.0.0.1:25565 until alive
 *   6. Notify callback → launch GameActivity
 */
public class SinglePlayerManager {
    private static final String TAG = "SinglePlayer";
    private static final int PORT = 25565;
    private static final String BINARY_NAME = "bareiron";
    private static final String DOWNLOAD_URL =
        "https://github.com/kaibuzz0/Termux-Mobile-BAREIRON-server/releases/download/latest/bareiron";
    // TODO: create a real release asset; for now we build locally

    private Context ctx;
    private Process serverProcess;
    private boolean starting = false;

    public SinglePlayerManager(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    /** Entry point: called from MainMenuActivity SINGLE PLAYER button */
    public void startSinglePlayer(final ServerReadyCallback callback) {
        if (starting) {
            callback.onStatus("Already starting...");
            return;
        }
        starting = true;

        callback.onStatus("Looking for server binary...");

        new Thread(() -> {
            try {
                File binary = findOrObtainBinary(callback);
                if (binary == null || !binary.canExecute()) {
                    callback.onFailed("Could not prepare server binary.");
                    starting = false;
                    return;
                }

                callback.onStatus("Starting local server...");
                serverProcess = startBinary(binary);

                callback.onStatus("Waiting for server (port " + PORT + ")...");
                boolean alive = pollPort(PORT, 30000); // 30s timeout

                if (alive) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onReady());
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onFailed("Server did not start. Check logs.");
                    });
                    killServer();
                }

            } catch (Exception e) {
                Log.e(TAG, "startSinglePlayer failed", e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailed(e.getMessage()));
            } finally {
                starting = false;
            }
        }).start();
    }

    /** Find bareiron: app files → Termux paths → download */
    private File findOrObtainBinary(ServerReadyCallback callback) {
        // 1. App private storage
        File appBin = new File(ctx.getFilesDir(), BINARY_NAME);
        if (appBin.exists() && appBin.canExecute()) {
            callback.onStatus("Found binary in app storage.");
            return appBin;
        }

        // 2. Termux home / known paths
        String[] termuxPaths = {
            "/data/data/com.termux/files/home/bareiron",
            "/data/data/com.termux/files/home/Termux-Mobile-BAREIRON-server/bareiron",
            "/data/data/com.termux/files/usr/bin/bareiron",
            "/data/data/com.termux/files/home/Termux-BAREIRON-server-to--andriod-app/server/bareiron"
        };
        for (String path : termuxPaths) {
            File f = new File(path);
            if (f.exists()) {
                callback.onStatus("Found Termux binary.");
                // Copy to app storage so we don't depend on Termux forever
                if (copyFile(f, appBin)) {
                    appBin.setExecutable(true);
                    return appBin;
                }
            }
        }

        // 3. Download from releases (future — stub for now)
        callback.onStatus("Binary not found locally.");
        callback.onStatus("Please build the server first.");
        // TODO: implement download once we have release artifacts
        return null;
    }

    /** Copy file from source to destination */
    private boolean copyFile(File src, File dst) {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "copyFile failed", e);
            return false;
        }
    }

    /** Start the binary as a subprocess */
    private Process startBinary(File binary) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(binary.getAbsolutePath());
        pb.directory(binary.getParentFile());
        pb.redirectErrorStream(true);
        // Working dir needs config/ etc. If binary is in app storage, we need assets.
        // For now, point to wherever configs live.
        Process proc = pb.start();

        // Log stdout for debugging
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "SERVER: " + line);
                }
            } catch (IOException ignored) {}
        }).start();

        return proc;
    }

    /** Poll TCP port until it accepts connections or timeout */
    private boolean pollPort(int port, int timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress("127.0.0.1", port), 1000);
                return true;
            } catch (IOException e) {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        return false;
    }

    /** Kill the server process */
    public void killServer() {
        if (serverProcess != null) {
            serverProcess.destroy();
            try { serverProcess.waitFor(); } catch (InterruptedException ignored) {}
            serverProcess = null;
        }
    }

    public interface ServerReadyCallback {
        void onStatus(String msg);
        void onReady();
        void onFailed(String reason);
    }
}
