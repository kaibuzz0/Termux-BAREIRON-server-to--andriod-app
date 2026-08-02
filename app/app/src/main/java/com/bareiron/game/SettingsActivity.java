// SettingsActivity.java — Game settings, audio, controls
package com.bareiron.game;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private SeekBar sbMusic, sbSfx;
    private Switch swNotifications, swVibration;
    private Button btnClearData, btnDevUnlock;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        sbMusic = findViewById(R.id.sbMusic);
        sbSfx = findViewById(R.id.sbSfx);
        swNotifications = findViewById(R.id.swNotifications);
        swVibration = findViewById(R.id.swVibration);
        btnClearData = findViewById(R.id.btnClearData);
        btnDevUnlock = findViewById(R.id.btnDevUnlock);
        
        sbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {}
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {
                Toast.makeText(SettingsActivity.this, "Music: " + s.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnClearData.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear All Data?")
                .setMessage("This will reset all progress, purchases, and friends.")
                .setPositiveButton("CLEAR", (d, w) -> {
                    getSharedPreferences("bareiron_progress", MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences("bareiron_billing", MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences("bareiron_friends", MODE_PRIVATE).edit().clear().apply();
                    Toast.makeText(this, "All data cleared", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
        
        btnDevUnlock.setOnClickListener(v -> {
            BillingManager.init(this);
            BillingManager.get().devOwnAll(this);
        });
    }
}
