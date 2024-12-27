package org.stupid.torrent.parser.api;

public interface ITrackerResponseParser {
    byte[] getConnectionIdBuffer();
    boolean isValid();
    byte[] getResponseTransactionIdBuffer();
    byte[] getRequestTransactionBuffer();
}
