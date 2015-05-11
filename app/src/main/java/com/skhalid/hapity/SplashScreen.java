package com.skhalid.hapity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;


public class SplashScreen extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "UUZdCKZznj6Hq5eA1w5poy5i9";
    private static final String TWITTER_SECRET = "YrhndnbiSNTDk3uxp98fGnURfWjkHMEujW4QG4Zv6PCaId4Znz";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        TextView stext = (TextView) findViewById(R.id.app_name);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "ManilaSansBld.otf");

        stext.setTypeface(tf);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i = new Intent(SplashScreen.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);


    }
}
