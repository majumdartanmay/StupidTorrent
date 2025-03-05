package org.stupid.torrent.parser.impl;

import org.stupid.torrent.model.dto.AnnounceCommunicationRecord;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.utils.StupidUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackerAnnounceResponseParser implements ITrackerResponseParser {

    private final byte[] annRequest, annResponse;
    private final AnnounceCommunicationRecord communicationRecord;

    public TrackerAnnounceResponseParser(final byte[] announceRequest, final byte[] announceResponse) {
        this.annResponse = announceResponse;
        this.annRequest = announceRequest;

        final ByteBuffer byteBuffer = ByteBuffer.wrap(announceResponse);
        final byte[] action = new byte[4];
        byteBuffer.get(action, 0, 4);

        final byte[] transactionId = new byte[4];
        byteBuffer.get(transactionId, 4, 4);

        final byte[] interval = new byte[4];
        byteBuffer.get(interval, 8, 4);
        final int intervalCount = StupidUtils.convertByteArrayToInt(interval);

        final byte[] leechers = new byte[4];
        byteBuffer.get(leechers, 12, 4);
        final int leechersCount = StupidUtils.convertByteArrayToInt(leechers);

        final byte[] seeders = new byte[4];
        byteBuffer.get(seeders, 16, 4);
        final int seedersCount = StupidUtils.convertByteArrayToInt(seeders);

        final List<String> peerAddress = new ArrayList<>();

        for (int i = 20; i < announceResponse.length; i += 6) {
            final byte[] ipBuffer = new byte[4];
            byteBuffer.get(ipBuffer, i, 4);
            final StringBuilder builder = new StringBuilder();
            for (final byte group : ipBuffer) {
                builder.append(Byte.toUnsignedInt(group));
                builder.append(".");
            }

            final int ipLength = builder.length();
            final String ip = builder.substring(0, ipLength - 1);

            final byte[] portBuffer = new byte[2];
            byteBuffer.get(portBuffer, i + 4, 2);
            final long port = StupidUtils.convertByteArrayToLong(portBuffer);

            final String fullServer = "%s:%s".formatted(ip, port);
            peerAddress.add(fullServer);
        }

        communicationRecord = new AnnounceCommunicationRecord(
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

    public int getInterval() {
        return communicationRecord.interval();
    }

    public int getLeechers() {
        return communicationRecord.leechers();
    }

    public int getSeeders() {
        return communicationRecord.seeders();
    }

    public AnnounceCommunicationRecord getCommunicationRecord() {
        return communicationRecord;
    }
}
