package org.stupid.torrent.model.dto;

import java.util.List;

public record AnnounceCommunicationRecord(byte[] action,
                                          byte[] transactionId,
                                          int interval,
                                          int leechers,
                                          int seeders,
                                          List<String> servers) {
}
