// PlayerProgress.java — Tracks everything needed for unlockables
package com.bareiron.game;

import android.content.Context;
import android.content.SharedPreferences;

public class PlayerProgress {
    private static final String PREFS_NAME = "bareiron_progress";
    private static PlayerProgress instance;
    private SharedPreferences prefs;
    
    // Stats
    private int totalKills;
    private int highestWave;
    private int bossesDefeated;
    private int questsCompleted;
    private int villagesVisited;
    private int totalPlayTimeMinutes;
    private int totalScore;
    
    private PlayerProgress(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        load();
    }
    
    public static void init(Context ctx) {
        if (instance == null) instance = new PlayerProgress(ctx);
    }
    
    public static PlayerProgress get() {
        return instance;
    }
    
    private void load() {
        totalKills = prefs.getInt("kills", 0);
        highestWave = prefs.getInt("wave", 0);
        bossesDefeated = prefs.getInt("bosses", 0);
        questsCompleted = prefs.getInt("quests", 0);
        villagesVisited = prefs.getInt("villages", 0);
        totalPlayTimeMinutes = prefs.getInt("playtime", 0);
        totalScore = prefs.getInt("score", 0);
    }
    
    private void save() {
        SharedPreferences.Editor e = prefs.edit();
        e.putInt("kills", totalKills);
        e.putInt("wave", highestWave);
        e.putInt("bosses", bossesDefeated);
        e.putInt("quests", questsCompleted);
        e.putInt("villages", villagesVisited);
        e.putInt("playtime", totalPlayTimeMinutes);
        e.putInt("score", totalScore);
        e.apply();
    }
    
    // ── Stat Getters ───────────────────────────────────────────
    public int getKills() { return totalKills; }
    public int getHighestWave() { return highestWave; }
    public int getBossesDefeated() { return bossesDefeated; }
    public int getQuestsCompleted() { return questsCompleted; }
    public int getVillagesVisited() { return villagesVisited; }
    public int getPlayTimeMinutes() { return totalPlayTimeMinutes; }
    public int getTotalScore() { return totalScore; }
    
    public int getStat(String type) {
        switch (type) {
            case "kills": return totalKills;
            case "waves": return highestWave;
            case "bosses": return bossesDefeated;
            case "quests": return questsCompleted;
            case "visits": return villagesVisited;
            case "playtime": return totalPlayTimeMinutes;
            case "score": return totalScore;
            default: return 0;
        }
    }
    
    // ── Stat Incrementers ──────────────────────────────────────
    public void addKill(int n) { totalKills += n; save(); }
    public void setWave(int w) { if (w > highestWave) { highestWave = w; save(); } }
    public void addBossKill() { bossesDefeated++; save(); }
    public void addQuestComplete() { questsCompleted++; save(); }
    public void visitVillage() { villagesVisited++; save(); }
    public void addPlayTime(int minutes) { totalPlayTimeMinutes += minutes; save(); }
    public void addScore(int s) { totalScore += s; save(); }
}
