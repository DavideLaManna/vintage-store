# Vintage Store - Android Marketplace App

A modern Android marketplace application for buying and selling secondhand clothing. This app provides a user-friendly platform for secondhand fashion exchange, connecting buyers and sellers in an engaging mobile experience.

## Features

- **User Authentication**: Secure login and registration system
- **Browse Items**: Explore various clothing items with detailed information
- **Search & Filter**: Find items by category, size, brand, or specific search terms
- **User Profiles**: Manage your personal profile and track your items
- **Favorites**: Save items you like for later
- **Messaging**: Direct communication between buyers and sellers
- **Item Management**: Easily list new items for sale with photos and details
- **Checkout Process**: Streamlined purchase experience

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android SDK with minimum API level 24 (Android 7.0 Nougat)
- Java Development Kit (JDK) 17

### Installation

1. Clone this repository
   ```
   git clone https://github.com/your-username/vintage-store.git
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build the project

### Running the App

- **Development Build**: Select the 'app' configuration and run on an emulator or connected device
- **Debug Build**: Run `./gradlew installDebug` from the terminal
- **Release Build**: Run `./gradlew assembleRelease` to generate a signed APK

## Project Structure

- **app/src/main/java/com/example/vintagestore/**
  - **adapter/**: RecyclerView adapters for lists
  - **data/**: Database and repository classes
  - **model/**: Data models (Item, User, Message)
  - **ui/**: Activities and Fragments organized by feature
  - **util/**: Utility classes and helpers

## Building for Release

1. Configure your release signing key in `app/build.gradle`
2. Set environment variables for secure keystore access:
   ```
   export KEYSTORE_PASSWORD=your_keystore_password
   export KEY_ALIAS=your_key_alias
   export KEY_PASSWORD=your_key_password
   ```
3. Run the release build:
   ```
   ./gradlew bundleRelease
   ```
4. The app bundle will be generated in `app/build/outputs/bundle/release/`

## Performance Considerations

- Images are efficiently loaded and cached using Glide
- RecyclerView items use DiffUtil for optimal list updates
- Database operations run on background threads
- Proper view recycling is implemented for smooth scrolling
- Multidex support for large app development

## Security Features

- ProGuard rules for code obfuscation
- Secure storage for user credentials
- HTTPS for all network communications
- Content provider security with proper permissions

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design components for modern UI
- Android Architecture Components for robust app architecture
- Glide for efficient image loading and caching
