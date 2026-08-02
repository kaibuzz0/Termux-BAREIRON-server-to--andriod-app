// ProfileActivity.java — Player stats, achievements, loadouts
package com.bareiron.game;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvName, tvKills, tvWaves, tvBosses, tvQuests, tvScore, tvPlaytime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        tvName = findViewById(R.id.tvName);
        tvKills = findViewById(R.id.tvKills);
        tvWaves = findViewById(R.id.tvWaves);
        tvBosses = findViewById(R.id.tvBosses);
        tvQuests = findViewById(R.id.tvQuests);
        tvScore = findViewById(R.id.tvScore);
        tvPlaytime = findViewById(R.id.tvPlaytime);
        
        PlayerProgress.init(this);
        PlayerProgress pg = PlayerProgress.get();
        
        tvName.setText("Player Profile");
        tvKills.setText("🧟 Zombies Killed: " + pg.getKills());
        tvWaves.setText("🌊 Highest Wave: " + pg.getHighestWave());
        tvBosses.setText("👹 Bosses Defeated: " + pg.getBossesDefeated());
        tvQuests.setText("📜 Quests Completed: " + pg.getQuestsCompleted());
        tvScore.setText("🏆 Total Score: " + pg.getTotalScore());
        tvPlaytime.setText("⏱️ Play Time: " + pg.getPlayTimeMinutes() + " min");
    }
}
