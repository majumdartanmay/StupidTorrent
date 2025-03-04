package org.stupid.torrent.parser.impl;

import org.stupid.torrent.parser.api.ITrackerResponseParser;

public class TrackerAnnounceResponseParser implements ITrackerResponseParser {

    private byte[] annRequest, annResponse;

    public TrackerAnnounceResponseParser(final byte[] announceRequest, final byte[] announceResponse) {
        this.annResponse = announceResponse;
        this.annRequest = announceRequest;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public byte[] getResponseTransactionIdBuffer() {
        return new byte[0];
    }

    @Override
    public byte[] getRequestTransactionBuffer() {
        return new byte[0];
    }
}
