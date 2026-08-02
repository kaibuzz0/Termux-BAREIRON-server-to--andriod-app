// StorePackAdapter.java — Shows packs in store with tier-specific UI
package com.bareiron.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StorePackAdapter extends RecyclerView.Adapter<StorePackAdapter.ViewHolder> {
    private List<ContentPack> packs;
    private OnStoreActionListener listener;
    
    public interface OnStoreActionListener {
        void onActivate(ContentPack pack);
        void onBuy(ContentPack pack);
        void onPreview(ContentPack pack);
    }
    
    public StorePackAdapter(List<ContentPack> packs, OnStoreActionListener listener) {
        this.packs = packs;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_store_pack, parent, false);
        return new ViewHolder(v);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ContentPack p = packs.get(pos);
        
        h.tvName.setText(p.name);
        h.tvType.setText(p.type.toUpperCase() + " | " + p.version);
        h.tvDesc.setText(p.description);
        h.tvPrice.setText(p.getPriceDisplay());
        
        // Stats
        h.tvStats.setText(p.blockCount + " blocks | " + p.mobCount + " mobs | " + p.questCount + " quests");
        
        // Progress bar for unlockables
        if (p.tier.equals("unlockable") && !p.unlocked) {
            h.pbUnlock.setVisibility(View.VISIBLE);
            h.pbUnlock.setMax(p.unlockValue);
            h.pbUnlock.setProgress(Math.min(p.progress, p.unlockValue));
            h.tvProgress.setVisibility(View.VISIBLE);
            h.tvProgress.setText(p.progress + "/" + p.unlockValue + " " + p.unlockType);
        } else {
            h.pbUnlock.setVisibility(View.GONE);
            h.tvProgress.setVisibility(View.GONE);
        }
        
        // Action button
        String status = p.getStatusDisplay();
        h.btnAction.setText(status);
        
        if (status.equals("ACTIVE")) {
            h.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            h.btnAction.setEnabled(false);
        } else if (status.equals("BUY")) {
            h.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF9800));
            h.btnAction.setOnClickListener(v -> listener.onBuy(p));
        } else if (status.equals("LOCKED")) {
            h.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF666666));
            h.btnAction.setEnabled(false);
        } else {
            h.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2196F3));
            h.btnAction.setOnClickListener(v -> listener.onActivate(p));
        }
        
        // Preview button
        h.btnPreview.setOnClickListener(v -> listener.onPreview(p));
        
        // Tier badge color
        if (p.tier.equals("free")) {
            h.tvTierBadge.setText("FREE");
            h.tvTierBadge.setTextColor(0xFF4CAF50);
        } else if (p.tier.equals("unlockable")) {
            h.tvTierBadge.setText("UNLOCK");
            h.tvTierBadge.setTextColor(0xFFFFEB3B);
        } else {
            h.tvTierBadge.setText("PREMIUM");
            h.tvTierBadge.setTextColor(0xFFFF9800);
        }
    }
    
    @Override
    public int getItemCount() { return packs.size(); }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDesc, tvPrice, tvStats, tvProgress, tvTierBadge;
        Button btnAction, btnPreview;
        ProgressBar pbUnlock;
        
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvType = v.findViewById(R.id.tvType);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStats = v.findViewById(R.id.tvStats);
            tvProgress = v.findViewById(R.id.tvProgress);
            tvTierBadge = v.findViewById(R.id.tvTierBadge);
            btnAction = v.findViewById(R.id.btnAction);
            btnPreview = v.findViewById(R.id.btnPreview);
            pbUnlock = v.findViewById(R.id.pbUnlock);
        }
    }
}
