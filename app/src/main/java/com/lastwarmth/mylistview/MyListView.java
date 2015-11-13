package com.lastwarmth.mylistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ListView;

public class MyListView extends ListView {

    private VelocityTracker mVelocityTracker;
    private float xDown;
    private float yDown;
    private int MAX_Y = 5;
    private int MAX_X = 3;
    private int mTouchPosition;
    private MyItemLayout mTouchView;
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;
    private int mTouchState;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        createVelocityTracker(ev);
        float moveX;
        float moveY;
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                xDown = ev.getX();
                yDown = ev.getY();
                mTouchState = TOUCH_STATE_NONE;
                int prevPosition = mTouchPosition;
                mTouchPosition = pointToPosition((int) xDown, (int) yDown);
                if (prevPosition == mTouchPosition && mTouchView != null && mTouchView.isMenuOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    return true;
                }
                if (mTouchView != null && mTouchView.isMenuOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    return false;
                }
                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
                if (view instanceof MyItemLayout) {
                    mTouchView = (MyItemLayout) view;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX() - xDown;
                moveY = ev.getY() - yDown;
                if (mTouchState == TOUCH_STATE_X) {
                    if (!mTouchView.isMenuOpen()) {
                        mTouchView.setLeftMargin((int) moveX);
                    }
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(moveY) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (Math.abs(moveX) > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                moveX = ev.getX() - xDown;
                if (mTouchState == TOUCH_STATE_X && mTouchView != null) {
                    if (-moveX > mTouchView.getMenuWidth() / 2 || (moveX < 0 && getScrollVelocity() > 200)) {
                        if (!mTouchView.isMenuOpen()) {
                            mTouchView.smoothOpenMenu();
                        }
                    } else {
                        mTouchView.smoothCloseMenu();
                        mTouchView = null;
                    }
                    recycleVelocityTracker();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}