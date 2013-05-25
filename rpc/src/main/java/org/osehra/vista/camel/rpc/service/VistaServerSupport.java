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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VistaServerSupport extends VistaServiceSupport implements Runnable {
    private final static Logger LOG = LoggerFactory.getLogger(VistaServiceSupport.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    private AtomicBoolean completed = new AtomicBoolean();
    private long duration = -1;
    private Thread shutdownHook;

    @Override
    protected void startInternal() throws RuntimeException {
        shutdownHook = new HangupInterceptor(this);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        LOG.debug("Added shutdown hook for {}", this.getClass().getSimpleName());
    }

    @Override
    protected void stopInternal() throws RuntimeException {
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
            shutdownHook = null;
            LOG.debug("Removed shutdown hook for {}", this.getClass().getSimpleName());
        }
    }

    public void run() {
        if (!completed.get()) {
            // if we have an issue starting then propagate the exception to caller
            start();
            try {
                waitUntilCompleted();

                stop();
            } catch (Exception e) {
                LOG.error("Failed: {}", e);
            }
        }
    }

    protected void waitUntilCompleted() {
        while (!completed.get()) {
            try {
                if (duration >= 0) {
                    LOG.info("Running server for {} {}", duration, TimeUnit.MILLISECONDS);
                    latch.await(duration, TimeUnit.MILLISECONDS);
                    completed.set(true);
                } else {
                    latch.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void completed() {
        completed.set(true);
        latch.countDown();
    }

    /**
     * A class for intercepting the hang up signal and do a graceful shutdown
     */
    private static final class HangupInterceptor extends Thread {
        VistaService server;

        public HangupInterceptor(VistaService server) {
            this.server = server;
        }

        @Override
        public void run() {
            LOG.info("Hangup triggered - stopping server");
            server.stop();
        }
    }

}
