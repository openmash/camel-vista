/*
 * Copyright 2012-2013 The Open Source Electronic Health Record Agent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osehra.vista.camel.cia.codec;


import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.osehra.vista.camel.cia.CiaRequest;
import org.osehra.vista.camel.rpc.codec.RpcCodecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CiaRequestDecoder extends FrameDecoder {

    private final static Logger LOG = LoggerFactory.getLogger(CiaRequestDecoder.class);
    private final static int MIN_FRAME_LEN = 9;
    private final static String CIA_HEADER = "{CIA}";

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < MIN_FRAME_LEN) {
            // wait for more data
            return null;
        }

        // TODO: improve frame decoding when not enough bytes
        buffer.markReaderIndex();

        String hdr = buffer.readBytes(CIA_HEADER.length()).toString(RpcCodecUtils.DEF_CHARSET);
        if (!CIA_HEADER.equals(hdr) || buffer.readByte() != (byte)0xff) { // TODO: add constant
            throw new CorruptedFrameException("Invalid CIA frame (bad header)");
        }
        byte seq = buffer.readByte();
        byte type = buffer.readByte();

        Map<String, String> params = CiaCodecUtils.decodeRequestParams(buffer);
        if (params == null) {
            throw new CorruptedFrameException("Invalid CIA frame (bad parameter encoding)");
        }
        CiaRequest request = new CiaRequest().sequence(seq).type((char)type);
        request.getParameters().putAll(params);

        LOG.debug("Received request #{}", request.getSequence());
        return request;
    }

}

