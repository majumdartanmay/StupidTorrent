package org.stupid.torrent.model;

import java.util.List;

public record FileInfo(long length, List<String> path) {
}
