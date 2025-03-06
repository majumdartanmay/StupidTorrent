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

package org.stupid.torrent;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.parser.api.ITorrentParser;
import org.stupid.torrent.parser.impl.BencodeTorrentParser;
import org.stupid.network.UDPTrackerCommunicator;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.torrent.parser.impl.TrackerAnnounceResponseParser;
import org.stupid.torrent.parser.impl.TrackerConnectionResponseParser;
import org.stupid.trackers.TrackerProcessor;
import org.stupid.utils.StupidUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


/*
* https://www.bittorrent.org/beps/bep_0015.html
* https://allenkim67.github.io/programming/2016/05/04/how-to-make-your-own-bittorrent-client.html*
* */
public class Client {

    private static final StupidLogger log = StupidLogger.getLogger(Client.class.getName());

    public static void main(String[] args) throws Exception{

        log.info("Working directory : %s", new File(".").getCanonicalPath());

        if (args.length != 1) {
            log.error("Please provide a torrent path for processing");
            System.exit(-1);
        }

        final ITorrentParser parser = new BencodeTorrentParser();
        final String path = args[0];
        log.info("Path : %s", path);

        final Metadata torrentMetadata = parser.parse(path);
        log.info("Metadata is parsed");
        log.info("\nMetadata announce host : %s\nMetadata announce port : %d", torrentMetadata.announce().getHost(), torrentMetadata.announce().getPort());
        log.fine("Torrent info hash hex : %s", StupidUtils.sha1HexString(torrentMetadata.infoHash()));
        log.fine("Torrent info hash bytes : %s", Arrays.toString(torrentMetadata.infoHash()));

        try(final ITrackerCommunicator communicator = new UDPTrackerCommunicator(torrentMetadata)) {
            final TrackerProcessor processor = TrackerProcessor.getInstance();
            final Optional<TrackerResponseRecord> trackerResponseRecordOpt =
                    processor.findAnyHealthTracker(torrentMetadata, communicator);

            if (trackerResponseRecordOpt.isEmpty()) {
                log.error("Unable to find any working tracker. Quitting the application for now. ");
                System.exit(-1);
            }

            final TrackerResponseRecord connectResponse = trackerResponseRecordOpt.get();
            log.info("Current tracker URI : %s. ", connectResponse);
            parseConnectResponse(connectResponse);

            final Map<String, byte[]> announceResponsePayload = communicator.sendAnnounceRequest(connectResponse);
            final byte[] announceResponse = announceResponsePayload.get("response");
            final byte[] announceRequest = announceResponsePayload.get("request");
            log.info("Announce result : %s", Arrays.toString(announceResponse));

            parseAnnounceResponse(announceRequest, announceResponse);
        }
    }

    public static void parseConnectResponse(final TrackerResponseRecord record) {
        final TrackerConnectionResponseParser parser = new TrackerConnectionResponseParser(record.response(), record.request());
        log.fine(
                """
                        Tracker response parser status
                        Request transaction buffer : %s
                        Response transaction buffer : %s
                        Response connection buffer : %s
                        Transaction buffer valid : %s
                        """,
                Arrays.toString(parser.getRequestTransactionBuffer()),
                Arrays.toString(parser.getResponseTransactionIdBuffer()),
                Arrays.toString(parser.getResponseConnectionIdBuffer()),
                parser.isValid()
                );
    }

    public static void parseAnnounceResponse(final byte[] request, final byte[] response) {
        final TrackerAnnounceResponseParser parser = new TrackerAnnounceResponseParser(request, response);
    }
}