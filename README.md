# JNetcat

> A netcat utility for Windows  

[*linux version in development*]

A tool to easily debug or monitor traffic on TCP/UDP and fake a server or a client
  * Easier and more verbose than telnet (also work using UDP)
    + Verify if a port is open or not
    + Send custom data (interactive mode enabled -- client only)
  * No installation required, unzip the package and start the batch file
  * Use configuration file
    + with some parameters that can be easily overriden on commandline
    + use default custom protocol
  
Debug your application network issues :
  * Start in server mode (without multithreading) to see how your app act when server won't respond
  * Tests your timeout values
  * Test how your application act when you send random data
  * Verify the data that are sent from your application by looking at the server side
 
## RELEASE

> Version 1.0.0-RC4 and more recent are usable. Some configuration options are not implemented.

## CONFIGURATION FILE

conf/options.json

*"formatOutputType"* support one of these values:  
  * **NO_OUTPUT**:  Communications are in silent mode  
  * **SIMPLE**:     Standard input/output details  
  * **PRETTY_HEX**: Display all data using hexadecimal values  

*"serverType"* support one of these values (TCP): 
  * **BASIC**:  Listen only server"),
  * **ECHO**:   Echo requests on the server"),
  * **PROXY**:  Forward requests to another server");
  
*"serverType"* support one of these values (UDP):  
  * **BASIC**:  Listen only server  
  * **QUOTE**:  QUOTE server  
  * **SHELL**:  SHELL server  

## COMMAND LINE

```
C:\Projects\source\jnetcat\target\dist>jnc -h
A useful network tool to debug for client/server
        Version: 1.0.0-RC4

   -p -> Override Port parameter
  -ci -> interactive mode
   -c -> <t|u> Connect using: t-> TCP [default], u-> UDP
   -t -> <c|s> Act as: c-> client [default], s-> server
   -f -> Configuration file to use for default parameters
   -h -> Display complete informations about the parameters
   -i -> Override IP parameter
```

jnc-client => start as a client (using options.json for other parameters)  
jnc-server => start as a server (using options.json for other parameters)  

## TODO

* Move protocol implementation to external script 
  + eg; using groovy to implement or easily modify protocols
* Implement PROXY mode in TCP
* Fix SHELL UDP to return values when packet is bigger
* Implement multithreading
* Format output to match send and receive (actually: only receive)

