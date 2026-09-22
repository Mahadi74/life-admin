# Release Build Guide

This guide explains how to create a production-ready release build of Life Admin.

## Prerequisites

- Android Studio installed
- JDK 17 or higher
- Release keystore file (instructions below)

## Step 1: Generate Release Keystore

If you don't have a release keystore yet, generate one:

```bash
keytool -genkey -v -keystore release-keystore.jks \
  -alias life-admin-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD
```

**Important:**
- Store this keystore file securely - you cannot update your app without it!
- Back up the keystore file to multiple secure locations
- Never commit the keystore to version control
- Use strong passwords for both store and key

When prompted, fill in the certificate information:
- First and Last Name: Your name or company name
- Organization Unit: Your department/team
- Organization: Your company
- City/Locality: Your city
- State/Province: Your state
- Country Code: Two-letter country code (e.g., US, UK, BD)

## Step 2: Configure Keystore Properties

1. Copy the template file:
   ```bash
   cp keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` with your actual values:
   ```properties
   storeFile=release-keystore.jks
   storePassword=YOUR_ACTUAL_STORE_PASSWORD
   keyAlias=life-admin-key
   keyPassword=YOUR_ACTUAL_KEY_PASSWORD
   ```

3. Place your `release-keystore.jks` file in the project root directory

**Security Note:** The `keystore.properties` file is already in `.gitignore` and will NOT be committed to Git.

## Step 3: Update Production Configuration

Before building for production, update these files:

### 1. AdMob IDs (if using ads)

Edit `app/build.gradle.kts` and replace test AdMob IDs:

```kotlin
buildConfigField("String", "ADMOB_BANNER_ID", "\"YOUR_ACTUAL_ADMOB_BANNER_ID\"")
buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"YOUR_ACTUAL_ADMOB_INTERSTITIAL_ID\"")
```

### 2. AndroidManifest.xml

Update the AdMob app ID in `app/src/main/AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY" />
```

## Step 4: Build Release APK/AAB

### Option A: Build APK (for testing)

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### Option B: Build AAB (for Play Store)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

**Note:** Google Play requires AAB format for new apps.

## Step 5: Test Release Build

Before publishing, thoroughly test the release build:

### Install Release APK

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Test Checklist

- [ ] App launches successfully
- [ ] All features work (CRUD operations, notifications, search, calendar, backup/restore)
- [ ] ProGuard hasn't broken any functionality
- [ ] Notifications are delivered correctly
- [ ] No crashes or unexpected behavior
- [ ] Performance is acceptable
- [ ] App size is reasonable

### Test on Multiple Devices

Test on different:
- Android versions (API 26-34)
- Screen sizes (phone, tablet)
- Manufacturers (Samsung, Google, etc.)

## Step 6: Verify APK/AAB

### Check APK signature

```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

### Analyze APK size

```bash
./gradlew analyzeReleaseBundle
```

Or use Android Studio: **Build → Analyze APK**

## Step 7: Prepare Play Store Assets

Before uploading to Play Store, prepare:

### Required Assets

1. **App Icon** (512x512 PNG)
   - Already created in `app/src/main/res/drawable/ic_launcher_foreground.xml`
   - Export as 512x512 PNG for Play Store

2. **Feature Graphic** (1024x500 PNG)
   - Create promotional banner

3. **Screenshots**
   - At least 2 phone screenshots (1080x1920 or higher)
   - Recommended: 4-8 screenshots showing key features
   - Optional: Tablet screenshots

4. **Privacy Policy**
   - Already created: `PRIVACY_POLICY.md`
   - Host it online (GitHub Pages, your website, etc.)

5. **App Description**
   - Short description (80 characters max)
   - Full description (4000 characters max)
   - See `README.md` for content ideas

## Step 8: Upload to Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app or select existing app
3. Navigate to **Production** → **Create new release**
4. Upload `app-release.aab`
5. Fill in release notes
6. Complete all required sections (content rating, target audience, etc.)
7. Submit for review

## Versioning

When preparing updates, increment version in `app/build.gradle.kts`:

```kotlin
versionCode = 2      // Increment by 1 for each release
versionName = "1.0.1"  // Semantic versioning
```

**Version Code Rules:**
- Must be greater than previous release
- Integer, typically incremented by 1
- Cannot be reused

**Version Name Best Practices:**
- Follow semantic versioning: MAJOR.MINOR.PATCH
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes

## Troubleshooting

### Build fails with "Task assembleRelease failed"

Check ProGuard rules in `proguard-rules.pro`. Common fixes:
```proguard
-keep class com.lifeadmin.app.** { *; }
```

### App crashes after ProGuard

Add keep rules for classes causing issues:
```proguard
-keep class com.your.problematic.Class { *; }
```

### Signature verification fails

Ensure you're using the correct keystore and passwords.

### Play Store rejects APK

- Check minimum API level (26)
- Verify target API level (34)
- Ensure all required permissions are declared
- Check for policy violations

## Security Best Practices

1. **Never commit keystore files to Git**
2. **Store keystore backups securely** (encrypted cloud storage, USB drive in safe)
3. **Use strong, unique passwords**
4. **Document keystore location and passwords** in secure password manager
5. **Share credentials only via secure channels** (never email/Slack)
6. **Rotate API keys regularly**

## Support

For issues or questions:
- GitHub Issues: https://github.com/Mahadi74/life-admin/issues
- Email: [Your support email]

## References

- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [Upload to Play Store](https://support.google.com/googleplay/android-developer/answer/9859152)
- [ProGuard](https://developer.android.com/studio/build/shrink-code)
- [Play Console Help](https://support.google.com/googleplay/android-developer)
