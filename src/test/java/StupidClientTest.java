import org.junit.jupiter.api.Test;
import org.stupid.network.UDPTrackerCommunicator;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.torrent.parser.api.ITorrentParser;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.torrent.parser.impl.BencodeTorrentParser;
import org.stupid.torrent.parser.impl.TrackerConnectionResponseParser;
import org.stupid.trackers.TrackerProcessor;

import java.io.File;
import java.util.Optional;

public class StupidClientTest {

    @Test
    public void torrentAnnounceRequest() throws Exception {
        final ITorrentParser parser = new BencodeTorrentParser();
        final Metadata torrentMetadata = parser.parse(new File("src/test/resources/big-buck-bunny.torrent").getCanonicalPath());

        try(final ITrackerCommunicator communicator = new UDPTrackerCommunicator(torrentMetadata)) {
            final TrackerProcessor processor = TrackerProcessor.getInstance();
            final Optional<TrackerResponseRecord> trackerResponseRecordOpt =
                    processor.findAnyHealthTracker(torrentMetadata, communicator);
            if (trackerResponseRecordOpt.isPresent()) {
                final ITrackerResponseParser responseParser = new TrackerConnectionResponseParser(trackerResponseRecordOpt.get());

            }
        }
    }

//    @Test
//    public void testTorrentParsing() throws Exception {
//        final ITorrentParser parser = new BencodeTorrentParser();
//        final Metadata outputMetadata = parser.parse(new File("src/test/resources/big-buck-bunny.torrent").getCanonicalPath());
//        final List<URI> announceURI =
//                Arrays.stream("udp://tracker.leechers-paradise.org:6969, udp://tracker.coppersurfer.tk:6969, udp://tracker.opentrackr.org:1337, udp://explodie.org:6969, udp://tracker.empire-js.us:1337, wss://tracker.btorrent.xyz, wss://tracker.openwebtorrent.com, wss://tracker.fastcast.nz".split(", "))
//                        .map(URI::create)
//                        .toList();
//        final Metadata actualMetadata = new Metadata(
//                URI.create("udp://tracker.leechers-paradise.org:6969"),
//                announceURI,
//                "WebTorrent <https://webtorrent.io>",
//                "UTF-8",
//
//
//        );
//        System.out.println(metadata);
//    }

    /*
Metadata[announce=udp://tracker.leechers-paradise.org:6969, announceList=[udp://tracker.leechers-paradise.org:6969, udp://tracker.coppersurfer.tk:6969, udp://tracker.opentrackr.org:1337, udp://explodie.org:6969, udp://tracker.empire-js.us:1337, wss://tracker.btorrent.xyz, wss://tracker.openwebtorrent.com, wss://tracker.fastcast.nz], createdBy=WebTorrent <https://webtorrent.io>, creationDate=1490916601, encoding=UTF-8, info=TorrentInfo[files=[FileInfo[length=140, path=[Big Buck Bunny.en.srt]], FileInfo[length=276134947, path=[Big Buck Bunny.mp4]], FileInfo[length=310380, path=[poster.jpg]]], name=Big Buck Bunny, pieceLength=262144, pieces=[B@22a637e7, privateNumber=-1], urlList=[https://webtorrent.io/torrents/], infoBencodeBytes=[B@6fe7aac8, infoHash=[B@1d119efb]

     * */
}
