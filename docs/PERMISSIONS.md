# 🔒 Permissions & Privacy Model

**Zad Al-Mu'min** operates with an **Offline-First, Privacy-Centric** guarantee. No personal telemetry or audio recordings are transmitted to external servers.

---

## 📋 Declared Permissions & Technical Justifications

| Permission | Category | Purpose | Privacy Guarantee |
| :--- | :--- | :--- | :--- |
| `RECORD_AUDIO` | Dangerous | Used only in the **Tasmee3 (Recitation Verification)** screen for the local Sherpa-ONNX model. | Audio is processed in memory on the CPU/NPU. **No audio files are saved to disk or uploaded to the cloud.** |
| `ACCESS_FINE_LOCATION` | Dangerous | Resolves latitude/longitude for astronomical prayer time and Qiblah angle calculations. | Coordinates remain local. Location is never tracked continuously or shared. |
| `SCHEDULE_EXACT_ALARM` | System | Triggers Azan notifications and full-screen popup at the exact second prayer begins. | Used strictly for scheduled religious alarms. |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Service | Keeps Quran audio and Quran Radio streams playing when screen is off or app is minimized. | Standard Media3 ExoPlayer background playback. |
| `RECEIVE_BOOT_COMPLETED` | Receiver | Re-registers prayer alarms with the system after a device reboot. | Ensures alarms are never missed after phone restarts. |
| `POST_NOTIFICATIONS` | Normal | Displays prayer alarms, upcoming prayer countdowns, and daily Adhkar reminders. | User can toggle notifications per prayer in settings. |
| `WAKE_LOCK` / `USE_FULL_SCREEN_INTENT` | System | Wakes device and shows the Azan full-screen activity when a prayer time arrives. | Dismissable with a single tap. |
