package com.example.garbage.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.garbage.R;
import com.example.garbage.storage.UserSharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SplashActivity<Imageview> extends AppCompatActivity {

    private static int SPLASH_SCREEN = 1000;
    Animation top, bottom, fade;
    ImageView image;
    TextView name;
    UserSharedPreferences share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        share = new UserSharedPreferences(SplashActivity.this);
        //Animations
        DrawerActivity.flag = 0;
        top = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bot_animation);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);

        image = findViewById(R.id.logo);
        name = findViewById(R.id.appname);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        long currMillis=System.currentTimeMillis();
        Date dateNow = null;

        long dateSavedMillis = getSharedPreferences("Notifications", MODE_PRIVATE).getLong("date", System.currentTimeMillis());
        Date dateSaved = null;
        try {
            dateNow = formatter.parse(formatter.format(new Date(currMillis)));
            dateSaved = formatter.parse(formatter.format(new Date(dateSavedMillis)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!Objects.equals(dateNow, dateSaved)){
            getSharedPreferences("Notifications", MODE_PRIVATE).edit().putInt("notified", 0).apply();
            getSharedPreferences("Notifications", MODE_PRIVATE).edit().putLong("date", System.currentTimeMillis()).apply();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "logo_trans");
                pairs[1] = new Pair<View, String>(name, "welcome_trans");

                if(share.getFilename()== "") {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                }
                else{
                    startActivity(new Intent(getApplicationContext(), DrawerActivity.class));
                    finish();
                }

            }
        },SPLASH_SCREEN);
    }
}
