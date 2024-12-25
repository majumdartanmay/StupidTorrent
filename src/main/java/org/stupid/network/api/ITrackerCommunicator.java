package org.stupid.network.api;

import java.net.URI;

public interface ITrackerCommunicator extends AutoCloseable {
    byte[] buildConnectionRequest();

    String sendConnectionRequest(URI announce) throws Exception;
}
