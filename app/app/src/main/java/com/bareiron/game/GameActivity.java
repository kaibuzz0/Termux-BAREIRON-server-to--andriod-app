// GameActivity.java — In-game HUD + chat + controls
package com.bareiron.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;

public class GameActivity extends AppCompatActivity {
    private TextView tvHealth, tvAmmo, tvWave, tvScore, tvChat;
    private EditText etChatInput;
    private Button btnSend, btnPause;
    private ScrollView svChat;
    private LinearLayout layoutChat;
    
    private String serverIP;
    private int serverPort;
    private boolean singlePlayer;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        // Hide system UI for immersive
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
        serverIP = getIntent().getStringExtra("server_ip");
        serverPort = getIntent().getIntExtra("server_port", 25565);
        singlePlayer = getIntent().getBooleanExtra("single_player", true);
        
        tvHealth = findViewById(R.id.tvHealth);
        tvAmmo = findViewById(R.id.tvAmmo);
        tvWave = findViewById(R.id.tvWave);
        tvScore = findViewById(R.id.tvScore);
        tvChat = findViewById(R.id.tvChat);
        etChatInput = findViewById(R.id.etChatInput);
        btnSend = findViewById(R.id.btnSend);
        btnPause = findViewById(R.id.btnPause);
        svChat = findViewById(R.id.svChat);
        layoutChat = findViewById(R.id.layoutChat);
        
        btnSend.setOnClickListener(v -> sendChatMessage());
        btnPause.setOnClickListener(v -> showPauseMenu());
        
        connectToServer();
    }
    
    private void connectToServer() {
        new Thread(() -> {
            try {
                if (singlePlayer) {
                    // In single player, we'd ideally start a local server
                    // For now, show demo HUD
                    handler.post(() -> {
                        tvHealth.setText("❤️ 100");
                        tvAmmo.setText("🔫 30/90");
                        tvWave.setText("🌊 Wave 1");
                        tvScore.setText("🏆 0");
                        addChatMessage("SYSTEM", "Single Player mode. Local server integration coming soon.");
                    });
                    return;
                }
                
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, serverPort), 10000);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                
                // Minecraft handshake
                sendHandshake();
                
                // Read game state
                handler.post(() -> addChatMessage("SYSTEM", "Connected to " + serverIP + ":" + serverPort));
                
                // Game loop
                while (running) {
                    // Read packets
                    // Parse and update HUD
                    Thread.sleep(50);
                }
                
            } catch (Exception e) {
                handler.post(() -> {
                    addChatMessage("ERROR", e.getMessage());
                    Toast.makeText(this, "Connection failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    private void sendHandshake() throws IOException {
        ByteArrayOutputStream hs = new ByteArrayOutputStream();
        DataOutputStream hw = new DataOutputStream(hs);
        hw.writeByte(0x00);
        writeVarInt(hw, 772);
        writeVarInt(hw, serverIP.length());
        hw.writeBytes(serverIP);
        hw.writeShort(serverPort);
        hw.writeByte(0x02); // login
        
        byte[] bytes = hs.toByteArray();
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
    
    private void sendChatMessage() {
        String msg = etChatInput.getText().toString().trim();
        if (msg.isEmpty()) return;
        
        addChatMessage("You", msg);
        etChatInput.setText("");
        
        // Send to server
        new Thread(() -> {
            try {
                if (out != null) {
                    // Send chat packet
                    ByteArrayOutputStream packet = new ByteArrayOutputStream();
                    DataOutputStream pw = new DataOutputStream(packet);
                    pw.writeByte(0x03); // chat packet ID
                    String json = "{\"text\":\"" + msg + "\"}";
                    writeVarInt(pw, json.length());
                    pw.writeBytes(json);
                    
                    byte[] data = packet.toByteArray();
                    writeVarInt(out, data.length);
                    out.write(data);
                }
            } catch (Exception e) {
                handler.post(() -> addChatMessage("ERROR", "Failed to send: " + e.getMessage()));
            }
        }).start();
    }
    
    private void addChatMessage(String sender, String message) {
        TextView tv = new TextView(this);
        tv.setText(sender + ": " + message);
        tv.setTextColor(sender.equals("You") ? 0xFF4CAF50 : 
            (sender.equals("SYSTEM") ? 0xFFFFFF00 : 0xFFFFFFFF));
        tv.setTextSize(12);
        tv.setPadding(4, 2, 4, 2);
        layoutChat.addView(tv);
        
        svChat.post(() -> svChat.fullScroll(ScrollView.FOCUS_DOWN));
    }
    
    private void showPauseMenu() {
        // Show pause dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Paused")
            .setItems(new String[]{"Resume", "Settings", "Leave Game"}, (d, which) -> {
                if (which == 2) {
                    running = false;
                    finish();
                }
            })
            .show();
    }
    
    private void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0) {
            out.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        try { if (socket != null) socket.close(); } catch (Exception e) {}
    }
}
