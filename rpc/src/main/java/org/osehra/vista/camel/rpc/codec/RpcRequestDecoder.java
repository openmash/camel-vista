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

package org.osehra.vista.camel.rpc.codec;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.osehra.vista.camel.rpc.Parameter;
import org.osehra.vista.camel.rpc.RpcConstants;
import org.osehra.vista.camel.rpc.RpcRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcRequestDecoder extends ReplayingDecoder<RpcRequestDecoder.State> {

    private final static Logger LOG = LoggerFactory.getLogger(RpcRequestDecoder.class);

    private String namespace;
    private String code;
    private String version;
    private String name;
    private List<Parameter> params = new ArrayList<Parameter>();

    public RpcRequestDecoder() {
        super(State.READ_NS);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {

        // TODO: improve logging so every byte received is traced
        switch (state) {
        case READ_NS: {
            byte b = buffer.readByte();
            if (b != RpcConstants.NS_START) {
                break;
            }
            int ridx = buffer.readerIndex();
            int rbytes = actualReadableBytes();
            int pos = buffer.indexOf(ridx, ridx + rbytes, (byte)RpcConstants.NS_STOP);
            if (pos == -1) {
                // Namespace delimiter (']') not found
                if (rbytes > RpcConstants.MAX_NS_LEN) {
                    throw new TooLongFrameException();
                } else {
                    // Wait until more data is received
                    return null;
                }
            }
            int len = pos - ridx;
            if (len > RpcConstants.MAX_NS_LEN) {
                throw new TooLongFrameException();
            }
            namespace = buffer.readBytes(len).toString(RpcCodecUtils.DEF_CHARSET);
            buffer.skipBytes(1);

            LOG.debug("RPC.decode: namespace={}", namespace);
            checkpoint(State.READ_CODE);
        }
        case READ_CODE: {
            // Code has a fixed size of 5 chars
            code = buffer.readBytes(RpcConstants.CODE_LEN).toString(RpcCodecUtils.DEF_CHARSET);
            LOG.debug("RPC.decode: code={}", code);
            checkpoint(State.READ_NAME);
        }
        case READ_NAME: {
            byte b = '\0';
            while (name == null) {
                b = buffer.readByte();
                if (b == '\0') {
                    throw new CorruptedFrameException();
                }
                String s = buffer.readBytes((int)b).toString(RpcCodecUtils.DEF_CHARSET);
                if (version == null && s.charAt(0) >= '0' && s.charAt(0) <= '9') {
                    // TODO: when is version mandatory and when is it optional, if ever?
                    version = s;
                } else {
                    name = s;
                }
            }
            
            checkpoint(State.READ_PARAMS);
        }
        case READ_PARAMS: {
            byte b = buffer.readByte();
            if (b == RpcConstants.FRAME_STOP) {
                // no parameters
                break;
            }
            if (b != RpcConstants.PARAMS_START) {
                throw new CorruptedFrameException("Expected either parameters of end of frame.");
            }

            boolean eoframe = false;
            while (!eoframe) {
                Parameter param = RpcCodecUtils.decodeParameter(buffer);
                eoframe = param != null;
                if (!eoframe) {
                    params.add(param);
                }
            }
            break;
        }
        default:
            // Should not get here, all cases are handled
            throw new CorruptedFrameException();
        }

        ctx.getPipeline().remove(this);
        return new RpcRequest().name(name);
    }

    enum State {
        READ_NS,
        READ_CODE,
        READ_NAME,
        READ_PARAMS
    }

}

