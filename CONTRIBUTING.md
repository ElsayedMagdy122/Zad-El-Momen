# Contributing to Zad Al-Mu'min (زاد المؤمن)

First off, thank you for considering contributing to **Zad Al-Mu'min**! 🎉 

This application is built with devotion to provide the global Muslim community with a clean, privacy-respecting, and modern spiritual tool. Whether you are fixing an edge-case bug, improving documentation or Arabic typography, adding a new translation, or engineering a major feature, your contribution is deeply appreciated.

---

## 🧭 Table of Contents
- [Code of Conduct](#-code-of-conduct)
- [How Can I Contribute?](#-how-can-i-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Your First Code Contribution](#your-first-code-contribution)
- [Git Workflow & Branching Strategy](#-git-workflow--branching-strategy)
  - [Branch Naming Rule (Enforced by CI)](#branch-naming-rule-enforced-by-ci)
- [Development Setup](#-development-setup)
- [Coding Standards & Architectural Guidelines](#-coding-standards--architectural-guidelines)
  - [Clean Architecture & Layer Separation](#clean-architecture--layer-separation)
  - [Jetpack Compose Guidelines](#jetpack-compose-guidelines)
  - [Dependency Injection with Koin](#dependency-injection-with-koin)
  - [Commit Message Guidelines](#commit-message-guidelines)
- [Submitting a Pull Request (PR)](#-submitting-a-pull-request-pr)

---

## 📜 Code of Conduct
By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please keep discussions respectful, constructive, and focused on building a beneficial application.

---

## 💡 How Can I Contribute?

### Reporting Bugs
Before filing an issue, please search the [Issue Tracker](https://github.com/ElsayedMagdy122/Zad-El-Momen/issues) to verify if the issue has already been reported.

If you find a new bug, please open an issue including:
- **A clear, descriptive title.**
- **Steps to reproduce the behavior.**
- **Expected vs. actual behavior.**
- **Device information:** Device model, Android OS version, and app version (e.g., Pixel 7, Android 14, Zad Al-Mu'min v1.2.0).
- **Screenshots or screen recordings** where applicable.
- **Logcat output** (ensure no private data is included).

### Suggesting Enhancements
Feature requests are always welcome! When opening a feature request:
- Explain **why** this feature would benefit users.
- Provide mockups, sketches, or reference implementations if possible.
- Detail edge cases or theological considerations (e.g., calculation methods, Quranic text integrity).

---

## 🌿 Git Workflow & Branching Strategy

Our primary development branch is `develop`. The `main` branch contains stable, production-ready releases.

1. **Fork** the repository to your own GitHub account.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/<your-username>/Zad-El-Momen.git
   cd Zad-El-Momen
   ```
3. Set the upstream remote:
   ```bash
   git remote add upstream https://github.com/ElsayedMagdy122/Zad-El-Momen.git
   git fetch upstream
   ```
4. Create a branch from `upstream/develop`:
   ```bash
   git checkout -b <branch-name> upstream/develop
   ```

### Branch Naming Rule (Enforced by CI)
Our GitHub Actions automated pipeline strictly verifies PR branch names against the following regular expression:
```regex
^(feature|feat|fix|test|refactor|chore|style|screen|docs|component|ci)\/[\w-\/.]+$
```

Please prefix your branch with one of the approved types:
- `feature/` or `feat/` – New user-facing features (e.g., `feature/khatmah-planner`)
- `screen/` – A new Compose screen or UI flow (e.g., `screen/tasmee3-feedback`)
- `component/` – Reusable UI kit / Design System component (e.g., `component/circular-progress`)
- `fix/` – Bug fixes (e.g., `fix/athan-lockscreen-crash`)
- `refactor/` – Code changes that neither fix a bug nor add a feature (e.g., `refactor/koin-modules`)
- `style/` – Formatting, typography, or UI polish without logic changes (e.g., `style/uthmani-font-padding`)
- `test/` – Adding or correcting unit or UI tests (e.g., `test/prayer-time-calculations`)
- `docs/` – Documentation updates (e.g., `docs/readme-updates`)
- `chore/` – Build scripts, dependencies, or tool configurations (e.g., `chore/bump-room-version`)
- `ci/` – GitHub Actions or workflow changes (e.g., `ci/add-lint-check`)

---

## 💻 Development Setup

1. **JDK Requirement:** Ensure you have **JDK 17** installed and configured in Android Studio (`Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`).
2. **Setup `local.properties`:** Add your development configuration keys:
   ```properties
   SUPABASE_URL="your_supabase_url"
   SUPABASE_KEY="your_supabase_key"
   SUPPORT_1="support_1"
   SUPPORT_5="support_5"
   SUPPORT_10="support_10"
   SUPPORT_25="support_25"
   SUPPORT_100="support_100"
   ```
3. **Google Services:** Ensure `app/google-services.json` is present.
4. **Compile the App:**
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📐 Coding Standards & Architectural Guidelines

### Clean Architecture & Layer Separation
The codebase is structured into three distinct layers:
1. **`domain` Layer (Pure Kotlin):**
   - Must **never** import Android SDK dependencies (`android.*`).
   - Contains business models (`entity`), abstract repository interfaces (`repository`), and single-responsibility `usecase` classes.
2. **`data` Layer:**
   - Implements the contracts defined in the `domain` layer.
   - Houses Room database entities, DAOs, Supabase data sources, mappers, and hardware/sensor managers.
   - Converts raw network/database DTOs into pure domain models using dedicated mappers.
3. **`presentation` & `design_system` Layer:**
   - Contains Jetpack Compose UI composables, ViewModels, and state representations.
   - ViewModels extend `BaseViewModel` and expose immutable `StateFlow<UiState>` to UI screens.
   - Keep Composables stateless whenever possible by hoisting state and events.

### Jetpack Compose Guidelines
- **Explicit Parameter Ordering:** Required parameters first, followed by optional parameters, with `modifier: Modifier = Modifier` as the first optional parameter.
- **Stable Modifiers:** Always chain modifiers to the outer container. Never recreate modifiers inside nested loops.
- **RTL & Localization:** Always design components with bidirectional text in mind. Use `Start`/`End` padding rather than `Left`/`Right`.
- **Theme Consistency:** Consume colors and typography exclusively from `MehrabTheme.colors` and `MehrabTheme.typography` (under `design_system`). Do not hardcode raw hex values or font sizes inside screen composables.

### Dependency Injection with Koin
- Define module bindings inside the respective layer:
  - `data/di`: `LocalModule`, `RemoteModule`, `RepositoryModule`
  - `domain/di`: `DomainModule`
  - `presentation/di`: `PresentationModule`
- Prefer constructor injection. Avoid resolving dependencies via the global `get()` inside composable functions; inject dependencies into ViewModels instead.

### Commit Message Guidelines
We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```text
<type>(<scope>): <short description in imperative mood>

[optional body explaining the motivation and changes]

[optional footer referencing issue numbers, e.g., Closes #42]
```

**Examples:**
- `feat(tasmee3): add offline audio token alignment for surah Al-Fatiha`
- `fix(azan): prevent duplicate notification scheduling after device reboot`
- `docs(readme): add Arabic documentation and architecture diagram`
- `refactor(prayer): optimize astronomical coordinate cache in local database`

---

## 🚀 Submitting a Pull Request (PR)

1. **Rebase with `upstream/develop`** before opening your PR:
   ```bash
   git fetch upstream
   git rebase upstream/develop
   ```
2. **Verify Local Build & Tests:**
   ```bash
   ./gradlew assembleDebug testDebugUnitTest
   ```
3. **Push to Your Fork:**
   ```bash
   git push origin <your-branch-name>
   ```
4. **Open the PR:**
   - Target branch: **`develop`** (do not target `main` directly).
   - Fill in the PR description template: describe the purpose of the change, link related issues (e.g. `Fixes #12`), and attach screenshots/GIFs for UI changes.
5. **CI Checks:** Ensure the automated GitHub Actions build and branch name checks pass. If there are review suggestions, address them promptly.

---

## 🤲 Thank You!
Your effort directly supports thousands of Muslims worldwide seeking a distraction-free, reliable tool for their daily devotion. May Allah accept your effort and reward you abundantly!
