package org.stupid.network;

import org.stupid.logging.StupidLogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

public class StupidUDP {

    private static final StupidLogger log = StupidLogger.getLogger(StupidUDP.class.getName());

    public String sendUDP(final URI target, final byte[] payload) throws Exception{
        log.fine("Sending UDP request to %s", target);
        log.fine("UDP payload : %s", Arrays.toString(payload));

        try(final DatagramSocket socket = new DatagramSocket()) {
            final InetAddress trackerHost = InetAddress.getByName(target.getHost());
            final int port = target.getPort();

            final DatagramPacket packet = new DatagramPacket(payload, payload.length, trackerHost, port);
            socket.send(packet);

            final byte[] responseBytes = new byte[1024];
            final DatagramPacket resPacket = new DatagramPacket(responseBytes, responseBytes.length);
            socket.receive(resPacket);

            final String resRaw = new String(resPacket.getData(), 0, resPacket.getLength());
            log.fine("Response raw : %s", resRaw);

            return resRaw;
        }catch (final Exception e) {
            final String errorTitle = Optional.ofNullable(e.getMessage()).orElse("Unknown Error");
            log.error("Unable to send UDP request : %s", errorTitle);
            throw e;
        }
    }

}
