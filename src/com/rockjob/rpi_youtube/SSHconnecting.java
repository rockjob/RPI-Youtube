package com.rockjob.rpi_youtube;

import java.util.Properties;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHconnecting extends AsyncTask<MyChannel, Void, MyChannel> {
	MainActivity Parentactivity;
	Channel channel;
	Session session;
	String exception;
	
	public SSHconnecting(MainActivity a) {
		this.Parentactivity = a;
	}
	
	public void disconnect(){
		try{
			channel.disconnect();
			session.disconnect();
		}
		catch (Exception e)
	    {
		      System.out.println(e.getMessage());
		    }
		
}
	@Override
	protected MyChannel doInBackground(MyChannel... params) {
		try
			{
				JSch jsch = new JSch();
				session = jsch.getSession(params[0].username,params[0].host, params[0].port);
				session.setPassword(params[0].password);
				Properties prop = new Properties();
				prop.put("StrictHostKeyChecking", "no");
				session.setConfig(prop);
				System.out.println("Session Connecting");
				session.connect(10000);
				channel = (Channel) session.openChannel("shell");
				channel.connect();
				System.out.println("Channel Connecting");
				MainActivity.channel = channel;
				//"omxplayer -o hdmi $(youtube-dl -g https://www.youtube.com/watch?v=7s2-lJKS-PM)"
		    }
		catch (JSchException e)
			{
				System.out.println(e.getMessage());
				exception = e.getMessage();
			}
			return null;
	}
	
	@Override
	protected void onPostExecute(MyChannel result) {
		if(channel != null){
			Parentactivity.onceConnected();
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Parentactivity);
			// set title
			alertDialogBuilder.setTitle("Connection Failed");
			// set dialog message
			alertDialogBuilder.setMessage("Failed to connect: " + exception);
			alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					Parentactivity.finish();
				}
			});
			alertDialogBuilder.create().show();
			//Toast.makeText(Parentactivity.getApplicationContext(), "Could not connect to Pi: " + exception, Toast.LENGTH_LONG).show();
			//Parentactivity.finish();
		}
	}
}