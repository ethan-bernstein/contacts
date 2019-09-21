package com.ethanb.contacts;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.view.ViewTreeObserver;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SnapPositionHelper.SnapPositionListener, AvatarListAdapter.AvatarOnClickListener {

    private ContactsViewModel mViewModel;
    private RecyclerView mAvatarList;
    private RecyclerView mDetailsList;
    private AvatarListAdapter mAvatarListAdapter;
    private DetailsListAdapter mDetailsListAdapter;
    private int mAvatarListPadding = 0;
    private int mListPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Insert custom view in app bar to get centered app title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.appbar);

        mViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);

        mAvatarList = findViewById(R.id.avatarList);
        mAvatarListAdapter = new AvatarListAdapter(this, mAvatarList, this);
        mAvatarList.setAdapter(mAvatarListAdapter);
        mAvatarList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mDetailsList = findViewById(R.id.detailsList);
        mDetailsListAdapter = new DetailsListAdapter(this);
        mDetailsList.setAdapter(mDetailsListAdapter);
        mDetailsList.setLayoutManager(new LinearLayoutManager(this));

        // Add snapping
        SnapHelper avatarSnapHelper = new LinearSnapHelper();
        avatarSnapHelper.attachToRecyclerView(mAvatarList);
        mAvatarList.addOnScrollListener(new SnapPositionHelper(avatarSnapHelper, this));

        SnapHelper detailsSnapHelper = new LinearSnapHelper();
        detailsSnapHelper.attachToRecyclerView(mDetailsList);
        mDetailsList.addOnScrollListener(new SnapPositionHelper(detailsSnapHelper, this));

        // Add synchronized scrolling
        UserScrollingListener.addUserScrollingListener(mAvatarList, new UserScrollingListener.OnScrollListener() {
            @Override
            public void onScrolled(int dx, int dy) {
                int target = getDetailsListOffsetFromAvatarListOffset();
                int scrollBy = target - mDetailsList.computeVerticalScrollOffset();
                mDetailsList.scrollBy(0, scrollBy);
            }
        });

        UserScrollingListener.addUserScrollingListener(mDetailsList, new UserScrollingListener.OnScrollListener() {
            @Override
            public void onScrolled(int dx, int dy) {
                int target = getAvatarListOffsetFromDetailsListOffset();
                int scrollBy = target - mAvatarList.computeHorizontalScrollOffset();
                mAvatarList.scrollBy(scrollBy, 0);
            }
        });

        // Scroll if selected position changes outside of scrolling
        mViewModel.getSelectedPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                if (mListPosition != position) {
                    mListPosition = position;
                    mAvatarListAdapter.setSelectedPosition(position);
                    mAvatarList.smoothScrollToPosition(position);
                    mDetailsList.smoothScrollToPosition(position);
                }
            }
        });
        mViewModel.getContactList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contactList) {
                mAvatarListAdapter.setContactList(contactList);
                mDetailsListAdapter.setContactList(contactList);
                findViewById(R.id.main_layout).forceLayout();
            }
        });

        // Need to set padding programatically so first and last item don't go past center
        mAvatarList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()  {
                if (mViewModel.getContactList().getValue().size() > 0) {
                    // Only run this the first time after contact list is loaded.
                    mAvatarList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int position = mViewModel.getSelectedPosition().getValue();
                    int avatarItemWidth = mAvatarList.getLayoutManager().findViewByPosition(position).getMeasuredWidth();

                    mAvatarListPadding = (mAvatarList.getMeasuredWidth() - avatarItemWidth) / 2;
                    mAvatarList.setPadding(mAvatarListPadding, mAvatarList.getPaddingTop(), mAvatarListPadding, mAvatarList.getPaddingBottom());

                    // Select the first contact to force the views to adjust correctly after
                    // setting padding.
                    mViewModel.getSelectedPosition().setValue(position);
                }
            }
        });
    }

    // Offset translation for synced scrolling
    private int getAvatarListOffsetFromDetailsListOffset() {
        int percent100 = 10000 * mDetailsList.computeVerticalScrollOffset() / (mDetailsList.computeVerticalScrollRange() - mDetailsList.computeVerticalScrollExtent());
        return (mAvatarList.computeHorizontalScrollRange() - mAvatarList.computeHorizontalScrollExtent()) * percent100 / 10000;
    }

    // Offset translation for synced scrolling
    private int getDetailsListOffsetFromAvatarListOffset() {
        int percent100 = 10000 * mAvatarList.computeHorizontalScrollOffset() / (mAvatarList.computeHorizontalScrollRange() - mAvatarList.computeHorizontalScrollExtent());
        return (mDetailsList.computeVerticalScrollRange() - mDetailsList.computeVerticalScrollExtent()) * percent100 / 10000;
    }

    @Override
    public void onSnapPositionChanged(int position) {
        if (mListPosition != position) {
            mListPosition = position;
            mAvatarListAdapter.setSelectedPosition(position);
            mViewModel.getSelectedPosition().setValue(position);
        }
    }

    @Override
    public void onScrollIdle() {
        mAvatarList.smoothScrollToPosition(mListPosition);
        mDetailsList.smoothScrollToPosition(mListPosition);
    }

    @Override
    public void onClickAvatar(int position) {
        mViewModel.getSelectedPosition().setValue(position);
    }
}