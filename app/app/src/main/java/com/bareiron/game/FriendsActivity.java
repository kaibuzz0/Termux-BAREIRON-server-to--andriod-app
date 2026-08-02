// FriendsActivity.java — Friend list, invites, party system
package com.bareiron.game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class FriendsActivity extends AppCompatActivity {
    private LinearLayout layoutFriends;
    private EditText etAddFriend;
    private Button btnAddFriend;
    private TextView tvOnlineCount;
    
    private static final String PREFS_FRIENDS = "bareiron_friends";
    private List<Friend> friends = new ArrayList<>();
    
    static class Friend {
        String name;
        String status; // "online", "offline", "in_game", "in_menu"
        String currentServer;
        boolean isPartyMember;
        
        Friend(String name, String status) {
            this.name = name;
            this.status = status;
            this.currentServer = "";
            this.isPartyMember = false;
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        
        layoutFriends = findViewById(R.id.layoutFriends);
        etAddFriend = findViewById(R.id.etAddFriend);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        tvOnlineCount = findViewById(R.id.tvOnlineCount);
        
        loadFriends();
        renderFriends();
        
        btnAddFriend.setOnClickListener(v -> {
            String name = etAddFriend.getText().toString().trim();
            if (!name.isEmpty()) {
                addFriend(name);
                etAddFriend.setText("");
            }
        });
        
        // Simulate online status updates
        simulateStatuses();
    }
    
    private void loadFriends() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FRIENDS, MODE_PRIVATE);
        String json = prefs.getString("friend_list", "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                friends.add(new Friend(obj.getString("name"), obj.getString("status")));
            }
        } catch (Exception e) {}
        
        // Demo friends if empty
        if (friends.isEmpty()) {
            friends.add(new Friend("ShadowHunter", "in_game"));
            friends.add(new Friend("NoobSlayer99", "online"));
            friends.add(new Friend("PixelQueen", "offline"));
            friends.add(new Friend("YetiKing", "in_menu"));
            saveFriends();
        }
    }
    
    private void saveFriends() {
        try {
            JSONArray arr = new JSONArray();
            for (Friend f : friends) {
                JSONObject obj = new JSONObject();
                obj.put("name", f.name);
                obj.put("status", f.status);
                arr.put(obj);
            }
            getSharedPreferences(PREFS_FRIENDS, MODE_PRIVATE)
                .edit().putString("friend_list", arr.toString()).apply();
        } catch (Exception e) {}
    }
    
    private void addFriend(String name) {
        for (Friend f : friends) {
            if (f.name.equalsIgnoreCase(name)) {
                Toast.makeText(this, "Already friends with " + name, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        friends.add(new Friend(name, "offline"));
        saveFriends();
        renderFriends();
        Toast.makeText(this, "Added " + name, Toast.LENGTH_SHORT).show();
    }
    
    private void renderFriends() {
        layoutFriends.removeAllViews();
        int online = 0;
        
        for (Friend f : friends) {
            if (!f.status.equals("offline")) online++;
            
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(16, 12, 16, 12);
            
            // Status dot
            TextView dot = new TextView(this);
            dot.setText("●");
            dot.setTextSize(20);
            dot.setTextColor(getStatusColor(f.status));
            
            // Name + status
            TextView tv = new TextView(this);
            tv.setText(f.name + "\n" + getStatusText(f.status));
            tv.setTextColor(0xFFFFFFFF);
            tv.setTextSize(14);
            tv.setPadding(16, 0, 0, 0);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            // Action buttons
            LinearLayout btnRow = new LinearLayout(this);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);
            
            if (!f.status.equals("offline")) {
                Button btnJoin = new Button(this);
                btnJoin.setText("Join");
                btnJoin.setTextSize(10);
                btnJoin.setPadding(8, 4, 8, 4);
                btnJoin.setOnClickListener(v -> {
                    Toast.makeText(this, "Joining " + f.name + "...", Toast.LENGTH_SHORT).show();
                });
                btnRow.addView(btnJoin);
            }
            
            Button btnInvite = new Button(this);
            btnInvite.setText("Invite");
            btnInvite.setTextSize(10);
            btnInvite.setPadding(8, 4, 8, 4);
            btnInvite.setOnClickListener(v -> {
                Toast.makeText(this, "Invited " + f.name, Toast.LENGTH_SHORT).show();
            });
            btnRow.addView(btnInvite);
            
            row.addView(dot);
            row.addView(tv);
            row.addView(btnRow);
            layoutFriends.addView(row);
            
            // Divider
            View div = new View(this);
            div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            div.setBackgroundColor(0xFF333333);
            layoutFriends.addView(div);
        }
        
        tvOnlineCount.setText("Friends Online: " + online + "/" + friends.size());
    }
    
    private int getStatusColor(String status) {
        switch (status) {
            case "online": return 0xFF4CAF50;
            case "in_game": return 0xFFFF9800;
            case "in_menu": return 0xFF2196F3;
            default: return 0xFF666666;
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "online": return "In Lobby";
            case "in_game": return "In Game";
            case "in_menu": return "In Menu";
            default: return "Offline";
        }
    }
    
    private void simulateStatuses() {
        // In production, this would poll a backend
        // For demo, randomly rotate statuses every 30 seconds
        new android.os.Handler().postDelayed(() -> {
            if (!isFinishing()) {
                for (Friend f : friends) {
                    if (Math.random() > 0.7) {
                        String[] statuses = {"online", "in_game", "in_menu", "offline"};
                        f.status = statuses[(int)(Math.random() * statuses.length)];
                    }
                }
                renderFriends();
                simulateStatuses();
            }
        }, 30000);
    }
}
