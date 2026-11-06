package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * What the one-off message commands ({@code /whisper}, {@code /yell}, {@code /lo}) need off a live server: a plugin, a
 * player who has a character card, and a messenger that records each broadcast.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what these commands read is filled
 * in. The real {@link EphemeralData}, {@link CardRepository} and {@link ArgumentParser} are used; the alert colors are
 * fixed so that no {@code config.yml} is needed.</p>
 */
public final class ChatCommandFixture {
    public static final ChatColor POSITIVE = ChatColor.GREEN;
    public static final ChatColor NEGATIVE = ChatColor.RED;
    public static final ChatColor NEUTRAL = ChatColor.AQUA;
    public static final String CHARACTER = "Aldric of Wessex";
    public static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public final YamlConfiguration config = new YamlConfiguration();
    public final EphemeralData ephemeralData = new EphemeralData();
    public final RecordingMessenger messenger = new RecordingMessenger();
    public final Set<String> permissions = new HashSet<>();
    public final List<String> received = new ArrayList<>();
    public final Player player = player();
    public final MedievalRoleplayEngine plugin;

    public ChatCommandFixture() throws Exception {
        plugin = plugin();
    }

    /** A sender that is not a player. Any call on it fails the test. */
    public static CommandSender console() {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        throw new AssertionError("The console sender should not be consulted: " + method.getName());
                    }
                });
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
                                return CHARACTER;
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    private MedievalRoleplayEngine plugin() throws Exception {
        CardRepository cardRepository = new CardRepository();
        CharacterCard card = new CharacterCard(null, PLAYER_UUID);
        card.setName(CHARACTER);
        cardRepository.getCards().add(card);

        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine allocated =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, allocated, "colorChecker", new FixedColorChecker());
        setField(MedievalRoleplayEngine.class, allocated, "argumentParser", new ArgumentParser());
        setField(MedievalRoleplayEngine.class, allocated, "cardRepository", cardRepository);
        setField(MedievalRoleplayEngine.class, allocated, "ephemeralData", ephemeralData);
        setField(MedievalRoleplayEngine.class, allocated, "messenger", messenger);
        setField(JavaPlugin.class, allocated, "newConfig", config);
        return allocated;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Resolves color names as configured, with fixed alert colors. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
        }

        @Override
        public ChatColor getPositiveAlertColor() {
            return POSITIVE;
        }

        @Override
        public ChatColor getNegativeAlertColor() {
            return NEGATIVE;
        }

        @Override
        public ChatColor getNeutralAlertColor() {
            return NEUTRAL;
        }
    }

    /** One message handed to the messenger. */
    public static final class Broadcast {
        public final Player sender;
        public final String message;
        public final int distance;

        Broadcast(Player sender, String message, int distance) {
            this.sender = sender;
            this.message = message;
            this.distance = distance;
        }
    }

    /** Records each broadcast instead of looking for nearby players, and reports {@link #recipientCount} heard it. */
    public static final class RecordingMessenger extends Messenger {
        public final List<Broadcast> roleplay = new ArrayList<>();
        public final List<Broadcast> outOfCharacter = new ArrayList<>();
        public int recipientCount;

        RecordingMessenger() {
            super(null);
        }

        @Override
        public int sendRPMessageToPlayersWithinDistance(Player sender, String message, int distance) {
            roleplay.add(new Broadcast(sender, message, distance));
            return recipientCount;
        }

        @Override
        public int sendOOCMessageToPlayersWithinDistance(Player sender, String message, int distance) {
            outOfCharacter.add(new Broadcast(sender, message, distance));
            return recipientCount;
        }
    }
}
