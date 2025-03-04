package org.stupid.network.api;

import org.stupid.torrent.model.dto.TrackerResponseRecord;

import java.net.URI;
import java.util.Map;

public interface ITrackerCommunicator extends AutoCloseable {

    byte[] buildConnectionRequest();

    Map<String, byte[]> sendConnectionRequest(URI announce) throws Exception;

    Map<String, byte[]> sendAnnounceRequest(TrackerResponseRecord connectResponse) throws Exception;

    public byte[] getTransactionIdBuffer();
}
