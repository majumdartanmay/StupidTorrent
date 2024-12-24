package org.stupid.network;

import org.stupid.logging.StupidLogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.util.Optional;

public class StupidUDP {

    private static final StupidLogger log = StupidLogger.getLogger(StupidUDP.class.getName());

    public void sendUDP(final URI target, final byte[] payload) throws Exception{
        log.fine("Sending UDP request to %s", target);

        try(final DatagramSocket socket = new DatagramSocket()) {
            final InetAddress serverAddr = InetAddress.getByName(target.getHost());
            final int port = target.getPort();

            final DatagramPacket packet = new DatagramPacket(payload, payload.length, serverAddr, port);
            socket.send(packet);

            final byte[] responseBytes = new byte[1024];
            final DatagramPacket resPacket = new DatagramPacket(responseBytes, responseBytes.length);
            socket.receive(resPacket);

            final String resRaw = new String(resPacket.getData(), 0, resPacket.getLength());
            log.fine("Response raw : %s", resRaw);
        }catch (final Exception e) {
            final String errorTitle = Optional.ofNullable(e.getMessage()).orElse("Unknown Error");
            log.error("Unable to send UDP request : %s", errorTitle);
            throw e;
        }
    }

    /*
    * import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            // Create a socket
            socket = new DatagramSocket();

            // Server address and port
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int port = 12345;

            // Message to send
            String message = "Hello, UDP Server!";
            byte[] buffer = message.getBytes();

            // Create a packet to send the message
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, port);

            // Send the packet
            System.out.println("Sending message: " + message);
            socket.send(packet);

            // Buffer for receiving a response
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

            // Receive the response
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Response from server: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the socket
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}

    * */

}
