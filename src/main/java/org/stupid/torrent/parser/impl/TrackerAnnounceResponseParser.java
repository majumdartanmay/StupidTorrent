/*
 * MIT License
 *
 * Copyright (c) 2025 Tanmay Majumdar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.stupid.torrent.parser.impl;

import org.stupid.logging.StupidLogger;
import org.stupid.torrent.model.ActionType;
import org.stupid.torrent.model.dto.AnnounceCommunicationRecord;
import org.stupid.torrent.parser.StupidParserException;
import org.stupid.torrent.parser.api.ITrackerResponseParser;
import org.stupid.utils.StupidUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackerAnnounceResponseParser implements ITrackerResponseParser {

    private final byte[] annRequest;
    private final byte[] annResponse;

    private static final StupidLogger logger = StupidLogger.getLogger(TrackerAnnounceResponseParser.class.getName());

    public TrackerAnnounceResponseParser(final byte[] announceRequest, final byte[] announceResponse) {
        this.annResponse = announceResponse;
        this.annRequest = announceRequest;
    }

    private void handleInvalidAction(ActionType actionType) throws IllegalStateException, StupidParserException {
        if (actionType != ActionType.ERROR) {
            throw new IllegalStateException("Invalid action : %s. Announce parser can only parse announce response".formatted(actionType));
        }

        final byte[] errorPayload = Arrays.copyOfRange(annResponse, 4, annResponse.length);
        throw new StupidParserException("Announce parser fatal error : %s".formatted(new String(errorPayload)) );
    }

   public AnnounceCommunicationRecord parse() throws StupidParserException {

        final byte[] announceResponse = annResponse;
        logger.finest("Announce response3 length : %d", announceResponse.length);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(announceResponse);
        byteBuffer.mark();
        final int action = byteBuffer.getInt();

        final ActionType actionType = StupidUtils.getActionType((byte)action);
        if (actionType != ActionType.ANNOUNCE) {
            logger.error("Found invalid action type : %s. Trying to interpret situation", actionType);
            handleInvalidAction(actionType);
        }
        final byte[] transactionId = new byte[4];
        byteBuffer.get(transactionId);

        final int intervalCount = byteBuffer.getInt();
        final int leechersCount = byteBuffer.getInt();
        final int seedersCount = byteBuffer.getInt();

        final List<String> peerAddress = new ArrayList<>();

        int seek = 20;
        for (; seek < announceResponse.length; seek += 6) {

            final byte[] ipBlock = new byte[4];
            byteBuffer.get(ipBlock);
			final String ip = ipBlock[0] +
					"." +
                    ipBlock[1] +
					"." +
                    ipBlock[2] +
					"." +
                    ipBlock[3];

            final byte[] portBuffer = new byte[2];
            byteBuffer.get(portBuffer);
            final int port = StupidUtils.convertByteArrayToInt(portBuffer);

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
