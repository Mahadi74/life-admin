# Life Admin - Quick Start Guide

Get the app running in 5 minutes! 🚀

---

## Prerequisites

Before you start, make sure you have:
- ✅ Android Studio Hedgehog (2023.1.1) or newer
- ✅ JDK 17
- ✅ Android SDK 34
- ✅ An Android device or emulator (API 26+)

---

## Step 1: Clone & Open (1 min)

```bash
# Clone the repository
cd /Users/expert/projects/game/game/life-admin

# Open in Android Studio
# File → Open → Select the 'life-admin' folder
```

---

## Step 2: Sync Gradle (2 min)

Android Studio will automatically prompt you to sync Gradle. If not:

1. Click **File → Sync Project with Gradle Files**
2. Wait for dependencies to download (~2-3 minutes)
3. Look for "BUILD SUCCESSFUL" in the Build Output

### Common Issues
- **SDK not found**: Install Android SDK 34 via SDK Manager
- **JDK version**: Ensure you're using JDK 17 (File → Project Structure → SDK Location)
- **KSP errors**: Clean project (Build → Clean Project) and rebuild

---

## Step 3: Run the App (2 min)

### On a Physical Device
1. Enable Developer Mode on your Android device
2. Enable USB Debugging
3. Connect via USB
4. Click the green **Run** button (▶️) in Android Studio
5. Select your device
6. App will install and launch!

### On an Emulator
1. Open AVD Manager (Tools → Device Manager)
2. Create a device (Pixel 6, API 34 recommended)
3. Click **Run** button (▶️)
4. Select your emulator
5. Wait for emulator to boot (~30 seconds)
6. App will launch!

---

## Step 4: Explore the App

### What You'll See

1. **First Launch**:
   - Empty state with "Let's get started" message
   - Tap the **Add Reminder** FAB (blue button)

2. **Create Your First Reminder**:
   - Select a type (Bill, Subscription, etc.)
   - Fill in title (e.g., "Electric Bill")
   - Set date and time
   - Choose recurrence if needed
   - Tap **Save**

3. **View Your Reminder**:
   - See it on the Home screen
   - Tap to open details
   - Try actions: Complete, Snooze, Edit, etc.

4. **Try Other Features**:
   - **Search**: Tap search icon in top bar
   - **Calendar**: Tap calendar icon
   - **Settings**: Tap settings icon → Export/Import

---

## Step 5: Understanding the Code (Optional)

### Key Files to Explore

```
app/src/main/kotlin/com/lifeadmin/app/

📱 UI Screens (Start Here!)
├── features/home/HomeScreen.kt          # Main screen
├── features/search/SearchScreen.kt      # Search & filters
├── features/calendar/CalendarScreen.kt  # Calendar view
├── features/itemdetails/ItemDetailsScreen.kt  # Item details
├── features/additem/AddItemSheet.kt     # Add/Edit form
└── features/settings/SettingsScreen.kt  # Settings

🧠 ViewModels (Business Logic)
├── features/home/HomeViewModel.kt
├── features/search/SearchViewModel.kt
├── features/calendar/CalendarViewModel.kt
├── features/itemdetails/ItemDetailsViewModel.kt
└── features/additem/AddItemViewModel.kt

💾 Database (Data Layer)
├── core/database/LifeAdminDatabase.kt   # Room database
├── core/database/dao/ReminderDao.kt     # Reminder queries
├── data/local/entity/ReminderEntity.kt  # Reminder table
└── data/repository/ReminderRepositoryImpl.kt  # Repository

🔔 Notifications
├── core/notifications/NotificationScheduler.kt  # Schedule logic
├── core/notifications/NotificationWorker.kt     # Worker
└── domain/usecase/CalculateNextOccurrenceUseCase.kt  # Recurrence

🎨 Design System
├── ui/theme/Color.kt        # Color scheme
├── ui/theme/Type.kt         # Typography
├── ui/theme/Dimensions.kt   # Spacing
└── ui/theme/Theme.kt        # Theme setup
```

### Architecture Overview

```
HomeScreen (Compose UI)
    ↓
HomeViewModel (StateFlow)
    ↓
ReminderRepository (Interface)
    ↓
ReminderRepositoryImpl (Implementation)
    ↓
ReminderDao (Room)
    ↓
SQLite Database
```

---

## Common Tasks

### Run Tests
```bash
./gradlew test
```

### Check Code Quality
```bash
./gradlew lint
```

### Clean Build
```bash
./gradlew clean assembleDebug
```

### Generate Release APK
```bash
./gradlew assembleRelease
```

### View Database (Debug)
```bash
# Using ADB
adb shell
run-as com.lifeadmin.app.debug
cd databases
sqlite3 life_admin.db
.tables
SELECT * FROM reminders;
```

---

## Debugging Tips

### Enable Debug Logging
In `AndroidManifest.xml`:
```xml
<application
    android:debuggable="true"
    ...>
```

### View Logs
```bash
# Filter by app
adb logcat | grep "LifeAdmin"

# View Room queries
adb logcat | grep "RoomDatabase"

# View WorkManager
adb logcat | grep "WM-"
```

### Inspect Compose Layout
Tools → Layout Inspector → Select your device

### Database Inspector
View → Tool Windows → App Inspection → Database Inspector

---

## Testing Features

### Test Notifications
1. Create a reminder with due date in 1 minute
2. Wait 1 minute (keep app open)
3. Notification should appear
4. Try killing the app → notification still appears

### Test Recurring
1. Create a monthly recurring reminder
2. Mark it complete
3. Check Home → new occurrence created automatically
4. Check calendar → dots appear on both dates

### Test Search
1. Create 3-4 reminders with different titles
2. Go to Search
3. Type partial title → results appear instantly
4. Try filters → results update

### Test Backup/Restore
1. Create a few reminders
2. Go to Settings → Export Backup
3. Note the file path
4. Go to Settings → Import Backup
5. Select file, choose "Merge"
6. Verify reminders are preserved

---

## Troubleshooting

### App Doesn't Build
**Issue**: Gradle sync fails  
**Solution**: 
```bash
./gradlew clean
./gradlew --refresh-dependencies
```

### Notifications Don't Work
**Issue**: Notifications not appearing  
**Solution**:
1. Check device settings → Enable notifications for app
2. Check battery optimization → Disable for app
3. Check Do Not Disturb mode

### Database Errors
**Issue**: Room schema errors  
**Solution**:
```bash
# Clear app data
adb shell pm clear com.lifeadmin.app.debug
# Reinstall
./gradlew installDebug
```

### UI Not Updating
**Issue**: UI doesn't reflect data changes  
**Solution**:
- Check ViewModel is collecting Flow
- Check StateFlow is being updated
- Check Compose recomposition (use Layout Inspector)

### Date Calculations Wrong
**Issue**: Recurring dates incorrect  
**Solution**:
- Check device timezone settings
- Check `CalculateNextOccurrenceUseCase` logic
- Add breakpoints and debug

---

## Next Steps

### For Developers
1. ✅ Read [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md) for progress
2. ✅ Check [README.md](README.md) for architecture details
3. ✅ Browse phase verification docs for feature details
4. ✅ Start contributing! See remaining phases

### For Testers
1. ✅ Create reminders of different types
2. ✅ Test recurring patterns
3. ✅ Test notifications (wait for them)
4. ✅ Try search and calendar
5. ✅ Test backup/restore
6. ✅ Report bugs or suggestions

### For Users
1. ✅ Download from Play Store (coming soon!)
2. ✅ Or install APK from releases
3. ✅ Start managing your reminders
4. ✅ Enjoy offline privacy!

---

## Getting Help

### Resources
- **README.md**: Architecture and setup
- **DEVELOPMENT_STATUS.md**: Current progress
- **Phase Verification Docs**: Feature checklists
- **Code Comments**: KDoc throughout codebase

### Common Questions

**Q: How do I add a new item type?**  
A: Edit `domain/model/ItemType.kt` enum, then add UI mapping in `AddItemSheet.kt`

**Q: How do I add a new category?**  
A: Edit `data/local/DefaultCategories.kt`, increment database version, add migration

**Q: How do I change colors?**  
A: Edit `ui/theme/Color.kt` for light/dark color schemes

**Q: How do notifications work?**  
A: WorkManager schedules OneTimeWorkRequest for each reminder. See `NotificationScheduler.kt`

**Q: What about recurring edge cases?**  
A: All handled in `CalculateNextOccurrenceUseCase.kt` with comprehensive logic

---

## Pro Tips

### For Compose UI
- Use `remember` for expensive calculations
- Use `derivedStateOf` for derived state
- Use `LaunchedEffect` for side effects
- Use `collectAsStateWithLifecycle()` for Flows

### For ViewModels
- Expose `StateFlow`, not `MutableStateFlow`
- Use `viewModelScope` for coroutines
- Keep ViewModels testable (inject dependencies)
- Single source of truth via repository

### For Database
- Always use indexed columns in WHERE clauses
- Use `observeXxx()` methods for reactive UI
- Use `Flow` instead of `LiveData`
- Test migrations in instrumented tests

### For Performance
- Use `LazyColumn` for lists (not `Column`)
- Use `key` parameter for stable item identity
- Profile with Layout Inspector
- Generate baseline profiles for release

---

## You're Ready! 🎉

The app should be running now. Explore, test, and enjoy!

**Next**: Read [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md) to see what's been built and what's remaining.

---

**Questions?** Check the documentation or explore the code - it's well-commented and organized!

*Happy coding!* 🚀
