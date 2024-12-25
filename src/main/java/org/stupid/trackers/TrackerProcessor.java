package org.stupid.trackers;

import org.stupid.torrent.model.Metadata;
import org.stupid.trackers.api.ITrackerPreProcessor;

import java.net.URI;

/**
 *
 * Responsible to perform operations specific to trackers
 *
 */
public class TrackerProcessor implements ITrackerPreProcessor {
    private static final class LazyHolder {
        private static final TrackerProcessor instance = new TrackerProcessor();
    }

    public static TrackerProcessor getInstance() {
        return LazyHolder.instance;
    }

    private TrackerProcessor(){}

    @Override
    public URI findAnyHealthTracker(Metadata metadata) {

        return null;
    }

}
