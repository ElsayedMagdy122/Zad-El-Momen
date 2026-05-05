# Radio ANR Fix Documentation

## Issue

The app froze (ANR - Application Not Responding) every time a user clicked play on a radio station. The UI became unresponsive for several seconds.

## Root Causes

### 1. Duplicate Koin DI Bindings

```kotlin
// ❌ BEFORE: Two separate AudioPlayerManager instances
single { AudioPlayerManager(get()) }                      // Instance A
single<PlayerController> { AudioPlayerManager(androidContext()) }  // Instance B  
single { AudioPlayerService() }                           // Wrong - Android manages Services
```

**Problem**: ViewModel and Service received different player instances. State wasn't synchronized.

### 2. Synchronous Operations in Service

```
┌─────────────────────────────────────────────────────────────────┐
│                    ❌ BEFORE: Blocking Flow                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  User Click ──► startService() ──► onStartCommand()            │
│                                          │                      │
│                                          ▼                      │
│                                    playerController.play()      │
│                                          │                      │
│                                          ▼                      │
│                                    ExoPlayer.Builder().build()  │
│                                          │  ⏱️ BLOCKING         │
│                                          ▼                      │
│                                    player.prepare()             │
│                                          │  ⏱️ BLOCKING         │
│                                          ▼                      │
│                                    updateForegroundNotification │
│                                          │  ⏱️ BLOCKING         │
│                                          │  (BitmapFactory)     │
│                                          ▼                      │
│                                    return START_NOT_STICKY      │
│                                                                 │
│  ⚠️  Main thread blocked until ALL operations complete          │
│  ⚠️  If total time > 5 seconds → ANR                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Problems**:
- `ExoPlayer.Builder().build()` - Player initialization on main thread
- `player.prepare()` - Called synchronously  
- `BitmapFactory.decodeResource()` - Large bitmap decode for notification
- All blocking the main thread in `onStartCommand()`

## Solution

### 1. Fixed Koin DI

```kotlin
// ✅ AFTER: Single shared instance
single<PlayerController> { AudioPlayerManager(androidContext()) }
```

### 2. Async Service Operations

```
┌─────────────────────────────────────────────────────────────────┐
│                    ✅ AFTER: Non-Blocking Flow                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  User Click ──► startService() ──► onStartCommand()            │
│                                          │                      │
│                                          ▼                      │
│                                    showForegroundNotification() │
│                                          │  (lightweight)       │
│                                          ▼                      │
│                                    serviceScope.launch { ─────┐ │
│                                          │                    │ │
│                                          ▼                    │ │
│                                    return START_NOT_STICKY    │ │
│                                                               │ │
│  ✅ Main thread FREE                                          │ │
│                                                               │ │
│  ┌──────────────── Coroutine (async) ◄────────────────────────┘ │
│  │                                                              │
│  │  playerController.play(url)                                  │
│  │         │                                                    │
│  │         ▼                                                    │
│  │  mainHandler.post { ──────────────────────┐                  │
│  │         │                                 │                  │
│  │         ▼                                 │                  │
│  │  return (state updated)                   │                  │
│  │                                           │                  │
│  │  ✅ UI shows loading spinner              │                  │
│  │                                           │                  │
│  └───────────────────────────────────────────│──────────────────│
│                                              │                  │
│  ┌──────────── Handler Message Queue ◄───────┘                  │
│  │                                                              │
│  │  ensurePlayerInitialized()                                   │
│  │  player.setMediaItem()                                       │
│  │  player.prepare()                                            │
│  │  player.play()                                               │
│  │                                                              │
│  └──────────────────────────────────────────────────────────────│
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Key Changes

| Component | Before | After |
|-----------|--------|-------|
| **DI Bindings** | 2 separate instances | 1 shared instance |
| **Service.onStartCommand** | Sync player.play() | Async via coroutine |
| **AudioPlayerManager.play()** | Direct ExoPlayer calls | Handler.post() deferred |
| **Notification bitmap** | BitmapFactory.decodeResource() | Removed (icon only) |
| **Notification timing** | After play | Before play (Android requirement) |

## Files Modified

1. `presentation/di/PresentationModule.kt` - Fixed DI bindings
2. `presentation/screen/radio/player/AudioPlayerService.kt` - Async service
3. `presentation/screen/radio/player/AudioPlayerManager.kt` - Handler-based play

## Lessons Learned

1. **Never block `onStartCommand()`** - Return quickly, do work async
2. **Single source of truth** - One DI binding per interface
3. **Foreground notification first** - Android requires it before long operations
4. **Avoid heavy operations on main thread** - Bitmap decode, player init, network prep

## Testing

1. Open Radio screen
2. Click play on any station
3. UI should show loading spinner immediately (no freeze)
4. Audio should start playing after buffering
5. Repeat with different stations - should remain smooth
