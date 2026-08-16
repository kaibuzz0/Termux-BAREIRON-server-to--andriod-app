# 🧟 BAREIRON — Android Server Host + Game Server

This repository packages the existing **BAREIRON C game server** into a consumer-friendly Android host app.

The product goal is deliberately simple:

1. Install the BAREIRON Host app on an Android device.
2. Tap **START SERVER**.
3. The embedded native BAREIRON server listens on port **25565**.
4. Other compatible devices on the same network connect and play.
5. The host does not need Termux, gcc, Java, shell commands, or a rented cloud server.

The Android device is the server appliance.

---

## Current Architecture

```text
Google Play / APK
      |
      v
Android BAREIRON Host dashboard
      |
      v
Foreground host service
      |
      v
JNI bridge
      |
      v
Android NDK shared library
      |
      v
Existing BAREIRON C server
      |
      v
TCP port 25565
      |
      +--> game clients on the local network
```

The standalone server remains in `server/` and can still be built separately. The Android build reuses that C source through CMake/NDK rather than extracting and executing a downloaded binary.

---

## Android Host Milestone

The Android host currently contains:

- one-button **Start Server / Stop Server** controls
- LAN IPv4 address + port display
- shareable join address
- Android foreground-service lifecycle
- JNI `run / stop / status / player-count` bridge
- NDK/CMake build of the existing BAREIRON C server
- app-private working directory for BAREIRON save data
- live dashboard polling for server status and connected-player count

The first Android native build intentionally uses BAREIRON's plain LAN transport. The existing OpenSSL TLS implementation remains available to standalone builds; Android TLS packaging is a later hardening milestone.

---

## Server

The BAREIRON server is a memory-first Minecraft Java protocol server fork expanded into a zombie survival game. Server-side systems include zombie waves, bosses, weapons, NPCs, villages, quests, crafting, teams, trading, persistence, and related gameplay systems.

The Android app does **not** replace that server. It is the appliance/control layer around it.

### Standalone server build

```bash
cd server
bash build.sh
```

### Android app build

```bash
cd app
./gradlew assembleDebug
```

The Android build requires the Android NDK and CMake versions declared in `app/app/build.gradle`.

---

## Product Direction

The intended free product is a small local BAREIRON server anyone can run from an Android device without technical setup. Future product work can add restrained advertising in the host-management experience and an optional supporter/development subscription without ad-bombing players or requiring the project owner to centrally host everyone's game servers.

Immediate engineering priorities are:

- prove a real second device can join the Android-hosted server
- validate start/stop/restart repeatedly on physical Android hardware
- harden save-file lifecycle and crash recovery
- improve server configuration and player/admin controls
- add compatibility/status diagnostics for non-technical users
- prepare Play Store policy, signing, billing, and ads only after hosting is reliable

---

## Credits

BAREIRON is based on the upstream memory-first server project and the expanded BAREIRON zombie-survival server work contained in this repository.
