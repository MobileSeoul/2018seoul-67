package com.example.junmung.hangangparksmap.Map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.junmung.hangangparksmap.R;

public class AnimatingLayout extends ConstraintLayout{
    private Context context;
    private Animation showAnimation, hideAnimation;


    public AnimatingLayout(Context context) {
        super(context);
        this.context = context;
        initAnimation();
    }

    public AnimatingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAnimation();
    }

    public AnimatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAnimation();
    }

    private void initAnimation() {
        showAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_container_show);
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_container_hide);
    }

    public void show() {
        if (isVisible())
            return;
        show(true);
    }

    public void show(boolean withAnimation) {
        if (withAnimation)
            this.startAnimation(showAnimation);
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (!isVisible())
            return;
        hide(true);
    }

    public void hide(boolean withAnimation) {
        if (withAnimation)
            this.startAnimation(hideAnimation);
        this.setVisibility(View.GONE);
    }

    public boolean isVisible() {
        return (this.getVisibility() == View.VISIBLE);
    }
}
