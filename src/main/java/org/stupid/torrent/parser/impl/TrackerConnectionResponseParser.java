package org.stupid.torrent.parser.impl;

import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.utils.StupidUtils;

import java.util.Arrays;

public class TrackerConnectionResponseParser implements ITrackerResponseParser {

    private final byte[] resBuffer;
    private final byte[] reqBuffer;

    public TrackerConnectionResponseParser(byte[] resBuffer, final byte[] requestBuffer) {
        this.resBuffer = StupidUtils.convertArrayToUnsigned(resBuffer);
        this.reqBuffer = StupidUtils.convertArrayToUnsigned(requestBuffer);
    }

    public TrackerConnectionResponseParser(final TrackerResponseRecord record) {
        this.resBuffer = record.response();
        this.reqBuffer = record.request();
    }

    public byte[] getResponseConnectionIdBuffer() {
        return Arrays.copyOfRange(resBuffer, 8, 16);
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
