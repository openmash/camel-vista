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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.osehra.vista.camel.rpc.RpcConstants;
import org.osehra.vista.camel.rpc.codec.RpcClientPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VistaClientMain implements Runnable {
    private final static Logger LOG = LoggerFactory.getLogger(VistaClientMain.class);
    
    private String host;
    private int port;

    private VistaClientMain() {
        this(RpcConstants.DEFAULT_HOST, RpcConstants.DEFAULT_PORT);
    }

    private VistaClientMain(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String... args) {
        // TODO: use configured defaults?
        if (args.length != 2) {
            System.err.println("Usage: " + VistaClientMain.class.getSimpleName() + " <host> <port>");
            return;
        }

        new VistaClientMain(args[0], Integer.parseInt(args[1])).run();
    }

    public void run() {
        LOG.info("Starting VistA RPC client");

        try {
            // Configure the client.
            ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

            // Set up the event pipeline factory.
            bootstrap.setPipelineFactory(new RpcClientPipelineFactory());
            
            ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));
            Channel channel = connectFuture.awaitUninterruptibly().getChannel();

            // Shut down all thread pools to exit.
            bootstrap.releaseExternalResources();
        } finally {
            // TODO: anything to closer gracefully?
        }
    }

}
