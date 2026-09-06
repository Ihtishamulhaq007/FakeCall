# Fake Call (Escape Call)

An always-on notification you tap when you need out of a meeting. N seconds after
you tap it, a fake incoming call arrives — full ringtone + vibration + Answer/Decline.

## How it works
1. **MainActivity** — pick a delay (10s test / 1 / 5 / 15 min), tap **Enable**.
   This posts a *persistent* (ongoing, non-dismissable) notification titled
   "Escape Call armed".
2. That notification sits in your tray indefinitely — it does **not** trigger
   anything on its own.
3. When you actually want out: pull down the shade and **tap the standing
   notification**. Only then does the N-second countdown start
   (`ArmReceiver` schedules an `AlarmManager` alarm).
4. When the countdown elapses, `AlarmReceiver` posts a second, separate
   "Incoming call" notification (and tries a full-screen intent so it can pop
   over the lock screen).
5. Tapping *that* notification opens **FakeCallActivity**: your default
   ringtone loops + phone vibrates, with Answer/Decline buttons. Either one
   stops it and closes the screen. The standing notification then resets
   itself back to "ready" so you can use it again.
6. The standing notification survives app-swipe-away and even reboot (via
   `BootReceiver`) as long as you haven't tapped **Disable**.

## Opening the project
1. Install **Android Studio** (Hedgehog or newer).
2. `File > Open`, select the `FakeCall` folder, let it sync.
3. Run on a device or emulator (minSdk 26 / Android 8.0+).

Or build it headlessly via the included GitHub Actions workflow
(`.github/workflows/build.yml`) — push to a repo and download the APK
artifact from the Actions tab.

## Permissions
- **Notifications** (Android 13+) — requested on first launch.
- **Alarms & reminders** — for exact-second timing on Android 12+, enable it
  for the app in system Settings; otherwise the call still arrives, just with
  a small possible delay.
- **Full-screen intent** — lets the incoming-call alert show over the lock
  screen; some OEM skins (Samsung/Xiaomi/etc.) may still require you to
  disable battery optimization for the app to guarantee this.

## Customizing
- Fake caller name: `res/layout/activity_fake_call.xml`.
- Delay presets: `RadioGroup` in `activity_main.xml` + `MainActivity.kt`.
- Standing-notification wording: `NotificationHelper.showPersistentNotification`.
