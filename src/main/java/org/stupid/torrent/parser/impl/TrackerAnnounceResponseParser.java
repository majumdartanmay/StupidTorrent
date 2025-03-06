package org.stupid.torrent.parser.impl;

import org.stupid.logging.StupidLogger;
import org.stupid.torrent.model.dto.AnnounceCommunicationRecord;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.utils.StupidUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackerAnnounceResponseParser implements ITrackerResponseParser {

    private final byte[] annRequest, annResponse;
    private static final StupidLogger logger = StupidLogger.getLogger(TrackerAnnounceResponseParser.class.getName());

    public TrackerAnnounceResponseParser(final byte[] announceRequest, final byte[] announceResponse) {
        this.annResponse = announceResponse;
        this.annRequest = announceRequest;
    }

    private void parseError() {
        
    }

   public AnnounceCommunicationRecord parse() {

        final byte[] announceResponse = annResponse;

        final ByteBuffer byteBuffer = ByteBuffer.wrap(announceResponse);
        final byte[] action = byteBuffer.slice(0, 4).array();

        final byte[] transactionId = byteBuffer.slice(4, 4).array();

        final byte[] interval = byteBuffer.slice(8, 4).array();
        final int intervalCount = StupidUtils.convertByteArrayToInt(interval);

        final byte[] leechers = byteBuffer.slice(12, 4).array();
        final int leechersCount = StupidUtils.convertByteArrayToInt(leechers);

        final byte[] seeders = byteBuffer.slice(16, 4).array();
        final int seedersCount = StupidUtils.convertByteArrayToInt(seeders);

        final byte[] err = byteBuffer.slice(8, announceResponse.length - 8).array();
        logger.error("Announce Response Error : %s", new String(err));

        final List<String> peerAddress = new ArrayList<>();

        for (int i = 20; i < announceResponse.length; i += 6) {
            final byte[] ipBuffer = byteBuffer.slice(i, 4).array();
            final StringBuilder builder = new StringBuilder();
            for (final byte group : ipBuffer) {
                builder.append(Byte.toUnsignedInt(group));
                builder.append(".");
            }

            final int ipLength = builder.length();
            final String ip = builder.substring(0, ipLength - 1);

            final byte[] portBuffer = byteBuffer.slice(i +4, 2).array();
            final long port = StupidUtils.convertByteArrayToLong(portBuffer);

            final String fullServer = "%s:%s".formatted(ip, port);
            peerAddress.add(fullServer);
        }

        return new AnnounceCommunicationRecord(
                action,
                transactionId,
                intervalCount,
                leechersCount,
                seedersCount,
                peerAddress
        );

    }

    @Override
    public boolean isValid() {
        return Arrays.equals(getRequestTransactionBuffer(), getResponseTransactionIdBuffer());
    }

    @Override
    public byte[] getResponseTransactionIdBuffer() {
        return Arrays.copyOfRange(annResponse, 4, 8);
    }

    @Override
    public byte[] getRequestTransactionBuffer() {
        return Arrays.copyOfRange(annRequest, 12, 16);
    }

}
