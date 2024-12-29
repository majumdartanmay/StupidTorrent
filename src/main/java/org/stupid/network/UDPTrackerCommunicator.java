package org.stupid.network;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.torrent.parser.impl.TrackerResponseParser;
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
    private final Metadata metadata;

    public UDPTrackerCommunicator(Metadata metadata) throws SocketException {
        this.metadata = metadata;
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
    public byte[] buildAnnounceRequest(final TrackerResponseRecord record) {
        logger.finest("Creating announce request... ");

        final ITrackerResponseParser parser = new TrackerResponseParser(record);

        final byte[] connectionIdBuffer = parser.getResponseConnectionIdBuffer();
        final byte[] transactionIdBuffer = parser.getResponseConnectionIdBuffer();
        final int ANNOUNCE_REQUEST_SIZE = 98;
        final byte[] announceRequest = new byte[ANNOUNCE_REQUEST_SIZE];

        StupidUtils.copyArray(announceRequest, connectionIdBuffer, 0, connectionIdBuffer.length, 0, connectionIdBuffer.length);

        // skipping byte index 8-10. Since announce request is 0001
        announceRequest[11] = 0x1;
        // transaction ID
        StupidUtils.copyArray(announceRequest, transactionIdBuffer, 12, 15, 0, transactionIdBuffer.length);

        return announceRequest;
    }

    @Override
    public byte[] getInfoHash() {
        return new byte[0];
    }

    @Override
    public void close()  {
        udpTalker.close();
    }
}
