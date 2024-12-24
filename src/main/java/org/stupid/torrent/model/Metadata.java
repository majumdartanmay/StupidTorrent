package org.stupid.torrent.model;

import java.net.URI;
import java.util.List;

public record Metadata(URI announce,
                       List<String> announceList,
                       String createdBy,
                       long creationDate,
                       String encoding,
                       TorrentInfo info,
                       List<String> urlList
                       ) {
}
