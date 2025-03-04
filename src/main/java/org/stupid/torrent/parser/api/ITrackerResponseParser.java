package org.stupid.torrent.parser.api;

public interface ITrackerResponseParser {
    boolean isValid();
    byte[] getResponseTransactionIdBuffer();
    byte[] getRequestTransactionBuffer();
}
