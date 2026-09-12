# 8092915Assignment2 — Album Explorer

Student: Zaeem Rasool  
Student ID: 8092915  
Unit: NIT3213  
Campus: Sydney  
API topic/keypass: `music`

## Overview

An Android album catalogue with login, dashboard and details screens. One
`MainActivity` hosts `LoginFragment`, `DashboardFragment` and `DetailsFragment`.
The dashboard uses a RecyclerView. Every row displays the artist, album title,
release year, genre, track count and popular track. Details displays these fields
and the complete description. Album content comes from the API.

## Interface

The XML layouts use small vector icons, rounded outlined cards and consistent
spacing. Two 180 ms property animators fade between fragments, including Back
from Details. Material buttons and album cards provide press feedback.

## Requirements and versions

- Android Studio with support for Android Gradle Plugin 8.9.2.
- Android SDK Platform 35 and Android SDK Build-Tools 35.0.0.
- JDK 17 selected for Gradle; use JDK 17 for terminal builds too.
- An emulator or Android device running API 24 or later.
- Internet access for dependency downloads and the API.
- Gradle wrapper 8.11.1; Kotlin 2.1.21; Koin 4.1.1; Retrofit 3.0.0.
- The complete dependency list is in `app/build.gradle.kts`.

These versions are intentionally fixed for this teaching project.

## Build and run

1. Clone this repository with Android Studio or Git.
2. Open the repository root containing `settings.gradle.kts` in Android Studio.
3. Install SDK Platform 35 and Build-Tools 35.0.0 from SDK Manager if needed.
4. Select JDK 17 in the Gradle settings and allow Gradle to sync.
5. Select an emulator/device running API 24+ and run the `app` configuration.
6. For this student account, enter `8092915` without `s` and your first name
   with the exact capitalization accepted by the class API. Other valid class
   accounts can enter their own 7- or 8-digit IDs and first names.

Build from Windows PowerShell in the project root:

```powershell
.\gradlew.bat :app:assembleDebug
```

Build from macOS/Linux:

```bash
chmod +x gradlew
./gradlew :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
Android Studio creates the local SDK configuration; `local.properties` is
machine-specific and is excluded from Git.

## API

Base URL: `https://nit3213apinew.onrender.com/`

- Login: `POST sydney/auth`, with JSON fields `username` and `password`.
- Dashboard: `GET dashboard/{keypass}`, using the value returned by login.
- This account returned `{"keypass":"music"}`, so its dashboard is
  `https://nit3213apinew.onrender.com/dashboard/music`.
- The app reads the live login response when choosing the dashboard path.
- The production list of albums is obtained from the API.
- HTTPS is used; internet permission is declared in the manifest.

## Code structure

| Location | Responsibility |
| --- | --- |
| `MainActivity.kt` | Hosts the fragments and changes screens |
| `AlbumApplication.kt` | Starts Koin when the app starts |
| `data/Models.kt` | JSON request/response and album data classes |
| `data/ApiService.kt` | Retrofit endpoint definitions |
| `data/AlbumRepository.kt` | Repository interface and API implementation |
| `di/AppModule.kt` | Registers API, repository and ViewModels with Koin |
| `ui/login/` | Login UI, validation and login state |
| `ui/dashboard/` | Dashboard UI, list adapter and loading state |
| `ui/details/` | Selected album details |
| `ui/ErrorMessages.kt` | Converts errors into readable messages |
| `app/src/test/` | Local unit tests and Mockito mock dependencies |

The ViewModels expose read-only StateFlow. The fragments collect it using
`viewLifecycleOwner.lifecycleScope` and `repeatOnLifecycle(STARTED)`, so
collection stops when the view stops and restarts when it becomes active. The
ViewModels receive `AlbumRepository` through their constructors. Koin creates
the network implementation in the running app; tests inject Mockito mocks. Retrofit
suspending functions run network requests without blocking the UI thread.

## Flow and Mockito

`MutableStateFlow` stays private in each ViewModel and `asStateFlow()` exposes
read-only state. Login success is cleared after navigation to prevent
repeated navigation when a new collector starts.

Tests use Mockito Core 5.12.0 and Mockito-Kotlin 5.4.0 alongside JUnit 4.
The test resource `mockito-extensions/org.mockito.plugins.MockMaker` selects
`mock-maker-subclass`. It supports our repository/API interfaces without Java
agent attachment; this configuration does not mock final classes or methods.
`TestData.kt` contains sample values only; mocked interfaces supply behavior.
There is no handwritten fake repository in the updated suite.

## Dependency injection

`AppModule.kt` registers shared objects with `single { ... }` and ViewModels with
`viewModel { ... }`. `get()` resolves the constructor dependencies. The fragments
obtain lifecycle-managed ViewModels using `by viewModel()`.

## Unit tests

Run local tests without starting an emulator:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

On macOS/Linux:

```bash
./gradlew :app:testDebugUnitTest
```

Open `app/build/reports/tests/testDebugUnitTest/index.html` for the results.

The supplied suite contains 19 tests: seven login ViewModel tests, seven
dashboard ViewModel tests, four repository tests and one Koin wiring test.
They cover invalid input, loading, case preservation, returned keypass usage,
authentication failure, connection errors, retry, empty results, duplicate
request prevention and retaining a loaded list. Tests use Mockito mock dependencies
and do not contact the live API. `MainDispatcherRule` supplies a test main
dispatcher. The success tests collect StateFlow to check loading and result
emissions. Mockito-Kotlin provides `mock`, `whenever` and `verify`.

The tests verify application logic. A device/emulator run is also needed to
check layouts, real authentication, JSON integration and fragment navigation.

The local suite includes one DI wiring check that exercises several real
components together; it is an integration-style check. The remaining tests
isolate ViewModels or the repository with Mockito mocks. Automated UI tests for the
assignment flows are not included; use the manual checks below.

## Manual verification

- Invalid ID and empty password show a readable error.
- Incorrect credentials show an error without opening the dashboard.
- Valid credentials load the live album list.
- The dashboard excludes descriptions; Details shows every field.
- Back returns from Details to the dashboard.
- Refresh reports a connection error when offline and Retry works after reconnecting.
- Rotating the dashboard/details screen retains the current screen.
- Long album names, large font settings and the keyboard leave controls usable.
- The screen fade works during login, opening Details and returning with Back.

## Current scope

The app uses the Sydney class endpoint and the supplied album schema. A fresh
launch starts at login. This project does not persist a login across a fresh
launch and does not provide an offline database. A loaded list is retained in
the dashboard ViewModel while that screen remains in the fragment back stack.

## References

- [Android fragments](https://developer.android.com/guide/fragments/create)
- [Android View Binding](https://developer.android.com/topic/libraries/view-binding)
- [Koin 4.1 Android ViewModel guide](https://insert-koin.io/docs/4.1/quickstart/android-viewmodel/)
- [Retrofit](https://square.github.io/retrofit/)
- [Testing Kotlin coroutines](https://developer.android.com/kotlin/coroutines/test)
- [StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Mockito-Kotlin](https://github.com/mockito/mockito-kotlin)

Add any course-required acknowledgement of tools or assistance you used, in
your own words.
