package com.sareen.squarelabs.techygeekadmin;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ashish on 15/7/17.
 */

public class PublishButtonClickListener implements View.OnClickListener
{

    private Context mContext;

    public PublishButtonClickListener(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void onClick(View view)
    {
        PublishPostTask publishPostTask = new PublishPostTask();
        publishPostTask.execute();
    }

    public class PublishPostTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            publishPosts();
            return null;
        }

        private void publishPosts()
        {
            DatabaseReference mDatabaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("testData");

            DatabaseReference db = mDatabaseReference.push();
            db.setValue(12);
        }
    }


}
