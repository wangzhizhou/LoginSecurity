LoginSecurity 3 [![Build Status](https://ci.codemc.io/job/lenis0012/job/LoginSecurity/badge/icon)](https://ci.codemc.io/job/lenis0012/job/LoginSecurity/)
=================
Simple, light, fast and secure user authentication management. Since 2012.  
Now even lighter and faster than before!

# Links
- [Development Builds](https://ci.codemc.org/view/Author/job/lenis0012/job/LoginSecurity/)
- [SpigotMC](https://www.spigotmc.org/resources/loginsecurity.19362/)

# Changes in 3.0
* Lightweight download (over 20x smaller than v2.1)
* Improved performance, resulting in higher tps
* Removed AutoIn support, consider migrating to FastLogin
* Removed deprecated hashing algorithms
* Migrated from mcstats to bstats for statistics
* Block opening inventory while not logged in
* Fix errors when using some NPC plugins (like FakePlayers)
* Add unregister command
* Change updater message format
* Fixed bug where some messages are unintentionally hidden from the log
* Added import command for importing profiles to/from mysql
* Force users to use exactly the same case-sensitive name every time (#85)
* Added password confirmation to register command (#67)
* Added changepassword to admin commands (#104)
* Improved event handling
* Allow other plugins to log users in while they are not registered

# Features
- 6 useful commands to manage your password
- Light, fast and easy to set up
- Secure password storage using industry-standard cryptography
- Protects and hides user's location and inventory
- IP & time-based session continuation
- Straightforward administrative control
- User-friendly captcha system for new players
- Used by thousands of server owners
- Stay secure with automatic update notifications
- Prevents players from getting kicked for being logged in from another location
- 20+ supported languages and more to come

# Installation
```shell script
git clone https://github.com/lenis0012/LoginSecurity-2.git LoginSecurity
cd LoginSecurity
git submodule init
git submodule update
mvn clean install
```

Update changes in the translations repo using `git submodule update --remote src/main/resources/lang`

# Building

This project supports Gradle (Kotlin DSL) and Maven.

- Gradle: `./gradlew build` (uses Gradle Wrapper)
- Maven: `mvn package`

Gradle build shades dependencies and relocates:
- `com.lenis0012.pluginutils` → `com.lenis0012.bukkit.loginsecurity.libs.pluginutils`
- `com.lenis0012.updater` → `com.lenis0012.bukkit.loginsecurity.libs.updater`
- `org.bstats` → `com.lenis0012.bukkit.loginsecurity.libs.bstats`
- `io.papermc.lib` → `com.lenis0012.bukkit.loginsecurity.libs.paper`

- Resource filtering: `plugin.yml` placeholders `${project.name}`, `${project.version}`, `${updater.manifestUrl}` are injected at build time.

# Publishing

Hangar publish via Gradle:

- Set environment `HANGAR_API_KEY` to your Hangar API key
- For snapshots use version with `-SNAPSHOT`, channel `Snapshot` will be used
- For releases use version without `-SNAPSHOT`, channel `Release` will be used
- Commands:
- `./gradlew publishAllPublicationsToHangar`
- `./gradlew publishLoginsecurityPublicationToHangar`
- Optional changelog: `-PhangarChangelog="Removed Herobrine"`

Resource filtering substitutes `plugin.yml` placeholders with Gradle properties.

# Local Dev & Debugging

Run a Paper server with your plugin automatically (via run-task):

- Start server: `./gradlew runServer`
- Override runtime MC: `./gradlew runServer -PpaperRunVersion=1.21.1`
- Default JDWP debug on `*:5005` is configured in Gradle

## Gradle Version & Java Requirements

- Wrapper configured for Gradle `9.x`
- Running with Java `21` toolchain; plugin targets Java `8` bytecode via `--release 8`
- If wrapper jar is missing, generate with a local Gradle: `gradle wrapper --gradle-version 9.0`

## Paper API Version

- Default `paperApiVersion` is `1.21-R0.1-SNAPSHOT`
- Override via: `./gradlew build -PpaperApiVersion=1.21.1-R0.1-SNAPSHOT`

## Paper Compatibility Note

- Paper deprecated `PlayerSpawnLocationEvent`. The plugin avoids listening to this event and masks location on join to ensure safe behavior on Paper.
