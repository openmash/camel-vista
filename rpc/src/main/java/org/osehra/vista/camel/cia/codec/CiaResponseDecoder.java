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


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import org.osehra.vista.camel.cia.CiaResponse;
import org.osehra.vista.camel.rpc.codec.RpcCodecUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CiaResponseDecoder extends FrameDecoder {

    private final static Logger LOG = LoggerFactory.getLogger(CiaResponseDecoder.class);

    @Override
    protected Object decode(final ChannelHandlerContext ctx,
            final Channel channel, final ChannelBuffer buffer) throws Exception {

        byte seq = buffer.readByte();
        if (buffer.readByte() != 0) {
            throw new CorruptedFrameException("Frame start expected (0x00)");
        }
        return new CiaResponse().sequence(seq)
            .message(buffer.readBytes(buffer.readableBytes()).toString(RpcCodecUtils.DEF_CHARSET));
    }

}

