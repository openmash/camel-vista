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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.osehra.vista.camel.rpc.EmptyParameter;
import org.osehra.vista.camel.rpc.GlobalParameter;
import org.osehra.vista.camel.rpc.LiteralParameter;
import org.osehra.vista.camel.rpc.MapParameter;
import org.osehra.vista.camel.rpc.Parameter;
import org.osehra.vista.camel.rpc.ReferenceParameter;
import org.osehra.vista.camel.rpc.RpcConstants;
import org.osehra.vista.camel.rpc.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class RpcCodecUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RpcCodecUtils.class);

    public static final Charset DEF_CHARSET = Charset.forName("UTF-8");

    public static ChannelBuffer encodeRequest(final RpcRequest request) {
        return encodeRequest(request, true);
    }

    public static ChannelBuffer encodeRequest(final RpcRequest request, boolean encodeEmptyParamList) {
        ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
        
        try {
            cb.writeByte((byte) RpcConstants.NS_START);
            cb.writeBytes(request.getNamespace().getBytes(DEF_CHARSET));
            cb.writeByte((byte) RpcConstants.NS_STOP);

            cb.writeBytes(request.getCode().getBytes(DEF_CHARSET));
            
            encodeText(request.getVersion(), cb);
            encodeText(request.getName(), cb);
            
            List<Parameter> params = request.getParmeters();
            if (params.size() > 0 || encodeEmptyParamList) {
                cb.writeByte((byte) RpcConstants.PARAMS_START);
                if (params.size() > 0) {
                    for (Parameter p : params) {
                        encodeParameter(p, cb);
                    }
                } else {
                    encodeEmptyParameter(cb);
                }
            }
            cb.writeByte((byte) RpcConstants.FRAME_STOP);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Failed to encode request", e);
        }
        return cb;
    }

    public static void encodeText(final String text, ChannelBuffer out) throws UnsupportedEncodingException {
        if (text != null && text.length() > 0) {
            out.writeByte(text.length());
            out.writeBytes(text.getBytes(DEF_CHARSET));
        }
    }

    // TODO: refactor these as the default value of PARAM_PACK_LEN cannot be easily overriden, need a profile or something
    public static void encodeField(final String text, ChannelBuffer out) throws UnsupportedEncodingException {
        encodeField(text, RpcConstants.PARAM_PACK_LEN, out);
    }

    public static void encodeField(final String text, int lenlen, ChannelBuffer out) throws UnsupportedEncodingException {
        String f = "%0" + lenlen + "d"; // left pad with 0's up to length of lenlen
        out.writeBytes(String.format(f, text.length()).getBytes(DEF_CHARSET));
        out.writeBytes(text.getBytes(DEF_CHARSET));
    }

    public static void encodeParameter(final Parameter param, ChannelBuffer out) throws UnsupportedEncodingException {
        // TODO: there is a more elegant way of doing this
        if (param instanceof LiteralParameter) {
            encodeLiteralParameter((LiteralParameter)param, out);
        } else if (param instanceof ReferenceParameter) {
            encodeRefParameter((ReferenceParameter)param, out);
        } else if (param instanceof MapParameter) {
            encodeMapParameter((MapParameter)param, out);
        } else if (param instanceof GlobalParameter) {
            encodeGlobalParameter((GlobalParameter)param, out);
        } else if (param instanceof EmptyParameter) {
            encodeEmptyParameter((EmptyParameter)param, out);
        }
    }

    public static void encodeLiteralParameter(final LiteralParameter param, ChannelBuffer out) throws UnsupportedEncodingException {
        out.writeByte(RpcConstants.PARAM_TYPE_LITERAL);
        encodeField(param.getValue(), out);
        out.writeByte(RpcConstants.PARAM_STOP);
    }

    public static void encodeRefParameter(final ReferenceParameter param, ChannelBuffer out) throws UnsupportedEncodingException {
        out.writeByte(RpcConstants.PARAM_TYPE_REF);
        encodeField(param.getValue(), out);
        out.writeByte(RpcConstants.PARAM_STOP);
    }

    public static void encodeMapParameter(final MapParameter param, ChannelBuffer out) throws UnsupportedEncodingException {
        out.writeByte(RpcConstants.PARAM_TYPE_MAP);
        Map<String, String> map = param.getParams();

        if (map.size() == 0) {
            encodeField("", out);
            out.writeByte(RpcConstants.PARAM_STOP);
            return;
        }

        boolean sep = false;
        for (Map.Entry<String, String> kv : param.getParams().entrySet()) {
            if (sep) {
                out.writeByte('t');
            }
            sep = true;

            String value = kv.getValue();
            if (value == null || value.length() == 0) {
                value = "\001";
            }
            encodeField(kv.getKey(), out);
            encodeField(value, out);
        }
        out.writeByte(RpcConstants.PARAM_STOP);
    }

    public static void encodeGlobalParameter(final GlobalParameter param, ChannelBuffer out) throws UnsupportedEncodingException {
        out.writeByte(RpcConstants.PARAM_TYPE_GLOBAL);
        encodeField(param.getKey(), out);
        encodeField(param.getValue(), out);
        out.writeByte(RpcConstants.PARAM_STOP);
    }

    public static void encodeEmptyParameter(final EmptyParameter param, ChannelBuffer out) throws UnsupportedEncodingException {
        encodeEmptyParameter(out);
    }

    public static void encodeEmptyParameter(ChannelBuffer out) throws UnsupportedEncodingException {
        out.writeByte(RpcConstants.PARAM_TYPE_EMPTY);
        out.writeByte(RpcConstants.PARAM_STOP);
    }

    public static Parameter decodeParameter(ChannelBuffer in) throws CorruptedFrameException {
        Parameter param = null;
        // Fist byte indicates the parameter type
        byte b = in.readByte();
        switch (b) {
        case RpcConstants.FRAME_STOP:
            return null;
        case RpcConstants.PARAM_TYPE_LITERAL: {
            param = new LiteralParameter(decodeField(in));
            break;
        }
        case RpcConstants.PARAM_TYPE_REF: {
            param = new ReferenceParameter(decodeField(in));
            break;
        }
        case RpcConstants.PARAM_TYPE_MAP: {
            param = new MapParameter(decodeMap(in));
            break;
        }
        case RpcConstants.PARAM_TYPE_GLOBAL: {
            param = new GlobalParameter(decodeField(in), decodeField(in));
            break;
        }
        case RpcConstants.PARAM_TYPE_EMPTY: {
            param = new EmptyParameter();
            break;
        }
        case RpcConstants.PARAM_TYPE_STREAM: {
            // TODO: implement me
            break;
        }
        default:
            throw new CorruptedFrameException("Unkown RPC parameter type: '" + String.format("%02x ", b) + "'");
        }
        
        b = in.readByte();
        if (b != RpcConstants.PARAM_STOP) {
            throw new CorruptedFrameException("Expected end of parameter, got '" + String.format("%02x ", b) + "' instead");
        }
        return param;
    }

    public static String decodeField(ChannelBuffer in) {
        return decodeField(in, RpcConstants.PARAM_PACK_LEN);
    }
    
    public static String decodeField(ChannelBuffer in, int len) {
        int count = Integer.parseInt(in.readBytes(len).toString(RpcCodecUtils.DEF_CHARSET));
        String value = in.readBytes(count).toString(RpcCodecUtils.DEF_CHARSET);
        return value;
    }

    public static Map<String, String> decodeMap(ChannelBuffer in) {
        return decodeMap(in, RpcConstants.PARAM_PACK_LEN);
    }
    
    public static Map<String, String> decodeMap(ChannelBuffer in, int len) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        String key = decodeField(in);
        if (key == null || key.length() == 0) {
            return null;
        }
        byte b = 0;
        while (b != RpcConstants.PARAM_STOP) {
            String value = decodeField(in);
            if (value.equals("\001")) {
                value = "";
            }
            result.put(key, value);
            b = in.getByte(in.readerIndex());
            if (b == 't') {
                in.readByte();  // skip it and read next key
                key = decodeField(in);
            }
        }
        return result;
    }
    

    private RpcCodecUtils() {
        // Utility class
    }

}
