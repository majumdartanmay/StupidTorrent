package org.stupid.network.api;

import java.net.URI;
import java.util.Map;

public interface ITrackerCommunicator extends AutoCloseable {
    byte[] buildConnectionRequest();

    Map<String, byte[]> sendConnectionRequest(URI announce) throws Exception;
}
