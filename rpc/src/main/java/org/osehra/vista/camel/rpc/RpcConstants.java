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

package org.osehra.vista.camel.rpc;


public final class RpcConstants {

    public static final int MAX_NS_LEN = 4;
    public static final int CODE_LEN = 5;
    public static final int PARAM_PACK_LEN = 3;

    public static final char NS_START = '[';
    public static final char NS_STOP = ']';
    public static final char PARAMS_START = '5';
    public static final char PARAM_TYPE_LITERAL = '0';
    public static final char PARAM_TYPE_REF = '1';
    public static final char PARAM_TYPE_LIST = '2';
    public static final char PARAM_TYPE_GLOBAL = '3';
    public static final char PARAM_TYPE_EMPTY = '4';
    public static final char PARAM_TYPE_STREAM = '5';
    public static final char PARAM_STOP = 'f';
    public static final char FRAME_STOP = '\4';

    public static final String RPC_DEFAULT_NS = "XWB";
    public static final String RPC_DEFAULT_CODE = "11302";
    public static final String RPC_VERSION = "2.0";
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 9220;

    private RpcConstants() {
        // Utility class
    }

}

