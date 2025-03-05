package org.stupid.torrent;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.parser.api.ITorrentParser;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
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
        log.fine(parser.getCommunicationRecord().toString());
    }
}