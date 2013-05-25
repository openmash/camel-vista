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

package org.osehra.vista.camel.rpc.service;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class VistaServiceSupport implements VistaService {
    // by convention:
    // IDLE == 0, STARTING = 1, ACTIVE = 2, STOPPING = 3
    private AtomicInteger state = new AtomicInteger(0);

    public void start() {
        if (getState() == STATE.IDLE) {
            state.set(1); // starting
            try {
                startInternal();
            } catch (RuntimeException ex) {
                state.set(0); // back to idle
                return;
            }
            state.set(2);
        }
    }

    public void stop() {
        if (getState() == STATE.ACTIVE) {
            state.set(3); // stopping
            try {
                stopInternal();
            } catch (RuntimeException ex) {
                // LOG
            }
            state.set(0);
        }
    }

    public STATE getState() {
        switch (state.get()) {
        case 1: return STATE.STARTING;
        case 2: return STATE.ACTIVE;
        case 3: return STATE.STOPPING;
        }
        return STATE.IDLE;
    }
    
    protected abstract void startInternal() throws RuntimeException;
    protected abstract void stopInternal() throws RuntimeException;

}
