package org.stupid.network.impl;

import org.stupid.logging.StupidLogger;
import org.stupid.network.StupidUDP;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.Metadata;
import org.stupid.utils.StupidUtils;

import java.util.Arrays;

public class UDPTrackerCommunicator implements ITrackerCommunicator {

    private final Metadata metadata;
    private final StupidUDP udpTalker = new StupidUDP();
    private final StupidLogger logger = StupidLogger.getLogger(UDPTrackerCommunicator.class.getName());

    public UDPTrackerCommunicator(final Metadata metadata) {
        this.metadata = metadata;
    }

    public byte[] buildConnectionRequestHardcoded() {
        byte[] PROTOCOL_ID = { 0x00, 0x00, 0x04, 0x17, 0x27, 0x10, 0x19, (byte) 0x80 };//-9216317402361102336l;
        byte[] buf = new byte[16];

        int action = 0;
        System.arraycopy(PROTOCOL_ID, 0, buf, 0, PROTOCOL_ID.length);

        buf[8] = ((byte) action);
        buf[9] = ((byte) (action >> 8));
        buf[10] = ((byte) (action >> 16));
        buf[11] = ((byte) (action >> 24));
        byte[] tid = new byte[4];
        System.arraycopy(tid, 0, buf, 12, tid.length);
        return buf;
    }

    @Override
    public byte[] buildConnectionRequest() {
        int offset = 0;
        final byte[] request = new byte[16];
        // Connection ID =  0x41727101980
        final byte[] connectionIdBuffer = StupidUtils.hexStringToByteArray("0000041727101980");
        logger.fine("ConnectionID buffer : %s", Arrays.toString(connectionIdBuffer));

        for (byte b : connectionIdBuffer) {
            request[offset++] = b;
        }
        logger.fine("Connection ID buffer flushed. Filled till offset : %d", offset);
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

        logger.fine("Connection request info : %s", Arrays.toString(connectionIdBuffer));
        return request;
    }

    @Override
    public String sendConnectionRequest() throws Exception{
        final String res = udpTalker.sendUDP(metadata.announce(), buildConnectionRequest());
        logger.fine("Response received from UDP : %s", metadata.announce());
        return res;
    }

}
