package com.rockjob.rpi_youtube;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.jcraft.jsch.Channel;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends Activity   {
	public static TextView txtview;
	public static EditText edittxt,edittxt2;
	public static EditText edittxthostname,edittxtport,edittxtusername,edittxtpassword;
	static Button btn, btn2,btn04,btn06,btn07,btn08,btnsettingssave;
	static SSHconnecting connection;
	static ControlFragment controls;
	public static Channel channel;
	public static SSHread mysshread;
	public String urltoplay;
	DataOutputStream dataOut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
				.add(R.id.container, new settingsFragment(this)).commit();
		}
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				if (savedInstanceState == null) {
					controls = new ControlFragment(intent.getClipData().getItemAt(0).getText().toString(),this);
					getFragmentManager().beginTransaction().replace(R.id.container, controls).commit();
				} else{
					controls.handlelink();
					}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void onceConnected(){
		try {
			//txtview.setText("Connected to RPI!");
			DataInputStream dataIn;
			dataIn = new DataInputStream(channel.getInputStream());
			dataOut = new DataOutputStream(channel.getOutputStream());
			mysshread = new SSHread(this);
			mysshread.execute(dataIn);
			System.out.println("Sending command");
			dataOut.writeBytes("omxplayer -o hdmi $(youtube-dl -g " + urltoplay +")\n");  
			dataOut.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void appendedittxt(final String a){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				edittxt.append(a);
			}
		});
	}

	public void sendcommand(String a){
    	try {
			if (a!=null && dataOut != null){
    		dataOut.writeBytes(a);
			dataOut.flush();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}  
        
    }

	public void connecttopi(){
		connection = new SSHconnecting(this);
		MyChannel tempmychannel = new MyChannel();
		SharedPreferences settings = getSharedPreferences("RPI-Youtube", 0);
		String hostname = settings.getString("hostname", "192.168.1.110");
		Integer port = settings.getInt("port", 22);
		String username = settings.getString("username", "pi");
		String password = settings.getString("password", "raspberry");
		tempmychannel.host = hostname;
		tempmychannel.port = port;
		tempmychannel.username = username;
		tempmychannel.password = password;
		connection.execute(tempmychannel);
	}

	public class settingsFragment extends Fragment implements OnClickListener {
		MainActivity Parentactivity;
		SharedPreferences settings;
		public settingsFragment(MainActivity a) {
			Parentactivity = a;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.settings_view, container, false);
			btnsettingssave = (Button) rootView.findViewById(R.id.buttonsettingssave);
			btnsettingssave.setOnClickListener(this);
			rootView.findViewById(R.id.buttonsettingsquit).setOnClickListener(this);
			edittxthostname = (EditText) rootView.findViewById(R.id.editTextHostname);
			edittxtport = (EditText) rootView.findViewById(R.id.EditTextPort);
			edittxtusername = (EditText) rootView.findViewById(R.id.EditTextUsername);
			edittxtpassword = (EditText) rootView.findViewById(R.id.EditTextPassword);
			
			settings = getSharedPreferences("RPI-Youtube", 0);
			String hostname = settings.getString("hostname", "192.168.1.110");
			Integer port = settings.getInt("port", 22);
			String username = settings.getString("username", "pi");
			String password = settings.getString("password", "raspberry");
			edittxthostname.setText(hostname);
			edittxtport.setText(Integer.toString(port));
			edittxtusername.setText(username);
			edittxtpassword.setText(password);
			return rootView;
		}
		
		public void onClick(View view) {
			// detect the view that was "clicked"
			switch (view.getId()) {
			case R.id.buttonsettingssave:
				//System.out.println("Button Pressed"); 
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("hostname", edittxthostname.getText().toString());
				editor.putInt("port", Integer.parseInt(edittxtport.getText().toString()));
				editor.putString("username", edittxtusername.getText().toString());
				editor.putString("password", edittxtpassword.getText().toString());
				editor.commit();
				Toast.makeText(getApplicationContext(), "Settings saved", 3).show();
				String returnedurl = null;
				//YTURL test = new YTURL(returnedurl,"https://www.youtube.com/watch?v=n-BXNXvTvV4");
				//System.out.println("Returned url: " + test);
				break; 
			case R.id.buttonsettingsquit:
				Parentactivity.finish();
				break;
				
			}
		}
	}
	
	public class ControlFragment extends Fragment implements OnClickListener {
		private String data;
		private MainActivity parentactivity;
		public ControlFragment(String a, MainActivity b){
			data = a;
			parentactivity = b;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.controls, container, false);
			btn04 = (Button) rootView.findViewById(R.id.button04);
			btn06 = (Button) rootView.findViewById(R.id.button06);
			btn07 = (Button) rootView.findViewById(R.id.button07);
			btn08 = (Button) rootView.findViewById(R.id.button08);
			btn04.setOnClickListener(this);
			btn06.setOnClickListener(this);
			btn07.setOnClickListener(this);
			btn08.setOnClickListener(this);
			handlelink();
			return rootView;
		}
		
		public void handlelink() {
			//Test link
			System.out.println("Data Recieved " + data);
			if(data.contains("http://youtu.be")){
				System.out.println(data.substring(data.length()-27));
			}
			parentactivity.urltoplay = data.substring(data.length()-27);
			parentactivity.connecttopi();
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button04:
				//play/pause
				parentactivity.sendcommand("p");
				System.out.println("Play/Pause"); 
				break;
			case R.id.button06:
				//Vol-
				parentactivity.sendcommand("-");
				System.out.println("Vol-"); 
				break;
			case R.id.button08:
				//Quit
				parentactivity.sendcommand("q");
				parentactivity.connection.disconnect();
				parentactivity.finish();
				//finish();
				System.out.println("Quit"); 
				break;
			case R.id.button07:
				//Vol+
				parentactivity.sendcommand("+");
				System.out.println("Vol+"); 
				break;
			}
		}
	}
}
