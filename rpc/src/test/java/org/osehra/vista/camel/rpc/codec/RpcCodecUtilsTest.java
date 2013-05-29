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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.junit.Assert;
import org.junit.Test;


public class RpcCodecUtilsTest {

    @Test
    public void testTextEncoding() {
        runTextEncodingTest("dummy");
    }
    
    @Test
    public void testFieldEncoding() {
        runTextEncodingTest("dummy");
    }
    
    protected void runTextEncodingTest(String text) {
        try {
            ChannelBuffer out = ChannelBuffers.dynamicBuffer();
            RpcCodecUtils.encodeText(text, out);

            Assert.assertEquals(text.length() + 1, out.readableBytes());
            Assert.assertEquals(text.length(), out.readByte());
            Assert.assertEquals(text, out.readBytes(text.length()).toString(RpcCodecUtils.DEF_CHARSET));
        } catch (UnsupportedEncodingException e) {
            Assert.fail("String encoding failed " + e);
        }
    }

    protected void runFieldEncodingTest(String text, int lenlen) {
        try {
            ChannelBuffer out = ChannelBuffers.dynamicBuffer();
            RpcCodecUtils.encodeField(text, lenlen, out);

            Assert.assertEquals(text.length() + lenlen, out.readableBytes());
            int len = Integer.parseInt(out.readBytes(lenlen).toString(RpcCodecUtils.DEF_CHARSET));
            Assert.assertEquals(text.length(), len);
            Assert.assertEquals(text, out.readBytes(len).toString(RpcCodecUtils.DEF_CHARSET));
        } catch (UnsupportedEncodingException e) {
            Assert.fail("String encoding failed " + e);
        }
    }

}

