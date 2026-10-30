package dansplugins.rpsystem.placeholders;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes the PlaceholderAPI expansion documented under "Using Placeholders" in {@code USER_GUIDE.md}: the
 * identifier it registers under, which card fields each placeholder resolves to, and when it resolves to nothing.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what {@link PlaceholderAPI} reads
 * is filled in: {@code cardRepository} and {@code JavaPlugin}'s description, loaded from the shipped
 * {@code plugin.yml}. Every card field is given a distinct value so that each placeholder shows which field it read.</p>
 */
public class PlaceholderAPITest {
    private static final Pattern DOCUMENTED_PLACEHOLDER = Pattern.compile("^\\| `%([a-z]+)_([a-z_]+)%` \\|");

    private final CardRepository cardRepository = new CardRepository();
    private final UUID uuid = UUID.randomUUID();

    private PlaceholderAPI expansion;

    @Before
    public void setUp() throws Exception {
        CharacterCard card = new CharacterCard(null, uuid);
        card.setName("Aldric of Wessex");
        card.setAge(34);
        card.setRace("Saxon");
        card.setSubculture("Fenland");
        card.setGender("Male");
        card.setReligion("Old Faith");
        cardRepository.getCards().add(card);

        expansion = new PlaceholderAPI(plugin());
    }

    @Test
    public void eachCardPlaceholderResolvesToItsOwnField() {
        assertEquals("Aldric of Wessex", expansion.onPlaceholderRequest(player(uuid), "card_name"));
        assertEquals("34", expansion.onPlaceholderRequest(player(uuid), "card_age"));
        assertEquals("Saxon", expansion.onPlaceholderRequest(player(uuid), "card_race"));
        assertEquals("Fenland", expansion.onPlaceholderRequest(player(uuid), "card_subculture"));
        assertEquals("Male", expansion.onPlaceholderRequest(player(uuid), "card_gender"));
        assertEquals("Old Faith", expansion.onPlaceholderRequest(player(uuid), "card_religion"));
    }

    @Test
    public void anUnsetFieldResolvesToItsDefaultValue() {
        UUID fresh = UUID.randomUUID();
        cardRepository.getCards().add(new CharacterCard(null, fresh));

        assertEquals("defaultName", expansion.onPlaceholderRequest(player(fresh), "card_name"));
        assertEquals("0", expansion.onPlaceholderRequest(player(fresh), "card_age"));
    }

    @Test
    public void placeholderNamesAreMatchedRegardlessOfCase() {
        assertEquals("Aldric of Wessex", expansion.onPlaceholderRequest(player(uuid), "CARD_NAME"));
        assertEquals("Saxon", expansion.onPlaceholderRequest(player(uuid), "Card_Race"));
    }

    @Test
    public void anUnknownPlaceholderResolvesToNothing() {
        assertNull(expansion.onPlaceholderRequest(player(uuid), "card_title"));
        assertNull(expansion.onPlaceholderRequest(player(uuid), "name"));
        assertNull(expansion.onPlaceholderRequest(player(uuid), ""));
    }

    @Test
    public void withoutAPlayerNothingResolves() {
        assertNull(expansion.onPlaceholderRequest(null, "card_name"));
    }

    @Test
    public void aPlayerWithoutACardResolvesToNothing() {
        assertNull(expansion.onPlaceholderRequest(player(UUID.randomUUID()), "card_name"));
    }

    @Test
    public void theIdentifierIsThePluginNameFromPluginYml() {
        // USER_GUIDE.md documents the identifier in lower case, as it is written inside a placeholder.
        assertEquals("MedievalRoleplayEngine", expansion.getIdentifier());
        assertEquals("medievalroleplayengine", expansion.getIdentifier().toLowerCase(Locale.ROOT));
    }

    @Test
    public void theExpansionStaysRegisteredAcrossPlaceholderAPIReloads() {
        assertTrue(expansion.persist());
        assertTrue(expansion.canRegister());
    }

    @Test
    public void everyPlaceholderInTheUserGuideResolvesUnderTheDocumentedIdentifier() throws Exception {
        Set<String> documented = new TreeSet<>();
        for (String line : Files.readAllLines(Paths.get("USER_GUIDE.md"), StandardCharsets.UTF_8)) {
            Matcher matcher = DOCUMENTED_PLACEHOLDER.matcher(line);
            if (matcher.find()) {
                assertEquals(line, expansion.getIdentifier().toLowerCase(Locale.ROOT), matcher.group(1));
                documented.add(matcher.group(2));
            }
        }

        assertFalse("USER_GUIDE.md documents no placeholders", documented.isEmpty());
        for (String params : documented) {
            assertNotNull(params + " is documented but does not resolve",
                    expansion.onPlaceholderRequest(player(uuid), params));
        }
    }

    private static Player player(final UUID playerUUID) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getUniqueId":
                                return playerUUID;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return "Player " + playerUUID;
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the expansion reads. */
    private MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "cardRepository", cardRepository);
        setField(JavaPlugin.class, plugin, "description", pluginYml());
        return plugin;
    }

    /** The resource-filtered {@code plugin.yml} under {@code target/classes}, which is what ships in the jar. */
    private static PluginDescriptionFile pluginYml() throws Exception {
        try (InputStream stream = PlaceholderAPITest.class.getClassLoader().getResourceAsStream("plugin.yml")) {
            assertNotNull("plugin.yml is not on the test classpath", stream);
            return new PluginDescriptionFile(stream);
        }
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
