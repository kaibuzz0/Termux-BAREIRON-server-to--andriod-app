# 🧟 BAREIRON — Server + Android App

A unified repository containing the **BAREIRON Minecraft Zombie Shooter Server** and its **Android Content Manager companion app**.

```
    ╔═══════════════════════════════════════════════════════════════╗
    ║                                                               ║
    ║   🧟  BAREIRON — Server + Android App                         ║
    ║                                                               ║
    ║   Micro server (~227KB)  ←→  Macro content app               ║
    ║   Vanilla Minecraft Java Edition  Protocol 772                 ║
    ║                                                               ║
    ╚═══════════════════════════════════════════════════════════════╝
```

---

## 📁 Repository Layout

```
Termux-BAREIRON-server-to--andriod-app/
├── server/                    # 🖥️ BAREIRON C Server
│   ├── src/                   # C source files
│   ├── include/               # Headers (fathers_house.h, villages_npcs.h, etc.)
│   ├── config/                # game.json settings
│   ├── build.sh               # Compile script
│   ├── install.sh             # One-command setup
│   ├── quickstart.sh          # Fast rebuild + run
│   ├── test_server.py         # Minecraft protocol tester
│   └── README.md              # Server docs
│
├── app/                       # 📱 Android Content Manager
│   ├── app/src/main/
│   │   ├── java/com/bareiron/game/
│   │   │   ├── MainActivity.java
│   │   │   ├── ServerConnectActivity.java
│   │   │   ├── ContentManagerActivity.java
│   │   │   ├── ContentPackManager.java
│   │   │   └── ...
│   │   ├── res/layout/        # XML layouts
│   │   ├── res/values/        # Strings + themes
│   │   └── assets/content/    # JSON content packs
│   ├── build.gradle           # App build config
│   └── README.md              # Android docs
│
├── .github/workflows/         # CI/CD
│   └── build.yml              # Build server + Android
│
└── README.md                  # ← You are here
```

---

## 🖥️ Server (`server/`)

**Memory-first Minecraft server.** ~227KB binary. Runs on anything: Termux, Raspberry Pi, old Android phones, even ESP32-class devices.

### What's Inside

| Feature | Description |
|---------|-------------|
| **Zombie Shooter** | Wave-based survival with 8 zombie types |
| **The Father's House** | Biblical sanctuary at (352, 33, -318) |
| **The Ancient Boss** | 3-phase boss every 5 waves |
| **Villages** | 12 settlements, 15 NPC classes, quests, world events |
| **Crafting** | 27 items, 4 tiers, class requirements |
| **Multiplayer** | Teams, shared resources, player trading |
| **Save/Load** | Binary persistence with CRC32 checksum |

### Quick Start (Server)

```bash
cd server

# First-time install
bash install.sh

# Run
./quickstart.sh

# Or manual compile
bash build.sh

# Test
python3 test_server.py
```

### Server Size

| Component | Size |
|-----------|------|
| Binary | ~227KB |
| Memory footprint | ~2MB runtime |
| Protocol | Minecraft Java Edition 772 (1.21.8) |

---

## 📱 Android App (`app/`)

**Content Manager companion.** Browse realms, dimensions, cities, and add-ons. Activate what you want. The server stays lean.

### What's Inside

| Feature | Description |
|---------|-------------|
| **Server Connect** | Enter IP:port, ping with Minecraft protocol 772 |
| **Content Manager** | 4 tabs: Realms, Dimensions, Cities, Add-ons |
| **Pack Activation** | Tap to activate, server loads content |
| **JSON-Driven** | Unlimited content without code changes |

### Quick Start (App)

```bash
cd app

# Build APK
bash build.sh

# Or open in Android Studio
# File → Open → app/build.gradle
```

### Content Pack Format

```json
{
    "id": "realm_aetherfall",
    "name": "Aetherfall",
    "type": "realm",
    "version": "1.0",
    "blockCount": 256,
    "mobCount": 18,
    "questCount": 12,
    "biomes": ["floating_islands", "thunder_peaks"],
    "features": ["gravity_pads", "wing_serpent_boss"]
}
```

| Type | Description |
|------|-------------|
| `realm` | Large overworld expansion |
| `dimension` | Separate dimension (like Nether/End) |
| `city` | Built-up urban area |
| `addon` | Mob/item/recipe injection |

---

## 🔄 How They Work Together

```
┌──────────────────────────────────────────────────────────────┐
│                      Android Phone                           │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Content Manager App                                  │   │
│  │  • Browse realms/dimensions/cities/addons            │   │
│  │  • Activate content pack                              │   │
│  │  • Connect to server (IP:port)                        │   │
│  └────────────────┬─────────────────────────────────────┘   │
│                   │                                          │
│              Minecraft Protocol 772                          │
│                   │                                          │
│  ┌────────────────┴─────────────────────────────────────┐   │
│  │  BAREIRON Server (Termux / Linux / Android)          │   │
│  │  • Receives content activation from app              │   │
│  │  • Loads JSON world data                             │   │
│  │  • Hosts game for Minecraft Java clients             │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

**The server is micro. The app is macro. Best of both worlds.**

---

## 🏗️ Development

### Server Development

```bash
cd server

# Edit C files in src/ and include/
# Regenerate registries if needed
bash extract_registries.sh
node build_registries.js

# Compile
bash build.sh

# Test
python3 test_server.py
```

### Android Development

```bash
cd app

# Edit Java in app/src/main/java/com/bareiron/game/
# Add content packs: app/src/main/assets/content/<type>/<pack>.json
# Edit layouts: app/src/main/res/layout/

# Build
bash build.sh

# Or with Android Studio
# File → Open → app/build.gradle → Build → Build Bundle(s) / APK(s)
```

---

## 🚀 CI/CD (GitHub Actions)

| Workflow | Trigger | What It Does |
|----------|---------|-------------|
| `build.yml` | Push to `main` | Compiles C server, builds Android APK, uploads artifacts |

### Manual Build

```bash
# Server only
cd server && bash build.sh

# Android only
cd app && bash build.sh

# Both
cd server && bash build.sh && cd ../app && bash build.sh
```

---

## 📊 Feature Comparison

| Feature | Server | Android App |
|---------|--------|-------------|
| Binary size | ~227KB | ~15MB APK |
| Content storage | Minimal (core only) | Unlimited JSON packs |
| Realms | ✅ (core world) | ✅ (browse + activate) |
| Dimensions | ❌ (not in C) | ✅ (JSON-defined) |
| Cities | ✅ (12 villages) | ✅ (browse + activate more) |
| NPCs | ✅ (15 classes) | ✅ (display + manage) |
| Quests | ✅ (8 types) | ✅ (track progress) |
| Crafting | ✅ (27 items) | ✅ (recipes + requirements) |
| Save/load | ✅ (binary) | ✅ (cloud sync planned) |
| Multiplayer | ✅ (teams) | ✅ (server browser planned) |

---

## 📝 Content Pack Ideas

Want to add your own? Create a JSON file:

```bash
# Example: new realm
cat > app/app/src/main/assets/content/realms/my_realm.json <<'EOF'
{
    "id": "realm_crimson_wastes",
    "name": "Crimson Wastes",
    "type": "realm",
    "description": "A blood-red desert where sandstorms strip flesh from bone.",
    "version": "1.0",
    "author": "YourName",
    "blockCount": 384,
    "mobCount": 22,
    "itemCount": 16,
    "questCount": 14,
    "structureCount": 10,
    "biomes": ["crimson_desert", "bone_canyons", "blood_oasis"],
    "features": ["sandstorm_events", "blood_cultists", "pharaoh_boss"]
}
EOF
```

Then rebuild the APK. Or (future) upload to CDN and download in-app.

---

## 🆘 Troubleshooting

| Problem | Fix |
|---------|-----|
| Server won't compile | Check `include/registries.h` exists; run `extract_registries.sh` |
| Android app won't build | Check Android SDK, update Gradle, enable AndroidX |
| Can't connect to server | Verify IP/port, check firewall, use `test_server.py` |
| Content packs not showing | Check JSON syntax, rebuild APK, check logcat |
| Game crashes on load | Check `game.json` config, verify binary permissions |

---

## 🙏 Credits

- **bareiron** — Upstream minimalist Minecraft server
- **Hive OS** — Security integration concepts
- **Termux** — Android Linux environment

## 📜 License

See LICENSE file. Same as upstream bareiron project.

---

**Don't Panic. The Answer is 42.** 🐝🧟
