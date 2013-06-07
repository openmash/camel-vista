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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;

import org.osehra.vista.camel.cia.CiaRequest;
import org.osehra.vista.camel.rpc.codec.RpcCodecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CiaCodecUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CiaCodecUtils.class);
    public static final Charset DEF_CHARSET = Charset.forName("UTF-8");
    public static final byte EOD = (byte)0xff;
    private static byte sequence = 0;

    private static final Random RANDOM = new Random();
    private static String[] CIPHER = {
        "wkEo-ZJt!dG)49K{nX1BS$vH<&:Myf*>Ae0jQW=;|#PsO`'%+rmb[gpqN,l6/hFC@DcUa ]z~R}\"V\\iIxu?872.(TYL5_3",
        "rKv`R;M/9BqAF%&tSs#Vh)dO1DZP> *fX'u[.4lY=-mg_ci802N7LTG<]!CWo:3?{+,5Q}(@jaExn$~p\\IyHwzU\"|k6Jeb",
        "\\pV(ZJk\"WQmCn!Y,y@1d+~8s?[lNMxgHEt=uw|X:qSLjAI*}6zoF{T3#;ca)/h5%`P4$r]G'9e2if_>UDKb7<v0&- RBO.",
        "depjt3g4W)qD0V~NJar\\B \"?OYhcu[<Ms%Z`RIL_6:]AX-zG.#}$@vk7/5x&*m;(yb2Fn+l'PwUof1K{9,|EQi>H=CT8S!",
        "NZW:1}K$byP;jk)7'`x90B|cq@iSsEnu,(l-hf.&Y_?J#R]+voQXU8mrV[!p4tg~OMez CAaGFD6H53%L/dT2<*>\"{\\wI=",
        "vCiJ<oZ9|phXVNn)m K`t/SI%]A5qOWe\\&?;jT~M!fz1l>[D_0xR32c*4.P\"G{r7}E8wUgyudF+6-:B=$(sY,LkbHa#'@Q",
        "hvMX,'4Ty;[a8/{6l~F_V\"}qLI\\!@x(D7bRmUH]W15J%N0BYPkrs&9:$)Zj>u|zwQ=ieC-oGA.#?tfdcO3gp`S+En K2*<",
        "jd!W5[];4'<C$/&x|rZ(k{>?ghBzIFN}fAK\"#`p_TqtD*1E37XGVs@0nmSe+Y6Qyo-aUu%i8c=H2vJ\\) R:MLb.9,wlO~P",
        "2ThtjEM+!=xXb)7,ZV{*ci3\"8@_l-HS69L>]\\AUF/Q%:qD?1~m(yvO0e'<#o$p4dnIzKP|`NrkaGg.ufCRB[; sJYwW}5&",
        "vB\\5/zl-9y:Pj|=(R'7QJI *&CTX\"p0]_3.idcuOefVU#omwNZ`$Fs?L+1Sk<,b)hM4A6[Y%aDrg@~KqEW8t>H};n!2xG{",
        "sFz0Bo@_HfnK>LR}qWXV+D6`Y28=4Cm~G/7-5A\\b9!a#rP.l&M$hc3ijQk;),TvUd<[:I\"u1'NZSOw]*gxtE{eJp|y (?%",
        "M@,D}|LJyGO8`$*ZqH .j>c~h<d=fimszv[#-53F!+a;NC'6T91IV?(0x&/{B)w\"]Q\\YUWprk4:ol%g2nE7teRKbAPuS_X",
        ".mjY#_0*H<B=Q+FML6]s;r2:e8R}[ic&KA 1w{)vV5d,$u\"~xD/Pg?IyfthO@CzWp%!`N4Z'3-(o|J9XUE7k\\TlqSb>anG",
        "xVa1']_GU<X`|\\NgM?LS9{\"jT%s$}y[nvtlefB2RKJW~(/cIDCPow4,>#zm+:5b@06O3Ap8=*7ZFY!H-uEQk; .q)i&rhd",
        "I]Jz7AG@QX.\"%3Lq>METUo{Pp_ |a6<0dYVSv8:b)~W9NK`(r'4fs&wim\\kReC2hg=HOj$1B*/nxt,;c#y+![?lFuZ-5D}",
        "Rr(Ge6F Hx>q$m&C%M~Tn,:\"o'tX/*yP.{lZ!YkiVhuw_<KE5a[;}W0gjsz3]@7cI2\\QN?f#4p|vb1OUBD9)=-LJA+d`S8",
        "I~k>y|m};d)-7DZ\"Fe/Y<B:xwojR,Vh]O0Sc[`$sg8GXE!1&Qrzp._W%TNK(=J 3i*2abuHA4C'?Mv\\Pq{n#56LftUl@9+",
        "~A*>9 WidFN,1KsmwQ)GJM{I4:C%}#Ep(?HB/r;t.&U8o|l['Lg\"2hRDyZ5`nbf]qjc0!zS-TkYO<_=76a\\X@$Pe3+xVvu",
        "yYgjf\"5VdHc#uA,W1i+v'6|@pr{n;DJ!8(btPGaQM.LT3oe?NB/&9>Z`-}02*%x<7lsqz4OS ~E$\\R]KI[:UwC_=h)kXmF",
        "5:iar.{YU7mBZR@-K|2 \"+~`M%8sq4JhPo<_X\\Sg3WC;Tuxz,fvEQ1p9=w}FAI&j/keD0c?)LN6OHV]lGy'$*>nd[(tb!#" };


    public static byte nextSequenceIndex(byte current) {
        return (byte)(++current == 0 ? 1 : current);
    }

    public static ChannelBuffer encodeRequest(final CiaRequest request) {
        ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
        
        sequence = nextSequenceIndex(sequence);
        cb.writeBytes("{CIA}".getBytes(DEF_CHARSET));
        cb.writeByte(EOD);
        cb.writeByte(sequence);
        cb.writeByte(request.getType());

        for (Map.Entry<String, String> pair : request.getParameters().entrySet()) {
            cb.writeBytes(encodeLen(pair.getKey()));
            cb.writeBytes(pair.getKey().getBytes(DEF_CHARSET));
            cb.writeByte((byte)0);
            cb.writeBytes(encodeLen(pair.getValue()));
            cb.writeBytes(pair.getValue().getBytes(DEF_CHARSET));
        }
        cb.writeByte(EOD);

        return cb;
    }

    public static Map<String, String> decodeRequestParams(ChannelBuffer in) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        byte b = 0;
        int len = 0;
        while (b != EOD) {
            b = in.readByte();
            if (b == EOD) {
                break;
            }
            len = decodeLen(in, b);
            String key = in.readBytes(len).toString(RpcCodecUtils.DEF_CHARSET);
            b = in.readByte();
            if (b != 0x00) {
                return null;
            }
            b = in.readByte();
            len = decodeLen(in, b);
            String value = in.readBytes(len).toString(RpcCodecUtils.DEF_CHARSET);
            result.put(key, value);
        }
        return result;
    }

    public static int decodeLen(ChannelBuffer in, int len) {
        int result = 0;
        int low = len % 16;
        int count = len >> 4;

        byte[] dst = new byte[count];
        in.readBytes(dst);
        for (int i = 0; i < count; i++) {
            result += (dst[i] & 0xff);
            result <<= (i == count - 1 ? 4 : 8);
        }

        return result + low;
    }

    public static byte[] encodeLen(String value) {
        return encodeLen(value.length());
    }

    public static byte[] encodeLen(int slen) {
        int low = slen % 16;
        slen = slen >> 4;

        LinkedList<Byte> bytes = new LinkedList<Byte>();
        int highCount = 0;
        while (slen != 0) {
            bytes.addFirst((byte)slen);
            slen >>= 8;
            highCount++;
        }
        bytes.addFirst((byte)((highCount << 4) + low));

        int i = 0;
        byte[] result = new byte[bytes.size()];
        for (Byte b : bytes) { 
            result[i++] = b.byteValue();
        }
        return result;
    }

    public static String encrypt(String value) {
        int ra = RANDOM.nextInt(19);
        int rb;
        do {
            rb = RANDOM.nextInt(19);
        } while (rb == ra || rb == 0);
        ra = 7;
        rb = 17;
        String ca = CIPHER[ra];
        String cb = CIPHER[rb];
        int len = value.length();
        int pos = 0;

        byte buffer[] = new byte[len + 2];
        buffer[pos++]= (byte) (ra + 32);
        for (int i = 0; i < len; ++i) {
            char c = value.charAt(i);
            int index = ca.indexOf(c);
            buffer[pos++] = index == -1 ? (byte)c : (byte)cb.charAt(index);
        }
        buffer[pos++]= (byte)(rb + 32);
        try {
            return new String(buffer, 0, len + 2, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore, shouldn't happen
        }
        return "";
    }

    private CiaCodecUtils() {
        // Utility class
    }

}
