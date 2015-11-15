package com.lastwarmth.mylistview;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class MyItemLayout extends LinearLayout {

    private LinearLayout contentView;
    private LinearLayout menuView;
    private LayoutParams contentLayout;
    private boolean isMenuOpen;
    private int minLeftMargin;
    private int maxLeftMargin = 0;
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

    public void smoothOpenMenu() {
        isMenuOpen = true;
        mScroller.startScroll(contentLayout.leftMargin, 0, minLeftMargin - contentLayout.leftMargin, 0, 350);
        postInvalidate();
    }

    public void smoothCloseMenu() {
        isMenuOpen = false;
        mScroller.startScroll(contentLayout.leftMargin, 0, maxLeftMargin - contentLayout.leftMargin, 0, 350);
        postInvalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = (LinearLayout) getChildAt(0);
        menuView = (LinearLayout) getChildAt(1);
        ViewGroup.LayoutParams lp = menuView.getLayoutParams();
        minLeftMargin = -lp.width;
    }

    private int getScreenWidth() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    private class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = contentLayout.leftMargin;
            while (true) {
                leftMargin = leftMargin - speed[0];
                if (leftMargin > maxLeftMargin) {
                    leftMargin = maxLeftMargin;
                    break;
                }
                if (leftMargin < minLeftMargin) {
                    leftMargin = minLeftMargin;
                    break;
                }
                publishProgress(leftMargin);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isMenuOpen = speed[0] > 0;
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            contentLayout.leftMargin = leftMargin[0];
            contentView.setLayoutParams(contentLayout);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            contentLayout.leftMargin = leftMargin;
            contentView.setLayoutParams(contentLayout);
        }
    }

    public void setLeftMargin(int leftMargin) {
        if (leftMargin > maxLeftMargin) {
            leftMargin = maxLeftMargin;
        }
        if (leftMargin < minLeftMargin) {
            leftMargin = minLeftMargin;
        }
        contentLayout.leftMargin = leftMargin;
        contentView.setLayoutParams(contentLayout);
    }

    public void toOpenMenu() {
        new ScrollTask().execute(30);
    }

    public void toCloseMenu() {
        new ScrollTask().execute(-30);
    }

    public int getMenuWidth() {
        return -minLeftMargin;
    }

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

}
