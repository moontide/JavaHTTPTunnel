/* -*-mode:java; c-basic-offset:2; -*- */
/*
Copyright (c) 2004 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import com.jcraft.jhttptunnel.*;

import java.net.*;
import java.io.*;

public class JHTC{
  public static void main(String[] arg){
    try{

      if(arg.length==0){
	System.err.println("Enter hostname[:port]");
	System.exit(1);
      }

      String host=arg[0];
      int hport=8888;
      if(host.indexOf(':')!=-1){
	hport=Integer.parseInt(host.substring(host.lastIndexOf(':') + 1));
	host=host.substring(0, host.lastIndexOf(':'));
      }

      int port=2323;
      String _port=System.getProperty("F");
      if(_port!=null){
	port=Integer.parseInt(_port);
      }

      String proxy_host=System.getProperty("P");
      int proxy_port=8080;
      if(proxy_host!=null && proxy_host.indexOf(':')!=-1){
	proxy_port=Integer.parseInt(proxy_host.substring(proxy_host.lastIndexOf(':') + 1));
	proxy_host=proxy_host.substring(0, proxy_host.lastIndexOf(':'));
      }

      ServerSocket ss=new ServerSocket(port);
      while(true){
	Socket socket=ss.accept();
	socket.setTcpNoDelay(true);

	System.out.println("accept: "+socket);

        final InputStream sin=socket.getInputStream();
        final OutputStream sout=socket.getOutputStream();

	final JHttpTunnelClient jhtc=new JHttpTunnelClient(host, hport);
	if(proxy_host!=null){
	  jhtc.setProxy(proxy_host, proxy_port);
	}

	// jhtc.setInBound(new InBoundURL());
	// jhtc.setOutBound(new OutBoundURL());
	jhtc.setInBound(new InBoundSocket());
	jhtc.setOutBound(new OutBoundSocket());

	jhtc.connect();
        final InputStream jin=jhtc.getInputStream();
        final OutputStream jout=jhtc.getOutputStream();

	Runnable runnable=new Runnable(){
	    public void run(){
	      byte[] tmp=new byte[1024];
	      try{
		while(true){
		  int i=jin.read(tmp);
		  if(i>0){
		    sout.write(tmp, 0, i);
		    continue;
		  }
		  break;
		}
	      }
	      catch(Exception e){
	      }
	      try{
		sin.close();
		jin.close();
		jhtc.close();
	      }
	      catch(Exception e){
	      }
	    }
	  };
	(new Thread(runnable)).start();

	byte[] tmp=new byte[1024];
	try{
	  while(true){
	    int i=sin.read(tmp);
//System.out.println("i="+i+" "+jout);
	    if(i>0){
	      jout.write(tmp, 0, i);
	      continue;
	    }
	    break;
	  }
	}
	catch(Exception e){
	  //System.out.println(e);
	}
	try{
	  socket.close();
	  jin.close();
	  jhtc.close();
	}
	catch(Exception e){
	}
      }
    }
    catch(JHttpTunnelException e){
      System.err.println(e);
    }
    catch(IOException e){
      System.err.println(e);
    }
  }
}
