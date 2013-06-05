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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.junit.Assert;
import org.junit.Test;
import org.osehra.vista.camel.cia.codec.CiaCodecUtils;
import org.osehra.vista.camel.rpc.RpcConstants;


public class RpcCodecUtilsTest {

    @Test
    public void testTextEncoding() {
        try {
            String text = "dummy";
            ChannelBuffer out = ChannelBuffers.dynamicBuffer();
            RpcCodecUtils.encodeText(text, out);

            Assert.assertEquals(text.length() + 1, out.readableBytes());
            Assert.assertEquals("\5dummy", out.readBytes(text.length() + 1).toString(RpcCodecUtils.DEF_CHARSET));
        } catch (UnsupportedEncodingException e) {
            Assert.fail("String encoding failed " + e);
        }
    }

    @Test
    public void testValueEncoding() {
        try {
            int len = RpcConstants.PARAM_PACK_LEN;
            String value = "dummy";
            ChannelBuffer out = ChannelBuffers.dynamicBuffer();
            RpcCodecUtils.encodeField(value, len, out);

            Assert.assertEquals(value.length() + len, out.readableBytes());
            Assert.assertEquals("005dummy", out.readBytes(value.length() + len).toString(RpcCodecUtils.DEF_CHARSET));
        } catch (UnsupportedEncodingException e) {
            Assert.fail("String encoding failed " + e);
        }
    }

    @Test
    public void testFoo() {
        System.out.println((byte)14);
        System.out.println((byte)(14 & 0xff));
        System.out.println((byte)114);
        System.out.println((byte)(114 & 0xff));
        System.out.println((byte)1114);
        System.out.println((byte)(1114 & 0xff));
        System.out.println((byte)356);
        System.out.println((byte)(356 & 0xff));

        int slen = 21;
        int low = slen % 16;
        slen = slen >> 4;
        List<Byte> bytes = new ArrayList<Byte>();
        int highCount = 0;
        while (slen != 0) {
            bytes.add(new Byte((byte)slen));
            slen = slen >> 8;
            highCount += 1;
        }

        System.out.println("hicount=" + highCount);
        System.out.println("count=" + bytes.size());
        for (Byte b : bytes) {
            System.out.print(b);
            System.out.print(" ");
        }

        System.out.println("TOTAL");
        System.out.print((highCount << 4) + low);
        System.out.print(" ");
        Collections.reverse(bytes);
        for (Byte b : bytes) {
            System.out.print(b);
            System.out.print(" ");
        }

    }
}

