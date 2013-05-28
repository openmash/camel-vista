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

import org.osehra.vista.camel.api.EmptyParameter;
import org.osehra.vista.camel.api.GlobalParameter;
import org.osehra.vista.camel.api.LiteralParameter;
import org.osehra.vista.camel.api.Parameter;
import org.osehra.vista.camel.api.ReferenceParameter;
import org.osehra.vista.camel.rpc.RpcConstants;
import org.osehra.vista.camel.rpc.RpcRequest;


public class RpcCommandsSupport {

    public static RpcRequest connect() {
        // TODO: use computed vs hardcoded defaults
        return connect("120.0.0.1", "localhost");
    }
    public static RpcRequest connect(String ipaddress, String hostname) {
        return request()
            .code("10304")
            .version(null)
            .name("TCPConnect")
            .parameter(literal(ipaddress))
            .parameter(literal("0"))
            .parameter(literal(hostname));
    }
    public static RpcRequest disconnect() {
        return request()
            .code("10304")
            .version(null)
            .name("#BYE#");    
    }
    public static RpcRequest login(String access, String verify) {
        StringBuffer av = new StringBuffer()
            .append("1").append(access).append(";").append(verify).append("1");
        return request()
            .name("XUS AV CODE")
            .parameter(literal(av.toString()));    
    }
    public static RpcRequest signon() {
        return request()
            .name("XUS SIGNON SETUP");
    }

    public static RpcRequest request() {
        return new RpcRequest()
            .namespace(RpcConstants.RPC_DEFAULT_NS)
            .code(RpcConstants.RPC_DEFAULT_CODE)
            .version(RpcConstants.RPC_VERSION);
    }

    public static Parameter literal(String value) {
        return new LiteralParameter(value);
    }
    public static Parameter ref(String value) {
        return new ReferenceParameter(value);
    }
    public static Parameter global(String key, String value) {
        return new GlobalParameter(key, value);
    }
    public static Parameter empty() {
        return new EmptyParameter();
    }

    protected RpcCommandsSupport() {
        // TODO: allow inheritance for convenience or enforce static utility class?
    }

}

