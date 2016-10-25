package fi.lupapiste.jmx;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

import javax.net.ServerSocketFactory;

/**
 * Constructs server sockets that have SO_REUSEADDR flag on.
 */
public final class ReuseRMIServerSocketFactory implements RMIServerSocketFactory {

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
        serverSocket.setReuseAddress(true);
        return serverSocket;
    }

}
