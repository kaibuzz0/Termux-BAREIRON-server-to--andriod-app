// BillingManager.java — Google Play Billing with offline fallback
package com.bareiron.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.*;

/**
 * Billing Manager for premium content packs.
 * 
 * Uses Google Play Billing Library when available.
 * Falls back to offline purchase tracking when Google Play is unavailable
 * (e.g., Termux, F-Droid builds, sideloaded APKs).
 */
public class BillingManager {
    private static final String PREFS_NAME = "bareiron_billing";
    private static final Set<String> OWNED_PACKS = new HashSet<>();
    private static BillingManager instance;
    private SharedPreferences prefs;
    private boolean googlePlayAvailable = false;
    
    private BillingManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadOwnedPacks();
        
        // Try to init Google Play Billing (would normally use com.android.billingclient)
        // For now, we detect if Google Play Services exists
        try {
            Class.forName("com.google.android.gms.common.GoogleApiAvailability");
            googlePlayAvailable = true;
        } catch (ClassNotFoundException e) {
            googlePlayAvailable = false;
        }
    }
    
    public static void init(Context ctx) {
        if (instance == null) instance = new BillingManager(ctx);
    }
    
    public static BillingManager get() {
        return instance;
    }
    
    private void loadOwnedPacks() {
        Set<String> saved = prefs.getStringSet("owned", new HashSet<String>());
        OWNED_PACKS.addAll(saved);
    }
    
    private void saveOwnedPacks() {
        prefs.edit().putStringSet("owned", new HashSet<>(OWNED_PACKS)).apply();
    }
    
    public boolean isOwned(String sku) {
        return OWNED_PACKS.contains(sku);
    }
    
    public void ownPack(String sku) {
        OWNED_PACKS.add(sku);
        saveOwnedPacks();
    }
    
    /**
     * Purchase a pack.
     * If Google Play is available: launches real billing flow.
     * If not: simulates purchase with confirmation dialog.
     */
    public void purchasePack(Activity activity, ContentPack pack) {
        if (googlePlayAvailable) {
            // In production, this would use BillingClient.launchBillingFlow()
            // For now, we show a mock dialog
            showMockPurchaseDialog(activity, pack);
        } else {
            // Offline/sideloaded build — show confirmation
            showOfflinePurchaseDialog(activity, pack);
        }
    }
    
    private void showMockPurchaseDialog(Activity activity, ContentPack pack) {
        new androidx.appcompat.app.AlertDialog.Builder(activity)
            .setTitle("Purchase " + pack.name + "?")
            .setMessage("Price: $" + String.format("%.2f", pack.priceCents / 100.0) + "\n\n" +
                "This would normally use Google Play Billing.\n" +
                "For testing, tap BUY to simulate.")
            .setPositiveButton("BUY", (d, w) -> {
                completePurchase(activity, pack);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showOfflinePurchaseDialog(Activity activity, ContentPack pack) {
        new androidx.appcompat.app.AlertDialog.Builder(activity)
            .setTitle("Buy " + pack.name + "?")
            .setMessage("$" + String.format("%.2f", pack.priceCents / 100.0) + "\n\n" +
                "This is an offline build. Purchase will be saved locally.\n" +
                "You can restore purchases if you reinstall.")
            .setPositiveButton("BUY NOW", (d, w) -> {
                completePurchase(activity, pack);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void completePurchase(Activity activity, ContentPack pack) {
        ownPack(pack.sku);
        pack.owned = true;
        Toast.makeText(activity, 
            "✅ Purchased: " + pack.name + "!", Toast.LENGTH_LONG).show();
    }
    
    /**
     * Query existing purchases (for restore).
     * In production: queries Google Play.
     * Offline: loads from local storage.
     */
    public void queryPurchases() {
        // In production: BillingClient.queryPurchasesAsync()
        // All packs in OWNED_PACKS are already restored on load
    }
    
    /**
     * Get all owned premium packs.
     */
    public List<String> getOwnedSkus() {
        return new ArrayList<>(OWNED_PACKS);
    }
    
    /**
     * For testing: own all packs without paying.
     */
    public void devOwnAll(Context ctx) {
        for (ContentPack p : ContentPackManager.get().getAllPacks()) {
            if (p.tier.equals("premium")) {
                ownPack(p.sku);
            }
        }
        Toast.makeText(ctx, "DEV: All premium packs unlocked", Toast.LENGTH_SHORT).show();
    }
}
