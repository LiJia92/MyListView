package com.lastwarmth.mylistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ListView;

public class MyListView extends ListView {

    // 滑动速度追踪类
    private VelocityTracker mVelocityTracker;
    // ACTION_DOWN的坐标
    private float xDown;
    private float yDown;
    // 判断横滑、竖滑的最小值
    private int MAX_Y = 5;
    private int MAX_X = 3;
    // 当前点击的position
    private int mTouchPosition;
    // 当前点击的item View
    private MyItemLayout mTouchView;
    // 当前触摸状态
    private int mTouchState;
    private static final int TOUCH_STATE_NONE = 0; //ACTION_DOWN时设置的状态
    private static final int TOUCH_STATE_X = 1; //横滑
    private static final int TOUCH_STATE_Y = 2; //竖滑


    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在滑动的速度
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 触摸事件的控制
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 加入触摸跟踪类
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
                // 当前点击的Item正好是已经显示Menu的Item
                if (prevPosition == mTouchPosition && mTouchView != null && mTouchView.isMenuOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    return true; // 返回true表示接受了ACTION_DOWN，那么后面的事件依然会分发给MyListView
                }
                // 点击的Item不是正在显示Menu的Item，则直接关闭Menu
                if (mTouchView != null && mTouchView.isMenuOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    return false; // 返回false，那么后面的事件全部会接收不到
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
                        // 如果是横滑，并且菜单没有显示，则设置leftMargin
                        mTouchView.setLeftMargin((int) moveX);
                    }
                    return false;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    // 设置横滑还是竖滑
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
                    // 若滑动的距离是Menu宽度的一半，或者左滑速度大于200,
                    if (-moveX > mTouchView.getMenuWidth() / 2 || (moveX < 0 && getScrollVelocity() > 200)) {
                        // 若Menu是关闭的
                        if (!mTouchView.isMenuOpen()) {
                            // 滑动打开Menu
                            mTouchView.smoothOpenMenu();
                        }
                    } else {
                        // 滑动关闭Menu
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