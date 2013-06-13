camel-vista
===========

Integration components for the OSEHRA VistA server via [Apache Camel][camel]

This component is licensed to the Open Source Electronic Health Record Agent (OSEHRA) 
under the [Apache License version 2.0][alv2]


### Prerequisites:

 * Java 6 or later (`java version "1.6.0_45"`)
 * Apache [Maven][mvn] (`Apache Maven 3.0.5`)

### Build steps

Run the following commands to create and build a local copy of the project.

    git clone git@github.com:openmash/camel-vista.git
    cd camel-vista
    mvn clean install

At this point the project is built, the tests should have passed and the binaries should be available in your local maven repository. The next step is to deploy it.

## Running in an Apache Karaf OSGi container

The following commands assume you have a Karaf OSGi container installed at `/opt/tesb/container`. We first need to create a configuration file for the vista bundle. This is done by creating a configuration file called `org.osehra.vista.cfg` in the container's `etc` directory with the following content:

    vista.host=54.236.116.134
    vistarpc.local=9220
    vistarpc.remote=11920
    vistacia.local=9460
    vistacia.remote=11960

The configuration entries are pretty self explanatory. The camel-vista bundle acts as a proxy router, capable of complex rule based value added processing, such as validation, filtering, content based routing, etc. The `*.local` entries are the ports open on the local machine for rpc and cia broker listeners. As such, camel-vista effectively impersonates a VistA server. At the other end, camel-vista can connect to an actual VistA server deployed on the `vista.host` machine (both IP and hostname values work) on the respective `*.remote` ports. Without the values above properly configured the camel-vista bundle will fail to start.

    cd /opt/tesb/container
    edit etc/org.osehra.vista.cfg
      [add content above, provide correct values, save and exit]

Now let's run the container. The bundle is easy to deploy simply by installing a Karaf feature:

    bin/trun
    [...]
    
    karaf@CamelONE> features:addurl mvn:org.osehra.vista.camel/vista-osgi/0.5/xml/features
    karaf@CamelONE> features:listurl
      Loaded   URI 
      [...]
      true    mvn:org.apache.camel.karaf/apache-camel/2.10.4/xml/features
      true    mvn:org.osehra.vista.camel/vista-osgi/0.5/xml/features

    karaf@CamelONE> features:list | grep vista
      [uninstalled] [0.5            ] vista-camel                           vista-camel-0.5        

    karaf@CamelONE> features:install vista-camel
      karaf@CamelONE> features:list | grep vista
      [installed  ] [0.5            ] vista-camel                           vista-camel-0.5        

    karaf@CamelONE> osgi:list
      START LEVEL 50 , List Threshold: 50
      [...]
      [ 212] [Active     ] [            ] [       ] [   80] VistA :: Camel :: RPC (0.5.0)
      [ 213] [Active     ] [            ] [       ] [   80] VistA :: Camel :: Camel (0.5.0)


Now send traffic using any VistA client, such as CPRS to the machine running the Karaf container. The CPRS client will work as if connected directly to a VistA server. To monitor traffic one could use the karaf camel commands or look at the logs.

    karaf@CamelONE> camel:route-list
      Route Id             Context Name         Status              
        [camel-rpc         ] [openmash          ] [Started           ]
        [camel-cia         ] [openmash          ] [Started           ]

    karaf@CamelONE> camel:route-info camel-rpc
      Camel Route camel-rpc
              Camel Context: openmash
      
      Properties
                      id = camel-rpc
                      parent = 5956d3a9
      
      Statistics
              Exchanges Total: 0
              Exchanges Completed: 0
              Exchanges Failed: 0
              Min Processing Time: 0ms
              Max Processing Time: 0ms
              Mean Processing Time: 0ms
              Total Processing Time: 0ms
              Last Processing Time: 0ms
              Load Avg: 0.00, 0.00, 0.00
              First Exchange Date:
              Last Exchange Completed Date:
      
      Definition
      <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
      <route id="camel-rpc" xmlns="http://camel.apache.org/schema/spring">
          <from uri="netty:tcp://0.0.0.0:{{vistarpc.local}}?serverPipelineFactory=#rpc-in&amp;sync=true"/>
          <to uri="log:org.osehra.vista.camel.proxy?level=INFO" id="to7"/>
          <threads poolSize="4" maxPoolSize="8" threadName="Threads" id="threads1">
              <to uri="netty:tcp:/{{vista.host}}:{{vistarpc.remote}}?clientPipelineFactory=#rpc-out&amp;sync=true" id="to8"/>
          </threads>
          <to uri="log:org.osehra.vista.camel.proxy?level=INFO" id="to9"/>
      </route>

If interested in tracing the traffic set the log level to DEBUG, and look for frames like the one below:

    09:38:17,897 | DEBUG | I/O worker #1667 | ?                                   ? | 205 - org.jboss.netty - 3.6.6.Final | [org.jboss.netty.handler.logging.LoggingHandler] [id: 0xd6aa0ceb, /127.0.0.1:44781 => /127.0.0.1:9220] RECEIVED: BigEndianHeapChannelBuffer(ridx=0, widx=69, cap=69)
             +-------------------------------------------------+
             |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
    +--------+-------------------------------------------------+----------------+
    |00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|
    |00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|
    |00000020| 38 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |8.1.100f00010f00|
    |00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|
    |00000040| 6f 72 67 66 04                                  |orgf.           |
    +--------+-------------------------------------------------+----------------+
    09:38:17,899 | DEBUG | I/O worker #1667 | amel.rpc.codec.RpcRequestDecoder   79 | 206 - org.osehra.vista.camel.vista-rpc - 0.5.0.SNAPSHOT | RPC.decode: namespace=XWB




[alv2]: http://www.apache.org/licenses/LICENSE-2.0 "Apache License version 2.0"
[camel]: http://camel.apache.org "Apache Camel"
[mvn]: http://camel.apache.org "Apache Maven"
[tesb]: http://www.talend.com/resource/open-source-esb.html "Talend Open Source ESB"


