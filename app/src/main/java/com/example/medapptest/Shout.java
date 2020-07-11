package com.example.medapptest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.medapptest.common.Constants;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class Shout extends Fragment {

    private String[] shoutList = {"I want coffee","Let`s wash our hands","I want chocolate","Pizza time"};
    private ListView listView;
    private Context context;

    public Shout() {
        // Required empty public constructor
    }

    public Shout(Context context_) {
        // Required empty public constructor
        context = context_;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shout, container, false);
        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.activity_listview, shoutList);
        listView = (ListView) v.findViewById(R.id.shoutListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                new PostShoutTask().execute("text:"+ shoutList[position]);
            }
        });
        return v;
    }

    class PostShoutTask extends AsyncTask<String, String, Integer> {

        private Exception exception;
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            // Do some validation here
            try {
                URL url = new URL(Constants.SLACK_POST_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(params[0]);
                writer.close();
                connection.connect();
                return 1;
            }
            catch(Exception e) {
                Toast.makeText(context, "Unknown error while posting shout to Slack api", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        protected void onPostExecute(Integer response) {
            if(response == 0) {
                return;
            }
            Toast.makeText(context, "Successfully posted to Slack api", Toast.LENGTH_SHORT).show();
        }
    }
}
