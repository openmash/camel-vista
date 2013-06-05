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

package org.osehra.vista.camel.cia;

import java.util.LinkedHashMap;
import java.util.Map;


public final class CiaRequest {
    public static final String KEY_CTX = "CTX";
    public static final String KEY_UID = "UID";
    public static final String KEY_VER = "VER";
    public static final String KEY_RPC = "RPC";

    private char type;
    private byte sequence;
    private final Map<String, String> parameters = new LinkedHashMap<String, String>();

    public char getType() {
        return type;
    }
    public byte getSequence() {
        return sequence;
    }
    public Map<String, String> getParameters() {
        return parameters;
    }
    public String getParameter(String key) {
        return parameters.get(key);
    }
    public String getContext() {
        return parameters.get(KEY_CTX);
    }
    public String getUID() {
        return parameters.get(KEY_UID);
    }
    public String getVersion() {
        return parameters.get(KEY_VER);
    }
    public String getRpc() {
        return parameters.get(KEY_RPC);
    }

    public CiaRequest type(char type) {
        this.type = type;
        return this;
    }
    public CiaRequest sequence(byte sequence) {
        this.sequence = sequence;
        return this;
    }
    public CiaRequest context(String value) {
        return parameter(KEY_CTX, value);
    }
    public CiaRequest uid(String value) {
        return parameter(KEY_UID, value);
    }
    public CiaRequest ver(String value) {
        return parameter(KEY_VER, value);
    }
    public CiaRequest rpc(String value) {
        return parameter(KEY_RPC, value);
    }
    public CiaRequest parameters(String[] params) {
        int key = 0;
        for (String p : params) {
            parameters.put(Integer.toString(++key), p);
        }
        return this;
    }
    public CiaRequest parameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

}
