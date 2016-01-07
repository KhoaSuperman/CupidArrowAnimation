package test.com.demo_tablet;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import test.com.demo_tablet.util.Prefs;

public class MainActivity extends AppCompatActivity {
    public static String STICK_WIDTH_BOUND = "STICK_WIDTH_BOUND";
    public static String ARROW_BOT_STARTX = "ARROW_BOT_STARTX";
    public static String ARROW_BOT_STARTY = "ARROW_BOT_STARTY";

    public static final int DURATION_ROTATE = 700;
    public static final int DURATION_FLY = 200;
    public static final int FLY_Y = -400;
    public static float HEART_ROTATE = 140f;

    //TODO: need to calculate
//    public static float stick_width_bound = 206.28279f;
//    private float arrow_bot_startx = 786.447f;
//    private float arrow_bot_starty = 1434.447f;
    public static float stick_width_bound = 0;
    private float arrow_bot_startx = 0;
    private float arrow_bot_starty = 0;

    public static float STICK_ROTATE = 140f;

    ImageView imageView;
    ImageView ivArrowBot;
    View vStick;
    View vStickBg;

    ProgressBar progressBar;

    final AnimatorSet animatorSet = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.ivImage);
        vStick = findViewById(R.id.stick);
        vStickBg = findViewById(R.id.stickBackground);
        ivArrowBot = (ImageView) findViewById(R.id.ivArrowBot);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //init animation values
        int pivotY = vStick.getLayoutParams().height;
        float pivotX = vStick.getLayoutParams().width / 2;
        ViewHelper.setPivotY(vStick, pivotY);
        ViewHelper.setPivotX(vStick, pivotX);
        //get cached values
        stick_width_bound = Prefs.getFloat(getBaseContext(), STICK_WIDTH_BOUND);
        arrow_bot_startx = Prefs.getFloat(getBaseContext(), ARROW_BOT_STARTX);
        arrow_bot_starty = Prefs.getFloat(getBaseContext(), ARROW_BOT_STARTY);
        if(stick_width_bound != 0){
            progressBar.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rotate
                AnimatorSet animRotate = new AnimatorSet();
                animRotate.playTogether(
                        ObjectAnimator.ofFloat(imageView, "rotation", 0f, HEART_ROTATE),
                        ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.4f),
                        ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.4f),
                        ObjectAnimator.ofFloat(vStick, "scaleY", 1f, 9f),
                        ObjectAnimator.ofFloat(vStick, "rotation", 0f, STICK_ROTATE)
                );

                //arrow bottom appear
                AnimatorSet animArrowBottomAppear = new AnimatorSet();
                animRotate.playTogether(
                        ObjectAnimator.ofFloat(ivArrowBot, "translationX", vStick.getX(), vStick.getX() + stick_width_bound - ivArrowBot.getMeasuredWidth() - 45),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationY", vStick.getY(), vStick.getY() + stick_width_bound - ivArrowBot.getMeasuredHeight() - 0),
                        ObjectAnimator.ofFloat(ivArrowBot, "scaleX", 0.3f, 1f),
                        ObjectAnimator.ofFloat(ivArrowBot, "scaleY", 0.3f, 1f)
                );

                //fly
                final AnimatorSet animFly = new AnimatorSet();
                animFly.playTogether(
                        ObjectAnimator.ofFloat(imageView, "translationX", 0, FLY_Y),
                        ObjectAnimator.ofFloat(imageView, "translationY", 0, FLY_Y),
                        ObjectAnimator.ofFloat(vStick, "translationX", 0, FLY_Y),
                        ObjectAnimator.ofFloat(vStick, "translationY", 0, FLY_Y),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationX", arrow_bot_startx, arrow_bot_startx + FLY_Y),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationY", arrow_bot_starty, arrow_bot_starty + FLY_Y)
                );

                //config anim
                animRotate
                        .setDuration(DURATION_ROTATE)
                        .setInterpolator(new AnticipateOvershootInterpolator());
                animRotate.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        ivArrowBot.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        RectF rect = new RectF();
//                        vStick.getMatrix().mapRect(rect);
//                        STICK_WIDTH_BOUND = rect.right;
//
//                        ARROW_BOT_STARTX = vStick.getX() + STICK_WIDTH_BOUND - ivArrowBot.getMeasuredWidth() - 45;
//                        ARROW_BOT_STARTY = vStick.getY() + STICK_WIDTH_BOUND - ivArrowBot.getMeasuredHeight() - 0;

                        if (Prefs.getFloat(getBaseContext(), STICK_WIDTH_BOUND) == 0) {
                            //calculate stick width bound
                            RectF rect = new RectF();
                            vStick.getMatrix().mapRect(rect);
                            stick_width_bound = rect.right;
                            //arrow bot start x, y
                            arrow_bot_startx = vStick.getX() + stick_width_bound - ivArrowBot.getMeasuredWidth() - 45;
                            arrow_bot_starty = vStick.getY() + stick_width_bound - ivArrowBot.getMeasuredHeight() - 0;
                            //save
                            Prefs.setFloat(getBaseContext(), STICK_WIDTH_BOUND, stick_width_bound);
                            Prefs.setFloat(getBaseContext(), ARROW_BOT_STARTX, arrow_bot_startx);
                            Prefs.setFloat(getBaseContext(), ARROW_BOT_STARTY, arrow_bot_starty);
                        }

                        progressBar.setVisibility(View.GONE);

                        Log.d(MyCons.LOG, "MainActivity.onAnimationEnd" + "ARROW_BOT_STARTX: " + arrow_bot_startx + ", ARROW_BOT_STARTY: " + arrow_bot_starty);
                    }
                });
                animArrowBottomAppear
                        .setDuration(DURATION_ROTATE);
                animFly.setDuration(DURATION_FLY)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                animFly.setStartDelay(200);

                //chain
                animatorSet.playSequentially(animRotate, animArrowBottomAppear, animFly);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                reset();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        //TODO: should remove after done cache calculate animation values
//                        resetPosition();
                    }
                });
                animatorSet.start();
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatorSet.setInterpolator(new ReverseInterpolator());
                animatorSet.start();

//                reset();
            }
        });
    }

    private void reset() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }

    private void resetPosition() {
        imageView.setTranslationX(0);
        imageView.setTranslationY(0);
        ivArrowBot.setTranslationX(0);
        ivArrowBot.setTranslationY(0);
        vStick.setTranslationX(0);
        vStick.setTranslationY(0);
    }

    //http://stackoverflow.com/questions/4120824/android-reversing-an-animation
    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }
}
