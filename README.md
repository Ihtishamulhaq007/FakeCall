# Fake Call (Escape Call)

Android app: schedule a delay, get a silent notification, tap it, and it opens a
full-screen fake incoming call that rings your phone's default ringtone + vibrates,
with Answer/Decline buttons. Use it as your excuse to step out of a meeting.

## How it works
1. **MainActivity** — pick a delay (10s test / 1 / 5 / 15 min) and tap "Schedule Fake Call".
   This sets an `AlarmManager` alarm.
2. **AlarmReceiver** fires at that time and posts a notification (silent, high priority,
   styled like an incoming call).
3. Tapping the notification opens **FakeCallActivity**, which plays your device's default
   ringtone on loop + vibrates, and shows Answer/Decline buttons. Either button stops the
   sound and closes the screen.

## Opening the project
1. Install **Android Studio** (Hedgehog or newer).
2. `File > Open`, select the `FakeCall` folder.
3. Let it sync (Android Studio will fetch/repair the Gradle wrapper automatically on
   first sync if `gradlew` isn't runnable — accept the prompt, or just build with the
   IDE's bundled Gradle).
4. Run on a device or emulator (minSdk 26 / Android 8.0+).

## Permissions
- On first launch it requests **notification permission** (Android 13+).
- If you want the alarm to fire at the *exact* second on Android 12+, enable
  **"Alarms & reminders"** for the app in system Settings — otherwise it still fires,
  just with a small possible delay.

## Customizing
- Change the fake caller name: edit `callerName` text in
  `res/layout/activity_fake_call.xml`.
- Add more delay presets: edit the `RadioGroup` in `activity_main.xml` and the
  `when` block in `MainActivity.kt`.
- Change ringtone behavior: `FakeCallActivity.kt` uses the device's default ringtone
  via `RingtoneManager`; you could swap in a bundled mp3 in `res/raw` instead.
