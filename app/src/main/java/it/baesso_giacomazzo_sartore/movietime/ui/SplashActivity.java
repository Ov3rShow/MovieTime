package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import it.baesso_giacomazzo_sartore.movietime.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        YoYo.with(Techniques.SlideInLeft)
                .duration(1200)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {

                        YoYo.with(Techniques.RollOut)
                                .duration(1000)
                                .onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        Intent intent = new Intent(SplashActivity.this,ListActivity.class);
                                        finish();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivityForResult(intent, 0);
                                        overridePendingTransition(0,0);
                                    }
                                })
                                .playOn(findViewById(R.id.splash_img));
                    }
                })
                .playOn(findViewById(R.id.splash_img));

    }
}
