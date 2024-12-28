package org.stupid.torrent.parser.impl;

import org.stupid.torrent.parser.api.ITrackerResponseParser;

import java.util.Arrays;

public class TrackerResponseParser implements ITrackerResponseParser {

    private final byte[] resBuffer;
    private final byte[] reqBuffer;

    public TrackerResponseParser(byte[] resBuffer, final byte[] requestBuffer) {
        this.resBuffer = resBuffer;
        this.reqBuffer = requestBuffer;
    }

    @Override
    public byte[] getResponseConnectionIdBuffer() {
        return Arrays.copyOfRange(resBuffer, 8, 17);
    }

    @Override
    public boolean isValid() {
        final byte[] requestTransactionId = getRequestTransactionBuffer();
        return Arrays.equals(requestTransactionId, getResponseTransactionIdBuffer());
    }

    @Override
    public byte[] getResponseTransactionIdBuffer() {
        return Arrays.copyOfRange(resBuffer, 4, 8);
    }

    @Override
    public byte[] getRequestTransactionBuffer() {
        return Arrays.copyOfRange(reqBuffer, 12, reqBuffer.length);
    }
}
