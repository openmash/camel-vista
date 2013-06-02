camel-vista
===========

Integration components for the OSEHRA VistA server via [Apache Camel](camel.apache.org)

## Run Trace Server

To run the Trace server:

    $ cd rpc
    $ mvn exec:java -Dtarget.main.class=org.osehra.vista.camel.rpc.service.VistaRequestTracer

## Run VistA client

To run the VistA client (in a separate window):

    $ cd rpc
    $ mvn exec:java -P run-client -Dexec.args="localhost 9220"

## Sample output from Tracer server:

    org.osehra.vista.camel.rpc.service.VistaServiceSupport DEBUG Added shutdown hook for VistaRequestTracer
    org.jboss.netty.channel.socket.nio.SelectorUtil DEBUG Using select timeout of 500
    org.jboss.netty.channel.socket.nio.SelectorUtil DEBUG Epoll-bug workaround enabled = false
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] OPEN
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] OPEN
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] BOUND: /127.0.0.1:9220
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] BOUND: /127.0.0.1:9220
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] CONNECTED: /127.0.0.1:47377
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] CONNECTED: /127.0.0.1:47377
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 => /127.0.0.1:9220] RECEIVED: BigEndianHeapChannelBuffer(ridx=0, widx=69, cap=69)
             +-------------------------------------------------+
             |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
    +--------+-------------------------------------------------+----------------+
    |00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|
    |00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|
    |00000020| 39 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |9.1.100f00010f00|
    |00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|
    |00000040| 6f 72 67 66 04                                  |orgf.           |
    +--------+-------------------------------------------------+----------------+
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  Received 69/69 bytes
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 :> /127.0.0.1:9220] DISCONNECTED
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0x668c35a9, /127.0.0.1:47377 :> /127.0.0.1:9220] DISCONNECTED
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 :> /127.0.0.1:9220] UNBOUND
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0x668c35a9, /127.0.0.1:47377 :> /127.0.0.1:9220] UNBOUND
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0x668c35a9, /127.0.0.1:47377 :> /127.0.0.1:9220] CLOSED
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0x668c35a9, /127.0.0.1:47377 :> /127.0.0.1:9220] CLOSED
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] OPEN
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] OPEN
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] BOUND: /127.0.0.1:9220
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] BOUND: /127.0.0.1:9220
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] CONNECTED: /127.0.0.1:47379
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] CONNECTED: /127.0.0.1:47379
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] RECEIVED: BigEndianHeapChannelBuffer(ridx=0, widx=69, cap=69)
             +-------------------------------------------------+
             |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
    +--------+-------------------------------------------------+----------------+
    |00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|
    |00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|
    |00000020| 39 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |9.1.100f00010f00|
    |00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|
    |00000040| 6f 72 67 66 04                                  |orgf.           |
    +--------+-------------------------------------------------+----------------+
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  Received 69/69 bytes
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 => /127.0.0.1:9220] RECEIVED: BigEndianHeapChannelBuffer(ridx=0, widx=20, cap=20)
             +-------------------------------------------------+
             |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
    +--------+-------------------------------------------------+----------------+
    |00000000| 5b 58 57 42 5d 31 30 33 30 34 05 23 42 59 45 23 |[XWB]10304.#BYE#|
    |00000010| 35 34 66 04                                     |54f.            |
    +--------+-------------------------------------------------+----------------+
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  Received 20/89 bytes
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 :> /127.0.0.1:9220] DISCONNECTED
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0xf8e29f42, /127.0.0.1:47379 :> /127.0.0.1:9220] DISCONNECTED
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 :> /127.0.0.1:9220] UNBOUND
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0xf8e29f42, /127.0.0.1:47379 :> /127.0.0.1:9220] UNBOUND
    org.jboss.netty.handler.logging.LoggingHandler DEBUG [id: 0xf8e29f42, /127.0.0.1:47379 :> /127.0.0.1:9220] CLOSED
    org.osehra.vista.camel.rpc.service.TracerHandler INFO  [id: 0xf8e29f42, /127.0.0.1:47379 :> /127.0.0.1:9220] CLOSED
    ^Corg.osehra.vista.camel.rpc.service.VistaServiceSupport INFO  Hangup triggered - stopping server
