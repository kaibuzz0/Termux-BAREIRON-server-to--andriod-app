# 📋 RELEASE CHECKLIST — BAREIRON Android Game Client

**Status:** 🧪 TEST PILOT (NOT RELEASE READY)
**Current Version:** 1.0.0-alpha
**Target:** 1.0.0-stable (Google Play / F-Droid / GitHub Releases)

> ⚠️ This document tracks everything between "it compiles" and "it's in the store."
> Every unchecked item is a blocker. No exceptions.

---

## ✅ COMPLETE (Solid Foundation)

| # | Item | Evidence |
|---|------|----------|
| 1 | Micro C server compiles clean | 227KB, 0 errors, 0 warnings |
| 2 | Server passes Minecraft protocol 772 | test_server.py validates JSON response |
| 3 | Server runs on aarch64 Linux | Tested on Debian aarch64 |
| 4 | Android app project structure | 20 Java files, 13 layouts, Gradle 8.1 |
| 5 | Content pack system | JSON-driven, tiered (free/unlockable/premium) |
| 6 | Store UI with 3 tabs | FREE / UNLOCKABLES / PREMIUM ($0.99) |
| 7 | Player progress tracking | SharedPreferences, kills/waves/bosses/score |
| 8 | Friend list with status | Online/Offline/In-Game with color dots |
| 9 | Server browser with ping | Parses MOTD, player count, latency |
| 10 | In-game chat overlay | Scrollable chat + input field |
| 11 | Profile stats screen | All major stats displayed |
| 12 | Settings screen | Volume, notifications, clear data |
| 13 | GitHub CI/CD workflow | Builds server + APK on push |
| 14 | Unified README | Server + app docs in one place |

---

## 🔴 CRITICAL BLOCKERS (Must Fix Before Release)

### Networking & Gameplay

| # | Issue | Why It Blocks | Est. Effort |
|---|-------|---------------|-------------|
| N1 | **No real game client** | App shows HUD but does not render Minecraft world. Players need a separate Minecraft Java Edition client to actually play. | ~2-3 months |
| N2 | **GameActivity is a placeholder** | Creates socket but doesn't implement full protocol. No entity sync, no position updates, no inventory. | ~1 month |
| N3 | **No local server embedded** | Single Player mode does not actually start bareiron binary. Termux integration needed. | ~2 weeks |
| N4 | **Chat protocol incomplete** | Sends chat packets but doesn't decode incoming ones properly. Server chat won't show in app. | ~1 week |
| N5 | **No reconnection handling** | If server drops, game freezes. No retry, no rejoin, no "connection lost" screen. | ~1 week |
| N6 | **Friends status is simulated** | Randomly rotates statuses. Needs real backend or direct server query. | ~2 weeks |
| N7 | **No server registration/discovery** | Server browser uses hardcoded IPs. No master server list, no public server discovery. | ~1 month |

### Security

| # | Issue | Why It Blocks | Est. Effort |
|---|-------|---------------|-------------|
| S1 | **No TLS on sockets** | Plain TCP. MITM trivial. Must implement TLS for production. | ~2 weeks |
| S2 | **No anti-cheat** | Player stats are client-side. Easy to modify SharedPreferences and fake unlocks. | ~1 month |
| S3 | **No server authentication** | Anyone can connect. No passwords, no whitelist, no rate limiting. | ~1 week |
| S4 | **GitHub PAT exposed** | Token appeared in commit history. Must rotate immediately. | Immediate |

### Monetization

| # | Issue | Why It Blocks | Est. Effort |
|---|-------|---------------|-------------|
| M1 | **No Google Play Billing integration** | BillingManager.java stubs the flow. No real purchase verification. | ~2 weeks |
| M2 | **No purchase server verification** | Premium packs check local storage only. Easy to bypass. | ~2 weeks |
| M3 | **No receipt validation** | Google Play receipts not checked against server. Refund abuse possible. | ~1 week |
| M4 | **No regional pricing** | Hardcoded $0.99. No EUR, GBP, JPY, local pricing. | ~3 days |
| M5 | **No analytics** | No idea what players buy, what they play, where they churn. | ~1 week |

### Legal & Compliance

| # | Issue | Why It Blocks | Est. Effort |
|---|-------|---------------|-------------|
| L1 | **No privacy policy** | Google Play requires privacy policy for IAP. GDPR if EU users. | ~3 days |
| L2 | **No terms of service** | User agreement missing. No liability protection. | ~3 days |
| L3 | **No ESRB/PEGI rating** | Game has violence. Store submission requires age rating. | ~1 week |
| L4 | **No COPPA compliance** | If kids under 13 play, COPPA applies. Data collection restrictions. | ~1 week |
| L5 | **Mojang EULA compliance** | Using Minecraft protocol. Must not violate Mojang's Commercial Usage Guidelines. | Review needed |

### Performance & Stability

| # | Issue | Why It Blocks | Est. Effort |
|---|-------|---------------|-------------|
| P1 | **No crash reporting** | If app crashes, we don't know. No Firebase Crashlytics or similar. | ~3 days |
| P2 | **No ANR monitoring** | Main thread does network I/O. Will ANR on slow connections. | ~1 week |
| P3 | **No memory profiling** | Content packs load into RAM. Large packs may OOM on low-end devices. | ~1 week |
| P4 | **No battery optimization** | Keeps socket alive, drains battery. No Doze mode handling. | ~1 week |
| P5 | ** APK size untested** | Don't know final APK size with assets. Must be under 100MB for Google Play. | ~1 day |

### QA & Testing

| # | Issue | Why It Blocks | Est. Effort |
|---|-------|---------------|-------------|
| Q1 | **Never tested on real Android device** | Developed in Linux PRoot. UI may break on actual phones/tablets. | ~1 week |
| Q2 | **No automated UI tests** | Espresso/UI Automator missing. Every release risks regression. | ~2 weeks |
| Q3 | **No multiplayer stress test** | Unknown behavior with 4+ players. Server may crash. | ~1 week |
| Q4 | **No accessibility audit** | No screen reader labels, no font scaling, no colorblind support. | ~1 week |
| Q5 | **No localization** | English only. Store requires screenshots in local languages for some regions. | ~2 weeks per locale |

---

## 🟡 IMPORTANT (Fix Before 1.0, Not Blockers)

| # | Issue | Impact |
|---|-------|--------|
| I1 | **No push notifications** | Friends can't invite you when app is closed |
| I2 | **No cloud save** | Reinstall = lose everything |
| I3 | **No account system** | No cross-device play, no password recovery |
| I4 | **No tutorial/onboarding** | New players won't know how to play |
| I5 | **No sound design** | No gun sounds, no zombie groans, no music |
| I6 | **No achievements** | No progression beyond stats |
| I7 | **No leaderboard** | No competitive motivation |
| I8 | **No screenshots/video** | Store listing needs media assets |
| I9 | **No beta testing program** | No external testers before public launch |
| I10 | **No update system** | APK updates require manual re-download |

---

## 🟢 NICE TO HAVE (Post-Release)

- Voice chat
- Clan/guild system
- Seasonal events
- Custom skin support
- Replay system
- Twitch streaming integration
- iOS port
- Console port ( ambitious)

---

## 📊 ESTIMATED TIMELINE

| Phase | Duration | Deliverable |
|-------|----------|-------------|
| Alpha → Beta | 2-3 months | Working game client, real networking, crash reporting |
| Beta → Closed Test | 1 month | 100 external testers, bug fixes, balance |
| Closed → Open Test | 2 weeks | Public beta on Google Play |
| Open Test → Release | 2 weeks | Final polish, store assets, legal docs |
| **TOTAL** | **~4 months** | **Production release** |

---

## 🎯 IMMEDIATE NEXT STEPS (Priority Order)

1. **🔴 S4** — Rotate exposed GitHub PAT immediately
2. **🔴 N3** — Implement Termux bareiron binary launch for Single Player
3. **🔴 N4** — Complete chat protocol parsing in GameActivity
4. **🔴 S3** — Add server password/whitelist to C server
5. **🔴 N2** — Implement entity sync (position, health, inventory)
6. **🟡 I4** — Build tutorial/onboarding flow
7. **🟡 I8** — Create store screenshots and trailer
8. **🔴 M1** — Integrate real Google Play Billing Library
9. **🔴 L1** — Draft privacy policy and terms
10. **🔴 Q1** — Test on physical Android devices

---

## 📝 NOTES

- **This is a test pilot.** It proves the concept. It is not ready for users.
- The server is solid. The app is a shell. The gap between them is months of work.
- Monetization ($0.99 packs) cannot go live without real billing integration.
- Legal exposure is real. Mojang, Google Play, and regional laws all apply.
- If we ship this as-is, it will be removed from stores within days.

**Stay humble. Build right. Ship when ready.**
