package org.codeforcoffee.melloweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by codeforcoffee on 6/11/16.
 */
public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    private Map<String, Bitmap> bitmaps = new HashMap<>();

    private static class ViewHolder {
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView highTextView;
        TextView humidityTextView;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        public LoadImageTask(ImageView imgView) {
            this.imgView = imgView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = null;
            HttpURLConnection http = null;
            try {
                URL url = new URL(params[0]);
                http = (HttpURLConnection) url.openConnection();
                try (InputStream stream = http.getInputStream()) {
                    bm = BitmapFactory.decodeStream(stream);
                    bitmaps.put(params[0], bm);
                } catch (Exception ex) {
                    ex.printStackTrace();;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                http.disconnect();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            imgView.setImageBitmap(bm);
        }
    }

    public WeatherArrayAdapter(Context ctx, List<Weather> forecast) {
        super(ctx, -1, forecast);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather day = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.conditionImageView = (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.highTextView = (TextView) convertView.findViewById(R.id.highTextView);
            viewHolder.humidityTextView = (TextView) convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (bitmaps.containsKey(day.ICON_URL)) {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.ICON_URL));
        } else {
            new LoadImageTask(viewHolder.conditionImageView).execute(day.ICON_URL);
        }

        Context ctx = getContext();
        viewHolder.dayTextView.setText(ctx.getString(R.string.day_desc, day.DAY_OF_WEEK, day.DESCRIPTION));
        viewHolder.lowTextView.setText(ctx.getString(R.string.temp_low, day.MIN_TEMP));
        viewHolder.highTextView.setText(ctx.getString(R.string.temp_high, day.MAX_TEMP));
        viewHolder.humidityTextView.setText(ctx.getString(R.string.humidity, day.HUMIDITY));

        return convertView;
    }

}
