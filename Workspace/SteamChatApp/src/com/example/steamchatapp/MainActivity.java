package com.example.steamchatapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	public static final String API_KEY = "A93F7FE17F13E61A0AA7428D7BB15B90";
	public static final String DOMAIN = "bobjrsenior.pw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
