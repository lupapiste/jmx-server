package fi.lupapiste.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public final class ServerFactory {

    private static final Map<String,Registry> REGISTRIES = new HashMap<>();

    /**
     * Creates and starts a JMX server that listens on the given port.
     * Call {@link #stop(JMXConnectorServer)} to halt it.
     *
     * @param port TCP port number
     */
    public static JMXConnectorServer start(final int port) {
        JMXConnectorServer server = createServer(port);
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return server;
    }

    /**
     * Creates a JMX server on the given port but does not start it.
     * You'll need to call {@link JMXConnectorServer#start()} manually.
     * Call {@link #stop(JMXConnectorServer)} to halt it.
     *
     * @param port TCP port number
     */
    public static synchronized JMXConnectorServer createServer(final int port) {
        try {
            String url = String.format("service:jmx:rmi:///jndi/rmi://localhost:%d/jmxrmi", port);
            if (!REGISTRIES.containsKey(url)) {
                // Create Registry with address reuse socket option
                Registry registry = LocateRegistry.createRegistry(port, null, new ReuseRMIServerSocketFactory());
                REGISTRIES.put(url, registry);
            }

            JMXServiceURL jmxUrl = new JMXServiceURL(url);
            MBeanServer platformServer = ManagementFactory.getPlatformMBeanServer();
            return JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, platformServer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cleans up the server.
     *
     * @param server Server created with {@link #start(int)} or {@link #createServer(int)}
     */
    public static synchronized void stop(final JMXConnectorServer server) {
        String url = getServerUrlForStopping(server);
        Registry registry = getRegistryForStopping(url);
        try {
            server.stop();
            UnicastRemoteObject.unexportObject(registry, true);
            REGISTRIES.remove(url);
        } catch (NoSuchObjectException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getServerUrlForStopping(final JMXConnectorServer server) {
        JMXServiceURL address = server.getAddress();
        if (address == null) {
            throw new IllegalStateException("Server address is null! Already stopped?");
        }
        return address.toString();
    }

    private static Registry getRegistryForStopping(String url) {
        Registry registry = REGISTRIES.get(url);
        if (registry == null) {
            throw new IllegalStateException("Unknown url " + url);
        }
        return registry;
    }

    private ServerFactory() {
    }
}
