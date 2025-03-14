/*
 * MIT License
 *
 * Copyright (c) 2025 Tanmay Majumdar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.stupid.torrent.parser.impl;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import org.stupid.logging.StupidLogger;
import org.stupid.torrent.parser.api.ITorrentParser;
import org.stupid.torrent.model.torrentfile.FileInfo;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.torrent.model.torrentfile.TorrentInfo;
import org.stupid.utils.StupidUtils;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

        final Bencode bencode = new Bencode(StandardCharsets.UTF_8, false);
        final Bencode bencodeBinaryReader = new Bencode(StandardCharsets.UTF_8, true);
        final Map<String, Object> fullMData = bencode.decode(bytes, Type.DICTIONARY);
        final Map<String, Object> info = (Map<String, Object>) fullMData.get("info");
        final Map<String, Object> infoBinary = (Map<String, Object>) bencodeBinaryReader.decode(bytes, Type.DICTIONARY).get("info");
        final List<FileInfo> files = new ArrayList<>();

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
                ((ByteBuffer)infoBinary.get("pieces")).array(),
                (long) Optional.ofNullable(info.get("private")).orElse(-1L)
        );
        final List<URI> announceURIs = new ArrayList<>();
        final List<List<String>> announceRawList = (List<List<String>>)(fullMData.get("announce-list"));
        for (final List<String> announceURI : announceRawList) {
            announceURIs.add(new URI(announceURI.getFirst()));
        }

        final byte[] infoBencodeBytes = createInfoBencodeBytes(infoBinary);

        final Metadata mData = new Metadata(
                new URI((String)fullMData.get("announce")),
                announceURIs,
                (String)fullMData.get("created by"),
                (long)fullMData.get("creation date"),
                (String)(fullMData.get("encoding")),
                tInfo,
                (List<String>) fullMData.get("url-list"),
                infoBencodeBytes,
                StupidUtils.sha1bytes(infoBencodeBytes)
        );

        logger.finest("Torrent metadata : %s", mData);
        logger.finest("info hash metadata : %s", StupidUtils.sha1HexString(mData.infoBencodeBytes()));
        return mData;
    }

    private  byte[] createInfoBencodeBytes(final Map<String, Object> infoBinaryMap) {
        final Bencode bencode = new Bencode(StandardCharsets.UTF_8, false);
        return bencode.encode(infoBinaryMap);
    }
}
