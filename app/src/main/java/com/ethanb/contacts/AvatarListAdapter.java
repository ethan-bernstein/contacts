package com.ethanb.contacts;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvatarListAdapter extends RecyclerView.Adapter<AvatarListAdapter.AvatarHolder> {
    private List<Contact> mContactList;
    private int mSelectedPosition = RecyclerView.NO_POSITION;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;
    private AvatarOnClickListener mListener;

    public interface AvatarOnClickListener {
        void onClickAvatar(int position);
    }

    public AvatarListAdapter(Context context, RecyclerView recyclerView, AvatarOnClickListener listener) {
        mLayoutInflater = LayoutInflater.from(context);
        mRecyclerView = recyclerView;
        mListener = listener;
    }

    public void setContactList(List<Contact> contactList) {
        mContactList = contactList;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        if (position != mSelectedPosition) {
            if (mSelectedPosition != RecyclerView.NO_POSITION) {
                View oldAvatarView = mRecyclerView.getLayoutManager().findViewByPosition(mSelectedPosition);
                if (oldAvatarView != null) {
                    setSelectionState(findImageView(oldAvatarView), false);
                }
            }
            View newAvatarView = mRecyclerView.getLayoutManager().findViewByPosition(position);
            if (newAvatarView != null) {
                setSelectionState(findImageView(newAvatarView), true);
            }
            mSelectedPosition = position;
        }
    }

    static private ImageView findImageView(View avatarView) {
        return avatarView.findViewById(R.id.avatar_image);
    }

    static public void setSelectionState(ImageView avatarView, boolean isSelected) {
        PorterDuffColorFilter colorFilter = isSelected  ? null : new PorterDuffColorFilter(0, PorterDuff.Mode.CLEAR);
        avatarView.getBackground().setColorFilter(colorFilter);
    }

    @NonNull
    @Override
    public AvatarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.avatar_list_item, parent, false);
        return new AvatarHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.mImageView.setImageBitmap(contact.getAvatar());
        setSelectionState(holder.mImageView, position == mSelectedPosition);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    class AvatarHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;

        public AvatarHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = itemView.findViewById(R.id.avatar_image);
            setSelectionState(mImageView, false);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            mListener.onClickAvatar(position);
        }
    }
}
