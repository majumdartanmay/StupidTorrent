package org.stupid.torrent.parser.impl;

import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.utils.StupidUtils;

import java.util.Arrays;

public class TrackerAnnounceResponseParser implements ITrackerResponseParser {

    private byte[] annRequest, annResponse;

    public TrackerAnnounceResponseParser(final byte[] announceRequest, final byte[] announceResponse) {
        this.annResponse = announceResponse;
        this.annRequest = announceRequest;
    }

    @Override
    public boolean isValid() {
        return Arrays.equals(getRequestTransactionBuffer(), getResponseTransactionIdBuffer());
    }

    @Override
    public byte[] getResponseTransactionIdBuffer() {
        return Arrays.copyOfRange(annResponse, 4, 8);
    }

    @Override
    public byte[] getRequestTransactionBuffer() {
        return Arrays.copyOfRange(annRequest, 12, 16);
    }

    public int getInterval() {
        return StupidUtils.convertByteArrayToInt(Arrays.copyOfRange(annResponse, 8, 12));
    }

    public int getLeechers() {
        return StupidUtils.convertByteArrayToInt(Arrays.copyOfRange(annResponse, 12, 16));
    }

    public int getSeeders() {
        return StupidUtils.convertByteArrayToInt(Arrays.copyOfRange(annResponse, 16, 20));
    }
}
