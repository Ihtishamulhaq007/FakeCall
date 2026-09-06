# Fake Call (Escape Call)

Android app: a standing (ongoing, non-swipeable) notification sits in your
notification shade. Tap it any time and it opens a full-screen fake incoming
call — your phone's default ringtone loops + it vibrates — with Answer/Decline
buttons. Use it as your excuse to step out of a meeting.

## How it works
1. Open the app, tap **Enable**. This posts an ongoing notification titled
   "Escape Call ready" that can't be swiped away.
2. Whenever you need it, pull down the shade and tap that notification —
   **FakeCallActivity** opens immediately, playing the ringtone on loop and
   vibrating, with Answer/Decline buttons. Either one stops it and closes.
3. Tap **Disable** in the app to remove the standing notification.
4. If your phone reboots while it's enabled, `BootReceiver` re-posts it
   automatically.

## Opening the project
1. Install **Android Studio** (Hedgehog or newer).
2. `File > Open`, select the `FakeCall` folder.
3. Let it sync.
4. Run on a device or emulator (minSdk 26 / Android 8.0+).

## Permissions
- On first launch it requests **notification permission** (Android 13+).

## Customizing
- Change the fake caller name: edit `callerName` text in
  `res/layout/activity_fake_call.xml`.
- Change notification text: edit `NotificationHelper.showPersistentNotification`.
- Change ringtone behavior: `FakeCallActivity.kt` uses the device's default
  ringtone via `RingtoneManager`; you could swap in a bundled mp3 in
  `res/raw` instead.

## Building via GitHub Actions
Push to `main` and the workflow in `.github/workflows/build.yml` builds a
debug APK automatically — download it from the run's **Artifacts** section
on the **Actions** tab.
