package co.mobilemakers.githubrepos;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagPopularityFragment extends ListFragment {

    final static String LOG_TAG = TagPopularityFragment.class.getSimpleName();

    EditText mEditTextTag;
    TagPopularityAdapter mAdapter;

    public TagPopularityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tag_popularity, container, false);
        wireUpViews(rootView);
        prepareButton(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<TagPopularity> tags = new ArrayList<>();
        mAdapter = new TagPopularityAdapter(getActivity(),tags);
        setListAdapter(mAdapter);
    }

    private void prepareButton(View rootView) {
        Button buttonGetPopularity =(Button)rootView.findViewById(R.id.button_get_popularity_tag);
        buttonGetPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagToSearch = mEditTextTag.getText().toString();
                fetchReposInQueue(tagToSearch);
            }
        });
    }

    private void fetchReposInQueue(String tagToSearch){
        try {
            URL url = constructURLQuery(tagToSearch);
            Request request = new Request.Builder().url(url.toString()).build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseString = response.body().string();
                    final List<TagPopularity> listOfTags = parseResponse(responseString);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();
                            mAdapter.addAll(listOfTags);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void wireUpViews(View rootView) {
        mEditTextTag = (EditText)rootView.findViewById(R.id.edit_text_tag_name);
    }

    private URL constructURLQuery(String tag) throws MalformedURLException {
        final String INSTAGRAM_BASE_URL = "api.instagram.com";
        final String SCHEME = "https";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).authority(INSTAGRAM_BASE_URL)
                .appendPath("v1")
                .appendPath("tags")
                .appendPath("search")
                .appendQueryParameter("q", tag)
                .appendQueryParameter("access_token","845261011.1677ed0.462889c292eb4082b4321789a73e0219");
        Uri uri = builder.build();
        Log.d(LOG_TAG, "URI:" + uri.toString());
        return new URL(uri.toString());
    }

    private List<TagPopularity> parseResponse(String response){
        final String TAG_NAME = "name";
        final String POPULARITY = "media_count";
        List<TagPopularity> tagsPopularity = new ArrayList<>();
        response = formatToResponse(response);
        try {
            JSONArray responseJsonArray = new JSONArray(response);
            JSONObject object;
            TagPopularity tagPopularity;
            for (int i=0; i < responseJsonArray.length(); i++){
                object = responseJsonArray.getJSONObject(i);
                tagPopularity = new TagPopularity();
                tagPopularity.setTagName(object.getString(TAG_NAME));
                tagPopularity.setPopularity(object.getString(POPULARITY));
                tagsPopularity.add(tagPopularity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tagsPopularity;
    }

    private String formatToResponse(String response) {
        response = response.replace("{\"meta\":{\"code\":200},\"data\":", "");
        response = response.replace("]}", "]");
        return response;
    }

}
