package com.example.steamchatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements Login{
	
	public static final String API_KEY = "";
	public static final String DOMAIN = "bobjrsenior.pw";
	
	
	public LinearLayout layout;
	public EditText username;
	public EditText password;
	public String loginSettings;
	public String captcha_id;
	public String steamguard_id;

	public ProgressDialog progressDialog;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        layout = (LinearLayout) findViewById(R.id.LinearLayout1);
        username = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging In...");
        
        ((Button) findViewById(R.id.signinbutton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isConnectedOnline()){
					
					progressDialog.show();
					
					RequestParams params = new RequestParams("POST", "https://steamcommunity.com/login/getrsakey");
					params.addParam("username", username.getText().toString());
					/*RequestParams params = new RequestParams("GET", "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/");
					params.addParam("key", API_KEY);
					params.addParam("steamid", "76561198031483209");
					params.addParam("relatinoship", "friend");
					*/
					//new LoginAsyncTask(password.getText().toString(), username.getText().toString(), MainActivity.this).execute(params);
					new GetRSAInfoAsyncTask(MainActivity.this).execute(params);
					
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

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public Void StatusCheck(ArrayList<String> results) {
		progressDialog.dismiss();
		
		//Worked
		if(results.get(0).equals("1")){
			Log.d("resulting", "YAY");
		}
		else{
			Log.d("resulting", "Maybe?");
			//Captcha
			if(results.get(1).length() > 2){
				Log.d("resulting", "Got there");
				if(!results.get(1).equals("1")){
					if(findViewById(27015) == null){
						ImageView captcha_image = new ImageView(MainActivity.this);
						captcha_image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 135));
						captcha_image.setId(27015);
						captcha_image.setScaleType(ScaleType.FIT_XY);
						layout.addView(captcha_image);
					}
					captcha_id = results.get(1);
					RequestParams params = new RequestParams("GET", "https://steamcommunity.com/public/captcha.php?gid=" + results.get(1));
					new GetImage().execute(params);
					
					if(findViewById(27016) == null){
						EditText answer = new EditText(MainActivity.this);
						answer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						answer.setHint("Captcha");
						layout.addView(answer);
						answer.setId(27016);
						Button retry = new Button(MainActivity.this);
						retry.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						retry.setText("Check Captcha");
						layout.addView(retry);
						
						retry.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								new LoginAsyncTask(password.getText().toString(), username.getText().toString(), MainActivity.this).execute(loginSettings, "1", captcha_id, ((EditText) findViewById(27016)).getText().toString());
								progressDialog.show();
							}
						});
					}
					else{
						EditText answer = (EditText) findViewById(27016);
						answer.setText("");
						answer.setHint("Captcha");
					}
				}
			}
			else if(!results.get(2).equals("1")){
				Log.d("demo", "email Auth");
				steamguard_id = results.get(2);
				if(findViewById(27016) == null){
					EditText answer = new EditText(MainActivity.this);
					answer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					answer.setHint("Auth Code From Email");
					layout.addView(answer);
					answer.setId(27016);
					Button retry = new Button(MainActivity.this);
					retry.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					retry.setText("Authorize");
					layout.addView(retry);
					
					retry.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							new LoginAsyncTask(password.getText().toString(), username.getText().toString(), MainActivity.this).execute(loginSettings, "2", steamguard_id, ((EditText) findViewById(27016)).getText().toString());
							progressDialog.show();
						}
					});
				}
				else{
					EditText answer = (EditText) findViewById(27016);
					answer.setText("");
					answer.setHint("SteamGuard");
				}
			}
		}
		
		return null;
	}

	@Override
	public Void GetRSA(String result) {
		Log.d("RSA Check", "g" + result);
		loginSettings = result;
		//JSONObject obj = new JSONObject(result);
		new LoginAsyncTask(password.getText().toString(), username.getText().toString(), MainActivity.this).execute(result);
		
		return null;
	}
	
	
	public class GetImage extends AsyncTask<RequestParams, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(RequestParams... params) {
			BufferedReader reader = null;
			
			try {
				HttpsURLConnection con = params[0].setupConnection();
				return BitmapFactory.decodeStream(con.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			((ImageView) findViewById(27015)).setImageBitmap(result);
		}
		
		
	}
}


