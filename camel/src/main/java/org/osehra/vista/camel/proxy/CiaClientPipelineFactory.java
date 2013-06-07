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

package org.osehra.vista.camel.proxy;

import org.apache.camel.component.netty.ClientPipelineFactory;
import org.apache.camel.component.netty.NettyProducer;
import org.apache.camel.component.netty.handlers.ClientChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.osehra.vista.camel.cia.codec.CiaRequestEncoder;
import org.osehra.vista.camel.cia.codec.CiaResponseDecoder;


public class CiaClientPipelineFactory extends ClientPipelineFactory {
    private final NettyProducer producer;

    public CiaClientPipelineFactory(NettyProducer producer) {
        this.producer = producer;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {

        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("logger", new LoggingHandler(InternalLogLevel.INFO));
        pipeline.addLast("decoder", new CiaResponseDecoder());
        pipeline.addLast("encoder", new CiaRequestEncoder());
        pipeline.addLast("handler", new ClientChannelHandler(producer));

        return pipeline;
    }

    @Override
    public ClientPipelineFactory createPipelineFactory(NettyProducer producer) {
        return new CiaClientPipelineFactory(producer);
    }

}

