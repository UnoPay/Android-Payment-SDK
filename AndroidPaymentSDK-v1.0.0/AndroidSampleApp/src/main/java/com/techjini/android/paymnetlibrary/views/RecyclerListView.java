package com.techjini.android.paymnetlibrary.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bheema on 7/13/15.
 */
public class RecyclerListView extends RecyclerView {

    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;



    public interface OnItemClickListener {

        public void onItemClick(ListItemBaseView view, int position);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(ListItemBaseView view, int position);
    }


    public RecyclerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnItemTouchListener(new RecyclerItemClickListener(context, this));

    }

    public RecyclerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnItemTouchListener(new RecyclerItemClickListener(context, this));

    }

    public RecyclerListView(Context context) {
        super(context);
        addOnItemTouchListener(new RecyclerItemClickListener(context, this));

    }



    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }


    public class RecyclerItemClickListener extends RecyclerListView.SimpleOnItemTouchListener {

        private final GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerListView recyclerListView) {
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }


                @Override
                public void onLongPress(MotionEvent e) {
                    ListItemBaseView childView = (ListItemBaseView) recyclerListView.findChildViewUnder(e.getX(), e.getY());

                    if (childView != null && mItemLongClickListener != null) {
                        mItemLongClickListener.onItemLongClick(childView, recyclerListView.getChildAdapterPosition(childView));

                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && mItemClickListener != null && mGestureDetector.onTouchEvent(e)) {
                mItemClickListener.onItemClick((ListItemBaseView) childView, view.getChildAdapterPosition(childView));
                return false;
            }

            return mGestureDetector.onTouchEvent(e);
        }
    }

}
