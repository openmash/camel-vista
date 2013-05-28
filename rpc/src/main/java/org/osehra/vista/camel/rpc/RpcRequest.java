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

import java.util.ArrayList;
import java.util.List;

import org.osehra.vista.camel.api.Parameter;


public final class RpcRequest {
    private String namespace;
    private String code;
    private String version;
    private String name;
    private final List<Parameter> parameters = new ArrayList<Parameter>();

    public String getNamespace() {
        return namespace;
    }
    public String getCode() {
        return code;
    }
    public String getVersion() {
        return version;
    }
    public String getName() {
        return name;
    }
    public List<Parameter> getParmeters() {
        return parameters;
    }

    public RpcRequest namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }
    public RpcRequest code(String code) {
        this.code = code;
        return this;
    }
    public RpcRequest version(String version) {
        this.version = version;
        return this;
    }
    public RpcRequest name(String name) {
        this.name = name;
        return this;
    }
    public RpcRequest parameter(Parameter param) {
        parameters.add(param);
        return this;
    }

}

