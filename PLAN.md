# Echosphere — Development Plan

---

## Phase 1 — UI Shell ✅ Complete

- [x] Project setup, dependencies, theme
- [x] Song data model
- [x] Navigation (Screen, AppNavigation, BottomNav)
- [x] HomeScreen (fake data, LazyRow)
- [x] MiniPlayer (persistent bar, fake state)
- [x] NowPlayingScreen (controls, slider, bottom sheet)

---

## Phase 2 — Backend

### AWS Setup
- [ ] Create AWS Educate account
- [ ] Launch EC2 t2.micro instance (Ubuntu 22.04)
- [ ] SSH into EC2, install Node.js, npm, yt-dlp
- [ ] Set up nginx as reverse proxy
- [ ] Connect existing cPanel MySQL database

### Node.js + Express API
- [ ] Initialize Node.js project (Express, mysql2, axios, fluent-ffmpeg)
- [ ] YouTube Data API v3 integration
  - [ ] Search endpoint: GET /search?q=query
  - [ ] Trending endpoint: GET /trending
- [ ] yt-dlp audio streaming endpoint: GET /stream/:youtubeId
  - [ ] Cache audio to Cloudflare R2 on first request
  - [ ] Serve from R2 cache on subsequent requests
- [ ] Request queue for yt-dlp (max 3 concurrent processes)
- [ ] MySQL schema: users, songs, playlists, play_history

### Cloudflare R2
- [ ] Create R2 bucket for audio cache
- [ ] Wire R2 upload/download into Node.js backend

---

## Phase 3 — Connect App To Backend

### Retrofit Integration
- [ ] Define API interface (Retrofit)
- [ ] SearchResponse, SongResponse data classes
- [ ] Wire HomeScreen to real trending endpoint
- [ ] Wire SearchScreen to real search endpoint

### ExoPlayer
- [ ] Build PlayerService (foreground service, Media3)
- [ ] Wire play button to real stream URL from backend
- [ ] Background playback + notification controls
- [ ] Lock screen controls
- [ ] Queue management (MediaItem list)
- [ ] Wire MiniPlayer progress bar to real playback position
- [ ] Wire NowPlaying slider to real position + seek

### SearchScreen
- [ ] Search bar with debounced input
- [ ] Results LazyColumn
- [ ] Tap result → add to queue + navigate to NowPlaying

---

## Phase 4 — Local Storage + Library

### Room Database
- [ ] Liked songs table
- [ ] Play history table (for Recently Played on HomeScreen)
- [ ] Downloaded songs table + local file paths

### LibraryScreen
- [ ] Liked Songs section (from Room)
- [ ] Downloaded Songs section (from Room)

### MiniPlayer + NowPlaying
- [ ] Wire like button → Room liked songs
- [ ] Heart icon toggles filled/outline based on liked state

### HomeScreen
- [ ] Recently Played section (from Room play_history)
- [ ] Trending section (from YouTube API)

---

## Phase 5 — Polish + Play Store

### UI Polish
- [ ] Consistent padding, spacing, typography across all screens
- [ ] Smooth animations (screen transitions, sheet open/close)
- [ ] Loading states (shimmer placeholders while fetching)
- [ ] Error states (no internet, failed fetch)
- [ ] Empty states (no liked songs, no search results)
- [ ] Thumbnail rounded corners, proper aspect ratios

### NowPlaying Bottom Sheet
- [ ] Playlist tab — real ExoPlayer queue
- [ ] Lyrics tab — Genius/Musixmatch API integration
- [ ] Related tab — YouTube related videos endpoint

### Performance
- [ ] Image caching tuning (Coil)
- [ ] LazyColumn/LazyRow optimizations
- [ ] Backend response caching headers

### Play Store
- [ ] App icon (Image Asset Studio)
- [ ] Screenshots for store listing
- [ ] Privacy policy (required for Play Store)
- [ ] Sign release APK
- [ ] Submit to Google Play Console

---

## Distribution Strategy

Since yt-dlp violates YouTube ToS, Play Store submission carries risk.
Primary distribution plan:
- F-Droid (open source, no ToS issues)
- Direct APK download (GitHub releases)
- Evaluate Play Store after legal review

