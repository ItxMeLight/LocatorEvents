LocatorEvent is a highly customizable Minecraft plugin for **PaperMC 1.21.4+** (Java 21) that creates timed server-wide events where player visibility on maps is enabled. It features a dynamic BossBar, PlaceholderAPI support, and a robust scheduling system.

## 🎯 Features

- **Timed Random Events**: Automatically starts events at random intervals.
- **Locator Map Visibility**: Enables player markers on maps globally during events.
- **Dynamic BossBar**: Shows remaining time with progress tracking.
- **Customizable UI**: Support for titles, sounds, and particle effects.
- **PlaceholderAPI Support**: Use custom placeholders in messages and BossBars.
- **Configurable**: Fine-tune timings, worlds (whitelist/blacklist), and aesthetics.

## 🛠 Placeholders

- `%locatorevent_state%`: Current state of the event (`ACTIVE` or `INACTIVE`).
- `%locatorevent_time_left%`: Formatted time remaining (e.g., `mm:ss` or `hh:mm:ss`).
- Supports standard PAPI placeholders like `%player_name%` in BossBars and messages.

## 📜 Commands & Permissions

- `/locatorevent reload`: Reloads the plugin configuration. (`locatorevent.admin`)
- `/locatorevent start`: Manually force-starts an event. (`locatorevent.admin`)
- `/locatorevent stop`: Manually force-stops an event. (`locatorevent.admin`)

---

## 🚀 How to Build and Get the JAR

To compile the plugin from source and obtain the usable `.jar` file, follow these steps:

### Prerequisites

1.  **Java Development Kit (JDK) 21**: Ensure you have Java 21 installed.
2.  **Apache Maven**: You need Maven installed to handle dependencies and build the project.

### Build Instructions

1.  **Clone or download** the repository to your local machine.
2.  Open a terminal or command prompt in the project root directory (where `pom.xml` is located).
3.  Run the following command:
    ```bash
    mvn clean package
    ```
4.  Maven will download the necessary dependencies (Paper API, PlaceholderAPI, etc.) and compile the source code.
5.  Once the build is successful (`BUILD SUCCESS`), navigate to the `target/` directory.
6.  You will find the final plugin file:
    - **`LocatorEvent-1.0.0.jar`**: This is the shaded JAR ready to be dropped into your server's `plugins/` folder.

### Installation

1.  Copy the `LocatorEvent-1.0.0.jar` from the `target/` folder.
2.  Paste it into your Minecraft server's `plugins/` directory.
3.  Restart your server or use a plugin loader to enable it.
4.  The default `config.yml` will be generated upon first run.
