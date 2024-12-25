package org.stupid.trackers;

import org.stupid.logging.StupidLogger;
import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.Metadata;
import org.stupid.trackers.api.ITrackerPreProcessor;
import org.stupid.utils.StupidUtils;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    public Optional<URI> findAnyHealthTracker(Metadata metadata, final ITrackerCommunicator communicator) {

        final URI announce = metadata.announce();
        final List<URI> announceList = metadata.announceList();
        if (announceList.isEmpty()) {
            log.warn("Announce list is empty. We will fallback to the default torrent announce. %s", announce);
            return Optional.of(announce);
        }

        final Optional<URI> trackerURI = announceList
                .stream()
                .filter(x -> x.toString().toLowerCase().startsWith("udp"))
                .filter(x -> trackerWorking(x, communicator))
                .findFirst();

        log.fine("Potential tracker found? %s", trackerURI.isPresent());
        return trackerURI;
    }

    private boolean trackerWorking(final URI uri, final ITrackerCommunicator communicator) {
        log.finest("Checking if tracker : %s is working...", uri);
        try {
            communicator.sendConnectionRequest(uri);
            log.info("Working tracker found : %s", uri);
            return true;
        }catch (final Exception e) {
            log.warn("Tracker validation failed for %s. (%s)", uri, StupidUtils.cleanErrorTitle(e));
            return false;
        }
    }

}
