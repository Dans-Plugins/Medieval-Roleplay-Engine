package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.BukkitTestServer;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes how {@link InteractionListener} shows a character card when one player right-clicks another, as
 * described under "Viewing a Card by Right-Clicking" in {@code USER_GUIDE.md} and under {@code rightClickToViewCard}
 * in {@code CONFIG.md}: which clicks it ignores, which permission nodes it accepts, and the two-second (40-tick)
 * cooldown per viewer.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what {@link InteractionListener}
 * reads is filled in: {@code cardRepository}, {@code ephemeralData}, {@code messenger}, and {@code JavaPlugin}'s loaded
 * config and server. The server is {@link BukkitTestServer}, whose scheduler records the cooldown task instead of
 * running it, so a test can check its delay and then run it.</p>
 */
public class InteractionListenerTest {
    private static final UUID VIEWER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TARGET_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final CardRepository cardRepository = new CardRepository();
    private final EphemeralData ephemeralData = new EphemeralData();
    private final RecordingMessenger messenger = new RecordingMessenger();
    private final YamlConfiguration config = new YamlConfiguration();
    private final Set<String> permissions = new HashSet<>();

    private MedievalRoleplayEngine plugin;
    private InteractionListener listener;
    private Player viewer;
    private Player target;
    private CharacterCard targetCard;

    @Before
    public void setUp() throws Exception {
        BukkitTestServer.clearScheduledTasks();
        config.set("rightClickToViewCard", true);
        targetCard = new CharacterCard(null, TARGET_UUID);
        targetCard.setName("Aldric of Wessex");
        cardRepository.getCards().add(targetCard);

        plugin = plugin();
        listener = new InteractionListener(plugin);
        viewer = player(VIEWER_UUID, true);
        target = player(TARGET_UUID, false);
        permissions.add("rp.card.lookup");
    }

    @Test
    public void rightClickingAnotherPlayerShowsTheirCardToTheViewer() {
        rightClick(target);

        assertEquals(1, messenger.shown.size());
        assertSame(targetCard, messenger.shown.get(0).card);
        assertSame(viewer, messenger.shown.get(0).viewer);
    }

    @Test
    public void rightClickingSomethingOtherThanAPlayerDoesNothing() {
        rightClick(nonPlayerEntity());

        assertNothingHappened();
    }

    @Test
    public void rightClickingAPlayerWithoutACardDoesNothing() {
        cardRepository.getCards().clear();

        rightClick(target);

        assertNothingHappened();
    }

    @Test
    public void withRightClickToViewCardOffNothingIsShown() {
        config.set("rightClickToViewCard", false);

        rightClick(target);

        assertNothingHappened();
    }

    @Test
    public void rightClickToViewCardMissingFromTheConfigCountsAsOff() {
        config.set("rightClickToViewCard", null);

        rightClick(target);

        assertNothingHappened();
    }

    @Test
    public void aViewerWithoutAnAcceptedNodeIsShownNothingAndIsNotPutOnTheCooldown() {
        permissions.clear();
        permissions.add("rp.card.show");

        rightClick(target);

        assertNothingHappened();
    }

    @Test
    public void eachOfTheFourAcceptedNodesIsEnoughOnItsOwn() {
        for (String node : new String[]{"rp.card.lookup", "rp.card.show.others", "rp.card.*", "rp.default"}) {
            permissions.clear();
            permissions.add(node);
            ephemeralData.getPlayersWithRightClickCooldown().clear();
            messenger.shown.clear();

            rightClick(target);

            assertEquals("Node " + node + " was not accepted", 1, messenger.shown.size());
        }
    }

    @Test
    public void viewingACardPutsTheViewerOnACooldownClearedAfterFortyTicks() {
        rightClick(target);

        assertEquals(Collections.singletonList(VIEWER_UUID), ephemeralData.getPlayersWithRightClickCooldown());
        List<BukkitTestServer.ScheduledTask> tasks = BukkitTestServer.scheduledTasks();
        assertEquals(1, tasks.size());
        assertSame(plugin, tasks.get(0).plugin);
        assertEquals(40L, tasks.get(0).delayTicks);
    }

    @Test
    public void aSecondRightClickDuringTheCooldownShowsNothingAndSchedulesNothing() {
        rightClick(target);
        rightClick(target);

        assertEquals(1, messenger.shown.size());
        assertEquals(1, BukkitTestServer.scheduledTasks().size());
        assertEquals(Collections.singletonList(VIEWER_UUID), ephemeralData.getPlayersWithRightClickCooldown());
    }

    @Test
    public void onceTheCooldownTaskRunsTheViewerCanSeeACardAgain() {
        rightClick(target);
        BukkitTestServer.scheduledTasks().get(0).task.run();

        assertTrue(ephemeralData.getPlayersWithRightClickCooldown().isEmpty());

        rightClick(target);

        assertEquals(2, messenger.shown.size());
        assertEquals(2, BukkitTestServer.scheduledTasks().size());
    }

    private void rightClick(Entity clicked) {
        listener.handle(new PlayerInteractAtEntityEvent(viewer, clicked, new Vector(0, 1, 0)));
    }

    private void assertNothingHappened() {
        assertTrue(messenger.shown.isEmpty());
        assertTrue(ephemeralData.getPlayersWithRightClickCooldown().isEmpty());
        assertTrue(BukkitTestServer.scheduledTasks().isEmpty());
    }

    /** A player answering only its UUID and, for the viewer, its permissions; any other call fails the test. */
    private Player player(final UUID uuid, final boolean isViewer) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getUniqueId":
                                return uuid;
                            case "hasPermission":
                                if (!isViewer) {
                                    throw new AssertionError("The clicked player's permissions should not be consulted");
                                }
                                return permissions.contains((String) args[0]);
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return isViewer ? "viewer" : "target";
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    /** An entity that is not a player; the listener should not consult it at all. */
    private static Entity nonPlayerEntity() {
        return (Entity) Proxy.newProxyInstance(Entity.class.getClassLoader(), new Class<?>[]{Entity.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return "entity";
                            default:
                                throw new AssertionError("Unexpected call to Entity." + method.getName());
                        }
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the listener reads. */
    private MedievalRoleplayEngine plugin() throws Exception {
        BukkitTestServer.install();
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "cardRepository", cardRepository);
        setField(MedievalRoleplayEngine.class, plugin, "ephemeralData", ephemeralData);
        setField(MedievalRoleplayEngine.class, plugin, "messenger", messenger);
        setField(JavaPlugin.class, plugin, "newConfig", config);
        setField(JavaPlugin.class, plugin, "server", Bukkit.getServer());
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
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
}
