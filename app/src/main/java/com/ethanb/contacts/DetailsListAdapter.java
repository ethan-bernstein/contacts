package com.ethanb.contacts;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailsListAdapter extends RecyclerView.Adapter<DetailsListAdapter.DetailsHolder>{
    List<Contact> mContactList;
    private LayoutInflater mLayoutInflater;

    public DetailsListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setContactList(List<Contact> contactList) {
        mContactList = contactList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.details_list_item, parent, false);
        return new DetailsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsHolder holder, int position) {
        Contact contact = mContactList.get(position);
        String name = String.format("<b>%s</b> %s", contact.getFirstName(), contact.getLastName());
        holder.mName.setText(Html.fromHtml(name));
        holder.mTitle.setText(contact.getTitle());
        holder.mIntroduction.setText(contact.getIntroduction());
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    class DetailsHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final TextView mTitle;
        public final TextView mIntroduction;

        public DetailsHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mTitle = itemView.findViewById(R.id.title);
            mIntroduction = itemView.findViewById(R.id.introduction);
        }
    }
}
