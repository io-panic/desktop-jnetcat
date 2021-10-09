# JNetcat

A tool to easily debug or monitor traffic on TCP/UDP and simulate a server or client
  * No need of telnet anymore to test for a remote connection

This project wants to be similar to netcat while being user friendly
  * Use configuration file
  * Use default protocol
  * Use custom implementation (eg; using Groovy)
 

# RELEASE

The project is in draft mode and may or may not be usable at the moment.


# COMMAND LINE

```
C:\Projects\source\jnetcat\target\dist>jnetcat.bat -h
A useful network tool to debug for client/server
        Version: 1.0.0-RC1

   -p -> Override Port parameter
  -ci -> interactive mode
   -c -> Act as a client
   -s -> Act as a server
   -t -> Use TCP
   -u -> Use UDP
   -f -> Configuration file to use for default parameters
   -h -> Display complete informations about the parameters
   -i -> Override IP parameter
```
