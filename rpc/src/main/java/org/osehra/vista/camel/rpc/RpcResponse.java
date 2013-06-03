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


public final class RpcResponse {
    private List<List<String>> content = new ArrayList<List<String>>();

    public RpcResponse() {
    }

    public List<List<String>> getContent() {
        return content;
    }
    public List<String> getRow(int row) {
        return row >= 0 && row < content.size() ? content.get(row) : null;
    }
    public String getField(int row, int column) {
        List<String> r = getRow(row);
        return column >= 0 && column < r.size() ? r.get(column) : null;
    }

    public RpcResponse row() {
        return row(null);
    }
    public RpcResponse row(final List<String> row) {
        content.add(row == null ? new ArrayList<String>() : new ArrayList<String>(row));
        return this;
    }
    public RpcResponse field(String value) {
        if (content.size() == 0) {
            throw new IllegalArgumentException("Must create rows first");
        }
        getRow(content.size() - 1).add(value);
        return this;
    }

}

