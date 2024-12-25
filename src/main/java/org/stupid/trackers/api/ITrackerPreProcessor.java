package org.stupid.trackers.api;

import org.stupid.network.api.ITrackerCommunicator;
import org.stupid.torrent.model.Metadata;

import java.net.URI;
import java.util.Optional;

public interface ITrackerPreProcessor {
    Optional<URI> findAnyHealthTracker(final Metadata metadata, final ITrackerCommunicator communicator);
}
