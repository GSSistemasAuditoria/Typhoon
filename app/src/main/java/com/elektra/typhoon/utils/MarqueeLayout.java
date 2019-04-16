package com.elektra.typhoon.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 12/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class MarqueeLayout extends LinearLayout {
    private Animation animation;

    public MarqueeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueeLayout(Context context) {
        super(context);
    }

    public void setDuration(int durationMillis) {
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, +1f,
                Animation.RELATIVE_TO_SELF,	-1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );

        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.START_ON_FIRST_FRAME);
        animation.setDuration(durationMillis);
    }

    public void startAnimation() {
        startAnimation(animation);
    }
}