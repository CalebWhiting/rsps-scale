# RSPSScale

A cross-server scalable client

# Usage
  java -jar <jar> [option=value]...

# Options

  path:       [required] The path to the servers client.jar (External-URL or Local-File)
              Example "path=http://server.com/client.jar"

  client:     [required] The main class of the client
              Example "com.server.Client"

  scale:      [optional] The scale of the client as a floating point number
              Example "scale=1.5"
              Default  2.0

  width:      [optional] The non-scaled width of the client
              Example "width=765"
              Default  765

  height:     [optional] The non-scaled height of the client
              Example "width=503"
              Default 503

  properties: [optional] The applet-stub parameters (External-URL or Local-File)
              Example "properties=http://server.com/server-config.properties"

# Properties File

  The properties file is a simple text document with key=value pairs (one pair per line),
  these properties are server specific.

  Example:
   codebase=http://server.com/
   lang=0
   worldid=1
