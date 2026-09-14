# 8092915Assignment2 — Topic Explorer

**Student:** Zaeem Rasool  
**Student ID:** 8092915  
**Unit:** NIT3213 — Android Application Development  
**Campus:** Sydney

## Overview

Topic Explorer authenticates a student with the class API and displays the
catalogue associated with the returned `keypass`. The project uses one
`MainActivity` containing three Fragments: Login, Dashboard and Details.

The student's assigned topic is `music`, but the app also supports other
topics returned for valid accounts, such as `art`. Item properties are read
dynamically from the response instead of assuming that every item is an album.

## Features

- Login using a student ID without the leading `s` and a case-sensitive first name.
- Input validation, loading feedback and readable request errors.
- A RecyclerView dashboard showing each item's properties except `description`.
- A Details screen showing the selected item's properties and description.
- Refresh and retry, with existing content retained if a refresh fails.
- Music, art and book icons selected from recognised field names, with a general
  catalogue icon for other schemas.
- Sign out beside Refresh, returning to a fresh Login Fragment and clearing
  the previous navigation history.
- XML layouts, vector icons, outlined cards and short fade transitions.

## Requirements

| Component | Project configuration |
| --- | --- |
| Android Studio | A version compatible with Android Gradle Plugin 8.9.2 |
| Gradle | 8.11.1, through the project's Gradle wrapper |
| Gradle JDK | 17 |
| Kotlin | 2.1.21 |
| Compile / target SDK | 35 |
| SDK Build-Tools | 35.0.0 |
| Minimum device version | Android API 24 |

Internet access is needed to download build dependencies and contact the API.
Library dependencies are declared in `app/build.gradle.kts`, including Koin
4.1.1, Retrofit 3.0.0, OkHttp 4.12.0, Material Components, AndroidX Lifecycle
and RecyclerView. Retrofit uses the Gson converter.

## Build and run

1. Clone this repository or open an existing checkout in Android Studio.
2. Open the project root containing `settings.gradle.kts`, `app` and `gradlew`.
3. Install Android SDK Platform 35 and Build-Tools 35.0.0 using SDK Manager.
4. Select JDK 17 for Gradle and sync the project. If Gradle JVM criteria are
   enabled, make sure those criteria also select version 17.
5. Select an emulator or connected Android device running API 24 or later.
6. Run the `app` configuration.
7. Enter a valid Sydney class account's student ID and first name. This
   student's ID is `8092915`; the form accepts 7- or 8-digit IDs without `s`.
8. Open an item to view its details. Use Back to return to Dashboard, Refresh
   to reload, or Sign out to log in with another account.

Build from Windows PowerShell in the project root:

```powershell
.\gradlew.bat :app:assembleDebug
```

Build from macOS or Linux:

```bash
chmod +x gradlew
./gradlew :app:assembleDebug
```

Terminal builds also need JDK 17 and a configured Android SDK. Android Studio
creates the machine-specific `local.properties`; it should not be committed.
Keep the Gradle wrapper scripts and `gradle/wrapper` files in the repository.

Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## API integration

Base URL: `https://nit3213apinew.onrender.com/`

| Operation | Request | Response used by the app |
| --- | --- | --- |
| Login | `POST sydney/auth` with `username` and `password` in the JSON body | `keypass` |
| Dashboard | `GET dashboard/{keypass}` | `entities` and `entityTotal` |

The dashboard path always uses the keypass returned by login. It is not
hardcoded to `music` or `art`. The Android manifest declares INTERNET permission.
The HTTP client has a 30-second connection timeout, 60-second read timeout and
90-second total call timeout.

The configured authentication endpoint is for Sydney. For another campus,
change the auth path in `data/ApiService.kt` to the endpoint specified by the
course. Supporting different topics does not automatically select a campus.

### Dynamic topic data

`Models.kt` uses `typealias Album = Map<String, JsonElement>` for individual
items. The name `Album` is retained from the original music implementation;
it now represents a generic topic item. Some existing class names and XML IDs
also retain their original album names.

- `displayTitle()` chooses a title-like field, a `name` field, or another
  non-description field as the heading.
- `displaySummary()` labels and displays all non-description fields.
- `displayDescription()` reads the description for the Details screen.
- Camel-case and underscore-separated property names become readable labels.
- JSON null values display as `Not provided`; nested arrays and objects are
  displayed as JSON text.

This supports varying item properties while expecting the API's documented
`entities` / `entityTotal` response wrapper. Icon selection is a visual
enhancement: an unrecognised schema still displays its data using the general icon.

## Architecture and project structure

Kotlin source paths below are relative to
`app/src/main/java/com/example/albumassignment/`.

| Location | Responsibility |
| --- | --- |
| `MainActivity.kt` | Hosts Fragments, performs navigation and handles local sign-out |
| `AlbumApplication.kt` | Starts Koin |
| `data/Models.kt` | Request/response models, generic item type and display helpers |
| `data/ApiService.kt` | Retrofit HTTP endpoint declarations |
| `data/AlbumRepository.kt` | Repository interface and network implementation |
| `di/AppModule.kt` | Registers the HTTP client, Retrofit, repository and ViewModels |
| `ui/login/` | Login Fragment, validation and login state |
| `ui/dashboard/` | Dashboard Fragment, ViewModel and ListAdapter |
| `ui/details/` | Selected item display and Bundle arguments |
| `ui/TopicIcons.kt` | Selects a drawable based on recognised item fields |
| `ui/ErrorMessages.kt` | Converts request errors into readable messages |
| `app/src/main/res/` | XML layouts, strings, vector drawables and fade animators |
| `app/src/test/` | Local tests, test fixtures and coroutine test support |

### Dependency injection and StateFlow

Koin provides shared network components through `single` definitions and
ViewModels through `viewModel` definitions. ViewModels receive the
`AlbumRepository` interface through constructor injection. Fragments obtain
their ViewModels using Koin's `by viewModel()` delegate.

Each ViewModel owns a private `MutableStateFlow` and exposes read-only
`StateFlow`. Fragments collect state through `viewLifecycleOwner.lifecycleScope`
and `repeatOnLifecycle(STARTED)`. Network work runs through Retrofit suspend
functions in `viewModelScope`. Dashboard guards against duplicate loads.

Navigation uses FragmentManager transactions. Details is added to the back
stack; sign-out clears the back stack and replaces the current screen with a
new Login Fragment. The current implementation has no stored authentication
session or API logout request. A new login creates a new Dashboard with its
own Fragment-scoped ViewModel and returned keypass.

## Tests

Tests use JUnit 4.13.2, Mockito Core 5.12.0, Mockito-Kotlin 5.4.0 and
kotlinx-coroutines-test 1.10.2.

Run on Windows:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Run on macOS or Linux:

```bash
./gradlew :app:testDebugUnitTest
```

Open `app/build/reports/tests/testDebugUnitTest/index.html` for the results
of the latest run.

| Test file | Coverage |
| --- | --- |
| `LoginViewModelTest.kt` | Input validation, loading, credential handling and login success/failure |
| `DashboardViewModelTest.kt` | Loading, errors, retry, empty results, duplicate requests and retained content |
| `NetworkAlbumRepositoryTest.kt` | Repository requests and response handling |
| `AppModuleTest.kt` | Koin dependency wiring |
| `TopicParsingTest.kt` | Synthetic music, art and other fields are preserved; description is excluded from summary |

Mockito supplies controlled repository/API behaviour; local tests do not
contact the live server. `MainDispatcherRule` replaces Main with a test
dispatcher and restores it afterwards. The test resource
`mockito-extensions/org.mockito.plugins.MockMaker` selects
`mock-maker-subclass` for the interface mocks.

The Koin wiring test is an integration-style check. The other tests isolate
logic or JSON parsing. These tests do not establish that live authentication,
icons, sign-out or screen layouts work; check those on a device using the
following checklist. No current test result is claimed here: use the generated
report after running the suite in this checkout.

## Manual verification checklist

- [ ] Invalid IDs and blank passwords show validation errors.
- [ ] Incorrect credentials show an error without opening Dashboard.
- [ ] A valid music account displays music properties and icons.
- [ ] A valid art account displays art properties and palette icons.
- [ ] Another topic displays its returned fields with an appropriate or general icon.
- [ ] Dashboard excludes description; Details includes it and all summary fields.
- [ ] Back returns from Details to Dashboard.
- [ ] Refresh reloads data; an offline refresh shows an error and preserves existing content.
- [ ] Retry works after connectivity is restored.
- [ ] Sign out opens a fresh Login screen; Back does not reopen the previous account.
- [ ] Signing in with another account uses its new keypass and displays its data.
- [ ] Signing out during refresh does not allow the previous screen to reappear.
- [ ] Rotation retains the current screen and displays the correct item data.
- [ ] Long values, larger font settings and the keyboard leave controls usable.

## Scope and limitations

- Data comes from the class API. No offline database is included.
- Existing dashboard state can survive configuration changes through the
  ViewModel; it is not durable storage after process death.
- Android can restore a screen and its Bundle arguments during recreation.
  There is no separate persistent-login feature.
- The same general layout is used for all topics. Nested data is shown as JSON
  text rather than a specialised nested interface.
- There is no universal item ID across the supplied topics. The adapter uses
  whole-item equality and treats changed items as replacements.

## References

- [Android Fragments](https://developer.android.com/guide/fragments/create)
- [Android View Binding](https://developer.android.com/topic/libraries/view-binding)
- [Koin Android ViewModels](https://insert-koin.io/docs/4.1/quickstart/android-viewmodel/)
- [Retrofit](https://square.github.io/retrofit/)
- [Gson](https://google.github.io/gson/UserGuide.html)
- [Testing Kotlin coroutines](https://developer.android.com/kotlin/coroutines/test)
