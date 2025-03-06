/*
 * MIT License
 *
 * Copyright (c) 2025 Tanmay Majumdar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.stupid.network;

import org.stupid.logging.StupidLogger;

import java.net.*;
import java.util.Arrays;
import java.util.Optional;

public class StupidUDP implements AutoCloseable{

    private static final StupidLogger log = StupidLogger.getLogger(StupidUDP.class.getName());
    private static final int TIMEOUT = 5000;
    private DatagramSocket socket;

    public Optional<String> sendUDP(final URI target, final byte[] payload) throws Exception{

        socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);

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
        if (socket != null && !socket.isClosed())
            socket.close();
    }
}
