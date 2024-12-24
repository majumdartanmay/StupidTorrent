package org.stupid.torrent.impl;

import com.dampcake.bencode.BencodeInputStream;
import org.stupid.logging.StupidLogger;
import org.stupid.torrent.api.ITorrentParser;
import org.stupid.torrent.model.FileInfo;
import org.stupid.torrent.model.Metadata;
import org.stupid.torrent.model.TorrentInfo;
import org.stupid.utils.StupidUtils;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BencodeTorrentParser implements ITorrentParser {

    private final StupidLogger logger = StupidLogger.getLogger(BencodeTorrentParser.class.getName());
    private byte[] bytes;

    private void readFile(final String path) throws Exception {
        logger.info("readFile %s", path);
        bytes = Files.readAllBytes(Path.of(path));
        logger.info("File reading is finished");
    }

    @Override
    public Metadata parse(String path) throws Exception{
        readFile(path);
        final BencodeInputStream bencode = new BencodeInputStream(new ByteArrayInputStream(bytes));
        final Map<String, Object> fullMData = bencode.readDictionary();
        final Map<String, Object> info = (Map<String, Object>) fullMData.get("info");
        List<FileInfo> files = new ArrayList<>();
        for (Object file : (List<Object>)info.get("files")) {
            final Map<String, Object> fileMap = (Map<String, Object>) file;
            final long length = (long) fileMap.get("length");
            final List<String> filePaths  = (List<String>) fileMap.get("path");
            files.add(new FileInfo(length, filePaths));
        }

        final TorrentInfo tInfo = new TorrentInfo(
                files,
                (String)info.get("name"),
                (long)info.get("piece length"),
                StupidUtils.hex(((String)(info.get("pieces"))).getBytes(StandardCharsets.UTF_8)),
                (long) Optional.ofNullable(info.get("private")).orElse(-1L)
        );

        final Metadata mData = new Metadata(
                new URI((String)fullMData.get("announce")),
                (List<String>)fullMData.get("announce-list"),
                (String)fullMData.get("created by"),
                (long)fullMData.get("creation date"),
                (String)fullMData.get("encoding"),
                tInfo,
                (List<String>) fullMData.get("url-list")
        );

        logger.fine("Torrent metadata : %s", mData);
        return mData;
    }
}
