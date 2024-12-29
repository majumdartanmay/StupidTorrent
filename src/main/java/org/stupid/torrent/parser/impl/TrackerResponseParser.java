package org.stupid.torrent.parser.impl;

import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.parser.api.ITrackerResponseParser;

import java.util.Arrays;

public class TrackerResponseParser implements ITrackerResponseParser {

    private final byte[] resBuffer;
    private final byte[] reqBuffer;

    public TrackerResponseParser(byte[] resBuffer, final byte[] requestBuffer) {
        this.resBuffer = resBuffer;
        this.reqBuffer = requestBuffer;
    }

    public TrackerResponseParser(final TrackerResponseRecord record) {
        this.resBuffer = record.response();
        this.reqBuffer = record.request();
    }

    @Override
    public byte[] getResponseConnectionIdBuffer() {
        return Arrays.copyOfRange(resBuffer, 8, 17);
    }

    @Override
    public boolean isValid() {
        final byte[] requestTransactionId = getRequestTransactionBuffer();
        return resBuffer.length >= 16 && Arrays.equals(requestTransactionId, getResponseTransactionIdBuffer());
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
