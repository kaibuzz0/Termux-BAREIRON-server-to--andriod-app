# 🧟 BAREIRON — Full Game Client + Server

A complete zombie shooter game: **Android app** that plays the game, connects to servers, chats with friends — plus a **micro C server** for hosting.

```
    ╔═══════════════════════════════════════════════════════════════╗
    ║                                                               ║
    ║   🧟  BAREIRON — Play. Connect. Survive.                    ║
    ║                                                               ║
    ║   Android Game Client  ←→  Micro C Server                     ║
    ║   Single Player | Multiplayer | Friends | Chat | Store      ║
    ║                                                               ║
    ╚═══════════════════════════════════════════════════════════════╝
```

---

## 📱 The App (What Players See)

### Main Menu
```
┌─────────────────────────────┐
│  🧟 BAREIRON                │
│  Zombie Shooter             │
│                             │
│  Player                     │
│  Kills: 42 | Wave: 5 | ...  │
│                             │
│  [🎮 SINGLE PLAYER]         │
│  [🌐 MULTIPLAYER]          │
│  [👥 FRIENDS]               │
│  [👤 PROFILE]               │
│  [🛒 STORE]                 │
│  [⚙️ SETTINGS]               │
└─────────────────────────────┘
```

### In-Game HUD
```
┌─────────────────────────────┐
│ ❤️100  🔫30/90  🌊Wave 3  🏆1250│
│                              │
│      [ GAME VIEW ]           │
│                              │
│  SYSTEM: Wave 3 starting...  │
│  ShadowHunter: Behind you!   │
│  You: Got it                 │
│  ┌──────────┐ [SEND]        │
└─────────────────────────────┘
```

### Server Browser
```
┌─────────────────────────────┐
│  🌐 SERVER BROWSER          │
│                              │
│  ⭐ Local Test              │
│  127.0.0.1:25565            │
│  2/20 players | 12ms       │
│  [JOIN SERVER]               │
│                              │
│  Friend's Server            │
│  192.168.1.100:25565        │
│  5/8 players | 45ms          │
│  [JOIN SERVER]               │
│                              │
│  [Direct Connect]            │
└─────────────────────────────┘
```

### Friends
```
┌─────────────────────────────┐
│  👥 FRIENDS (3/4 online)    │
│                              │
│  ● ShadowHunter              │
│    In Game | [Join] [Invite]│
│                              │
│  ● NoobSlayer99              │
│    In Lobby | [Invite]       │
│                              │
│  ○ PixelQueen                │
│    Offline                   │
│                              │
│  [+ ADD FRIEND]              │
└─────────────────────────────┘
```

---

## 🖥️ The Server (What Hosts Run)

**Micro C binary (~227KB)** that runs on anything:
- Old Android phones via Termux
- Raspberry Pi
- VPS / cloud server
- Your laptop

### Host a Server
```bash
cd server
bash install.sh       # One-time setup
./quickstart.sh       # Run
```

### Players Connect
1. Open Android app
2. Tap **MULTIPLAYER**
3. See your server in the list (or enter IP directly)
4. Tap **JOIN**
5. Play together

---

## 📁 Repository Layout

```
Termux-BAREIRON-server-to--andriod-app/
│
├── server/                    # 🖥️ Micro C Server
│   ├── src/                   # 16 C source files
│   ├── include/               # Headers
│   ├── build.sh               # Compile
│   ├── install.sh             # Termux setup
│   ├── quickstart.sh          # Run
│   └── README.md
│
├── app/                       # 📱 Android Game Client
│   ├── app/src/main/
│   │   ├── java/com/bareiron/game/
│   │   │   ├── MainMenuActivity.java      # Launcher
│   │   │   ├── GameActivity.java           # In-game HUD + chat
│   │   │   ├── ServerBrowserActivity.java  # Find + join servers
│   │   │   ├── FriendsActivity.java        # Friend list + invites
│   │   │   ├── ProfileActivity.java        # Stats + achievements
│   │   │   ├── StoreActivity.java          # Free / unlockable / $0.99
│   │   │   ├── SettingsActivity.java       # Audio, controls, dev tools
│   │   │   ├── ContentPackManager.java     # Load content packs
│   │   │   ├── PlayerProgress.java         # Track kills, waves, score
│   │   │   └── BillingManager.java         # Purchases
│   │   ├── res/layout/        # All screens
│   │   └── assets/content/    # JSON content packs
│   ├── build.gradle
│   └── README.md
│
└── README.md                  # ← This file
```

---

## 🎮 How to Play

### Single Player (No Server Needed)
1. Open app
2. Tap **SINGLE PLAYER**
3. App loads content packs
4. Play zombie survival

### Multiplayer (You + Friends)
**Host:**
1. Install Termux on Android (or any Linux)
2. `git clone` this repo
3. `cd server && bash install.sh && ./quickstart.sh`
4. Tell friends your IP

**Friends:**
1. Open app
2. Tap **MULTIPLAYER**
3. Enter host's IP:port
4. Tap **DIRECT CONNECT**
5. Or find it in the browser if it's public

### With Friends in the App
1. Tap **FRIENDS**
2. Add friends by username
3. See who's online/playing
4. Tap **Join** to hop into their game
5. Or **Invite** them to yours

---

## 🛒 Content Store

| Tier | How | Price |
|------|-----|-------|
| **FREE** | Always available | $0 |
| **UNLOCKABLE** | Earn by playing (kills, waves, bosses) | $0 |
| **PREMIUM** | One-time purchase | **$0.99** |

**Every pack is fully loaded** — no streaming, no waiting. Buy once, own forever.

### Example Packs
- **Meadowlands** (FREE) — Peaceful starter realm
- **Crimson Wastes** (UNLOCKABLE — 100 zombie kills) — Desert with pharaoh boss
- **Goldport** (PREMIUM — $0.99) — Massive trade city with thieves guild

---

## 💬 Chat System

In-game chat is always visible:
- **System messages** — wave starts, boss alerts
- **Player chat** — talk with everyone in the server
- **Friend invites** — join directly from chat
- **Quick messages** — preset responses (tap to send)

---

## 🔧 Development

### Build Server
```bash
cd server
bash build.sh
```

### Build Android App
```bash
cd app
bash build.sh
# Or open in Android Studio
```

### Add Content Pack
1. Create JSON in `app/app/src/main/assets/content/<type>/`
2. Define tier: free / unlockable / premium
3. Set unlock condition (for unlockables) or price (for premium)
4. Rebuild APK

---

## 🚀 Future Features
- [ ] In-app Minecraft renderer (play without separate client)
- [ ] Voice chat
- [ ] Clan/guild system
- [ ] Ranked leaderboards
- [ ] Seasonal events
- [ ] Cross-platform (iOS)
- [ ] Cloud save sync

---

**🧟 Download. Survive. Connect.**
