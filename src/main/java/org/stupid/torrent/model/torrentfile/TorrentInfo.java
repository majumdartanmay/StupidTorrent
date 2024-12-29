package org.stupid.torrent.model.torrentfile;

import java.util.List;

public record TorrentInfo(List<FileInfo> files, String name, long pieceLength, byte[] pieces, long  privateNumber) {
}
