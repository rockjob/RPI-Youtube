package com.rockjob.rpi_youtube;

import java.io.DataInputStream;
import java.io.IOException;

import android.os.AsyncTask;

public class SSHread extends AsyncTask<DataInputStream, String, Void>{
	MainActivity parentactivity;
	public SSHread (MainActivity a){
		parentactivity = a;
	}
	@Override
	protected Void doInBackground(DataInputStream... params) {
		System.out.println("Asynctask started");
		//parentactivity.edittxt.append("Reading started.");
		//System.out.println("made it past the first edittxtappend");
		String line;
		try {
			while ((line = params[0].readLine()) != null) {
				System.out.println(line + "\n");
				publishProgress(line);
				if(line == "have a nice day ;)"){
					parentactivity.connection.disconnect();
					parentactivity.finish();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return null;
	}
	@Override
	protected void onProgressUpdate(String... values) {
		
		//parentactivity.edittxt.append(values[0]);
		
		
	}

}
