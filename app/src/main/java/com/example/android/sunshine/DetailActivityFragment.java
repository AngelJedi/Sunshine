package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String forecast;
    private ShareActionProvider shareActionProvider;

    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    private static final int COL_ID = 0;
    private static final int COL_DATE = 1;
    private static final int COL_DESC = 2;
    private static final int COL_MAX_TEMP = 3;
    private static final int COL_MIN_TEMP = 4;
    private static final int COL_HUMIDITY = 5;
    private static final int COL_PRESSURE = 6;
    private static final int COL_WIND_SPEED= 7;
    private static final int COL_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private ImageView iconView;
    private TextView friendlyDateView;
    private TextView dateView;
    private TextView descriptionView;
    private TextView highView;
    private TextView lowView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        iconView = (ImageView) view.findViewById(R.id.detail_icon);
        dateView = (TextView) view.findViewById(R.id.detail_date_textview);
        friendlyDateView = (TextView) view.findViewById(R.id.detail_day_textview);
        descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        highView = (TextView) view.findViewById(R.id.detail_high_textview);
        lowView = (TextView) view.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        windView = (TextView) view.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (forecast != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + " " + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(), intent.getData(), DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

        iconView.setImageResource(R.drawable.ic_launcher);

        // Set date information on views
        long date = data.getLong(COL_DATE);
        String friendlyDate = Utility.getDayName(getActivity(), date);
        friendlyDateView.setText(friendlyDate);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        dateView.setText(dateText);

        // Set forecast description on views
        String description = data.getString(COL_DESC);
        descriptionView.setText(description);

        // Set high and low temps on view
        boolean isMetric = Utility.isMetric(getActivity());
        double high = data.getDouble(COL_MAX_TEMP);
        String highString = Utility.formatTemperature(getActivity(), high, isMetric);
        highView.setText(highString);
        double low = data.getDouble(COL_MIN_TEMP);
        String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
        lowView.setText(lowString);

        // Set humidity on view
        float humidity = data.getFloat(COL_HUMIDITY);
        humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Set wind speed and direction on view
        float windSpeed = data.getFloat(COL_WIND_SPEED);
        float windDirection = data.getFloat(COL_DEGREES);
        windView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDirection));

        // Set pressure on view
        float pressure = data.getFloat(COL_PRESSURE);
        pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
