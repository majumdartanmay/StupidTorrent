package org.stupid.network.api;

public interface ITrackerCommunicator {
    byte[] buildConnectionRequest();

    String sendConnectionRequest() throws Exception;
}
