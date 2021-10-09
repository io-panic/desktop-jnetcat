# JNetcat

A tool to easily debug or monitor traffic on TCP/UDP and fake a server or a client
  * Easier and more verbose than telnet (and work as an UDP 'telnet')
    + Verify if a port is open or not
    + Send custom data (interactive mode enabled)
  * No installation required, just unzip the package

Debug your application network issues :
  * Start in server mode (without multithreading) to see how your app act when server won't respond
  * Tests your timeout values
  * Test how your application act when you send random data
  * Verify the data that are sent from your application by looking at the server side

This project wants to be similar to netcat while being user friendly
  * Use configuration file (with some parameters that can be easily overriden)
  * Use default protocol (echo, quote, ...)
  * TODO: Use custom protocol implementation (eg; using Groovy)
 
 
# RELEASE

The project is in draft mode and may or may not be usable at the moment.


# COMMAND LINE

```
C:\Projects\source\jnetcat\target\dist>jnetcat -h
A useful network tool to debug for client/server
        Version: 1.0.0-RC2

   -p -> Override Port parameter
  -ci -> interactive mode
   -c -> <t|u> Connect using: t-> TCP [default], u-> UDP
   -t -> <c|s> Act as: c-> client [default], s-> server
   -f -> Configuration file to use for default parameters
   -h -> Display complete informations about the parameters
   -i -> Override IP parameter
```
