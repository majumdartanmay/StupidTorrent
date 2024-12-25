package org.stupid.torrent;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.parser.api.ITorrentParser;
import org.stupid.torrent.parser.impl.BencodeTorrentParser;
import org.stupid.network.impl.UDPTrackerCommunicator;
import org.stupid.torrent.model.Metadata;

import java.io.File;


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

        final ITrackerCommunicator communicator = new UDPTrackerCommunicator(output);
        communicator.sendConnectionRequest();

        log.info("We have sent the UDP request");
    }
}