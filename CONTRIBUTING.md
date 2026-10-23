# Contributing to Zad El Momen

Thank you for your interest in contributing to **Zad El Momen**! We welcome contributions from developers, designers, and the open-source community to make this Islamic companion app even better.

Please follow these guidelines to ensure a smooth contribution process.

---

## 🛠️ Code of Conduct

By participating in this project, you agree to abide by our core principles:
- Respect fellow contributors and maintain a welcoming, constructive environment.
- Write clean, maintainable, and well-tested code.
- Adhere to the established Clean Architecture and coding style.

---

## 🚀 How to Contribute

### 1. Fork and Clone
1. Fork the repository on GitHub.
2. Clone your fork locally:
   ```bash
   git clone https://github.com/your-username/Zad-El-Momen.git
   cd Zad-El-Momen
   ```

### 2. Create a Branch
Create a descriptive branch for your feature or bug fix:
- Feature branch: `feature/short-description`
- Bug fix branch: `bugfix/short-description`

```bash
git checkout -b feature/amazing-new-feature
```

### 3. Development Workflow
- Follow Kotlin coding conventions and Android best practices.
- Respect the **Clean Architecture** boundaries (`presentation`, `domain`, `data`).
- Do not introduce unnecessary dependencies; check existing Koin modules and build configurations before adding new libraries.
- Test your changes locally on an emulator or physical device.

### 4. Submitting a Pull Request (PR)
1. Push your changes to your remote fork:
   ```bash
   git push origin feature/amazing-new-feature
   ```
2. Open a Pull Request on the main repository.
3. Provide a clear description of the problem solved or feature added, along with testing details or screenshots if applicable.

---

## 📝 Code Style Guidelines

- **Kotlin**: Use idiomatic Kotlin features (coroutines, flows, data classes).
- **Jetpack Compose**: Use stateless/stateful composable patterns, proper modifiers, and Material 3 design tokens.
- **Naming Conventions**: Use camelCase for variables/functions and PascalCase for classes/composables.
