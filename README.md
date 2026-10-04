# Medieval Roleplay Engine

## Description
Medieval Roleplay Engine is a Minecraft plugin for players to use to roleplay more effectively. It currently supports Character Cards, Birds, Emotes, Dice, and different types of chats including local, global, whisper, yell, and local out-of-character.

## Supported Minecraft Versions
This plugin is supported on the Minecraft versions listed in [`minecraft-versions.json`](minecraft-versions.json): currently **1.19.4**, **1.21.11**, **26.2** and **26.3** (Spigot and its forks). Every stable release is booted on a real server of each of these versions before it is published, and every build checks that the plugin only uses Bukkit API that exists on all of them. Other versions from 1.19.4 onwards are expected to work but are not tested. To support another version, add it to the file: both checks pick it up.

## Installation

### First Time Installation

1. Download the plugin from [SpigotMC](https://www.spigotmc.org/resources/medieval-roleplay-engine.79993/).
2. Place the jar in the `plugins` folder of your server.
3. Restart your server.

### Optional Integrations

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) – enables placeholder support for character card data (see [Using Placeholders](USER_GUIDE.md#using-placeholders)).

## Works Well With
Medieval Roleplay Engine is the centre of the **medieval roleplay** set of Dan's Plugins. These plugins suit the same kind of server and run side by side; none of them is required. Medieval Factions and Mailboxes are declared as soft dependencies in `plugin.yml`, which only makes this plugin load after them: Medieval Roleplay Engine does not call into either one.

- [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) ([SpigotMC](https://www.spigotmc.org/resources/medieval-factions.79941/), `/dpm get medievalfactions`): nation-like factions with land claims, diplomacy and laws. Medieval Factions is the companion factions plugin; its add-ons are listed in its [Expansions](https://github.com/Dans-Plugins/Medieval-Factions#expansions) section.
- [Mailboxes](https://github.com/Dans-Plugins/Mailboxes) ([SpigotMC](https://www.spigotmc.org/resources/mailboxes.96611/), `/dpm get mailboxes`): persistent mail between players, with item attachments.
- [Medieval Economy](https://github.com/Dans-Plugins/Medieval-Economy) ([SpigotMC](https://www.spigotmc.org/resources/medieval-economy.81836/), `/dpm get medievaleconomy`): a coinpurse and a physical currency item.
- [PlayerLore](https://github.com/Dans-Plugins/PlayerLore) ([SpigotMC](https://www.spigotmc.org/resources/playerlore.98602/), `/dpm get playerlore`): players write their own lore onto their items.
- [Medieval Cookery](https://github.com/Dans-Plugins/Medieval-Cookery) (no SpigotMC page, no stable release yet): cooking recipes for custom foods, defined by the server owner.
- [Conquest Recipes](https://github.com/Dans-Plugins/Conquest-Recipes) ([SpigotMC](https://www.spigotmc.org/resources/conquest-recipes.83594/), `/dpm get conquestrecipes`): recipes for historical weapons, armour and shields named to match the Conquest resource pack.

For a survival server, the **survival flavour** set goes well alongside: [Food Spoilage](https://github.com/Dans-Plugins/FoodSpoilage), [Wild Pets](https://github.com/Dans-Plugins/Wild-Pets), [SimpleSkills](https://github.com/Dans-Plugins/SimpleSkills).

Every plugin above is listed on [dansplugins.com](https://dansplugins.com). Medieval Roleplay Engine is listed at [dansplugins.com/resources/medieval-roleplay-engine](https://dansplugins.com/resources/medieval-roleplay-engine) and can be installed in game with [Dan's Plugin Manager](https://github.com/Dans-Plugins/Dans-Plugin-Manager): `/dpm get medievalroleplayengine`.

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

Run the automated test suite with `mvn test`. It covers the `plugin.yml` contract — permission nodes, command registration, the `rp.card.*` parent, and the `USER_GUIDE.md` permission table — and runs as part of `mvn package` in CI. See [CONTRIBUTING.md](CONTRIBUTING.md#testing) for what it checks. For manual testing of anything else, use the Docker-based development server described below.

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

Usage reporting is on by default: when the plugin is enabled, and each time one of its commands is used, it sends its name, version and the command's name (`startup` and `command` events) to https://trace.danielstephenson.dev so it is known which plugins are actually in use. Nothing about players, worlds or IPs is sent, and nothing typed after a command. The plugin says on every startup whether reporting is on. Each event also carries a random server ID (the `server-id` line in `plugins/trace/config.yml`) so
servers can be counted rather than events. It identifies no person, account or IP address; delete
the line to get a new one.

To turn it off:

- `usage-reporting.enabled: false` in this plugin's `config.yml` (or `/rpconfig set usage-reporting.enabled false`; either way it takes effect on the next restart)
- for every plugin on the server that reports to trace: `enabled: false` in `plugins/trace/config.yml` (written by the first such plugin to start)
- the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1`

Details: https://github.com/Stephenson-Software/trace#usage-reporting

## Roadmap

- [Planned Features](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues?q=is%3Aopen+is%3Aissue+label%3Aimprovement)

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a release-by-release summary of changes.
