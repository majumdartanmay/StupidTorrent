package org.stupid.trackers.api;

import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.dto.TrackerResponseRecord;
import org.stupid.torrent.model.torrentfile.Metadata;

import java.util.Optional;

public interface ITrackerPreProcessor {
    Optional<TrackerResponseRecord> findAnyHealthTracker(final Metadata metadata, final ITrackerCommunicator communicator) throws Exception;
}
