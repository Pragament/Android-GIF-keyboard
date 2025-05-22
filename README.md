# Android GIF Keyboard

## Project Description
Android GIF Keyboard is an open-source Android keyboard app that allows users to search, select, and send GIFs directly from their keyboard. The project aims to provide a seamless and fun typing experience with easy GIF integration.

## Features
- Search and browse trending GIFs
- Insert GIFs into any text field
- Lightweight and responsive UI
- Customizable keyboard themes
- Support for multiple languages

## Project Overview for New Contributors

This project is structured as a typical Android application. Below is an overview of the main folders and files:

- **/app/**: Contains the main Android application source code.
  - **/src/main/java/**: Java source files for activities, fragments, adapters, and utility classes.
  - **/src/main/res/**: Resources such as layouts, drawables, and strings.
  - **/src/main/AndroidManifest.xml**: App manifest file.
- **/gradle/**: Gradle wrapper files for consistent build environment.
- **/build.gradle**: Project-level Gradle build configuration.
- **/settings.gradle**: Project settings.
- **/README.md**: This documentation file.

**Architecture Overview:**
- The app follows the standard Android architecture, with Activities and Fragments handling UI and user interactions.
- GIF search and retrieval are handled via network requests to a GIF provider API (e.g., Giphy).
- Data is managed using ViewModels and LiveData for reactive UI updates.
- There is no separate backend or database; all data is fetched live from the API.

**Component Interaction:**
- The keyboard UI interacts with the GIF API to fetch and display GIFs.
- User selections are inserted into the current text field via Android's input method framework.

## Getting Started

### Prerequisites
- Android Studio (latest stable version recommended)
- JDK 8–16 (JDK 11 recommended)
- Gradle 7.0.2 (handled via wrapper)

### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/Android-GIF-keyboard.git
   cd Android-GIF-keyboard
   ```

2. **Open in Android Studio:**
   - Select "Open an existing project" and choose the cloned folder.

3. **Configure JDK:**
   - Ensure your JDK is set to version 8–16 (JDK 11 recommended).
   - You can set the JDK path in `org.gradle.java.home` in `gradle.properties` if needed.

4. **Install dependencies:**
   - Android Studio will automatically sync and download dependencies via Gradle.

5. **Run the app:**
   - Connect an Android device or start an emulator.
   - Click "Run" in Android Studio.

## Roadmap

- [ ] Improve GIF search performance
- [ ] Add support for more GIF providers
- [ ] Enhance keyboard customization options
- [ ] Implement user favorites and history
- [ ] Add unit and UI tests

## Contributing

We welcome contributions from everyone! To get started:

1. Fork the repository and create your branch from `main`.
2. Make your changes with clear commit messages.
3. Test your changes thoroughly.
4. Submit a pull request with a description of your changes.

Please read our [CONTRIBUTING.md](CONTRIBUTING.md) for more details.

## Troubleshooting

- If you encounter Gradle or JDK errors, ensure your JDK version matches the required range.
- Use `--warning-mode all` with Gradle to see deprecation warnings.

## References

- [Gradle User Guide](https://docs.gradle.org/7.0.2/userguide/command_line_interface.html#sec:command_line_warnings)
- [Android Developer Documentation](https://developer.android.com/docs)

