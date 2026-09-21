package dansplugins.rpsystem;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/**
 * Installs a minimal {@link Server} into {@link Bukkit} so that Bukkit's own permission classes
 * ({@link Permission}, {@code PermissibleBase}, {@code PermissionAttachment}) can be exercised
 * off a live server. Only a real {@link SimplePluginManager} is provided; every other server call
 * returns a default value.
 *
 * <p>{@link Bukkit#setServer} accepts a single server per JVM, and Surefire runs every test class
 * in one JVM, so installation is idempotent and the plugin manager is shared between tests.</p>
 */
final class BukkitTestServer {
    private static final String PLUGIN_YML = "plugin.yml";

    private static boolean pluginYmlRegistered;

    private BukkitTestServer() {
    }

    /** Installs the stub server if it is not already installed and returns its plugin manager. */
    static synchronized PluginManager install() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(createServer());
        }
        return Bukkit.getServer().getPluginManager();
    }

    /**
     * Loads {@code plugin.yml} the way Bukkit does when it enables a plugin. The copy on the test
     * classpath is the resource-filtered one under {@code target/classes}, which is what ships in the jar.
     */
    static PluginDescriptionFile loadPluginYml() {
        try (InputStream stream = BukkitTestServer.class.getClassLoader().getResourceAsStream(PLUGIN_YML)) {
            if (stream == null) {
                throw new IllegalStateException(PLUGIN_YML + " is not on the test classpath");
            }
            return new PluginDescriptionFile(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + PLUGIN_YML, e);
        } catch (org.bukkit.plugin.InvalidDescriptionException e) {
            throw new IllegalStateException(PLUGIN_YML + " is not a valid plugin description", e);
        }
    }

    /**
     * Registers every permission declared in {@code plugin.yml} with the stub server's plugin manager,
     * exactly as {@code SimplePluginManager.loadPlugin} would on a live server. Safe to call from several
     * test classes; the nodes are only added once.
     */
    static synchronized PluginManager installWithPluginYmlPermissions() {
        PluginManager pluginManager = install();
        if (!pluginYmlRegistered) {
            for (Permission permission : loadPluginYml().getPermissions()) {
                pluginManager.addPermission(permission);
            }
            pluginYmlRegistered = true;
        }
        return pluginManager;
    }

    /** An enabled {@link Plugin} stand-in, which is all {@code PermissionAttachment} requires of its owner. */
    static Plugin enabledPlugin() {
        final PluginDescriptionFile description = loadPluginYml();
        return (Plugin) Proxy.newProxyInstance(Plugin.class.getClassLoader(), new Class<?>[]{Plugin.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "isEnabled":
                                return Boolean.TRUE;
                            case "getName":
                                return description.getName();
                            case "getDescription":
                                return description;
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    }
                });
    }

    private static Server createServer() {
        final Logger logger = Logger.getLogger(BukkitTestServer.class.getName());
        final PluginManager[] pluginManager = new PluginManager[1];
        Server server = (Server) Proxy.newProxyInstance(Server.class.getClassLoader(), new Class<?>[]{Server.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getLogger":
                                return logger;
                            case "getName":
                                return BukkitTestServer.class.getSimpleName();
                            case "getVersion":
                            case "getBukkitVersion":
                                return "test";
                            case "getPluginManager":
                                return pluginManager[0];
                            case "toString":
                                return BukkitTestServer.class.getSimpleName();
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    }
                });
        pluginManager[0] = new SimplePluginManager(server, new SimpleCommandMap(server));
        return server;
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive() || type == void.class) {
            return null;
        }
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == double.class) {
            return 0.0d;
        }
        if (type == float.class) {
            return 0.0f;
        }
        if (type == char.class) {
            return '\0';
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        return 0;
    }
}
