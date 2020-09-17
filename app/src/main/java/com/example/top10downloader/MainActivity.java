package com.example.top10downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private static final String TAG = "MainActivity";
    private ListView xmlListView;
    public static final String URL = "URL";
    public static final String FEED_LIMIT = "FEED_LIMIT";
    private String urlCached = "INVALIDATE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmlListView = findViewById(R.id.xmlListView);

        if (savedInstanceState != null) {
            url = savedInstanceState.getString(URL);
            feedLimit = savedInstanceState.getInt(FEED_LIMIT);
        }
        downloadURL(String.format(url, feedLimit));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnuFree).setChecked(true);
        } else {
            menu.findItem(R.id.mnuPaid).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // boolean isChecked=true;
        int id = item.getItemId();
        switch (id) {
            case R.id.mnuFree:
                url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: setting feedLimit to " + feedLimit);
                    Log.d(TAG, "onOptionsItemSelected: menu top 25 is clicked for the first time");

                }
                Log.d(TAG, "onOptionsItemSelected: menu top 25 clicked for the second time");
                break;
            case R.id.mnuRefresh:
                urlCached = "INVALIDATE";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadURL(String.format(url, feedLimit));
        return true;
    }

    private void downloadURL(String url) {
        if (url.equalsIgnoreCase(urlCached)) {
            DownloadData downloadData = new DownloadData();
            downloadData.execute(url);
            urlCached = url;
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications app = new ParseApplications();
            app.parse(s);
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this, R.layout.list_item,
//                    app.getApplications());
//            xmlListView.setAdapter(arrayAdapter);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_layout,
                    app.getApplications());
            xmlListView.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = DownloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error Downloading");
            }

            return rssFeed;
        }

        private String DownloadXML(String urlPath) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "DownloadXML: " + response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                int charRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charRead = reader.read(inputBuffer);
                    if (charRead < 0) {
                        break;
                    }
                    if (charRead > 0) {
                        stringBuilder.append(String.copyValueOf(inputBuffer, 0, charRead));
                    }
                }
                reader.close();
                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "DownloadXML: " + e.getMessage());
            } catch (IOException o) {
                Log.e(TAG, "DownloadXML: " + o.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "DownloadXML: security exception raised");
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(URL, url);
        outState.putInt(FEED_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }
}
