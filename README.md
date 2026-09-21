# Life Admin 📱

**Never forget what matters.**

A modern, offline-first Android reminder app built with Jetpack Compose and Material 3 Design. Life Admin helps you manage recurring bills, subscriptions, medications, and tasks with smart notifications and a beautiful, intuitive interface.

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-purple.svg)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## ✨ Features

### Core Functionality
- ✅ **Smart Reminders** - Create one-time or recurring reminders with custom intervals
- 🔔 **Notifications** - Battery-efficient notifications that survive device restarts
- 📅 **Calendar View** - Visual month calendar with all your reminders
- 🔍 **Smart Search** - Instant search across all reminders
- 📊 **Statistics** - Track completion rates and streaks
- 💾 **Backup & Restore** - Export/import your data as JSON
- 🌙 **Dark Mode** - Beautiful light and dark themes
- 📴 **100% Offline** - No internet required, no cloud dependencies

### Reminder Types
- 💊 Medicine & supplements
- 💰 Bills & payments
- 📺 Subscriptions (Netflix, Spotify, etc.)
- 🚰 Utilities (water, electricity, etc.)
- 🎂 Birthdays & anniversaries
- 📋 General tasks & to-dos

### Recurrence Options
- One-time only
- Daily
- Weekly
- Monthly
- Yearly

---

## � Design

Life Admin features a modern, clean design following Material 3 guidelines with:
- **Purple accent color** (#6366F1) throughout
- **Rounded corners** and smooth shadows
- **Intuitive navigation** with custom pill-shaped bottom bar
- **Dynamic stats** showing real-time completion data
- **Responsive animations** and transitions
- **Accessibility-focused** with proper contrast and touch targets

---

## 🏗️ Architecture

Built with modern Android development best practices:

```
MVVM + Clean Architecture
├── Presentation Layer (Jetpack Compose)
│   ├── ViewModels (State management)
│   └── UI Components (Reusable composables)
├── Domain Layer
│   ├── Use Cases (Business logic)
│   ├── Models (Data classes)
│   └── Repository Interfaces
└── Data Layer
    ├── Room Database (Local storage)
    ├── Repository Implementations
    └── WorkManager (Background tasks)
```

### Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines + Flow
- **DI**: Manual dependency injection
- **Notifications**: WorkManager
- **Design**: Material 3

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK 34
- Minimum Android 8.0 (API 26)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/life-admin.git
   cd life-admin
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Run the app**
   - Connect your Android device or start an emulator
   - Click the "Run" button (▶️) or press `Shift + F10`
   - The app will build and install automatically

### Build from Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

---

## 📱 Screenshots

### Home Screen
- Dashboard with today's tasks
- Quick action buttons
- Mini calendar widget
- Completion stats

### Add Reminder
- Beautiful form with smart validation
- Type selector with emoji icons
- Quick date selection
- Recurrence options

### Calendar View
- Full month calendar
- Task indicators
- Swipe between months

### Statistics
- Weekly completion charts
- Achievement badges
- Streak tracking

---

## 🗂️ Project Structure

```
app/src/main/kotlin/com/lifeadmin/app/
├── core/                    # Infrastructure
│   ├── database/            # Room setup, DAOs, entities
│   ├── di/                  # Dependency injection
│   ├── notifications/       # WorkManager notifications
│   └── util/                # Utilities and extensions
├── data/                    # Data layer
│   ├── local/               # Local data sources
│   └── repository/          # Repository implementations
├── domain/                  # Domain layer
│   ├── model/               # Domain models
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Business logic
├── features/                # Feature modules
│   ├── home/                # Home dashboard
│   ├── additem/             # Add/edit reminder
│   ├── alarm/               # Reminders list
│   ├── calendar/            # Calendar view
│   ├── search/              # Search functionality
│   ├── statistics/          # Stats and charts
│   └── settings/            # Settings and backup
├── navigation/              # Navigation setup
└── ui/theme/                # Design system
```

---

## 🎯 Roadmap

### Current Version (v1.0)
- ✅ Core reminder functionality
- ✅ Recurring reminders
- ✅ Notifications
- ✅ Calendar view
- ✅ Search
- ✅ Backup/restore
- ✅ Dark mode

### Upcoming (v1.1)
- [ ] Home screen widgets
- [ ] Custom categories
- [ ] Attachment support
- [ ] Voice input
- [ ] Enhanced statistics

### Future (v2.0+)
- [ ] Cloud sync (optional)
- [ ] Wear OS companion
- [ ] Multi-language support
- [ ] Tablet layouts
- [ ] Custom themes

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### How to Contribute
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Write clean, readable code

---

## � License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

Built with ❤️ using Kotlin and Jetpack Compose

---

## 🙏 Acknowledgments

- Material 3 Design System
- Android Jetpack Libraries
- Kotlin Coroutines
- Room Persistence Library

---

## 📧 Contact

For questions or feedback, please open an issue on GitHub.

---

**Never forget what matters.** 🎯
