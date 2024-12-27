package org.stupid.trackers;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.model.torrentfile.Metadata;
import org.stupid.trackers.api.ITrackerPreProcessor;
import org.stupid.utils.StupidUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * Responsible to perform operations specific to trackers
 *
 */
public class TrackerProcessor implements ITrackerPreProcessor {

    private static final StupidLogger log = StupidLogger.getLogger(TrackerProcessor.class.getName());

    private static final class LazyHolder {
        private static final TrackerProcessor instance = new TrackerProcessor();
    }

    public static TrackerProcessor getInstance() {
        return LazyHolder.instance;
    }

    private TrackerProcessor(){}

    @Override
    public Optional<TrackerResponseRecord> findAnyHealthTracker(Metadata metadata, final ITrackerCommunicator communicator) throws Exception {

        final URI announce = metadata.announce();
        final List<URI> announceList = metadata.announceList();
        if (announceList.isEmpty()) {
            log.warn("Announce list is empty. We will fallback to the default torrent announce. %s", announce);
            final Map<String, byte[]> rawResponse = communicator.sendConnectionRequest(announce);
            return Optional.of(new TrackerResponseRecord(announce, rawResponse.get("response"), rawResponse.get("request")));
        }

        final Map<URI, byte[]> trackerResponseMap = new HashMap<>();
        for (final URI tracker : announceList) {
            if (!tracker.toString().startsWith("udp")) {
                log.finest("Ignoring tracker : $s because its not UDP protocol", tracker);
                continue;
            }
            final Optional<TrackerResponseRecord> recOpt = trackerWorking(tracker, communicator, trackerResponseMap);
            if (recOpt.isPresent()) {
                return recOpt;
            }
        }

        return Optional.empty();
    }

    private Optional<TrackerResponseRecord> trackerWorking(final URI uri, final ITrackerCommunicator communicator, Map<URI, byte[]> trackerResponseMap) {
        log.finest("Checking if tracker : %s is working...", uri);
        try {
            final Map<String, byte[]> rawResponse = communicator.sendConnectionRequest(uri);
            final byte[] responseBuffer = rawResponse.get("response");
            final byte[] requestBuffer = rawResponse.get("request");
            trackerResponseMap.put(uri, responseBuffer);
            final boolean trackerFound =  !StupidUtils.NO_RESPONSE_RES.equals(new String(responseBuffer));
            if (!trackerFound) {
                log.fine("No response from tracker: %s", uri);
                return Optional.empty();
            }
            log.fine("\nTrackerStatus:\nTracker URL: %s\nRequest: %s\nResponse size : %d bytes\nResponse: %s\n",
                    uri,
                    Arrays.toString(requestBuffer),
                    responseBuffer.length,
                    Arrays.toString(responseBuffer));
            return Optional.of(new TrackerResponseRecord(uri, responseBuffer, requestBuffer));
        }catch (final Exception e) {
            log.warn("Tracker validation failed for %s. (%s)", uri, StupidUtils.cleanErrorTitle(e));
            return Optional.empty();
        }
    }

}
