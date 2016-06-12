package org.codeforcoffee.melloweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Weather> mWeatherList = new ArrayList<>();
    private WeatherArrayAdapter mWeatherArrayAdapter;
    private ListView mWeatherListView;

    private void dismissKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.url_openweather_api);
        try {
            String url = baseUrl + URLEncoder.encode(city, "UTF-8") + "&units=imperial&cnt=16&APPID=" + apiKey;
            return new URL(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void convertJSONtoArrayList(JSONObject forcast) {
        mWeatherList.clear();
        try {
            JSONArray list = forcast.getJSONArray("list");
            for (int inc = 0; inc < list.length(); ++inc) {
                JSONObject day = list.getJSONObject(inc);
                JSONObject temp = day.getJSONObject("temp");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                mWeatherList.add(new Weather(
                    day.getLong("dt"),
                        temp.getDouble("min"),
                        temp.getDouble("max"),
                        day.getDouble("humidity"),
                        weather.getString("description"),
                        weather.getString("icon")
                ));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) params[0].openConnection();
                int response = http.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                   StringBuilder sb = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
                        String l;
                        while ((l = reader.readLine()) != null) {
                            sb.append(l);
                        }
                    } catch (IOException ex) {
                        Snackbar.make(findViewById(R.id.coordinator_layout), R.string.err_reading, Snackbar.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }
                    return new JSONObject(sb.toString());
                } else {
                    Snackbar.make(findViewById(R.id.coordinator_layout), R.string.err_connection, Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.err_connection, Snackbar.LENGTH_LONG).show();
                ex.printStackTrace();
            } finally {
                http.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList(weather);
            mWeatherArrayAdapter.notifyDataSetChanged();
            mWeatherListView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWeatherListView = (ListView) findViewById(R.id.weatherListView);
        mWeatherArrayAdapter = new WeatherArrayAdapter(this, mWeatherList);
        mWeatherListView.setAdapter(mWeatherArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
                URL url = createURL(locationEditText.getText().toString());

                if (url != null) {
                    dismissKeyboard(locationEditText);
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                } else {
                    Snackbar.make(findViewById(R.id.coordinator_layout), R.string.url_invalid, Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
