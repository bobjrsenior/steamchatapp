package com.example.steamchatapp;

import java.security.spec.RSAKeyGenParameterSpec;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final String API_KEY = "Insert API Key Here";
	public static final String DOMAIN = "bobjrsenior.pw";
	
	public EditText username;
	public EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        username = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);
        
        ((Button) findViewById(R.id.signinbutton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isConnectedOnline()){
					//steam://friends/joinchat/103582791432297280
					RequestParams params = new RequestParams("POST", "https://steamcommunity.com/login/getrsakey");
					params.addParam("username", username.getText().toString());
					/*RequestParams params = new RequestParams("GET", "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/");
					params.addParam("key", API_KEY);
					params.addParam("steamid", "76561198031483209");
					params.addParam("relatinoship", "friend");
					*/
					new GetDataWithParams(password, 1, username.getText().toString()).execute(params);
					
				}
				else{
					Toast.makeText(MainActivity.this, "Not Connected to Internet", Toast.LENGTH_SHORT).show();
				}
			}
		}); 
    }
    
    protected boolean isConnectedOnline() {
    	// TODO Auto-generated method stub
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    	if(networkInfo != null && networkInfo.isConnected()){
    		return true;
    	}else{
    		return false;
    	}

    }
}


