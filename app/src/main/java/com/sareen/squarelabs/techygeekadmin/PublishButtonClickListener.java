package com.sareen.squarelabs.techygeekadmin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sareen.squarelabs.techygeekadmin.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ashish on 15/7/17.
 */

public class PublishButtonClickListener implements View.OnClickListener
{

    private ArrayList<Post> postArrayList;
    private Context mContext;
    private String nextUrl = "/filterWebContent?token=12108889-1675-4d2c-8594-bcecef593f25&format=json&ts=1500128066732&sort=crawled&q=language%3Aenglish%20is_first%3Atrue%20site%3Agsmarena.com";
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
            downloadPosts();
            publishPosts();
            return null;
        }

        private void downloadPosts()
        {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string
            String newsJsonStr = null;

            try
            {
                // Construct the url for webhose api query
                final String WEBHOSE_BASE_URL =
                        "http://webhose.io";



                URL WEBHOSE_API_URL = new URL(WEBHOSE_BASE_URL + nextUrl);

                urlConnection = (HttpURLConnection)WEBHOSE_API_URL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                {
                    // Nothing to do
                    Log.e(this.getClass().getSimpleName(), "Input stream in null");
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a lot easier if there is need to print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0)
                {
                    // Stream was empty. No point in parsing the string
                    Log.e(this.getClass().getSimpleName(), "Steam was empty");
                    return;
                }
                newsJsonStr = buffer.toString();
                getNewsDataFromJson(newsJsonStr);



            }
            catch (MalformedURLException e)
            {
                Log.e(this.getClass().getSimpleName(), "Error in forming url", e);
            }
            catch (IOException e)
            {
                Log.e(this.getClass().getSimpleName(), "Input/output error", e);
            }
            catch(JSONException e)
            {
                Log.e(this.getClass().getSimpleName(), "Error in parsing json", e);
            }
            finally
            {
                if(urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if(reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch(final IOException e)
                    {
                        Log.e(this.getClass().getSimpleName(), "Erro closing stream", e);
                    }
                }
            }
        }

        private void getNewsDataFromJson(String newsJsonStr) throws JSONException
        {
            JSONObject newsJson;
            final String WH_POSTS = "posts";
            final String WH_NEXT = "next";
            final String WH_POST_TITLE = "title";
            final String WH_POST_TEXT = "text";
            final String WH_POST_THREAD = "thread";
            final String WH_POST_IMAGE = "main_image";
            final String WH_POST_ID = "uuid";
            postArrayList = new ArrayList<>();
            try
            {
                newsJson = new JSONObject(newsJsonStr);

                nextUrl = newsJson.getString(WH_NEXT);

                JSONArray postsArray = newsJson.getJSONArray(WH_POSTS);
                for(int i=0; i<postsArray.length(); i++)
                {
                    JSONObject postJson = postsArray.getJSONObject(i);
                    JSONObject threadJson = postJson.getJSONObject(WH_POST_THREAD);
                    String title = postJson.getString(WH_POST_TITLE);
                    String text = postJson.getString(WH_POST_TEXT);
                    String image = threadJson.getString(WH_POST_IMAGE);
                    String id = postJson.getString(WH_POST_ID);

                    Post postObj = new Post(id, image, title, text);

                    postArrayList.add(postObj);
                }
            }
            catch (JSONException e)
            {
                Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        private void publishPosts()
        {
            DatabaseReference mDatabaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("posts")
                    .child("mobilePosts");



            for(int i=0; i<postArrayList.size(); i++)
            {
                DatabaseReference db = mDatabaseReference.push();
                db.setValue(postArrayList.get(i));
            }

            DatabaseReference nextDbRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("nextUrls")
                    .child("mobileNextUrl");
            nextDbRef.setValue(nextUrl);
        }
    }


}
