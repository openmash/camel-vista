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

import java.util.concurrent.Future;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.osehra.vista.camel.cia.CiaRequest;
import org.osehra.vista.camel.cia.CiaResponse;
import org.osehra.vista.camel.cia.codec.CiaCommandsSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcProxyBuilder extends RouteBuilder {
    private final static Logger LOG = LoggerFactory.getLogger(RpcProxyBuilder.class);

    @Override
    public void configure() throws Exception {
        LOG.info("Lookup found ");
        final ProducerTemplate async = getContext().createProducerTemplate();

        from("netty:tcp://localhost:9220?serverPipelineFactory=#cia-in&sync=true").routeId("camel-cia-in")
            .to("log:org.osehra.vista.camel.proxy?level=INFO")
            .threads(4)
            .to("netty:tcp://54.236.116.134:11960?clientPipelineFactory=#cia-out&sync=true")
            .to("log:org.osehra.vista.camel.proxy?level=INFO");
/*
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    Future<Object> reply = async.asyncRequestBody("direct:cia-proxy", exchange.getIn().getBody(CiaRequest.class));
                    LOG.info("SENT");
                    exchange.getOut().setBody(async.extractFutureBody(reply, CiaResponse.class));
                    LOG.info("RECEIVED");
                }
            });
*/
        from("timer:simple?period=15000").autoStartup(false)
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getOut().setBody(CiaCommandsSupport.connect("NOTVALID"));
                }
            })
//            .to("direct:cia-proxy")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    Future<Object> reply = async.asyncRequestBody("direct:cia-proxy", exchange.getIn().getBody(CiaRequest.class));
                    LOG.info("SENT");
                    exchange.getOut().setBody(async.extractFutureBody(reply, CiaResponse.class));
                    LOG.info("RECEIVED");

                    // CiaRequest request = exchange.getIn().getBody(CiaRequest.class);
                    // exchange.getOut().setBody(new CiaResponse().sequence(request.getSequence()).message("1^0^1.1^^1"));
                }
            })
            .to("log:org.osehra.vista.camel.proxy?level=INFO");

        from("direct:cia-proxy").routeId("camel-cia-out")
            .to("netty:tcp://54.236.116.134:11960?clientPipelineFactory=#cia-out&sync=true")
            .to("log:org.osehra.vista.camel.proxy?level=INFO");

/*
        from("direct:cia-proxy").routeId("camel-cia-out")
            .to("netty:tcp://54.236.116.134:11960?clientPipelineFactory=#cia-out&sync=true")
            .to("log:org.osehra.vista.camel.proxy?level=INFO")
            .process(new Processor() {
                // ignore response and send back a request
                public void process(Exchange exchange) throws Exception {
                    exchange.getOut().setBody(CiaCommandsSupport.connect("NOTVALID"));
                }
            });
*/
    }

}
