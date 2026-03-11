# 📱 SD News

Welcome to **SD News**, a clean, simple, and lightweight Android news application built using **RSS feeds**.  
Unlike most other cluttered news apps, SD News delivers information in a clean and distraction-free interface, helping users stay updated with the latest headlines effortlessly.

---

## ✨ Features

- 📄 Fetches news articles via **RSS feeds**
- 🗞️ Multiple news sources — The Hindu, NDTV, Times of India
- 🗂️ Category tabs — Top Stories, World, Science, Technology, Sports
- 🌙 Dark Mode with persistent preference
- 📲 Modern Android UI with RecyclerView + CardView
- 🔄 Pull-to-refresh functionality
- 🧭 Bottom Navigation Bar for switching sources
- 🎯 Clean, minimal, straight to the point..!

---

## 🗞️ News Sources

| Source | Categories |
|:-------|:-----------|
| The Hindu | Top Stories, World, Science, Technology, Sports |
| NDTV | Top Stories, World, Science, Technology, Sports |
| Times of India | Top Stories, World, Science, Technology, Sports |

---

## 📖 Why RSS is Better than APIs for News Apps

| RSS Feeds | Traditional APIs |
|:-----------|:----------------|
| Open and publicly available | Often require API keys and usage limits |
| Lightweight and fast | Can introduce additional overhead |
| No authentication needed | API rate limiting and quota restrictions |
| Flexible parsing for multiple sources | API responses are fixed and vendor-dependent |
| Consistent format (XML) | Varies across providers |

**→ RSS enables greater control, decentralization, and no dependency on third-party backend APIs.**  
This makes SD News more sustainable, faster, and simpler to maintain.

---

## 📷 Screenshots

_Add screenshots of your app's main screen view here_

| Home Screen |
|:------------|
| ![Home](screenshots/home_screen_v1.1.1.png) |

| Dark Mode |
|:------------|
| ![Home](screenshots/dark_mode_v1.1.1.png) |
---

## ⚙️ Tech Stack

- 📱 Android (Java)
- 📰 RSS Feed Parsing (XML)
- 📜 RecyclerView + CardView
- 🔄 SwipeRefreshLayout
- 🧭 ViewPager2 + TabLayout (category tabs)
- 🗂️ Bottom Navigation Bar (source switching)
- 🌙 AppCompatDelegate (Dark Mode)
- 🌐 Internet Permission Handling

---

## 🗺️ Roadmap

| Phase | Feature | Status |
|:------|:--------|:-------|
| ✅ Phase 1 | Multi-source RSS feeds (The Hindu, NDTV, TOI) | Done |
| ✅ Phase 1 | Category tabs (Top Stories, World, Science, Tech, Sports) | Done |
| ✅ Phase 1 | Dark Mode | Done |
| ✅ Phase 1 | Bottom Navigation Bar | Done |
| 🔄 Phase 2 | Better RSS parser (CDATA, Atom, edge cases) | In Progress |
| 🔜 Phase 3 | Share to WhatsApp | Planned |
| 🔜 Phase 4 | Push Notifications (Firebase FCM) | Planned |
| 🔜 Phase 5 | Offline Reading (Room DB cache) | Planned |

---

## 📦 Google Play Store

**SD News is live on the Google Play Store!**  
Download it here → [SD News on Google Play](https://play.google.com/store/apps/details?id=com.sd.sdnews)

---