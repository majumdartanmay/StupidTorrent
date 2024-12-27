package org.stupid.torrent.model.torrentfile;

import java.util.List;

public record FileInfo(long length, List<String> path) {
}
