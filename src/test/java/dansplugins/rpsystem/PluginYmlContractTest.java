package dansplugins.rpsystem;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Keeps {@code plugin.yml} in step with the code that consults it and the guide that documents it.
 *
 * <p>A permission node that is checked in code but never registered resolves to operator-only, which
 * is invisible to an operator testing the plugin in-game (#321, #322, #329). These tests fail at the
 * point such a node is introduced, rather than after a release.</p>
 */
public class PluginYmlContractTest {
    private static final Path MAIN_SOURCES = Paths.get("src", "main", "java");
    private static final Path COMMAND_SERVICE = MAIN_SOURCES.resolve(
            Paths.get("dansplugins", "rpsystem", "commands", "CommandService.java"));
    private static final Path USER_GUIDE = Paths.get("USER_GUIDE.md");

    /** Every {@code "rp.<node>"} string literal in the sources; the plugin consults no other permission prefix. */
    private static final Pattern PERMISSION_LITERAL = Pattern.compile("\"(rp\\.[A-Za-z0-9_.*-]+)\"");
    /** Every top-level command label {@code CommandService} dispatches on. Sub-commands compare {@code args[0]}, not {@code label}. */
    private static final Pattern DISPATCHED_LABEL = Pattern.compile("label\\.equalsIgnoreCase\\(\"([^\"]+)\"\\)");
    /** A row of the {@code USER_GUIDE.md} permission table: {@code | `node` | `default` | description |}. */
    private static final Pattern USER_GUIDE_ROW = Pattern.compile("^\\|\\s*`([^`]+)`\\s*\\|\\s*`([^`]+)`\\s*\\|");

    /**
     * Nodes that are checked in code but deliberately left out of {@code plugin.yml}, each documented under
     * "Known Permission Discrepancies" in {@code USER_GUIDE.md}. Registering one of them is a behaviour change
     * (an unregistered node is operator-only; a registered one takes its declared default), so it must be a
     * conscious decision that also removes the node from this list.
     */
    private static final Set<String> KNOWINGLY_UNREGISTERED = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "rp.admin",
            "rp.default",
            "rp.card.show.others",
            "rp.rphelp"
    )));

    private static final String CARD_WILDCARD = "rp.card.*";
    private static final Set<String> PLAYER_CARD_NODES = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(
            "rp.card.show",
            "rp.card.lookup",
            "rp.card.help",
            "rp.card.name",
            "rp.card.race",
            "rp.card.subculture",
            "rp.card.religion",
            "rp.card.age",
            "rp.card.gender"
    )));

    private static PluginDescriptionFile pluginYml;
    /** Registered nodes keyed by lower-cased name, since Bukkit resolves permissions case-insensitively. */
    private static Map<String, Permission> registeredPermissions;
    private static Set<String> permissionsCheckedInSource;

    @BeforeClass
    public static void loadPluginYmlAndSources() throws IOException {
        BukkitTestServer.install();
        pluginYml = BukkitTestServer.loadPluginYml();

        registeredPermissions = new LinkedHashMap<>();
        for (Permission permission : pluginYml.getPermissions()) {
            registeredPermissions.put(node(permission.getName()), permission);
        }

        permissionsCheckedInSource = new TreeSet<>();
        try (Stream<Path> files = Files.walk(MAIN_SOURCES)) {
            List<Path> javaFiles = files.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList());
            for (Path javaFile : javaFiles) {
                Matcher matcher = PERMISSION_LITERAL.matcher(read(javaFile));
                while (matcher.find()) {
                    permissionsCheckedInSource.add(node(matcher.group(1)));
                }
            }
        }
    }

    /** The form Bukkit compares permission names in ({@code PermissibleBase} and {@code SimplePluginManager} both lower-case). */
    private static String node(String name) {
        return name.toLowerCase(Locale.ENGLISH);
    }

    @Test
    public void pluginYmlLoadsAsABukkitPluginDescription() {
        assertEquals("MedievalRoleplayEngine", pluginYml.getName());
        assertEquals("dansplugins.rpsystem.MedievalRoleplayEngine", pluginYml.getMain());
        assertFalse("plugin.yml declares no permissions", registeredPermissions.isEmpty());
        assertFalse("plugin.yml declares no commands", pluginYml.getCommands().isEmpty());
    }

    @Test
    public void sourceScanFindsThePermissionChecks() {
        // Guards the scan itself: an empty result would make every "is registered" assertion pass vacuously.
        assertTrue("No rp.* permission literals found under " + MAIN_SOURCES, permissionsCheckedInSource.size() > 1);
    }

    @Test
    public void everyPermissionCheckedInSourceIsRegisteredOrKnowinglyUnregistered() {
        Set<String> unaccounted = new TreeSet<>(permissionsCheckedInSource);
        unaccounted.removeAll(registeredPermissions.keySet());
        unaccounted.removeAll(KNOWINGLY_UNREGISTERED);
        assertEquals("Permission nodes checked in code but neither registered in plugin.yml nor on the"
                + " knowingly-unregistered list (an unregistered node is operator-only)",
                Collections.emptySet(), unaccounted);
    }

    @Test
    public void everyRegisteredPermissionIsCheckedInSource() {
        Set<String> unused = new TreeSet<>(registeredPermissions.keySet());
        unused.removeAll(permissionsCheckedInSource);
        assertEquals("Permission nodes registered in plugin.yml that nothing in the code consults",
                Collections.emptySet(), unused);
    }

    @Test
    public void knowinglyUnregisteredListIsCurrent() {
        for (String node : KNOWINGLY_UNREGISTERED) {
            assertTrue(node + " is on the knowingly-unregistered list but nothing in the code checks it", permissionsCheckedInSource.contains(node));
            assertFalse(node + " is on the knowingly-unregistered list but is registered in plugin.yml", registeredPermissions.containsKey(node));
        }
    }

    @Test
    public void cardWildcardParentsExactlyTheNinePlayerCardNodes() {
        Permission wildcard = registeredPermissions.get(CARD_WILDCARD);
        assertTrue(CARD_WILDCARD + " is not registered in plugin.yml", wildcard != null);
        assertEquals(CARD_WILDCARD + " must keep the default Bukkit resolved it to while it was unregistered",
                PermissionDefault.OP, wildcard.getDefault());

        Map<String, Boolean> children = wildcard.getChildren();
        assertEquals("Children of " + CARD_WILDCARD, PLAYER_CARD_NODES, cardWildcardChildren());
        for (Map.Entry<String, Boolean> child : children.entrySet()) {
            assertTrue(child.getKey() + " must be granted, not revoked, by " + CARD_WILDCARD, child.getValue());
        }
        assertFalse("rp.card.forcesave is not gated on " + CARD_WILDCARD + " in code", cardWildcardChildren().contains("rp.card.forcesave"));
        assertFalse("rp.card.forceload is not gated on " + CARD_WILDCARD + " in code", cardWildcardChildren().contains("rp.card.forceload"));
    }

    @Test
    public void everyChildOfTheCardWildcardIsARegisteredNode() {
        for (String child : cardWildcardChildren()) {
            assertTrue(child + " is a child of " + CARD_WILDCARD + " but is not itself registered", registeredPermissions.containsKey(child));
        }
    }

    private static Set<String> cardWildcardChildren() {
        Set<String> children = new TreeSet<>();
        for (String child : registeredPermissions.get(CARD_WILDCARD).getChildren().keySet()) {
            children.add(node(child));
        }
        return children;
    }

    @Test
    public void everyRegisteredCommandIsDispatchedByCommandService() throws IOException {
        Set<String> dispatched = dispatchedLabels();
        Set<String> unhandled = new TreeSet<>(pluginYml.getCommands().keySet());
        unhandled.removeAll(dispatched);
        assertEquals("Commands registered in plugin.yml that CommandService never dispatches",
                Collections.emptySet(), unhandled);
    }

    @Test
    public void everyLabelDispatchedByCommandServiceIsARegisteredCommand() throws IOException {
        Set<String> unregistered = new TreeSet<>(dispatchedLabels());
        unregistered.removeAll(pluginYml.getCommands().keySet());
        assertEquals("Labels CommandService dispatches that plugin.yml does not register (Bukkit never routes them)",
                Collections.emptySet(), unregistered);
    }

    @Test
    public void userGuidePermissionTableMatchesPluginYml() throws IOException {
        Map<String, String> documented = userGuidePermissionTable();
        assertEquals("Nodes in the USER_GUIDE.md permission table must be exactly the nodes registered in plugin.yml",
                new TreeSet<>(registeredPermissions.keySet()), new TreeSet<>(documented.keySet()));
        for (Map.Entry<String, String> row : documented.entrySet()) {
            PermissionDefault actual = registeredPermissions.get(row.getKey()).getDefault();
            assertEquals("Default documented for " + row.getKey() + " in USER_GUIDE.md",
                    actual, PermissionDefault.getByName(row.getValue()));
        }
    }

    @Test
    public void userGuideNamesEveryKnowinglyUnregisteredNode() throws IOException {
        String guide = read(USER_GUIDE);
        int discrepancies = guide.indexOf("### Known Permission Discrepancies");
        assertTrue("USER_GUIDE.md has no 'Known Permission Discrepancies' section", discrepancies >= 0);
        String section = guide.substring(discrepancies);
        for (String node : KNOWINGLY_UNREGISTERED) {
            assertTrue(node + " is accepted but unregistered and USER_GUIDE.md's Known Permission Discrepancies does not say so",
                    section.contains("`" + node + "`"));
        }
    }

    private static Set<String> dispatchedLabels() throws IOException {
        Set<String> labels = new TreeSet<>();
        Matcher matcher = DISPATCHED_LABEL.matcher(read(COMMAND_SERVICE));
        while (matcher.find()) {
            labels.add(matcher.group(1));
        }
        assertFalse("No dispatched labels found in " + COMMAND_SERVICE, labels.isEmpty());
        return labels;
    }

    /** The permission table under {@code ## Permissions}: node name to the default it documents. */
    private static Map<String, String> userGuidePermissionTable() throws IOException {
        Map<String, String> table = new HashMap<>();
        boolean inSection = false;
        for (String line : read(USER_GUIDE).split("\n")) {
            if (line.startsWith("## ")) {
                inSection = line.trim().equals("## Permissions");
                continue;
            }
            if (!inSection) {
                continue;
            }
            Matcher row = USER_GUIDE_ROW.matcher(line);
            if (row.find() && !row.group(1).equals("Permission")) {
                table.put(node(row.group(1)), row.group(2));
            }
        }
        assertFalse("No permission table rows found under '## Permissions' in " + USER_GUIDE, table.isEmpty());
        return table;
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
