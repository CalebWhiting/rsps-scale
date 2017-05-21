# RSPSScale

A cross-server scalable client

# Usage
  java -jar <jar> [option=value]...

# Options

  path:       [required] The path to the servers client.jar (External-URL or Local-File)<br>
              Example "path=http://&lt;server&gt;.com/client.jar"

  client:     [required] The main class of the client<br>
              Example "client=com.server.Client"

  scale:      [optional] The scale of the client as a floating point number<br>
              Example "scale=1.5"<br>
              Default  2.0

  width:      [optional] The non-scaled width of the client<br>
              Example "width=765"<br>
              Default  765

  height:     [optional] The non-scaled height of the client<br>
              Example "width=503"<br>
              Default 503

  properties: [optional] The applet-stub parameters (External-URL or Local-File)<br>
              Example "properties=http://&lt;server&gt;.com/server-config.properties"

# Properties File

  The properties file is a simple text document with key=value pairs (one pair per line),
  these properties are server specific.

  Example:<br>
   codebase=http://&lt;server&gt;.com/<br>
   lang=0<br>
   worldid=1<br>
