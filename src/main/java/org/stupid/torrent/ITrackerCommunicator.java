package org.stupid.torrent;

public interface ITrackerCommunicator {
    byte[] buildConnectionRequest();

    void parseResponse(final String rawResponse) throws Exception;

    void sendRequest();
}
