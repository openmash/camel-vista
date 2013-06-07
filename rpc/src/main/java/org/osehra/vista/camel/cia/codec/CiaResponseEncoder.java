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
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.osehra.vista.camel.cia.CiaResponse;
import org.osehra.vista.camel.rpc.codec.RpcCodecUtils;


public class CiaResponseEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof CiaResponse) {
            CiaResponse response = (CiaResponse)msg;
            
            ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
            cb.writeByte(response.getSequence());
            cb.writeByte((byte)0x00);
            cb.writeBytes(response.getMessage().getBytes(RpcCodecUtils.DEF_CHARSET));
            cb.writeByte((byte)0xff);
            return cb;
        }
        return msg;
    }

}

