package org.stupid.torrent.model.torrentfile;

import java.net.URI;
import java.util.List;

public record Metadata(URI announce,
                       List<URI> announceList,
                       String createdBy,
                       long creationDate,
                       String encoding,
                       TorrentInfo info,
                       List<String> urlList,
                       byte[] infoBencodeBytes,
                       byte[] infoHash
                       ) {
}
