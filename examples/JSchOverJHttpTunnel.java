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

import com.jcraft.jsch.*;
import com.jcraft.jhttptunnel.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

public class JSchOverJHttpTunnel{
  public static void main(String[] arg){

    try{
      JSch jsch=new JSch();

      if(arg.length==0){
	System.err.println("Enter username@hostname[:port]");
	System.exit(1);
      }
      String host=arg[0];
      String user=host.substring(0, host.indexOf('@'));
      host=host.substring(host.indexOf('@')+1);
      int port=8888;
      if(host.indexOf(':')!=-1){
	try{
	  port=Integer.parseInt(host.substring(host.lastIndexOf(':') + 1));
	  host=host.substring(0, host.lastIndexOf(':'));
	} 
	catch (Exception e) {	
	  System.err.println(e);
	  System.exit(1);
	}
      }

      Session session=jsch.getSession(user, host, port);
      //session.setPassword("your password");
 
      UserInfo ui=new MyUserInfo();
      session.setUserInfo(ui);

      session.setSocketFactory(new SocketFactory() {
	  InputStream in=null;
	  OutputStream out=null;
	  JHttpTunnelClient jhtc=null;
	  public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
	    jhtc=new JHttpTunnelClient(host, port);
	    //jhtc.setProxy(proxy_host, proxy_port);

	    jhtc.setInBound(new InBoundSocket());
	    jhtc.setOutBound(new OutBoundSocket());

	    jhtc.connect();
	    return new Socket(); // dummy
	  }
	  public InputStream getInputStream(Socket socket) throws IOException {
	    if(in==null)
	      in=jhtc.getInputStream();
	    return in;
	  }
	  public OutputStream getOutputStream(Socket socket) throws IOException {
	    if(out==null)
	      out=jhtc.getOutputStream();
	    return out;
	  }
	});

      //java.util.Hashtable config=new java.util.Hashtable();
      //config.put("StrictHostKeyChecking", "no");
      //session.setConfig(config);

      session.connect();

      Channel channel=session.openChannel("shell");

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);

      channel.connect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  public static class MyUserInfo implements UserInfo{
    public String getPassword(){ return passwd; }
    public boolean promptYesNo(String str){
      Object[] options={ "yes", "no" };
      int foo=JOptionPane.showOptionDialog(null, 
             str,
             "Warning", 
             JOptionPane.DEFAULT_OPTION, 
             JOptionPane.WARNING_MESSAGE,
             null, options, options[0]);
       return foo==0;
    }
  
    String passwd;
    JTextField passwordField=(JTextField)new JPasswordField(20);

    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){ return true; }
    public boolean promptPassword(String message){
      Object[] ob={passwordField}; 
      int result=
	  JOptionPane.showConfirmDialog(null, ob, message,
					JOptionPane.OK_CANCEL_OPTION);
      if(result==JOptionPane.OK_OPTION){
	passwd=passwordField.getText();
	return true;
      }
      else{ return false; }
    }
    public void showMessage(String message){
      JOptionPane.showMessageDialog(null, message);
    }
  }

}
