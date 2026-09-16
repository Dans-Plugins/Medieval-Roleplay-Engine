# Medieval Roleplay Engine

## Description
Medieval Roleplay Engine is a Minecraft plugin for players to use to roleplay more effectively. It currently supports Character Cards, Birds, Emotes, Dice, and different types of chats including local, global, whisper, yell, and local out-of-character.

## Installation

### First Time Installation

1. Download the plugin from [SpigotMC](https://www.spigotmc.org/resources/medieval-roleplay-engine.79993/).
2. Place the jar in the `plugins` folder of your server.
3. Restart your server.

### Optional Integrations

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) – enables placeholder support for character card data (see [Using Placeholders](USER_GUIDE.md#using-placeholders)).
- [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) – companion plugin for faction-based roleplay.
- [Mailboxes](https://github.com/Dans-Plugins/Mailboxes) – companion plugin, declared as a soft dependency. The bird (mail) system works without it.

## Usage

### Documentation

- [User Guide](USER_GUIDE.md) – Getting started and common scenarios
- [Commands Reference](COMMANDS.md) – Complete list of all commands
- [Configuration Guide](CONFIG.md) – Detailed configuration options

### Wiki & Additional Resources

- [Wiki Guide](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/wiki/Guide)
- [FAQ](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/wiki/FAQ)

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues/new?labels=bug).

- [Known Bugs](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues?q=is%3Aopen+is%3Aissue+label%3Abug)

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)
- [Notes for Developers](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/wiki/Developer-Notes)

## Testing

This project does not yet have an automated unit test suite. For manual testing, use the Docker-based development server described below.

## Development

### Test Server with Plugin Hot-Reloading

A Docker-based test server is available for development.

#### Setup

1. Build the plugin: `mvn package` (this project has no Maven wrapper, so Maven must be installed; the build targets Java 8, which is also what CI uses)
2. Start the test server: `./up.sh`

#### Stopping the Test Server

```
./down.sh
```

## Authors and Acknowledgement

### Developers

| Name | Main Contributions |
|------|-------------------|
| DanTheTechMan | Creator |
| UndeadZeratul | Created a potential replacement for the /roll command |
| Caibinus | Implemented PlaceholderAPI integration |

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE) (GPL-3.0).

You are free to use, modify, and distribute this software, provided that:
- Source code is made available under the same license when distributed.
- Changes are documented and attributed.
- No additional restrictions are applied.

See the [LICENSE](LICENSE) file for the full text of the GPL-3.0 license.

## Project Status

This project is in active development.

### bStats

You can view the bStats page for the plugin [here](https://bstats.org/plugin/bukkit/Medieval%20Roleplay%20Engine/8996).

## Usage reporting

Usage reporting is on by default: when the plugin is enabled, and each time one of its commands is used, it sends its name, version and the command's name (`startup` and `command` events) to https://trace.danielstephenson.dev so it is known which plugins are actually in use. Nothing about players, worlds, IPs or the server is sent, and nothing typed after a command. The plugin says on every startup whether reporting is on. To turn it off:

- `usage-reporting.enabled: false` in this plugin's `config.yml` (or `/rpconfig set usage-reporting.enabled false`; either way it takes effect on the next restart)
- for every plugin on the server that reports to trace: `enabled: false` in `plugins/trace/config.yml` (written by the first such plugin to start)
- the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1`

Details: https://github.com/Stephenson-Software/trace#usage-reporting

## Roadmap

- [Planned Features](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues?q=is%3Aopen+is%3Aissue+label%3Aimprovement)

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a release-by-release summary of changes.
