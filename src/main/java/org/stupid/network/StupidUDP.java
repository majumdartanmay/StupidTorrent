package org.stupid.network;

import org.stupid.logging.StupidLogger;

import java.net.*;
import java.util.Arrays;
import java.util.Optional;

public class StupidUDP implements AutoCloseable{

    private static final StupidLogger log = StupidLogger.getLogger(StupidUDP.class.getName());
    private static final int TIMEOUT = 5000;
    private final DatagramSocket socket;

    public StupidUDP() throws SocketException {
        this.socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);
    }

    public Optional<String> sendUDP(final URI target, final byte[] payload) throws Exception{
        log.finest("Sending UDP request to %s", target);
        log.finest("UDP payload : %s", Arrays.toString(payload));

        try {
            socket.setSoTimeout(TIMEOUT);
            final InetAddress trackerHost = InetAddress.getByName(target.getHost());
            final int port = target.getPort();

            final DatagramPacket packet = new DatagramPacket(payload, payload.length, trackerHost, port);
            socket.send(packet);

            final byte[] responseBytes = new byte[1024];
            final DatagramPacket resPacket = new DatagramPacket(responseBytes, responseBytes.length);
            socket.receive(resPacket);

            final String resRaw = new String(resPacket.getData(), 0, resPacket.getLength());
            log.finest("Response raw : %s", resRaw);

            return Optional.of(resRaw);
        }catch (SocketTimeoutException exception) {
            log.finest("Did not find any data from announce : %s. Client will probably try for other trackers",
                    target);
            return Optional.empty();
        }
        catch (final Exception e) {
            final String errorTitle = Optional.ofNullable(e.getMessage()).orElse("Unknown Error");
            log.error("Unable to send UDP request for %e : %s",target, errorTitle);
            throw e;
        }
    }

    @Override
    public void close() {
        socket.close();
    }
}
