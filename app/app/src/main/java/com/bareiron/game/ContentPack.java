// ContentPack.java — Enhanced model with tiers, pricing, unlocks
package com.bareiron.game;

public class ContentPack {
    // ── Core ──────────────────────────────────────────────────
    public String id;
    public String name;
    public String type;           // realm, dimension, city, addon
    public String version;
    public String author;
    public String description;
    public String thumbnail;
    public String mcVersion;

    // ── Tier / Pricing ─────────────────────────────────────────
    public String tier;           // "free", "unlockable", "premium"
    public int priceCents;        // 0 for free/unlockable, 99 for $0.99
    public String currency;       // "USD", "EUR", etc
    public String sku;            // Google Play SKU, e.g. "pack_realm_crimson"

    // ── Unlock Condition ─────────────────────────────────────
    public String unlockType;     // "none", "kills", "waves", "bosses", "quests", "visits", "playtime", "score"
    public int unlockValue;       // e.g. 100 kills, wave 10, etc.
    public String unlockDescription; // "Defeat 100 zombies"

    // ── Preview (what you see before buying) ─────────────────
    public String[] previewBlocks;
    public String[] previewMobs;
    public String[] previewItems;
    public String[] previewScreenshots; // asset paths
    public String trailerUrl;     // YouTube or local video

    // ── Content counts ───────────────────────────────────────
    public int blockCount;
    public int mobCount;
    public int itemCount;
    public int questCount;
    public int structureCount;

    // ── State (runtime, NOT in JSON) ──────────────────────────
    public transient boolean installed;
    public transient boolean owned;
    public transient boolean unlocked;
    public transient boolean active;
    public transient int progress;      // current progress toward unlock

    // ── Metadata ─────────────────────────────────────────────
    public String[] dependencies;
    public String[] biomes;
    public String[] features;
    public String[] tags;
    public String releaseDate;
    public String rating;         // "E", "T", "M" etc

    public ContentPack() {}

    public ContentPack(String id, String name, String type, String tier, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.tier = tier;
        this.description = description;
        this.version = "1.0";
        this.priceCents = 0;
        this.currency = "USD";
        this.owned = tier.equals("free");
        this.unlocked = tier.equals("free");
    }

    public String getPriceDisplay() {
        if (tier.equals("free")) return "FREE";
        if (tier.equals("unlockable")) {
            if (unlocked) return "UNLOCKED";
            return "🔒 " + unlockDescription;
        }
        if (tier.equals("premium")) {
            if (owned) return "OWNED";
            return String.format("$%.2f", priceCents / 100.0);
        }
        return "";
    }

    public String getStatusDisplay() {
        if (active) return "ACTIVE";
        if (!installed) return "INSTALL";
        if (tier.equals("free")) return "ACTIVATE";
        if (tier.equals("unlockable")) return unlocked ? "ACTIVATE" : "LOCKED";
        if (tier.equals("premium")) return owned ? "ACTIVATE" : "BUY";
        return "";
    }

    public boolean canActivate() {
        if (!installed) return false;
        if (tier.equals("free")) return true;
        if (tier.equals("unlockable")) return unlocked;
        if (tier.equals("premium")) return owned;
        return false;
    }
}
