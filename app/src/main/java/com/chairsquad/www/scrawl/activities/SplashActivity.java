package com.chairsquad.www.scrawl.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.chairsquad.www.scrawl.R;
import com.chairsquad.www.scrawl.utilities.ScrawlConnection;

/**
 * Created by henry on 13/04/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        ScrawlConnection scrawlConnection = new ScrawlConnection(this);

        final Intent intent;
        if (!scrawlConnection.isLoggedIn()) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, AllNotesActivity.class);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);

                finish();

            }
        }, 2000);

    }


}
