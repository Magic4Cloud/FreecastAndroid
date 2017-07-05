package com.cloud4magic.freecast.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Date   2017/7/5
 * Editor  Misuzu
 */

public class TouchAnimButton extends AppCompatTextView {

    public TouchAnimButton(Context context) {
        super(context);
    }

    public TouchAnimButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(this, View.SCALE_X, 1, 0.9f);
                ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1, 0.9f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
                animatorSet.setDuration(100);
                animatorSet.start();
                break;
            case MotionEvent.ACTION_UP:
                ObjectAnimator objectAnimatorX1 = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.9f, 1);
                ObjectAnimator objectAnimatorY1 = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.9f, 1);
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.playTogether(objectAnimatorX1, objectAnimatorY1);
                animatorSet1.setDuration(100);
                animatorSet1.start();
                animatorSet1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        performClick();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;
        }
        return true;
    }
}
