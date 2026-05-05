# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

ACopy is an Android/Java reflection utility library. The repo contains two Gradle modules:
- `:acopy` — the library (single `ACopy.java` file)
- `:app` — a demo Android application

### Prerequisites

| Dependency | Version | Notes |
|---|---|---|
| JDK | 8 | AGP 3.4.1 requires JDK 8. Path: `/usr/lib/jvm/java-8-openjdk-amd64` |
| Android SDK | API 28, Build-tools 28.0.3 | Installed at `/opt/android-sdk` |
| Gradle | 5.1.1 (via wrapper) | `./gradlew` uses the wrapper in `gradle/wrapper/` |

### Environment variables

Before running any Gradle command, export:

```sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export ANDROID_SDK_ROOT=/opt/android-sdk
export ANDROID_HOME=/opt/android-sdk
```

A `local.properties` file with `sdk.dir=/opt/android-sdk` must exist in the project root (generated during setup).

### Common commands

| Task | Command |
|---|---|
| Build debug APK | `./gradlew assembleDebug --no-daemon` |
| Run unit tests | `./gradlew testDebugUnitTest --no-daemon` |
| Run lint | `./gradlew lintDebug --no-daemon` |
| Clean | `./gradlew clean --no-daemon` |

### Gotchas

- The `gradle/wrapper/` directory was missing from the repository. The update script regenerates it using a standalone Gradle 5.1.1 distribution if absent.
- Use `--no-daemon` with Gradle to avoid daemon-related issues in the cloud VM.
- Only build/test/lint the **debug** variant. The release variant may fail with stale merge errors unless you `clean` first.
- `sdkmanager` (in `cmdline-tools`) requires JDK 17+ to run; the project itself must be built with JDK 8. Use JDK 21 (pre-installed) for `sdkmanager`, JDK 8 for `./gradlew`.
- This is a pure Android library + demo app. There are no backend services, databases, or Docker dependencies.
- Instrumented tests (`androidTest`) require a device/emulator and cannot run in the cloud VM. Unit tests (`testDebugUnitTest`) run on the JVM and work fine.
