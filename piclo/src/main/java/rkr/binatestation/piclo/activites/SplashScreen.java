package rkr.binatestation.piclo.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rkr.binatestation.piclo.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        navigate();
    }

    private void navigate() {
        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
        finish();
    }

}
