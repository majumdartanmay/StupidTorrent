package org.stupid.torrent.impl;

import org.stupid.logging.StupidLogger;
import org.stupid.network.StupidUDP;
import org.stupid.torrent.ITrackerCommunicator;
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

    @Override
    public byte[] buildConnectionRequest() {
        int offset = 0;
        final byte[] request = new byte[16];
        // Connection ID =  0x41727101980
        final byte[] connectionIdBuffer = StupidUtils.hexStringToByteArray("041727101980");
        for (int i =0 ; i < connectionIdBuffer.length; i++) {
            request[offset++] = connectionIdBuffer[i];
        }
        // connect
        connectionIdBuffer[offset++] = 0;
        connectionIdBuffer[offset++] = 0;

        // transaction_id
        final byte[] transactionBuffer = StupidUtils.getRandomHexString(8);
        for (int i =offset ; i < transactionBuffer.length + offset; i++) {
            request[offset++] = transactionBuffer[i];
        }

        logger.fine("Connection request info : %s", Arrays.toString(connectionIdBuffer));
        return request;
    }

    @Override
    public void parseResponse(String rawResponse) throws Exception{
        final String res = udpTalker.sendUDP(metadata.announce(), buildConnectionRequest());
        logger.fine("Response received from UDP : %s", metadata.announce());
    }

    @Override
    public void sendRequest() {

    }
}
