package org.stupid.torrent.model.torrentfile;

import java.util.List;

public record TorrentInfo(List<FileInfo> files, String name, long pieceLength, byte[] pieces, long  privateNumber) {

    public long torrentFileSize() {
        //noinspection OptionalGetWithoutIsPresent
        return files.stream().map(FileInfo::length).reduce(Long::sum).get();
    }
}
