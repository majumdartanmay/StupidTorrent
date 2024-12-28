package org.stupid.torrent.parser.api;

public interface ITrackerResponseParser {
    byte[] getResponseConnectionIdBuffer();
    boolean isValid();
    byte[] getResponseTransactionIdBuffer();
    byte[] getRequestTransactionBuffer();
}
