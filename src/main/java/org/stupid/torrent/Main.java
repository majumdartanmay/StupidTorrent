package org.stupid.torrent;

import org.stupid.logging.StupidLogger;
import org.stupid.network.StupidUDP;
import org.stupid.torrent.api.ITorrentParser;
import org.stupid.torrent.impl.BencodeTorrentParser;
import org.stupid.torrent.model.Metadata;

import java.io.File;
import java.nio.charset.StandardCharsets;


public class Main {

    private static final StupidLogger log = StupidLogger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception{

        log.info("Working directory : %s", new File(".").getCanonicalPath());

        final ITorrentParser parser = new BencodeTorrentParser();
        final String path = args[0];

        log.info("Path : %s", path);
        final Metadata output = parser.parse(path);
        log.info("Metadata is parsed");

        final StupidUDP udp = new StupidUDP();
        udp.sendUDP(output.announce(), "Hello World".getBytes(StandardCharsets.UTF_8));

        log.info("We have sent the UDP request");
    }
}