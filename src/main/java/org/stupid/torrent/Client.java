package org.stupid.torrent;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.parser.api.ITorrentParser;
import org.stupid.torrent.parser.impl.BencodeTorrentParser;
import org.stupid.network.UDPTrackerCommunicator;
import org.stupid.torrent.model.Metadata;
import org.stupid.trackers.TrackerProcessor;

import java.io.File;
import java.net.URI;
import java.util.Optional;


public class Client {

    private static final StupidLogger log = StupidLogger.getLogger(Client.class.getName());

    public static void main(String[] args) throws Exception{

        log.info("Working directory : %s", new File(".").getCanonicalPath());

        final ITorrentParser parser = new BencodeTorrentParser();
        final String path = args[0];

        log.info("Path : %s", path);
        final Metadata output = parser.parse(path);
        log.info("Metadata is parsed");
        log.info("\nMetadata announce host : %s\nMetadata announce port : %d", output.announce().getHost(), output.announce().getPort());

        try(final ITrackerCommunicator communicator = new UDPTrackerCommunicator(output)) {
            final TrackerProcessor processor = TrackerProcessor.getInstance();
            final Optional<URI> trackerURI = processor.findAnyHealthTracker(output, communicator);
            if (trackerURI.isPresent()) {
                log.info("Current tracker URI : %s. ", trackerURI.get());
            }else {
                log.error("Unable to find any working tracker. We will try after sometime again...");
            }
        }
    }
}