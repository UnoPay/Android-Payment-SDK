package com.techjini.android.paymnetlibrary.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.Utils;


/**
 * Created by techjini on 15/09/15.
 */
public class SimpleDividerItemDecoration extends RecyclerListView.ItemDecoration {
    private final Context mContext;
    private Drawable mDivider;

    public SimpleDividerItemDecoration(Context context) {
        mContext = context;
        mDivider = Utils.getSupportDrawable(mContext, R.drawable.line_divider);
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() ;
        int right = parent.getWidth();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin+5;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}