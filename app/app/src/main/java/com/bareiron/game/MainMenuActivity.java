package com.bareiron.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;

/** Appliance-style dashboard for hosting the embedded BAREIRON server. */
public class MainMenuActivity extends AppCompatActivity {
    private static final int DEFAULT_PORT = 25565;

    private final Handler dashboardHandler = new Handler(Looper.getMainLooper());
    private final Runnable dashboardTicker = new Runnable() {
        @Override public void run() {
            refreshDashboard();
            dashboardHandler.postDelayed(this, 1000);
        }
    };

    private TextView tvServerState;
    private TextView tvAddress;
    private TextView tvPlayers;
    private TextView tvRuntimeNote;
    private Button btnStartServer;
    private Button btnStopServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tvServerState = findViewById(R.id.tvServerState);
        tvAddress = findViewById(R.id.tvAddress);
        tvPlayers = findViewById(R.id.tvPlayers);
        tvRuntimeNote = findViewById(R.id.tvRuntimeNote);
        btnStartServer = findViewById(R.id.btnStartServer);
        btnStopServer = findViewById(R.id.btnStopServer);
        Button btnShare = findViewById(R.id.btnShareServer);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnStartServer.setOnClickListener(v -> startServer());
        btnStopServer.setOnClickListener(v -> stopServer());
        btnShare.setOnClickListener(v -> shareServer());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dashboardHandler.post(dashboardTicker);
    }

    @Override
    protected void onPause() {
        dashboardHandler.removeCallbacks(dashboardTicker);
        super.onPause();
    }

    private void startServer() {
        Intent intent = new Intent(this, BareironServerService.class)
            .setAction(BareironServerService.ACTION_START);
        ContextCompat.startForegroundService(this, intent);
        refreshDashboard();
    }

    private void stopServer() {
        Intent intent = new Intent(this, BareironServerService.class)
            .setAction(BareironServerService.ACTION_STOP);
        startService(intent);
        refreshDashboard();
    }

    private void shareServer() {
        String address = getLanAddress();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT,
            "Join my BAREIRON server on the same network: " + address + ":" + DEFAULT_PORT);
        startActivity(Intent.createChooser(share, "Share BAREIRON server"));
    }

    private void refreshDashboard() {
        boolean running = BareironServerService.isRunning();
        tvServerState.setText(running ? "RUNNING" : "STOPPED");
        tvAddress.setText(getLanAddress() + ":" + DEFAULT_PORT);
        tvPlayers.setText("Players: " + BareironServerService.getPlayerCount() + " / 8");

        if (running) {
            tvRuntimeNote.setText("Native BAREIRON is hosted on this device. Keep this device on the same network as the players.");
        } else {
            int exitCode = BareironServerService.getLastExitCode();
            tvRuntimeNote.setText(exitCode == 0
                ? "Tap START SERVER. Other devices on this network can join at the address above."
                : "Server stopped with native exit code " + exitCode + ". Check CI/runtime diagnostics before release.");
        }

        btnStartServer.setEnabled(!running);
        btnStopServer.setEnabled(running);
    }

    private String getLanAddress() {
        try {
            for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!nif.isUp() || nif.isLoopback()) continue;
                for (java.net.InetAddress address : Collections.list(nif.getInetAddresses())) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }
}
