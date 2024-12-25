package org.stupid.trackers.api;

import org.stupid.torrent.model.Metadata;

import java.net.URI;

public interface ITrackerPreProcessor {
    URI findAnyHealthTracker(final Metadata metadata);
}
