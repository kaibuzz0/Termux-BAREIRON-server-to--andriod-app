// ServerBrowserActivity.java — Find servers, see player counts, direct connect
package com.bareiron.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerBrowserActivity extends AppCompatActivity {
    private LinearLayout layoutServers;
    private EditText etDirectIP, etDirectPort;
    private Button btnDirectConnect;
    private TextView tvRefreshing;
    private Handler handler = new Handler(Looper.getMainLooper());
    
    private List<ServerInfo> servers = new ArrayList<>();
    
    static class ServerInfo {
        String name;
        String ip;
        int port;
        int players;
        int maxPlayers;
        int ping;
        String motd;
        String version;
        boolean favorite;
        
        ServerInfo(String name, String ip, int port) {
            this.name = name;
            this.ip = ip;
            this.port = port;
            this.players = 0;
            this.maxPlayers = 20;
            this.ping = -1;
            this.motd = "";
            this.version = "";
            this.favorite = false;
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_browser);
        
        layoutServers = findViewById(R.id.layoutServers);
        etDirectIP = findViewById(R.id.etDirectIP);
        etDirectPort = findViewById(R.id.etDirectPort);
        btnDirectConnect = findViewById(R.id.btnDirectConnect);
        tvRefreshing = findViewById(R.id.tvRefreshing);
        
        btnDirectConnect.setOnClickListener(v -> {
            String ip = etDirectIP.getText().toString().trim();
            String portStr = etDirectPort.getText().toString().trim();
            if (ip.isEmpty()) {
                Toast.makeText(this, "Enter IP address", Toast.LENGTH_SHORT).show();
                return;
            }
            int port = portStr.isEmpty() ? 25565 : Integer.parseInt(portStr);
            joinServer(ip, port);
        });
        
        loadServers();
        renderServers();
        
        // Auto-refresh pings
        refreshPings();
    }
    
    private void loadServers() {
        // Load favorites
        SharedPreferences prefs = getSharedPreferences("bareiron_servers", MODE_PRIVATE);
        String json = prefs.getString("saved_servers", "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                ServerInfo s = new ServerInfo(
                    obj.getString("name"),
                    obj.getString("ip"),
                    obj.getInt("port")
                );
                s.favorite = true;
                servers.add(s);
            }
        } catch (Exception e) {}
        
        // Add demo servers if empty
        if (servers.isEmpty()) {
            servers.add(new ServerInfo("Local Test", "127.0.0.1", 25565));
            servers.add(new ServerInfo("Friend's Server", "192.168.1.100", 25565));
            servers.add(new ServerInfo("Public Server #1", "bareiron.example.com", 25565));
        }
    }
    
    private void renderServers() {
        layoutServers.removeAllViews();
        
        for (ServerInfo s : servers) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);
            card.setBackgroundColor(0xFF1E1E1E);
            
            // Name + favorite
            LinearLayout header = new LinearLayout(this);
            header.setOrientation(LinearLayout.HORIZONTAL);
            
            TextView tvName = new TextView(this);
            tvName.setText((s.favorite ? "⭐ " : "") + s.name);
            tvName.setTextColor(0xFFFFFFFF);
            tvName.setTextSize(16);
            tvName.setTextStyle(android.graphics.Typeface.BOLD);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            TextView tvPing = new TextView(this);
            tvPing.setText(s.ping >= 0 ? s.ping + "ms" : "...");
            tvPing.setTextColor(s.ping > 100 ? 0xFFFF9800 : 0xFF4CAF50);
            tvPing.setTextSize(12);
            
            header.addView(tvName);
            header.addView(tvPing);
            
            // MOTD
            TextView tvMotd = new TextView(this);
            tvMotd.setText(s.motd.isEmpty() ? s.ip + ":" + s.port : s.motd);
            tvMotd.setTextColor(0xFFAAAAAA);
            tvMotd.setTextSize(12);
            tvMotd.setPadding(0, 4, 0, 4);
            
            // Players
            TextView tvPlayers = new TextView(this);
            tvPlayers.setText(s.players + "/" + s.maxPlayers + " players");
            tvPlayers.setTextColor(0xFF888888);
            tvPlayers.setTextSize(12);
            
            // Join button
            Button btnJoin = new Button(this);
            btnJoin.setText("JOIN SERVER");
            btnJoin.setTextColor(0xFFFFFFFF);
            btnJoin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            btnJoin.setOnClickListener(v -> joinServer(s.ip, s.port));
            
            card.addView(header);
            card.addView(tvMotd);
            card.addView(tvPlayers);
            card.addView(btnJoin);
            
            layoutServers.addView(card);
            
            // Divider
            View div = new View(this);
            div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 8));
            layoutServers.addView(div);
        }
    }
    
    private void refreshPings() {
        tvRefreshing.setText("Refreshing...");
        
        for (ServerInfo s : servers) {
            new Thread(() -> {
                long start = System.currentTimeMillis();
                try {
                    Socket sock = new Socket();
                    sock.connect(new InetSocketAddress(s.ip, s.port), 5000);
                    
                    // Send status request
                    DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                    DataInputStream in = new DataInputStream(sock.getInputStream());
                    
                    // Handshake
                    ByteArrayOutputStream hs = new ByteArrayOutputStream();
                    DataOutputStream hw = new DataOutputStream(hs);
                    hw.writeByte(0x00);
                    writeVarInt(hw, 772);
                    writeVarInt(hw, s.ip.length());
                    hw.writeBytes(s.ip);
                    hw.writeShort(s.port);
                    hw.writeByte(0x01);
                    
                    byte[] hwBytes = hs.toByteArray();
                    writeVarInt(out, hwBytes.length);
                    out.write(hwBytes);
                    
                    // Status request
                    out.writeByte(0x01);
                    out.writeByte(0x00);
                    
                    // Read response
                    int len = readVarInt(in);
                    int packetId = readVarInt(in);
                    int jsonLen = readVarInt(in);
                    byte[] jsonBytes = new byte[jsonLen];
                    in.readFully(jsonBytes);
                    String json = new String(jsonBytes);
                    
                    // Parse JSON
                    JSONObject obj = new JSONObject(json);
                    JSONObject version = obj.optJSONObject("version");
                    if (version != null) {
                        s.version = version.optString("name", "");
                    }
                    JSONObject players = obj.optJSONObject("players");
                    if (players != null) {
                        s.players = players.optInt("online", 0);
                        s.maxPlayers = players.optInt("max", 20);
                    }
                    JSONObject desc = obj.optJSONObject("description");
                    if (desc != null) {
                        s.motd = desc.optString("text", s.ip);
                    }
                    
                    sock.close();
                    
                } catch (Exception e) {
                    s.motd = "Offline or unreachable";
                }
                
                s.ping = (int)(System.currentTimeMillis() - start);
                
                handler.post(() -> {
                    renderServers();
                    tvRefreshing.setText("");
                });
                
            }).start();
        }
    }
    
    private void joinServer(String ip, int port) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("single_player", false);
        intent.putExtra("server_ip", ip);
        intent.putExtra("server_port", port);
        startActivity(intent);
    }
    
    private void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0) {
            out.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }
    
    private int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte = in.readByte();
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x80) == 0) break;
            position += 7;
            if (position >= 32) throw new IOException("VarInt too big");
        }
        return value;
    }
}
