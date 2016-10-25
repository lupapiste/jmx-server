# jmx-server

A Java library for creating a JXM server with SO_REUSEADDR socket option.
This allows restarting Java and other JVM based (like Clojure or Scale)
applications immediately.

The JVM standard JMX server (started with `-Dcom.sun.management.jmxremote`
command line option) lacks this socket option. As a result, the JMX port might not
be available when the application is started soon after previous shutdown.

## Usage

Binaries are available from Clojars repository.
[![Clojars Project](https://img.shields.io/clojars/v/lupapiste/jmx-server.svg)](https://clojars.org/lupapiste/jmx-server)

### Java

```Java
JMXConnectorServer server = fi.lupapiste.jmx.ServerFactory.start(5050);
fi.lupapiste.jmx.ServerFactory.stop(server);
```

### Clojure

```Clojure
(let [server (fi.lupapiste.jmx.ServerFactory/start 5050)]
  (fi.lupapiste.jmx.ServerFactory/stop server))
```

## License

Copyright © 2016 Solita Oy

Distributed under the MIT License.
