package dansplugins.rpsystem.commands.card;

import dansplugins.rpsystem.BukkitTestServer;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardLookupService;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.config.ConfigService;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.storage.StorageService;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import dansplugins.rpsystem.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes the {@code /card} sub-commands as documented in {@code COMMANDS.md}: which permission nodes each one
 * accepts, the usage and error messages players see, which card field each one changes, the {@code /card name}
 * cooldown, and which storage calls {@code forcesave} and {@code forceload} make.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link CardCommand} reads are filled in. The real {@link CardRepository}, {@link EphemeralData} and
 * {@link ArgumentParser} are used; the config, card lookup, player-name lookup, storage and card display are
 * recording stand-ins. The name-change cooldown is scheduled on {@link BukkitTestServer}'s scheduler, which records
 * the task so the test can run it.</p>
 */
public class CardCommandTest {
    private static final ChatColor NEUTRAL = ChatColor.AQUA;
    private static final ChatColor POSITIVE = ChatColor.GREEN;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final CardRepository cardRepository = new CardRepository();
    private final EphemeralData ephemeralData = new EphemeralData();
    private final StubConfigService configService = new StubConfigService();
    private final Map<UUID, CharacterCard> lookupCards = new HashMap<>();
    private final Map<String, UUID> knownPlayers = new HashMap<>();
    private final RecordingMessenger messenger = new RecordingMessenger();
    private final RecordingStorageService storageService = new RecordingStorageService();
    private final List<String> received = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>();

    private MedievalRoleplayEngine plugin;
    private CardCommand cardCommand;
    private Player player;
    private CharacterCard card;

    @Before
    public void setUp() throws Exception {
        BukkitTestServer.clearScheduledTasks();
        plugin = plugin();
        cardCommand = new CardCommand(plugin);
        player = player();
        card = new CharacterCard(plugin, PLAYER_UUID);
        cardRepository.getCards().add(card);
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnoredByEverySubCommand() {
        CommandSender console = console();
        String[] args = {"name", "Aldric"};

        cardCommand.showCard(console, new String[0]);
        cardCommand.showHelpMessage(console);
        cardCommand.showPlayerInfo(console, new String[]{"lookup", "Steve"});
        cardCommand.changeName(console, args);
        cardCommand.changeRace(console, args);
        cardCommand.changeSubculture(console, args);
        cardCommand.changeReligion(console, args);
        cardCommand.changeGender(console, args);
        cardCommand.changeAge(console, new String[]{"age", "30"});
        assertFalse(cardCommand.forceSave(console));
        assertFalse(cardCommand.forceLoad(console));

        assertEquals("defaultName", card.getName());
        assertTrue(messenger.shown.isEmpty());
        assertTrue(storageService.calls.isEmpty());
    }

    @Test
    public void showCardDisplaysTheCardFoundByTheLookupService() {
        permissions.add("rp.card.show");
        CharacterCard lookedUp = new CharacterCard(plugin, PLAYER_UUID);
        lookupCards.put(PLAYER_UUID, lookedUp);

        cardCommand.showCard(player, new String[0]);

        assertEquals(1, messenger.shown.size());
        assertSame(lookedUp, messenger.shown.get(0).card);
        assertSame(player, messenger.shown.get(0).viewer);
        assertTrue(received.isEmpty());
    }

    @Test
    public void showCardWithoutACardSaysSo() {
        permissions.add("rp.card.show");

        cardCommand.showCard(player, new String[0]);

        assertEquals(Collections.singletonList(NEGATIVE + "You do not have a character card to view."), received);
        assertTrue(messenger.shown.isEmpty());
    }

    @Test
    public void everyPlayerSubCommandAcceptsItsOwnNodeTheWildcardOrDefault() {
        String[] subCommands = {"show", "help", "lookup", "name", "race", "subculture", "religion", "gender", "age"};
        for (String subCommand : subCommands) {
            for (String node : Arrays.asList("rp.card." + subCommand, "rp.card.*", "rp.default")) {
                permissions.clear();
                permissions.add(node);
                received.clear();

                run(subCommand);

                assertFalse(subCommand + " with " + node + ": " + received, received.toString().contains("Sorry!"));
            }
        }
    }

    @Test
    public void everyPlayerSubCommandWithoutPermissionNamesItsOwnNode() {
        String[] subCommands = {"show", "help", "lookup", "name", "race", "subculture", "religion", "gender", "age"};
        for (String subCommand : subCommands) {
            received.clear();

            run(subCommand);

            assertEquals(subCommand, Collections.singletonList(NEGATIVE + "Sorry! In order to use this command, you"
                    + " need the following permission: 'rp.card." + subCommand + "'"), received);
        }
        assertEquals("defaultName", card.getName());
        assertEquals(0, card.getAge());
        assertTrue(messenger.shown.isEmpty());
    }

    @Test
    public void helpListsTheEightPlayerSubCommandsButNotTheAdminOnes() {
        permissions.add("rp.card.help");

        cardCommand.showHelpMessage(player);

        assertEquals(Arrays.asList(
                ChatColor.BOLD + "" + NEUTRAL + " == Character Card Commands == ",
                NEUTRAL + "/card - View your character card.",
                NEUTRAL + "/card lookup (player) - View the character card of a specific player.",
                NEUTRAL + "/card name (name) - Change your character's name.",
                NEUTRAL + "/card race (race) - Change your character's race.",
                NEUTRAL + "/card subculture (subculture) - Change your character's subculture.",
                NEUTRAL + "/card age (age) - Change your character's age.",
                NEUTRAL + "/card gender (gender) - Change your character's gender.",
                NEUTRAL + "/card religion (religion) - Change your character's religion."), received);
    }

    @Test
    public void eachTextFieldIsSetFromEveryWordAfterTheSubCommand() {
        permissions.add("rp.default");

        cardCommand.changeName(player, new String[]{"name", "Aldric", "of", "Hale"});
        cardCommand.changeRace(player, new String[]{"race", "High", "Elf"});
        cardCommand.changeSubculture(player, new String[]{"subculture", "Nordic"});
        cardCommand.changeReligion(player, new String[]{"religion", "Old", "Gods"});
        cardCommand.changeGender(player, new String[]{"gender", "Female"});

        assertEquals("Aldric of Hale", card.getName());
        assertEquals("High Elf", card.getRace());
        assertEquals("Nordic", card.getSubculture());
        assertEquals("Old Gods", card.getReligion());
        assertEquals("Female", card.getGender());
        assertEquals(Arrays.asList(
                POSITIVE + "Name set! Type /card to see changes.",
                POSITIVE + "Race set! Type /card to see changes.",
                POSITIVE + "Subculture set! Type /card to see changes.",
                POSITIVE + "Religion set! Type /card to see changes.",
                POSITIVE + "Gender set! Type /card to see changes."), received);
    }

    @Test
    public void eachChangeWithoutAValueShowsItsUsageAndLeavesTheCardAlone() {
        permissions.add("rp.default");

        for (String subCommand : Arrays.asList("name", "race", "subculture", "religion", "gender", "age")) {
            run(subCommand, subCommand);
        }

        assertEquals(Arrays.asList(
                NEGATIVE + "Usage: /card name (character-name)",
                NEGATIVE + "Usage: /card race (character-race)",
                NEGATIVE + "Usage: /card subculture (character-subculture)",
                NEGATIVE + "Usage: /card religion (character-religion)",
                NEGATIVE + "Usage: /card gender (character-gender)",
                NEGATIVE + "Usage: /card age (character-age)"), received);
        assertEquals("defaultName", card.getName());
        assertEquals("defaultRace", card.getRace());
        assertEquals("defaultSubculture", card.getSubculture());
        assertEquals("defaultReligion", card.getReligion());
        assertEquals("defaultGender", card.getGender());
        assertEquals(0, card.getAge());
    }

    @Test
    public void aChangeForAPlayerWithoutACardDoesNothingAndSaysNothing() {
        permissions.add("rp.default");
        cardRepository.getCards().clear();

        for (String subCommand : Arrays.asList("name", "race", "subculture", "religion", "gender", "age")) {
            run(subCommand, subCommand, "30");
        }

        assertTrue(received.toString(), received.isEmpty());
        assertTrue(BukkitTestServer.scheduledTasks().isEmpty());
    }

    @Test
    public void changesEditTheCardInTheRepositoryNotTheLookupServiceCopy() {
        permissions.add("rp.default");
        CharacterCard lookedUp = new CharacterCard(plugin, PLAYER_UUID);
        lookupCards.put(PLAYER_UUID, lookedUp);

        cardCommand.changeRace(player, new String[]{"race", "Dwarf"});

        assertEquals("Dwarf", card.getRace());
        assertEquals("defaultRace", lookedUp.getRace());
    }

    @Test
    public void ageMustBeAWholeNumber() {
        permissions.add("rp.card.age");

        cardCommand.changeAge(player, new String[]{"age", "thirty"});
        cardCommand.changeAge(player, new String[]{"age", "30", "years"});
        cardCommand.changeAge(player, new String[]{"age", "-5"});

        assertEquals(Arrays.asList(
                NEGATIVE + "Must be a number.",
                NEGATIVE + "Must be a number.",
                POSITIVE + "Age set! Type /card to see changes."), received);
        assertEquals(-5, card.getAge());
    }

    @Test
    public void withAZeroCooldownTheNameCanBeChangedAgainAtOnce() {
        permissions.add("rp.card.name");
        configService.changeNameCooldown = 0;

        cardCommand.changeName(player, new String[]{"name", "Aldric"});
        cardCommand.changeName(player, new String[]{"name", "Brenna"});

        assertEquals("Brenna", card.getName());
        assertTrue(ephemeralData.getPlayersOnNameChangeCooldown().isEmpty());
        assertTrue(BukkitTestServer.scheduledTasks().isEmpty());
    }

    @Test
    public void aNameChangeStartsACooldownOfTheConfiguredSecondsInTicks() {
        permissions.add("rp.card.name");
        configService.changeNameCooldown = 300;

        cardCommand.changeName(player, new String[]{"name", "Aldric"});

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersOnNameChangeCooldown());
        List<BukkitTestServer.ScheduledTask> tasks = BukkitTestServer.scheduledTasks();
        assertEquals(1, tasks.size());
        assertSame(plugin, tasks.get(0).plugin);
        assertEquals(6000L, tasks.get(0).delayTicks);
    }

    @Test
    public void duringTheCooldownTheNameIsRefusedEvenBeforeTheUsageIsChecked() {
        permissions.add("rp.card.name");
        configService.changeNameCooldown = 300;
        cardCommand.changeName(player, new String[]{"name", "Aldric"});
        received.clear();

        cardCommand.changeName(player, new String[]{"name", "Brenna"});
        cardCommand.changeName(player, new String[]{"name"});

        assertEquals("Aldric", card.getName());
        assertEquals(Arrays.asList(
                NEGATIVE + "You must wait before changing your name again!",
                NEGATIVE + "You must wait before changing your name again!"), received);
        assertEquals(1, BukkitTestServer.scheduledTasks().size());
    }

    @Test
    public void theCooldownOnlyBlocksNameChanges() {
        permissions.add("rp.default");
        configService.changeNameCooldown = 300;
        cardCommand.changeName(player, new String[]{"name", "Aldric"});

        cardCommand.changeRace(player, new String[]{"race", "Human"});
        cardCommand.changeAge(player, new String[]{"age", "30"});

        assertEquals("Human", card.getRace());
        assertEquals(30, card.getAge());
    }

    @Test
    public void whenTheCooldownEndsThePlayerIsToldAndMayChangeTheirNameAgain() {
        permissions.add("rp.card.name");
        configService.changeNameCooldown = 300;
        cardCommand.changeName(player, new String[]{"name", "Aldric"});
        received.clear();

        BukkitTestServer.scheduledTasks().get(0).task.run();

        assertTrue(ephemeralData.getPlayersOnNameChangeCooldown().isEmpty());
        assertEquals(Collections.singletonList(POSITIVE + "You can now change your character's name again."),
                received);

        received.clear();
        cardCommand.changeName(player, new String[]{"name", "Brenna"});

        assertEquals("Brenna", card.getName());
        assertEquals(Collections.singletonList(POSITIVE + "Name set! Type /card to see changes."), received);
    }

    @Test
    public void lookupWithoutAPlayerNameShowsItsUsage() {
        permissions.add("rp.card.lookup");

        cardCommand.showPlayerInfo(player, new String[]{"lookup"});

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /card lookup (playerName)"), received);
    }

    @Test
    public void lookupOfAnUnknownPlayerSaysSo() {
        permissions.add("rp.card.lookup");

        cardCommand.showPlayerInfo(player, new String[]{"lookup", "Nobody"});

        assertEquals(Collections.singletonList(NEGATIVE + "That player wasn't found."), received);
        assertTrue(messenger.shown.isEmpty());
    }

    @Test
    public void lookupOfAKnownPlayerWithoutACardSaysSo() {
        permissions.add("rp.card.lookup");
        knownPlayers.put("Steve", OTHER_UUID);

        cardCommand.showPlayerInfo(player, new String[]{"lookup", "Steve"});

        assertEquals(Collections.singletonList(NEGATIVE + "That player doesn't have a card."), received);
        assertTrue(messenger.shown.isEmpty());
    }

    @Test
    public void lookupShowsTheOtherPlayersCardToTheSender() {
        permissions.add("rp.card.lookup");
        knownPlayers.put("Steve the Bold", OTHER_UUID);
        CharacterCard otherCard = new CharacterCard(plugin, OTHER_UUID);
        lookupCards.put(OTHER_UUID, otherCard);

        cardCommand.showPlayerInfo(player, new String[]{"lookup", "Steve", "the", "Bold"});

        assertEquals(1, messenger.shown.size());
        assertSame(otherCard, messenger.shown.get(0).card);
        assertSame(player, messenger.shown.get(0).viewer);
        assertTrue(received.isEmpty());
    }

    @Test
    public void forceSaveWritesTheFileListThenTheCards() {
        for (String node : Arrays.asList("rp.card.forcesave", "rp.admin")) {
            permissions.clear();
            permissions.add(node);
            storageService.calls.clear();

            assertTrue(node, cardCommand.forceSave(player));

            assertEquals(node, Arrays.asList("saveCardFileNames", "saveCards"), storageService.calls);
        }
        assertTrue("forcesave sends no confirmation", received.isEmpty());
    }

    @Test
    public void forceLoadReadsTheCards() {
        for (String node : Arrays.asList("rp.card.forceload", "rp.admin")) {
            permissions.clear();
            permissions.add(node);
            storageService.calls.clear();

            assertTrue(node, cardCommand.forceLoad(player));

            assertEquals(node, Collections.singletonList("loadCards"), storageService.calls);
        }
        assertTrue("forceload sends no confirmation", received.isEmpty());
    }

    @Test
    public void forceSaveAndForceLoadAreNotGrantedByTheWildcardOrDefault() {
        permissions.addAll(Arrays.asList("rp.card.*", "rp.default"));

        assertFalse(cardCommand.forceSave(player));
        assertFalse(cardCommand.forceLoad(player));

        assertEquals(Arrays.asList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission:"
                        + " 'rp.card.forcesave'",
                NEGATIVE + "Sorry! In order to use this command, you need the following permission:"
                        + " 'rp.card.forceload'"), received);
        assertTrue(storageService.calls.isEmpty());
    }

    /** Runs the named sub-command the way {@code CommandService} would, with {@code args} as typed after /card. */
    private void run(String subCommand, String... args) {
        switch (subCommand) {
            case "show":
                cardCommand.showCard(player, args);
                break;
            case "help":
                cardCommand.showHelpMessage(player);
                break;
            case "lookup":
                cardCommand.showPlayerInfo(player, args.length == 0 ? new String[]{"lookup", "Steve"} : args);
                break;
            case "name":
                cardCommand.changeName(player, args.length == 0 ? new String[]{"name", "Aldric"} : args);
                break;
            case "race":
                cardCommand.changeRace(player, args.length == 0 ? new String[]{"race", "Human"} : args);
                break;
            case "subculture":
                cardCommand.changeSubculture(player, args.length == 0 ? new String[]{"subculture", "Nordic"} : args);
                break;
            case "religion":
                cardCommand.changeReligion(player, args.length == 0 ? new String[]{"religion", "Northism"} : args);
                break;
            case "gender":
                cardCommand.changeGender(player, args.length == 0 ? new String[]{"gender", "Male"} : args);
                break;
            case "age":
                cardCommand.changeAge(player, args.length == 0 ? new String[]{"age", "30"} : args);
                break;
            default:
                throw new AssertionError("Unknown sub-command " + subCommand);
        }
    }

    private Player player() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
                            case "getUniqueId":
                                return PLAYER_UUID;
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return "Aldric";
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    private static CommandSender console() {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        throw new AssertionError("The console sender should not be consulted: " + method.getName());
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what {@link CardCommand} reads. */
    private MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(plugin, "cardRepository", cardRepository);
        setField(plugin, "cardLookupService", new CardLookupService() {
            @Override
            public CharacterCard lookup(UUID playerUUID) {
                return lookupCards.get(playerUUID);
            }
        });
        setField(plugin, "configService", configService);
        setField(plugin, "ephemeralData", ephemeralData);
        setField(plugin, "argumentParser", new ArgumentParser());
        setField(plugin, "colorChecker", new FixedColorChecker());
        setField(plugin, "messenger", messenger);
        setField(plugin, "uuidChecker", new UUIDChecker() {
            @Override
            public UUID findUUIDBasedOnPlayerName(String playerName) {
                return knownPlayers.get(playerName);
            }
        });
        setField(plugin, "storageService", storageService);
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Answers {@code changeNameCooldown} without a {@code config.yml}. */
    private static final class StubConfigService extends ConfigService {
        private int changeNameCooldown;

        StubConfigService() {
            super(null);
        }

        @Override
        public int getInt(String option) {
            if (!"changeNameCooldown".equals(option)) {
                throw new AssertionError("Unexpected integer option " + option);
            }
            return changeNameCooldown;
        }
    }

    /** Fixed alert colors, so messages can be compared without a {@code config.yml}. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
        }

        @Override
        public ChatColor getNeutralAlertColor() {
            return NEUTRAL;
        }

        @Override
        public ChatColor getPositiveAlertColor() {
            return POSITIVE;
        }

        @Override
        public ChatColor getNegativeAlertColor() {
            return NEGATIVE;
        }
    }

    /** Records which card was shown to whom instead of formatting it. */
    private static final class RecordingMessenger extends Messenger {
        private final List<Shown> shown = new ArrayList<>();

        RecordingMessenger() {
            super(null);
        }

        @Override
        public void sendCardInfoToPlayer(CharacterCard card, Player player) {
            shown.add(new Shown(card, player));
        }
    }

    private static final class Shown {
        private final CharacterCard card;
        private final Player viewer;

        Shown(CharacterCard card, Player viewer) {
            this.card = card;
            this.viewer = viewer;
        }
    }

    /** Records each storage call instead of touching the disk. */
    private static final class RecordingStorageService extends StorageService {
        private final List<String> calls = new ArrayList<>();

        RecordingStorageService() {
            super(null);
        }

        @Override
        public void saveCardFileNames() {
            calls.add("saveCardFileNames");
        }

        @Override
        public void saveCards() {
            calls.add("saveCards");
        }

        @Override
        public void loadCards() {
            calls.add("loadCards");
        }
    }
}
