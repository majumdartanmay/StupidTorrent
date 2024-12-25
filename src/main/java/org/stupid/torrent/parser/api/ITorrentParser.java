package org.stupid.torrent.parser.api;

import org.stupid.torrent.model.Metadata;

public interface ITorrentParser {
     Metadata parse(final String path) throws Exception;
}
