package com.ethanb.contacts;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

// Helper which listens to the scrolling of a snapped RecyclerView and
// reports back when the snapped position changed and when scrolling has stopped
public class SnapPositionHelper extends RecyclerView.OnScrollListener {
    private int mPosition = RecyclerView.NO_POSITION;

    public interface SnapPositionListener {
        void onSnapPositionChanged(int position);
        void onScrollIdle();
    }

    private SnapHelper mSnapHelper;
    private SnapPositionListener mListener;

    public SnapPositionHelper(SnapHelper snapHelper, SnapPositionListener listener) {
        mSnapHelper = snapHelper;
        mListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int newPosition = getSnapPosition(recyclerView);
        if (mPosition != newPosition) {
            mPosition = newPosition;
            mListener.onSnapPositionChanged(newPosition);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            mListener.onScrollIdle();
        }
    }

    private int getSnapPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        View snapView = mSnapHelper.findSnapView(layoutManager);
        return layoutManager.getPosition(snapView);
    }
}
