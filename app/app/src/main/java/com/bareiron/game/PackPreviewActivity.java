// PackPreviewActivity.java — Preview premium packs before purchase
package com.bareiron.game;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PackPreviewActivity extends AppCompatActivity {
    private TextView tvName, tvType, tvDesc, tvStats, tvFeatures, tvPrice, tvUnlockInfo;
    private Button btnBuy, btnBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_preview);
        
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvDesc = findViewById(R.id.tvDesc);
        tvStats = findViewById(R.id.tvStats);
        tvFeatures = findViewById(R.id.tvFeatures);
        tvPrice = findViewById(R.id.tvPrice);
        tvUnlockInfo = findViewById(R.id.tvUnlockInfo);
        btnBuy = findViewById(R.id.btnBuy);
        btnBack = findViewById(R.id.btnBack);
        
        String packId = getIntent().getStringExtra("pack_id");
        ContentPack pack = ContentPackManager.get().getPack(packId);
        
        if (pack == null) {
            Toast.makeText(this, "Pack not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        tvName.setText(pack.name);
        tvType.setText(pack.type.toUpperCase() + " | v" + pack.version + " | by " + pack.author);
        tvDesc.setText(pack.description);
        tvStats.setText(pack.blockCount + " blocks | " + pack.mobCount + " mobs | " + 
            pack.itemCount + " items | " + pack.questCount + " quests | " + pack.structureCount + " structures");
        
        StringBuilder feat = new StringBuilder("Features:\n");
        for (String f : pack.features) feat.append("  • ").append(f).append("\n");
        if (pack.biomes != null) {
            feat.append("\nBiomes:\n");
            for (String b : pack.biomes) feat.append("  • ").append(b).append("\n");
        }
        tvFeatures.setText(feat.toString());
        
        if (pack.tier.equals("free")) {
            tvPrice.setText("FREE");
            tvPrice.setTextColor(0xFF4CAF50);
            btnBuy.setText("INSTALL FREE");
            btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        } else if (pack.tier.equals("unlockable")) {
            int current = PlayerProgress.get().getStat(pack.unlockType);
            tvPrice.setText("UNLOCKABLE");
            tvPrice.setTextColor(0xFFFFEB3B);
            tvUnlockInfo.setText(pack.unlockDescription + "\nProgress: " + current + "/" + pack.unlockValue);
            tvUnlockInfo.setVisibility(android.view.View.VISIBLE);
            
            if (current >= pack.unlockValue) {
                btnBuy.setText("UNLOCK NOW");
                btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            } else {
                btnBuy.setText("LOCKED");
                btnBuy.setEnabled(false);
                btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF666666));
            }
        } else {
            tvPrice.setText("$" + String.format("%.2f", pack.priceCents / 100.0));
            tvPrice.setTextColor(0xFFFF9800);
            btnBuy.setText("BUY NOW");
            btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF9800));
        }
        
        final ContentPack finalPack = pack;
        btnBuy.setOnClickListener(v -> {
            if (finalPack.tier.equals("premium")) {
                BillingManager.get().purchasePack(this, finalPack);
            } else {
                ContentPackManager.get().activatePack(finalPack.id);
                Toast.makeText(this, "Activated: " + finalPack.name, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        btnBack.setOnClickListener(v -> finish());
    }
}
