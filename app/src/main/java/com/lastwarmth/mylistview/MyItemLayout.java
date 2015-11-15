package com.lastwarmth.mylistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class MyItemLayout extends LinearLayout {

    // content View
    private LinearLayout contentView;
    // menu View
    private LinearLayout menuView;
    // content View的布局参数对象
    private LayoutParams contentLayout;
    // 菜单是否打开
    private boolean isMenuOpen;
    // contentView最小的leftMargin
    private int minLeftMargin;
    // contentView最大的leftMargin
    private int maxLeftMargin = 0;
    // 滑动类
    private Scroller mScroller = null;

    public MyItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        contentLayout = new LayoutParams(getScreenWidth(), LayoutParams.WRAP_CONTENT);
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setLeftMargin(mScroller.getCurrX());
            postInvalidate();
        }
    }

    /**
     * Scroller平滑打开Menu
     */
    public void smoothOpenMenu() {
        isMenuOpen = true;
        mScroller.startScroll(contentLayout.leftMargin, 0, minLeftMargin - contentLayout.leftMargin, 0, 350);
        postInvalidate();
    }

    /**
     * Scroller平滑关闭Menu
     */
    public void smoothCloseMenu() {
        isMenuOpen = false;
        mScroller.startScroll(contentLayout.leftMargin, 0, maxLeftMargin - contentLayout.leftMargin, 0, 350);
        postInvalidate();
    }

    /**
     * 在布局inflate完成后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 第一个孩子是contentView
        contentView = (LinearLayout) getChildAt(0);
        // 第二个孩子是MenuView
        menuView = (LinearLayout) getChildAt(1);
        // 最小的leftMargin为负的menuView宽度
        ViewGroup.LayoutParams lp = menuView.getLayoutParams();
        minLeftMargin = -lp.width;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 给contentView设置leftMargin
     * @param leftMargin
     */
    public void setLeftMargin(int leftMargin) {
        // 控制leftMargin不越界
        if (leftMargin > maxLeftMargin) {
            leftMargin = maxLeftMargin;
        }
        if (leftMargin < minLeftMargin) {
            leftMargin = minLeftMargin;
        }
        contentLayout.leftMargin = leftMargin;
        // 通过设置leftMargin，达到menu显示的效果
        contentView.setLayoutParams(contentLayout);
    }

    /**
     * 获取menuView宽度
     * @return
     */
    public int getMenuWidth() {
        return -minLeftMargin;
    }

    /**
     * Menu是否打开
     * @return
     */
    public boolean isMenuOpen() {
        return isMenuOpen;
    }

}
