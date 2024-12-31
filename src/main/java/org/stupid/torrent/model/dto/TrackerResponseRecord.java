package org.stupid.torrent.model.dto;

import java.net.URI;

public record TrackerResponseRecord(URI trackerAddress, byte[] response, byte[] request) {

}
