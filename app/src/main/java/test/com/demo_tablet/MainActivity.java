package test.com.demo_tablet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.animation.AnimatorProxy;

public class MainActivity extends AppCompatActivity {

    public static final int DURATION_ROTATE = 800;
    public static final int DURATION_FLY = 300;
    ImageView imageView;
    View vStick;
    View vStickBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.ivImage);
        vStick = findViewById(R.id.stick);
        vStickBg = findViewById(R.id.stickBackground);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.setPivotY(vStick, 0);
                ViewHelper.setPivotX(vStick, 0);

                //rotate
                AnimatorSet animRotate = new AnimatorSet();
                animRotate.playTogether(
                        ObjectAnimator.ofFloat(imageView, "rotation", 0f, 135f),
                        ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.5f),
                        ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.5f),
                        ObjectAnimator.ofFloat(vStickBg, "translationY", 0f, -vStickBg.getHeight()),
                        ObjectAnimator.ofFloat(vStickBg, "rotation", 0f, 135f),
                        ObjectAnimator.ofFloat(vStick, "rotation", 0f, 135f)
                );

                //fly
                final AnimatorSet animFly = new AnimatorSet();
                int fly = -500;
                animFly.playTogether(
                        ObjectAnimator.ofFloat(imageView, "translationX", 0, fly),
                        ObjectAnimator.ofFloat(imageView, "translationY", 0, fly)
                );

                //config anim
                animRotate
                        .setDuration(DURATION_ROTATE)
                        .setInterpolator(new AnticipateOvershootInterpolator());
                animFly.setDuration(DURATION_FLY)
                        .setInterpolator(new AccelerateDecelerateInterpolator());

                //chain
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(animRotate, animFly);
                animatorSet.start();


            }
        });
    }

}
