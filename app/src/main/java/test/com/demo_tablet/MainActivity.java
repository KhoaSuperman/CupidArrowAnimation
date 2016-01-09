package test.com.demo_tablet;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import de.hdodenhof.circleimageview.CircleImageView;
import test.com.demo_tablet.util.Prefs;

public class MainActivity extends AppCompatActivity {
    public static String STICK_WIDTH_BOUND = "STICK_WIDTH_BOUND";
    public static String ARROW_BOT_STARTX = "ARROW_BOT_STARTX";
    public static String ARROW_BOT_STARTY = "ARROW_BOT_STARTY";

    public static final int DURATION_ROTATE = 400;
    public static final int DURATION_FLY = 350;
    public static float FLY_X = 0;
    public static float FLY_Y = 0;
    public static float HEART_ROTATE = 155f;

    public static float stick_width_bound = 0;
    private float arrow_bot_startx = 0;
    private float arrow_bot_starty = 0;

    public static float STICK_ROTATE = 155f;

    ImageView imageView;
    ImageView ivArrowBot;
    View vStick;
    View vStickBg;
    CircleImageView ivAvatar;
    View vCircle;
    Button btnReset;

    View vLoading;

    final AnimatorSet animatorSet = new AnimatorSet();
    boolean isReset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.ivImage);
        vStick = findViewById(R.id.stick);
        vStickBg = findViewById(R.id.stickBackground);
        ivArrowBot = (ImageView) findViewById(R.id.ivArrowBot);
        ivAvatar = (CircleImageView) findViewById(R.id.ivAvatar);
        vCircle = findViewById(R.id.circle);
        vLoading = findViewById(R.id.loading);
        btnReset = (Button) findViewById(R.id.btnReset);

        //init animation values
        int pivotY = vStick.getLayoutParams().height;
        float pivotX = vStick.getLayoutParams().width / 2;
        ViewHelper.setPivotY(vStick, pivotY);
        ViewHelper.setPivotX(vStick, pivotX);

//        ivArrowBot.setX(733);
//        ivArrowBot.setY(1152);
        //location on screen of image avatar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                float toX = ivAvatar.getX() + ivAvatar.getWidth() / 2 - imageView.getWidth() / 2;
                float toY = ivAvatar.getY() + ivAvatar.getHeight() / 2 - imageView.getHeight() / 2;

                FLY_Y = -(imageView.getY() - toY);
                FLY_X = -(imageView.getX() - toX);
            }
        }, 500);
        //get cached values
        stick_width_bound = Prefs.getFloat(getBaseContext(), STICK_WIDTH_BOUND);
        arrow_bot_startx = Prefs.getFloat(getBaseContext(), ARROW_BOT_STARTX);
        arrow_bot_starty = Prefs.getFloat(getBaseContext(), ARROW_BOT_STARTY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rotate
                AnimatorSet animRotate = new AnimatorSet();
                animRotate.playTogether(
                        ObjectAnimator.ofFloat(imageView, "rotation", 0f, HEART_ROTATE),
                        ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.5f),
                        ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.5f),
                        ObjectAnimator.ofFloat(vStick, "scaleY", 1f, 9f),
                        ObjectAnimator.ofFloat(vStick, "rotation", 0f, STICK_ROTATE)
                );

                //arrow bottom appear
                AnimatorSet animArrowBottomAppear = new AnimatorSet();
                animRotate.playTogether(
                        ObjectAnimator.ofFloat(ivArrowBot, "alpha", 0.0f, 1.0f),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationX", vStick.getX(), arrow_bot_startx),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationY", vStick.getY(), arrow_bot_starty),
                        ObjectAnimator.ofFloat(ivArrowBot, "scaleX", 0.3f, 1f),
                        ObjectAnimator.ofFloat(ivArrowBot, "scaleY", 0.3f, 1f)
                );

                //fly
                AnimatorSet animFly = new AnimatorSet();
                animFly.playTogether(
                        ObjectAnimator.ofFloat(imageView, "translationX", 0, FLY_X),
                        ObjectAnimator.ofFloat(imageView, "translationY", 0, FLY_Y),
                        ObjectAnimator.ofFloat(vStick, "translationX", 0, FLY_X),
                        ObjectAnimator.ofFloat(vStick, "translationY", 0, FLY_Y),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationX", arrow_bot_startx, arrow_bot_startx + FLY_X),
                        ObjectAnimator.ofFloat(ivArrowBot, "translationY", arrow_bot_starty, arrow_bot_starty + FLY_Y)
                );

                //avatar alpha change
                AnimatorSet animAlpha = new AnimatorSet();
                animFly.playTogether(
                        ObjectAnimator.ofFloat(ivAvatar, "alpha", 1.0f, 0.3f)
                );

                //expand circle
                AnimatorSet animCollapse = new AnimatorSet();
                animFly.playTogether(
                        ObjectAnimator.ofFloat(vCircle, "scaleX", 1.0f, 0.0f),
                        ObjectAnimator.ofFloat(vCircle, "scaleY", 1.0f, 0.0f)
                );
                animFly
                        .setDuration(0);
                AnimatorSet animExpand = new AnimatorSet();
                animFly.playTogether(
                        ObjectAnimator.ofFloat(vCircle, "scaleX", 0.0f, 1.0f),
                        ObjectAnimator.ofFloat(vCircle, "scaleY", 0.0f, 1.0f),
                        ObjectAnimator.ofFloat(vCircle, "alpha", 0.0f, 1.0f)
                );
                animExpand
                        .setDuration(700)
                        .setInterpolator(new BounceInterpolator());
                animExpand.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ivAvatar.setAlpha(1.0f);
                        if (!isReset) {
                            ivAvatar.setBorderColorResource(R.color.colorPrimary);
                            showArrow(false);
                        }
                    }
                });
                ObjectAnimator alphaDown = ObjectAnimator.ofFloat(vCircle, "alpha", 1.0f, 0.0f);
                alphaDown.setDuration(400);

                AnimatorSet animCircleExpand = new AnimatorSet();
                animCircleExpand.playSequentially(animCollapse, animExpand, alphaDown);

                //config anim
                animRotate
                        .setDuration(DURATION_ROTATE)
                        .setInterpolator(new AnticipateOvershootInterpolator());
                animRotate.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        //take a look at R.drawable.pirvot_animation explanation first
                        if (
//                                true
                                Prefs.getFloat(getBaseContext(), STICK_WIDTH_BOUND) == 0
                                ) {
                            //calculate stick width bound
                            RectF rect = new RectF();
                            vStick.getMatrix().mapRect(rect);
                            stick_width_bound = rect.right;
                            //arrow bot start x, y
                            //Cy
                            float AB = rect.bottom;
                            float Ay = vStick.getY();
                            float Cy = Ay + AB;
                            float HC = AB / 4;
                            float Hy = Cy - HC;

                            float BC = (float) (Math.tan(Math.toRadians(180 - HEART_ROTATE)) * AB);
                            float Bx = vStick.getX();
                            float Cx = Bx + BC;
                            float KH = (float) (Math.tan(Math.toRadians(180 - HEART_ROTATE)) * HC);
                            float Kx = Cx - KH;
                            //hot fix: -1/2 of width of arrowbottom
                            Kx = Kx - ivArrowBot.getWidth() / 4;

                            //final result
                            arrow_bot_starty = Hy;
                            arrow_bot_startx = Kx;

                            //save
                            Prefs.setFloat(getBaseContext(), STICK_WIDTH_BOUND, stick_width_bound);
                            Prefs.setFloat(getBaseContext(), ARROW_BOT_STARTX, arrow_bot_startx);
                            Prefs.setFloat(getBaseContext(), ARROW_BOT_STARTY, arrow_bot_starty);
                        }
                        Log.d(MyCons.LOG, "MainActivity.onAnimationEnd" + "ARROW_BOT_STARTX: " + arrow_bot_startx + ", ARROW_BOT_STARTY: " + arrow_bot_starty);
                    }
                });
                animArrowBottomAppear
                        .setDuration(DURATION_ROTATE);
                animFly
                        .setDuration(DURATION_FLY)
                        .setInterpolator(new FastOutLinearInInterpolator());
                animFly.setStartDelay(300);
                animAlpha
                        .setDuration(DURATION_FLY + DURATION_ROTATE);

                //chain
                animatorSet.playSequentially(animRotate, animArrowBottomAppear, animFly, animAlpha, animCircleExpand);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isReset = false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        showArrow(true);
                    }
                });
                animatorSet.start();
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReset = true;
                ivAvatar.setBorderColorResource(R.color.gray);
                animatorSet.setInterpolator(new ReverseInterpolator());
                animatorSet.start();
                showArrow(true);
            }
        });

        if (stick_width_bound != 0) {
            vLoading.setVisibility(View.GONE);
        } else {
            //For init animation values
            //1. click on heart icon for perform animation. Ex: stick_width_bound, arrow_bot_startx, arrow_bot_starty
            imageView.performClick();

            //2. after finish, perform reset animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnReset.performClick();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //3. done calculate animation values
                            vLoading.setVisibility(View.GONE);
                        }
                    }, animatorSet.getDuration());
                }
            }, 7000);
        }

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

    public void showArrow(boolean show) {
        ivArrowBot.setVisibility(show ? View.VISIBLE : View.GONE);
        imageView.setVisibility(show ? View.VISIBLE : View.GONE);
        vStick.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //http://stackoverflow.com/questions/4120824/android-reversing-an-animation
    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

    public float percentOf(float value, float percent) {
        return percent * 100 / value;
    }
}
