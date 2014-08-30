
                              JHttpTunnel
                     by ymnk@jcraft.com, JCraft,Inc.

                    http://www.jcraft.com/jhttptunnel/

Last modified: Mon May 16 17:15:05 UTC 2005


Description
===========
JHttpTunnel is the implementation of GNU httptunnel's protocol in
pure Java.


Documentation
=============
* README files all over the source tree have info related to the stuff
  in the directories. 
* ChangeLog: what changed from the previous version?


Directories & Files in the Source Tree
======================================
* src/ has source trees of JHttpTunnel
* example/ has some samples, which demonstrate the usages.


Why JHttpTunnel?
================
Our initial motivation for developing this stuff was to loose users from 
the cage made of HTTP on mobile platform based on J2ME. We have
believed the protocol of GNU httptunnel may be a key for it. GNU httptunnel is 
written and maintained by Lars Brinkhoff(http://lars.nocrew.org/) and 
its web page(http://www.nocrew.org/software/httptunnel.html) says as follows,

  httptunnel creates a bidirectional virtual data connection tunnelled in 
  HTTP requests. The HTTP requests can be sent via an HTTP proxy if so 
  desired. 
  This can be useful for users behind restrictive firewalls. If WWW access 
  is allowed through a HTTP proxy, it's possible to use httptunnel and, say,
  telnet or PPP to connect to a computer outside the firewall. 

The protocol of GNU httptunnel has been described in HACK file included in
its distribution archive.


Features
========
* JHttpTunnel implements GNU httptunnel's protocol.
  The current implementation works with httptunnel 3.0.5.
* accessing via HTTP proxy.
  We have confirmed Squid has allowed the communication.
* client side code
* JHttpTunnel is licensed under BSD style license(refer to LICENSE.txt).


How To Use
==========
Suppose your java program has following lines,

  Socket socket=new Socket(host, port);
  InputStream in=socket.getInputStream();
  OutputStream out=socket.getOutputStream();

You can replace them with following lines,

  JHttpTunnelClient jhtc=new JHttpTunnelClient(remote_host, remote_port);
  //jhtc.setProxy(porxy_host, proxy_port);
  jhtc.setInBound(new InBoundSocket());
  jhtc.setOutBound(new OutBoundSocket());
  jhtc.connect();
  InputStream in=jhtc.getInputStream();
  OutputStream out=jhtc.getOutputStream();

if you have already run 'hts' of GNU httptunnel on the host 'remote_host' with
following options,
  $ hts -F host:port remote_port

Please refer to 'examples/JHTC.java', which works like 'htc' of 
GNU httptunnel and will be good example to learn how to use JHttpTunnel's API.


SSH Over HttpTunnel in Java
===========================
We have enjoyed SSH over HttpTunnel in pure Java by using JSch(http://www.jcraft.com/jsch).
Please refer to 'examples/JSchOverJHttpTunnel.java' as an example.


JHttpTunnel for J2ME/CLDC/MIDP
=============================
We have confirmed that current version works on J2ME/CLDC/MIDP
environment included in Sun's Wireless Toolkit 2.2.
Use 'com.jcraft.jhttptunnel.InBoundConnection' and 
'com.jcraft.jhttptunnel.OutBoundConnection' and then apply the patch
'misc/httptunnel-3.0.5-0.0.3.patch' for GNU HttpTunnel 3.0.5.
For example, we have succeeded SSH2 remote execution over HttpTunnel
on Wireless Toolkit's emulator.  
The source code, jar and jad files for this example is available 
at http://j2me.jsch.org/


JHttpTunnel for J2ME/CLDC/DoJa
=============================
DoJa is an yet another J2ME/CLDC profile and widely used in Japan.
The difficulties of supporting this profile is that DoJa will not 
allow us to establish 2 http sessions simultaneously, but 
GNU HttpTunnel's 'hts' expects to have.
So, we have to implement our own 'hts' in pure Java.
After some hour hacks with reusing our code in our streaming server 'JRoar',
at last, we have confirmed that we can established bi-directional streams
from F900iC to the remote.

If you are interested in this preliminary hack, you can try as follows,
* Suppose that you are on the host 'foo.bar.com' and  you have 
  'foo.html', 'foo.jam' and 'foo.jar' in the current directory, where
  'foo.html' refers to 'foo.jam' and 'foo.jam' refers to 'foo.jar'.
* Run JHttpTunnelServer.
  $ java -DF=127.0.0.1:25 com.jcraft.jhttptunnel.JHttpTunnelServer 8888
  In this example, JHttpTunnel server will wait for requests at the
  port '8888' and establish a forwarding stream to 127.0.0.1:25.
* In your iAppli(foo.jar), you may have following lines,

  JHttpTunnelClient jhtc=new JHttpTunnelClinet("foo.bar.com", 8888);
  IOBoundDoJa iobd=new IOBoundDoJa();
  jhtc.setInBound(iobd.getInBound());
  jhtc.setOutBound(iobd.getOutBound());
  jhtc.connect();
  InputStream jin=jhtc.getInputStream();
  OutputStream jout=jhtc.getOutputStream();

  // operations for jin and jout

  jhtc.close(); 

* Now, get access to 'http://foo.bar.com:8888/foo.html'.

Frankly to say, the current implementation is still immature and
we will not recommend to use this hack for practical use.


TODO
====
* server side code
* HTTP-keepalive support on client and server


Copyrights & Disclaimers
========================
JHttpTunnel is copyrighted by ymnk, JCraft,Inc. and is licensed through 
BSD style license.
Read the LICENSE.txt file for the complete license.


Credits and Acknowledgments
============================
* JHttpTunnel is the implementation of GNU httptunnel's protocol.
  Without GNU httptunnel, we had not started this hack.


If you have any comments, suggestions and questions, write us 
at jhttptunnel@jcraft.com


``SSH is a registered trademark and Secure Shell is a trademark of
SSH Communications Security Corp (www.ssh.com)''.
