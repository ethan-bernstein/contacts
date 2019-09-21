package com.ethanb.contacts;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContactsViewModel extends AndroidViewModel {
    private final String KEY_FIRST_NAME = "first_name";
    private final String KEY_LAST_NAME = "last_name";
    private final String KEY_AVATAR_FILENAME = "avatar_filename";
    private final String KEY_TITLE = "title";
    private final String KEY_INTRODUCTION = "introduction";

    private final MutableLiveData<List<Contact>> mContactList = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSelectedPosition = new MutableLiveData<>();

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        mContactList.setValue(new ArrayList<Contact>());
        mSelectedPosition.setValue(RecyclerView.NO_POSITION);
        new ContactListLoader().execute(application);
    }

    public LiveData<List<Contact>> getContactList() {
        return mContactList;
    }

    public MutableLiveData<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    private Contact parseContact(JSONObject jsonObject, AssetManager assetManager) throws JSONException, IOException {
        String firstName = jsonObject.getString(KEY_FIRST_NAME);
        String lastName = jsonObject.getString(KEY_LAST_NAME);
        String avatarFileName = jsonObject.getString(KEY_AVATAR_FILENAME).replace(' ', '_');
        String title = jsonObject.getString(KEY_TITLE);
        String introduction = jsonObject.getString(KEY_INTRODUCTION);

        Bitmap avatar = BitmapFactory.decodeStream(assetManager.open(avatarFileName));

        return new Contact(firstName, lastName, title, introduction, avatar);
    }

    @WorkerThread
    private List<Contact> loadContactList(Resources resources, AssetManager assetManager) {
        List<Contact> contactList = new ArrayList<>();
        try {
            InputStream inputStream = resources.openRawResource(R.raw.contacts);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder contactListJson = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                contactListJson.append(line).append('\n');
            }

            JSONTokener jsonTokener = new JSONTokener(contactListJson.toString());
            JSONArray jsonArray = new JSONArray(jsonTokener);
            int numContacts = jsonArray.length();
            for (int i = 0; i < numContacts; i++) {
                contactList.add(parseContact(jsonArray.getJSONObject(i), assetManager));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactList;
    }

    private class ContactListLoader extends AsyncTask<Application, Void, List<Contact>> {

        @Override
        protected List<Contact> doInBackground(Application... applications) {
            return loadContactList(applications[0].getResources(), applications[0].getAssets());
        }

        @Override
        protected void onPostExecute(List<Contact> result) {
            mContactList.setValue(result);
            mSelectedPosition.setValue(0);
        }
    }
}
