// StoreActivity.java — Full store with free, unlockable, and premium tabs
package com.bareiron.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private RecyclerView rvPacks;
    private TextView tvCurrency;
    private Button btnRestorePurchases;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        
        tabLayout = findViewById(R.id.tabLayout);
        rvPacks = findViewById(R.id.rvPacks);
        tvCurrency = findViewById(R.id.tvCurrency);
        btnRestorePurchases = findViewById(R.id.btnRestorePurchases);
        
        // Setup tabs
        tabLayout.addTab(tabLayout.newTab().setText("FREE"));
        tabLayout.addTab(tabLayout.newTab().setText("UNLOCKABLES"));
        tabLayout.addTab(tabLayout.newTab().setText("PREMIUM ($0.99)"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { refreshList(tab.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        btnRestorePurchases.setOnClickListener(v -> {
            BillingManager.get().queryPurchases();
            Toast.makeText(this, "Restoring purchases...", Toast.LENGTH_SHORT).show();
        });
        
        // Show player's score as currency
        PlayerProgress.init(this);
        int score = PlayerProgress.get().getTotalScore();
        tvCurrency.setText("🏆 Score: " + score);
        
        refreshList(0);
    }
    
    private void refreshList(int tabPosition) {
        String filterTier;
        switch (tabPosition) {
            case 0: filterTier = "free"; break;
            case 1: filterTier = "unlockable"; break;
            case 2: filterTier = "premium"; break;
            default: filterTier = "free";
        }
        
        List<ContentPack> filtered = new ArrayList<>();
        for (ContentPack p : ContentPackManager.get().getAllPacks()) {
            if (p.tier.equals(filterTier)) {
                // Update unlock state based on progress
                if (p.tier.equals("unlockable")) {
                    int current = PlayerProgress.get().getStat(p.unlockType);
                    p.progress = current;
                    p.unlocked = current >= p.unlockValue;
                }
                filtered.add(p);
            }
        }
        
        StorePackAdapter adapter = new StorePackAdapter(filtered, new StorePackAdapter.OnStoreActionListener() {
            @Override
            public void onActivate(ContentPack pack) {
                ContentPackManager.get().activatePack(pack.id);
                Toast.makeText(StoreActivity.this, "Activated: " + pack.name, Toast.LENGTH_SHORT).show();
                refreshList(tabLayout.getSelectedTabPosition());
            }
            
            @Override
            public void onBuy(ContentPack pack) {
                BillingManager.get().purchasePack(StoreActivity.this, pack);
            }
            
            @Override
            public void onPreview(ContentPack pack) {
                Intent intent = new Intent(StoreActivity.this, PackPreviewActivity.class);
                intent.putExtra("pack_id", pack.id);
                startActivity(intent);
            }
        });
        
        rvPacks.setLayoutManager(new GridLayoutManager(this, 1));
        rvPacks.setAdapter(adapter);
    }
}
