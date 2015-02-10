package co.mobilemakers.githubrepos;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagPopularityFragment extends Fragment {

    final static String LOG_TAG = TagPopularityFragment.class.getSimpleName();

    EditText mEditTextTag;
    TextView mTextViewPopularityTag;

    public TagPopularityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tag_popularity, container, false);
        mEditTextTag = (EditText)rootView.findViewById(R.id.edit_text_tag);
        mTextViewPopularityTag = (TextView)rootView.findViewById(R.id.text_view_popularity_tag);
        Button buttonGetRepos =(Button)rootView.findViewById(R.id.button_get_popularity_tag);
        buttonGetRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = mEditTextTag.getText().toString();
               // String message = String.format(getString(R.id.getting_repos_for_user));
                //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                new FetchReposTask().execute(tag);
            }
        });
        return rootView;
    }

    private URL constructURLQuery(String tag) throws MalformedURLException {
        final String INSTAGRAM_BASE_URL = "api.instagram.com";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority(INSTAGRAM_BASE_URL)
                .appendPath("v1")
                .appendPath("tags")
                .appendPath("search")
                .appendQueryParameter("q", tag)
                .appendQueryParameter("access_token","845261011.1677ed0.462889c292eb4082b4321789a73e0219");
        Uri uri = builder.build();
        Log.d(LOG_TAG, "Built URI:" + uri.toString());
        return new URL(uri.toString());
    }

    private String readFullResponse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        String line;
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line).append("\n");
        }
        if(stringBuilder.length() > 0){
            response = stringBuilder.toString();
        }
        return response;
    }

    private String parseResponse(String response){
        final String TAG_NAME = "name";
        final String POPULARITY = "media_count";
        HashMap<String, String> tagsPopularity = new HashMap<>();
        response = response.replace("{\"meta\":{\"code\":200},\"data\":", "");
        response = response.replace("]}", "]");
        try {
            JSONArray responseJsonArray = new JSONArray(response);
            JSONObject object;
            for (int i=0; i < responseJsonArray.length(); i++){
                object = responseJsonArray.getJSONObject(i);
                tagsPopularity.put(object.getString(TAG_NAME), object.getString(POPULARITY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder string = new StringBuilder();
        for(String key : tagsPopularity.keySet()){
            string.append(key).append(": "+tagsPopularity.get(key)).append("\n");
        }
        return string.toString();
    }

    class FetchReposTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String tag;
            String listOfTags = "";
            if(params.length > 0){
                tag = params[0];
            }else{
                tag = "snowy";
            }
            try {
                URL url = constructURLQuery(tag);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                try{
                    String response = readFullResponse(httpConnection.getInputStream());
                    listOfTags = parseResponse(response);
                }   catch (IOException e) {
                    e.printStackTrace();
                }   finally {
                    httpConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listOfTags;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            mTextViewPopularityTag.setText(response);
        }
    }

}
