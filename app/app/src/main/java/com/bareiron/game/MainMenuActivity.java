// MainMenuActivity.java — Main launcher: Single Player, Multiplayer, Friends, Profile, Store
package com.bareiron.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    private TextView tvPlayerName;
    private TextView tvStats;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvStats = findViewById(R.id.tvStats);
        
        Button btnSinglePlayer = findViewById(R.id.btnSinglePlayer);
        Button btnMultiplayer = findViewById(R.id.btnMultiplayer);
        Button btnFriends = findViewById(R.id.btnFriends);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnStore = findViewById(R.id.btnStore);
        Button btnSettings = findViewById(R.id.btnSettings);
        
        btnSinglePlayer.setOnClickListener(v -> startGame(true));
        btnMultiplayer.setOnClickListener(v -> {
            startActivity(new Intent(this, ServerBrowserActivity.class));
        });
        btnFriends.setOnClickListener(v -> {
            startActivity(new Intent(this, FriendsActivity.class));
        });
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
        btnStore.setOnClickListener(v -> {
            startActivity(new Intent(this, StoreActivity.class));
        });
        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
        
        loadProfile();
    }
    
    private void loadProfile() {
        PlayerProgress.init(this);
        PlayerProgress pg = PlayerProgress.get();
        tvPlayerName.setText("Player");
        tvStats.setText("Kills: " + pg.getKills() + " | Wave: " + pg.getHighestWave() + 
            " | Score: " + pg.getTotalScore());
    }
    
    private void startGame(boolean singlePlayer) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("single_player", singlePlayer);
        if (singlePlayer) {
            // Use localhost
            intent.putExtra("server_ip", "127.0.0.1");
            intent.putExtra("server_port", 25565);
        }
        startActivity(intent);
    }
}
