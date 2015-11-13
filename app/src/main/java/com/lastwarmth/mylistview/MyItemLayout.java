package com.lastwarmth.mylistview;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

public class MyItemLayout extends LinearLayout {

    private LinearLayout contentView;
    private LinearLayout menuView;
    private LayoutParams contentLayout;
    private boolean isMenuOpen;
    private int minLeftMargin = -720;
    private int maxLeftMargin = 0;

    public MyItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        contentLayout = new LayoutParams(getScreenWidth(), LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = (LinearLayout) getChildAt(0);
        menuView = (LinearLayout) getChildAt(1);
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
        contentLayout.leftMargin = leftMargin;
        contentView.setLayoutParams(contentLayout);
    }

    public void smoothOpenMenu() {
        new ScrollTask().execute(30);
    }

    public void smoothCloseMenu() {
        new ScrollTask().execute(-30);
    }

    public int getMenuWidth() {
        return -minLeftMargin;
    }

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

}
