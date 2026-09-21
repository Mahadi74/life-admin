# Contributing to Life Admin

Thank you for your interest in contributing to Life Admin! 🎉

---

## 🌟 How to Contribute

### Ways to Contribute
- 🐛 Report bugs
- 💡 Suggest features
- 📝 Improve documentation
- 🔧 Fix issues
- ✨ Add new features
- 🧪 Write tests
- 🎨 Improve UI/UX

---

## 🚀 Getting Started

### 1. Fork & Clone
```bash
# Fork the repo on GitHub
# Then clone your fork
git clone https://github.com/YOUR_USERNAME/life-admin.git
cd life-admin
```

### 2. Set Up Development Environment
Follow [QUICK_START.md](QUICK_START.md) to get the app running.

### 3. Create a Branch
```bash
# Create a feature branch
git checkout -b feature/amazing-feature

# Or a bugfix branch
git checkout -b bugfix/fix-notification-issue
```

### 4. Make Your Changes
- Write clean, readable code
- Follow the existing code style
- Add comments for complex logic
- Update documentation if needed

### 5. Test Your Changes
```bash
# Run unit tests
./gradlew test

# Run lint
./gradlew lint

# Test on a real device
./gradlew installDebug
```

### 6. Commit Your Changes
```bash
# Stage your changes
git add .

# Commit with a clear message
git commit -m "Add: New feature for XYZ"
# Or
git commit -m "Fix: Notification not showing on Android 14"
```

### 7. Push & Create Pull Request
```bash
# Push to your fork
git push origin feature/amazing-feature

# Then create a Pull Request on GitHub
```

---

## 📝 Code Style Guidelines

### Kotlin Style
Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
class HomeViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadReminders() {
        viewModelScope.launch {
            // Implementation
        }
    }
}

// ❌ Bad
class HomeViewModel(private val reminderRepository:ReminderRepository):ViewModel(){
    var uiState = MutableStateFlow(HomeUiState())
    fun loadReminders(){
        // Implementation
    }
}
```

### Compose Style
```kotlin
// ✅ Good - Clear, organized, properly spaced
@Composable
fun ReminderCard(
    reminder: ReminderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.medium)
        ) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// ❌ Bad - Cramped, hard to read
@Composable
fun ReminderCard(reminder:ReminderItem,onClick:()->Unit,modifier:Modifier=Modifier){
    Card(modifier=modifier.fillMaxWidth(),onClick=onClick){
        Column(modifier=Modifier.padding(Dimensions.medium)){
            Text(text=reminder.title,style=MaterialTheme.typography.titleMedium)
        }
    }
}
```

### Naming Conventions
- **Classes**: PascalCase (`HomeViewModel`, `ReminderRepository`)
- **Functions**: camelCase (`loadReminders`, `calculateNextDate`)
- **Variables**: camelCase (`uiState`, `reminderList`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`)
- **Private properties**: Start with underscore (`_uiState`, `_isLoading`)

### File Organization
```kotlin
// 1. Package declaration
package com.lifeadmin.app.features.home

// 2. Imports (grouped and sorted)
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

// 3. Class documentation
/**
 * ViewModel for the home screen
 * Manages reminder loading and state
 */
class HomeViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    // 4. Properties
    // 5. Init block
    // 6. Public methods
    // 7. Private methods
}
```

---

## 🏗️ Architecture Guidelines

### Follow MVVM + Clean Architecture

```
UI (Compose)
    ↓
ViewModel (StateFlow)
    ↓
Use Case (Business Logic)
    ↓
Repository (Interface)
    ↓
Repository Impl (Data Access)
    ↓
DAO / Data Source
```

### Key Principles
1. **Single Responsibility**: Each class has one job
2. **Dependency Inversion**: Depend on abstractions (interfaces)
3. **Immutability**: Use `val` over `var`, immutable data classes
4. **Reactive**: Use Flow for data streams
5. **Lifecycle Aware**: Collect Flows with lifecycle

### Example: Adding a New Feature

```kotlin
// 1. Create domain model (domain/model/)
data class NewFeature(
    val id: Long,
    val name: String
)

// 2. Create repository interface (domain/repository/)
interface NewFeatureRepository {
    fun observeAll(): Flow<List<NewFeature>>
    suspend fun create(feature: NewFeature)
}

// 3. Create repository implementation (data/repository/)
class NewFeatureRepositoryImpl(
    private val dao: NewFeatureDao
) : NewFeatureRepository {
    override fun observeAll() = dao.observeAll().map { it.toDomain() }
    override suspend fun create(feature: NewFeature) = dao.insert(feature.toEntity())
}

// 4. Create ViewModel (features/newfeature/)
class NewFeatureViewModel(
    private val repository: NewFeatureRepository
) : ViewModel() {
    val uiState = repository.observeAll()
        .map { NewFeatureUiState(items = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NewFeatureUiState())
}

// 5. Create UI (features/newfeature/)
@Composable
fun NewFeatureScreen(
    viewModel: NewFeatureViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    // UI implementation
}
```

---

## 🧪 Testing Guidelines

### Write Tests For
- ✅ Date calculations (edge cases)
- ✅ Recurrence logic
- ✅ Use cases
- ✅ Repositories
- ✅ ViewModels (optional but recommended)

### Test Example
```kotlin
class CalculateNextOccurrenceUseCaseTest {
    private lateinit var useCase: CalculateNextOccurrenceUseCase
    
    @Before
    fun setup() {
        useCase = CalculateNextOccurrenceUseCase()
    }
    
    @Test
    fun `monthly recurrence on Jan 31 should return Feb 28 in non-leap year`() {
        // Given
        val currentDate = LocalDate.of(2023, 1, 31)
        val recurrence = RecurrenceType.MONTHLY
        
        // When
        val result = useCase.execute(currentDate, recurrence)
        
        // Then
        assertEquals(LocalDate.of(2023, 2, 28), result)
    }
}
```

---

## 📚 Documentation Guidelines

### Code Comments
```kotlin
// ✅ Good - Explains WHY, not WHAT
/**
 * Calculates the next occurrence date for a recurring reminder.
 * Handles edge cases like month-end dates (Jan 31 → Feb 28).
 */
fun calculateNextDate(current: LocalDate, type: RecurrenceType): LocalDate

// ❌ Bad - States the obvious
/**
 * Adds one month to the date
 */
fun addMonth(date: LocalDate): LocalDate
```

### Update Documentation
If your change affects:
- **User features**: Update README.md
- **Developer setup**: Update QUICK_START.md
- **Project status**: Update DEVELOPMENT_STATUS.md
- **Phase completion**: Create/update verification doc

---

## 🐛 Bug Reports

### Before Reporting
1. Check if it's already reported (Issues page)
2. Reproduce the bug consistently
3. Test on latest version

### Bug Report Template
```markdown
**Description**
Clear description of the bug

**Steps to Reproduce**
1. Go to Home screen
2. Tap Add button
3. Select Bill type
4. ...

**Expected Behavior**
What should happen

**Actual Behavior**
What actually happens

**Environment**
- Device: Pixel 6
- Android Version: 14
- App Version: 1.0.0

**Screenshots**
If applicable

**Logs**
```
[Paste relevant logs from Logcat]
```
```

---

## 💡 Feature Requests

### Feature Request Template
```markdown
**Feature Description**
Clear description of the feature

**Problem It Solves**
What user problem does this address?

**Proposed Solution**
How would it work?

**Alternatives Considered**
Any other approaches?

**Additional Context**
Screenshots, mockups, examples from other apps
```

---

## ✅ Pull Request Guidelines

### Before Submitting
- [ ] Code builds successfully
- [ ] Tests pass (`./gradlew test`)
- [ ] Lint checks pass (`./gradlew lint`)
- [ ] Tested on physical device
- [ ] Documentation updated
- [ ] Commit messages are clear

### PR Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Tested on device
- [ ] Tested edge cases

## Screenshots (if applicable)
Before/after screenshots

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Commented hard-to-understand areas
- [ ] Documentation updated
- [ ] No new warnings
```

---

## 🏆 Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Given credit in app (if significant contribution)

---

## 📜 Code of Conduct

### Our Standards
- ✅ Be respectful and inclusive
- ✅ Accept constructive criticism
- ✅ Focus on what's best for the project
- ✅ Show empathy towards others

### Not Acceptable
- ❌ Harassment or discriminatory language
- ❌ Trolling or insulting comments
- ❌ Personal or political attacks
- ❌ Publishing others' private information

---

## 🎯 Priority Areas

### High Priority (Help Needed!)
- 🧪 **Testing** (Phase 16): Write unit tests for date logic
- 🎨 **Icons** (Phase 17): Design custom icons
- ♿ **Accessibility**: Test with TalkBack, improve

### Medium Priority
- 📝 **Documentation**: Improve guides and examples
- 🌍 **Internationalization**: Add multi-language support
- 🎭 **Themes**: Add more color schemes

### Future Enhancements
- 📱 **Widgets**: Home screen widgets
- ⌚ **Wear OS**: Companion app
- 🖥️ **Desktop**: Kotlin Multiplatform
- 🌐 **Web**: Progressive Web App

---

## 📞 Contact

- **Issues**: [GitHub Issues](https://github.com/YOUR_USERNAME/life-admin/issues)
- **Discussions**: [GitHub Discussions](https://github.com/YOUR_USERNAME/life-admin/discussions)
- **Email**: TBD

---

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project (TBD).

---

**Thank you for contributing to Life Admin!** 🎉

Every contribution, no matter how small, helps make the app better for everyone.

*Never forget what matters.*
