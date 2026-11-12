# Contributing to Zad Al-Mu'min

Thank you for your interest in contributing to Zad Al-Mu'min. Here is a quick guide to help you get started with the codebase and our contribution workflow.

## Git Workflow

We use `develop` as our main integration branch. All pull requests should target `develop`, not `main`.

1. Fork the repo and clone it locally.
2. Checkout the `develop` branch and make sure it is up to date:
   ```bash
   git checkout develop
   git pull origin develop
   ```
3. Create a feature or bugfix branch.

### Branch Naming Convention

Our CI checks pull request branch names automatically. Your branch name must follow this format:

`<type>/<branch-name>`

Allowed types:
`feature`, `feat`, `fix`, `test`, `refactor`, `chore`, `style`, `screen`, `docs`, `component`, `ci`

Examples:
* `feature/audio-download`
* `fix/athan-alarm-time`
* `screen/khatmah-view`

## Local Setup

1. Open the project in Android Studio (Ladybug or later recommended).
2. Use JDK 17.
3. Make sure you have a `local.properties` file in the project root containing your SDK path and necessary keys:
   ```properties
   sdk.dir=/path/to/android-sdk
   SUPABASE_URL="your_url"
   SUPABASE_KEY="your_key"
   SUPPORT_1="support_1"
   SUPPORT_5="support_5"
   SUPPORT_10="support_10"
   SUPPORT_25="support_25"
   SUPPORT_100="support_100"
   ```
4. Place your `google-services.json` inside the `app/` folder.
5. Run `./gradlew assembleDebug` to verify the build.

## Code Guidelines

* **Clean Architecture:** Keep `domain` free of Android dependencies. Place database, network, and hardware implementations in `data`, and Compose screens and ViewModels in `presentation`.
* **Jetpack Compose:** Build reusable, stateless UI components wherever possible. Use the existing theme colors and typography defined in `design_system`.
* **Koin:** Register dependencies in their respective layer modules (`LocalModule`, `RepositoryModule`, `DomainModule`, `PresentationModule`).

## Pull Requests

1. Keep commits focused and write descriptive commit messages.
2. Make sure the project builds cleanly before opening a pull request:
   ```bash
   ./gradlew assembleDebug
   ```
3. Open a pull request against the `develop` branch.
4. Describe what changed and include screenshots or screen recordings for UI changes.
