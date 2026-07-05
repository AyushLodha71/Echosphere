# Changelog

All notable changes to Echosphere are documented here.
Format follows [Semantic Versioning](https://semver.org/):
- MAJOR → breaking changes / complete rewrite
- MINOR → new features added
- PATCH → bug fixes only

---

## [0.2.0] - July 2026 — Phase 1 Complete

### Changed

#### Song.kt
- Renamed field `thumbnailUrl` to `thumbnailId` — more accurate since the field stores
  a YouTube video ID (e.g. "4NRXx6U8ABQ"), not a full URL. The full thumbnail URL
  is constructed dynamically wherever needed using string templates:
  `"https://i.ytimg.com/vi/${song.thumbnailId}/hqdefault.jpg"`

#### AppNavigation.kt
- Removed internal `rememberNavController()` call — `navController` is now accepted
  as a parameter (`NavHostController`) instead of being created internally
- Added `modifier: Modifier = Modifier` parameter, applied to `NavHost` so
  `innerPadding` from `Scaffold` can be correctly passed in from `MainActivity`
- Wired `HomeScreen(navController)` into the Home route composable, replacing the
  placeholder `Text("Home Screen")`
- Wired `NowPlayingScreen(navController)` into the NowPlaying route composable,
  replacing the placeholder `Text("Now Playing Screen")`

#### MainActivity.kt
- `navController` now created once in `MainActivity` using `rememberNavController()`
  and passed down to both `AppNavigation` and `BottomNav` — ensures both share the
  same navigation state so bottom nav tab taps correctly affect screen content
- `Scaffold` `bottomBar` slot updated to contain a `Column` with `MiniPlayer` stacked
  above `BottomNav` — both conditionally hidden on the NowPlaying route
- Current route tracked via `navController.currentBackStackEntryAsState()` and
  `navBackStackEntry?.destination?.route` — used to conditionally show/hide `MiniPlayer`
- `MiniPlayer` hidden when `currentRoute == Screen.NowPlaying.route` so the full
  player screen is unobstructed
- `BottomNav` remains always visible across all screens including NowPlaying

#### HomeScreen.kt
- Switched inner layout from `Row` (image left, text right) to `Column` (image top,
  text below) per item — matches Spotify-style horizontal album card layout
- Switched from `LazyColumn` (vertical list) to `LazyRow` (horizontal scroll) —
  songs now scroll sideways like a "Recently Played" section
- `Arrangement.spacedBy(12.dp)` added to `LazyRow` — prevents cards from touching
- YouTube thumbnail URL now built dynamically using string template:
  `"https://i.ytimg.com/vi/${song.youtubeId}/hqdefault.jpg"`
- `song.youtubeId` stores just the video ID (e.g. "4NRXx6U8ABQ") not the full URL
- Cards now `clickable` — tapping any song navigates to `Screen.NowPlaying.route`
- `navController: NavController` added as function parameter to enable navigation
- `Modifier.weight(1f)` added to inner `Column` to prevent text overflow

### Added

#### AndroidManifest.xml
- Added `<uses-permission android:name="android.permission.INTERNET" />` — required
  for any network call including Coil image loading (AsyncImage) and future Retrofit
  API calls. Without this, OkHttp throws `SecurityException: Permission denied`

#### build.gradle.kts
- Added `androidx.compose.material:material-icons-extended:1.6.1` dependency —
  unlocks the full Material Icons library including Shuffle, SkipPrevious, SkipNext,
  Repeat, Timer, QueueMusic and thousands more. Base icon pack only ships ~150 icons;
  extended pack ships 5800+. Tradeoff: adds ~5MB to APK size

#### MiniPlayer.kt (new file — ui/components)
- Persistent mini player bar shown above BottomNav on all screens except NowPlaying
- Displays fake hardcoded song (Blinding Lights — The Weeknd) as placeholder
- Layout: `Column` wrapping `Row` (thumbnail + text + buttons) + `LinearProgressIndicator`
- `AsyncImage` (56dp) loads YouTube thumbnail via Coil
- `Column` with `Modifier.weight(1f)` holds title (`titleSmall`) and artist (`bodySmall`)
  using `MaterialTheme.typography` — pushes play button to the right edge
- `IconButton` with `FavoriteBorder` icon — like button (placeholder, wired in Phase 4)
- `IconButton` with `PlayArrow` icon — play/pause button (placeholder, wired in Phase 3)
- `LinearProgressIndicator` full width below the Row — shows fake 0.3f progress
- `verticalAlignment = Alignment.CenterVertically` on Row — thumbnail and text aligned
- Entire component `clickable` → navigates to `Screen.NowPlaying.route`
- `MaterialTheme.typography` used for text styles — consistent with app theme

#### NowPlayingScreen.kt (new file — ui/screens)
Full player screen shown when user taps a song or the MiniPlayer. Complete UI shell
with placeholder/fake data — real ExoPlayer wiring comes in Phase 3.

Layout (top to bottom inside a `Column` with `Arrangement.spacedBy(8.dp)`):

**Top Bar Row**
- `IconButton` with `KeyboardArrowDown` — back button, calls `navController.popBackStack()`
- `IconButton` with `MoreVert` — future dropdown menu (sleep timer, add to playlist, share)
- `Arrangement.SpaceBetween` — back button left, menu button right

**Album Art**
- `AsyncImage` sized at 280dp — loads YouTube thumbnail via Coil
- Centered via `horizontalAlignment = Alignment.CenterHorizontally` on outer Column

**Song Info Row**
- `Column` with `Modifier.weight(1f)` — title (`titleLarge`) + artist (`bodyLarge`)
- `IconButton` with `FavoriteBorder` — like button (placeholder, wired in Phase 4)
- `Arrangement.SpaceBetween` — text left, like button right

**Progress Slider**
- `Slider` with fake `value = 0.3f` and empty `onValueChange = { }` 
- Replaced by real ExoPlayer position in Phase 3

**Timestamp Row**
- `Text("0:00")` left — current position (fake, replaced in Phase 3)
- `Text("3:21")` right — total duration (from `song.duration` in Phase 3)

**Playback Controls Row**
- 5 `IconButton`s with `Arrangement.SpaceBetween`:
  - `Shuffle` — shuffle toggle (Phase 3)
  - `SkipPrevious` — previous song (Phase 3)
  - `PlayArrow` — play/pause toggle (Phase 3)
  - `SkipNext` — next song (Phase 3)
  - `Timer` — sleep timer (Phase 4)
- All `onClick = { }` placeholders

**Spacer**
- `Modifier.weight(1f)` — pushes everything above up, pins bottom bar to screen bottom

**Bottom Bar (drag-to-open sheet trigger)**
- Wrapped in `Column` with `pointerInput(Unit)` + `detectVerticalDragGestures`
- Upward drag (`dragAmount < -10`) sets `showSheet = true` → opens bottom sheet
- `HorizontalDivider` (40dp wide, 4dp thick, 30% opacity) — visual drag handle hint
- `Row` with 3 `TextButton`s — Playlist, Lyrics, Related
- Active tab highlighted with `MaterialTheme.colorScheme.primary` color
- Tapping any tab sets `selectedTab` index and opens sheet

**ModalBottomSheet**
- Shown when `showSheet = true`, dismissed when user drags down or taps outside
- `rememberModalBottomSheetState()` tracks expanded/collapsed state
- `@OptIn(ExperimentalMaterial3Api::class)` annotation suppresses experimental warning
- Contains identical tab switcher Row inside sheet for in-sheet tab switching
- `when (selectedTab)` block renders correct tab content:
  - `0` → `PlaylistTab()` — "Playlist coming soon" placeholder
  - `1` → `LyricsTab()` — "Lyrics coming soon" placeholder
  - `2` → `RelatedTab()` — "Related coming soon" placeholder
- `selectedTab` state defined in `NowPlayingScreen` scope — persists between
  sheet open/close cycles (last selected tab remembered)

**Private composables (same file)**
- `PlaylistTab()` — placeholder, replaced with ExoPlayer queue in Phase 3
- `LyricsTab()` — placeholder, replaced with Genius/Musixmatch API in Phase 5
- `RelatedTab()` — placeholder, replaced with YouTube related videos in Phase 3

---

## [0.1.0] - June 2026 — Initial Commit

### Project Setup
- Kotlin + Jetpack Compose project initialized
- Package name: `com.example.echosphere`
- Minimum SDK: API 26 (Android 8.0) — covers 95%+ of active Android devices
- Target SDK: 34
- Compile SDK: 35
- Java/Kotlin compatibility: VERSION_11
- Dependencies added:
  - `androidx.media3:media3-exoplayer:1.3.1` — audio/video playback engine
  - `androidx.media3:media3-ui:1.3.1` — player UI components
  - `androidx.media3:media3-session:1.3.1` — connects player to Android media
    notification and lock screen controls
  - `com.squareup.retrofit2:retrofit:2.11.0` — HTTP client for backend API calls
  - `com.squareup.retrofit2:converter-gson:2.11.0` — converts JSON responses to
    Kotlin data classes automatically
  - `com.squareup.okhttp3:okhttp:4.12.0` — networking layer used under Retrofit
  - `com.squareup.okhttp3:logging-interceptor:4.12.0` — logs every HTTP request
    and response in Logcat during development
  - `io.coil-kt:coil-compose:2.6.0` — fast async image loading for Compose
    (used for YouTube thumbnails and album art)
  - `androidx.room:room-runtime:2.6.1` — local SQLite database wrapper
  - `androidx.room:room-ktx:2.6.1` — Kotlin coroutine extensions for Room
  - `androidx.navigation:navigation-compose:2.7.7` — screen navigation for Compose
  - `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0` — ViewModel integration

### Package Structure Created
```
com.example.echosphere/
├── data/
│   ├── local/       → Room database (Phase 4)
│   ├── model/       → Data classes (Song.kt)
│   └── remote/      → Retrofit API interfaces (Phase 3)
├── player/          → ExoPlayer service (Phase 3)
├── ui/
│   ├── components/  → Reusable UI (BottomNav, MiniPlayer)
│   ├── navigation/  → Screen routes and NavHost
│   ├── screens/     → Full screens (Home, Search, Library, NowPlaying)
│   └── theme/       → Colors, typography, theme
└── viewmodel/       → ViewModels (Phase 3)
```

### Theme
- Dark color palette defined (Color.kt):
  - `EchoBackground` #0A0A0A — near-black main background
  - `EchoSurface` #121212 — slightly lighter surface (cards, bottom nav)
  - `EchoPrimary` #6C63FF — vibrant purple, main accent color
  - `EchoOnPrimary` #FFFFFF — text/icons on primary colored backgrounds
  - `EchoOnBackground` #FFFFFF — main body text color
  - `EchoOnSurface` #B3B3B3 — secondary/muted text color
  - `EchoSurfaceVariant` #1E1E1E — slightly elevated surface variant
- Typography system (Type.kt) — 8 named text styles using `FontFamily.Default`
  (Roboto on most Android devices):
  - `displaySmall` — 34sp Bold, for large headings
  - `titleLarge` — 22sp Bold, for screen titles
  - `titleMedium` — 16sp Medium, for song titles in player
  - `titleSmall` — 14sp Medium, for song titles in lists
  - `bodyMedium` — 14sp Normal, for artist names in player
  - `bodySmall` — 12sp Normal, for artist names in lists and timestamps
  - `labelMedium` — 11sp Medium, for bottom nav labels
  - `labelSmall` — 10sp Medium, for metadata and duration text
- `EchosphereTheme` (Theme.kt):
  - Dark theme only — no light theme for now
  - Dynamic color disabled — app always uses Echosphere purple palette
  - `MaterialTheme` wired with `DarkColorScheme` + `Typography`
  - Any composable inside `EchosphereTheme` can access colors via
    `MaterialTheme.colorScheme.*` and text styles via `MaterialTheme.typography.*`

### Data Model
- `Song.kt` (data/model) — Kotlin data class representing one song:
  - `id: String` — unique identifier (YouTube video ID used as ID)
  - `title: String` — song title, never null
  - `artist: String` — artist name, never null
  - `youtubeId: String?` — YouTube video ID for thumbnail + stream URL, nullable
    (null before first backend fetch)
  - `duration: Long` — song length in milliseconds, never null
  - `streamUrl: String?` — direct audio URL from backend/yt-dlp, nullable
    (null until user taps play; populated by backend on demand)
  - Kotlin `data class` auto-generates: constructor, getters, equals(),
    hashCode(), toString(), copy()
  - All fields `val` (immutable) — song data doesn't change after creation

### Navigation
- `Screen.kt` (ui/navigation) — sealed class defining all navigable destinations:
  - `Screen.Home` → route: "home"
  - `Screen.Search` → route: "search"
  - `Screen.Library` → route: "library"
  - `Screen.NowPlaying` → route: "now_playing"
  - `sealed class` ensures compiler knows all possible screens — exhaustive `when`
    expressions without `else` branch, compile error if a screen is missed
  - Each is an `object` (singleton) — only one instance exists per screen type
  - `val route: String` inherited from parent — type-safe route strings
    preventing runtime typos that would cause navigation crashes

- `AppNavigation.kt` (ui/navigation) — `@Composable` NavHost setup:
  - `NavHost` maps each `Screen.*.route` to its composable function
  - `startDestination = Screen.Home.route` — app always opens on Home
  - 4 `composable()` blocks: Home, Search, Library, NowPlaying
  - Accepts `navController: NavHostController` as parameter (shared from MainActivity)
  - Accepts `modifier: Modifier` parameter for `innerPadding` from Scaffold

- `BottomNav.kt` (ui/components):
  - `BottomNavItem` data class — holds `route: String`, `label: String`,
    `icon: ImageVector` per nav item
  - 3 items: Home (`Icons.Default.Home`), Search (`Icons.Default.Search`),
    Library (`Icons.Default.List`)
  - `NavigationBar` with `NavigationBarItem` per tab
  - Active tab detected via `navController.currentBackStackEntryAsState()`
    and `navBackStackEntry?.destination?.route`
  - Selected tab highlighted automatically via `selected = currentRoute == item.route`
  - Tapping a tab calls `navController.navigate(item.route)`
  - `forEach` loop over items list — avoids 3 separate repeated `NavigationBarItem` blocks

### MainActivity
- Single `ComponentActivity` — entire app runs in one Android window
- `enableEdgeToEdge()` — app draws behind status bar and navigation bar
  for a premium full-screen feel
- `setContent { EchosphereTheme { ... } }` — entire app wrapped in dark theme
- `Scaffold` with `bottomBar` slot — handles padding so content never hides
  behind the bottom bar
- `innerPadding` applied to `AppNavigation` via `Modifier.padding(innerPadding)`
  — prevents song list from being hidden behind the bottom nav bar

