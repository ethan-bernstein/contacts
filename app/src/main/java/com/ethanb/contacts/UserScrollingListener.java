package com.ethanb.contacts;

import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

// Helper which filters scrolling events to those which came from direct user interaction
public class UserScrollingListener extends RecyclerView.OnScrollListener implements View.OnTouchListener {
    public interface OnScrollListener {
        void onScrolled(int dx, int dy);
    }

    private OnScrollListener mListener;
    private boolean mTouched = false;

    public static void addUserScrollingListener(RecyclerView recyclerView, OnScrollListener listener) {
        UserScrollingListener userScrollingListener = new UserScrollingListener(listener);
        recyclerView.addOnScrollListener(userScrollingListener);
        recyclerView.setOnTouchListener(userScrollingListener);
    }

    private UserScrollingListener(OnScrollListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouched = true;
        return false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (mTouched) {
            mListener.onScrolled(dx, dy);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            mTouched = false;
        }
    }
}
