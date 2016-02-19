package com.pili.pldroid.streaming.camera.demo.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

// A RotateLayout is designed to display a single item and provides the
// capabilities to rotate the item.
public class RotateLayout extends ViewGroup implements Rotatable {
    @SuppressWarnings("unused")
    private static final String TAG = "RotateLayout";
    private int mOrientation;
    protected View mChild;
    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // The transparent background here is a workaround of the render issue
        // happened when the view is rotated as the device's orientation
        // changed. The view looks fine in landscape. After rotation, the view
        // is invisible.
        setBackgroundResource(android.R.color.transparent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChild = getChildAt(0);
        mChild.setPivotX(0);
        mChild.setPivotY(0);
    }

    @Override
    protected void onLayout(
            boolean change, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        switch (mOrientation) {
            case 0:
            case 180:
                mChild.layout(0, 0, width, height);
                break;
            case 90:
            case 270:
                mChild.layout(0, 0, height, width);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int w = 0, h = 0;
        switch(mOrientation) {
            case 0:
            case 180:
                measureChild(mChild, widthSpec, heightSpec);
                w = mChild.getMeasuredWidth();
                h = mChild.getMeasuredHeight();
                break;
            case 90:
            case 270:
                measureChild(mChild, heightSpec, widthSpec);
                w = mChild.getMeasuredHeight();
                h = mChild.getMeasuredWidth();
                break;
        }
        setMeasuredDimension(w, h);
        switch (mOrientation) {
            case 0:
                mChild.setTranslationX(0);
                mChild.setTranslationY(0);
                break;
            case 90:
                mChild.setTranslationX(0);
                mChild.setTranslationY(h);
                break;
            case 180:
                mChild.setTranslationX(w);
                mChild.setTranslationY(h);
                break;
            case 270:
                mChild.setTranslationX(w);
                mChild.setTranslationY(0);
                break;
        }
        mChild.setRotation(-mOrientation);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    // Rotate the view counter-clockwise
    @Override
    public void setOrientation(int orientation, boolean animation) {
        orientation = orientation % 360;
        if (mOrientation == orientation) return;
        mOrientation = orientation;
        requestLayout();
    }
}