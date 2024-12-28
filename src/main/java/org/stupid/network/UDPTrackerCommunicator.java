package org.stupid.network;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.utils.StupidUtils;

import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UDPTrackerCommunicator implements ITrackerCommunicator{

    private final StupidUDP udpTalker = new StupidUDP();
    private final StupidLogger logger = StupidLogger.getLogger(UDPTrackerCommunicator.class.getName());

    public UDPTrackerCommunicator() throws SocketException {
    }

    @Override
    public byte[] buildConnectionRequest() {
        int offset = 0;
        final byte[] request = new byte[16];
        // Connection ID = udptrac 0x41727101980
        final byte[] connectionIdBuffer = StupidUtils.hexStringToByteArray("0000041727101980");
        logger.finest("ConnectionID buffer : %s", Arrays.toString(connectionIdBuffer));

        for (byte b : connectionIdBuffer) {
            request[offset++] = b;
        }
        logger.finest("Connection ID buffer flushed. Filled till offset : %d", offset);
        // connect
        request[offset++] = 0;
        request[offset++] = 0;
        request[offset++] = 0;
        request[offset++] = 0;

        // transaction_id
        final byte[] transactionBuffer = StupidUtils.getRandomHexString(8);
        int transactionBufferIdx = 0;

        while (transactionBufferIdx < transactionBuffer.length) {
            request[offset++] = transactionBuffer[transactionBufferIdx++];
        }

        logger.finest("Connection request info : %s", Arrays.toString(connectionIdBuffer));
        return request;
    }

    @Override
    public Map<String, byte[]> sendConnectionRequest(final URI announce) throws Exception{
        final byte[] request = buildConnectionRequest();
        final String res = udpTalker.sendUDP(announce, request).orElse(StupidUtils.NO_RESPONSE_RES);
        logger.finest("Response received from UDP : %s. Response : %s", announce, res);
        final Map<String, byte[]> connectPayloadInfo = new HashMap<>();
        connectPayloadInfo.put("request", request);
        connectPayloadInfo.put("response", res.getBytes(StandardCharsets.UTF_8));
        return connectPayloadInfo;
    }

    @Override
    public void close() throws Exception {
        udpTalker.close();
    }
}
