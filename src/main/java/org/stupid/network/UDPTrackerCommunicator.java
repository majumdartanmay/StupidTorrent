package org.stupid.network;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.torrent.parser.impl.TrackerConnectionResponseParser;
import org.stupid.utils.StupidUtils;

import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
* TODO: Currently we have no way to which tracker is sending the response.
*  Requests are being sent in bulk and whenever a response is being sent,
* we assume that the recent most tracker sent the response. This has to be
* fixed
*
* */
public class UDPTrackerCommunicator implements ITrackerCommunicator{

    private final StupidUDP udpTalker = new StupidUDP();
    private final StupidLogger logger = StupidLogger.getLogger(UDPTrackerCommunicator.class.getName());
    private final Metadata metadata;
    private final byte[] transactionBuffer = StupidUtils.getPositiveByteArr(4);

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
        logger.fine("Transaction buffer : %s", Arrays.toString(transactionBuffer));

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
        logger.finest("Connect response received from UDP : %s. Response : %s", announce, res);
        return formRequestResult(res.getBytes(StandardCharsets.UTF_8), request);
    }

    @Override
    public Map<String, byte[]> sendAnnounceRequest(final TrackerResponseRecord connectResponse) throws Exception{

        final URI announce = connectResponse.trackerAddress();
        final byte[] announceRequest = buildAnnounceRequest(connectResponse);
        final String res = udpTalker.sendUDP(announce, announceRequest).orElse(StupidUtils.NO_RESPONSE_RES);
        if (StupidUtils.NO_RESPONSE_RES.equals(res)) {
            logger.info("No announce response received from %s", announce);
        }else {
            logger.finest("Announce response received from UDP : %s. Response : %s", announce, res);
        }
        return formRequestResult(res.getBytes(StandardCharsets.UTF_8), announceRequest);
    }

    @Override
    public byte[] getTransactionIdBuffer() {
        return transactionBuffer;
    }

    private byte[] buildAnnounceRequest(final TrackerResponseRecord record) {
        logger.finest("Creating announce request... ");

        final TrackerConnectionResponseParser parser = new TrackerConnectionResponseParser(record);
        final byte[] connectionIdBuffer = parser.getResponseConnectionIdBuffer();
        final byte[] transactionIdBuffer = parser.getRequestTransactionBuffer();
        final int ANNOUNCE_REQUEST_SIZE = 98;
        final byte[] announceRequest = new byte[ANNOUNCE_REQUEST_SIZE];

        StupidUtils.copyArray(announceRequest, connectionIdBuffer, 0, 7, 0, connectionIdBuffer.length - 1);

        // skipping byte index 8-10. Since announce request is 0001
        announceRequest[11] = 0x1;

        // transaction ID
        StupidUtils.copyArray(announceRequest, transactionIdBuffer, 12, 15, 0, transactionIdBuffer.length - 1);

        // info hash
        StupidUtils.copyArray(announceRequest, metadata.infoHash(), 16, 35, 0, metadata.infoHash().length - 1);

        // Peer id
        StupidUtils.copyArray(announceRequest, StupidUtils.STUPID_PEER_ID, 36, 55, 0, StupidUtils.STUPID_PEER_ID.length - 1);

        // left
        StupidUtils.copyArray(announceRequest, StupidUtils.convertLongToBytes(metadata.info().torrentFileSize()), 64, 71, 0, Long.BYTES - 1);

        // key
        StupidUtils.copyArray(announceRequest, StupidUtils.generateRandomString(4).getBytes(StandardCharsets.UTF_8), 88, 91, 0, 4 - 1);

        // num want
        StupidUtils.copyArray(announceRequest, StupidUtils.convertIntToBytes(-1), 92, 95, 0, Integer.BYTES - 1);

        // port
        StupidUtils.copyArray(announceRequest, StupidUtils.get16BitInteger(6882), 96, 97, 0, 1);

        logger.fine("Announce request : %s", Arrays.toString(announceRequest));

        return announceRequest;
    }

    @Override
    public void close()  {
        udpTalker.close();
    }

    public Map<String, byte[]> formRequestResult(final byte[] response, final byte[] req) {
        final Map<String, byte[]> map = new HashMap<>();
        map.put("request", req);
        map.put("response", response);
        return map;
    }
}
