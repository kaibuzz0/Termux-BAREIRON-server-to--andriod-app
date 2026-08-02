// SinglePlayerLauncherActivity.java — Shows progress while starting local server
package com.bareiron.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SinglePlayerLauncherActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_launcher);
        
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);
        
        progressBar.setIndeterminate(true);
        
        SinglePlayerManager mgr = new SinglePlayerManager(this);
        mgr.startSinglePlayer(new SinglePlayerManager.ServerReadyCallback() {
            @Override
            public void onStatus(String msg) {
                runOnUiThread(() -> tvStatus.setText(msg));
            }
            
            @Override
            public void onReady() {
                runOnUiThread(() -> {
                    tvStatus.setText("Server ready! Launching game...");
                    Intent intent = new Intent(SinglePlayerLauncherActivity.this, GameActivity.class);
                    intent.putExtra("single_player", true);
                    intent.putExtra("server_ip", "127.0.0.1");
                    intent.putExtra("server_port", 25565);
                    startActivity(intent);
                    finish();
                });
            }
            
            @Override
            public void onFailed(String reason) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ " + reason);
                    progressBar.setIndeterminate(false);
                    progressBar.setProgress(0);
                    Toast.makeText(SinglePlayerLauncherActivity.this, 
                        "Failed: " + reason, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't kill server here — GameActivity needs it
    }
}
