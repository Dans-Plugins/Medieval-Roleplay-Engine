package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Covers the dispatch of an unknown {@code /card} sub-command, which {@link CommandService} answers itself. A
 * {@code false} return there would make Bukkit send the command's usage line after the list of sub-commands.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only {@code colorChecker} is set.</p>
 */
public class CommandServiceTest {
    private static final ChatColor NEGATIVE = ChatColor.RED;

    private final List<String> received = new ArrayList<>();

    @Test
    public void anUnknownCardSubCommandIsAnsweredWithTheListOfSubCommandsOnly() throws Exception {
        CommandService commandService = new CommandService(plugin());

        assertTrue(commandService.interpretCommand(sender(), "card", new String[]{"unknown"}));

        assertEquals(Collections.singletonList(NEGATIVE + "Sub-commands: help, lookup, name, race, subculture, religion,"
                + " age, gender, forcesave, forceload"), received);
    }

    private CommandSender sender() {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if (method.getName().equals("sendMessage")) {
                            received.add((String) args[0]);
                            return null;
                        }
                        throw new AssertionError("Unexpected call to CommandSender." + method.getName());
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the dispatch reads. */
    private static MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        Field colorChecker = MedievalRoleplayEngine.class.getDeclaredField("colorChecker");
        colorChecker.setAccessible(true);
        colorChecker.set(plugin, new ColorChecker(null) {
            @Override
            public ChatColor getNegativeAlertColor() {
                return NEGATIVE;
            }
        });
        return plugin;
    }
}
