package com.aicodix.rattlegram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private final LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private TextView statusTextView; // Add a TextView field

    public LocationHelper(Context context, TextView textView) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        statusTextView = textView; // Assign the passed TextView to the field
    }

    public interface LocationUpdateListener {
        void onLocationUpdate(String geolocationLink);
    }

    private LocationUpdateListener locationUpdateListener;

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        locationUpdateListener = listener;
    }


    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    DecimalFormat decimalFormat = new DecimalFormat("#.######");
                    String formattedLatitude = decimalFormat.format(latitude);
                    String formattedLongitude = decimalFormat.format(longitude);
                    String geolocationLink = formattedLatitude + "," + formattedLongitude;
                    Log.d(TAG, "Geolocation Link: " + geolocationLink);
               //     statusTextView.setText(geolocationLink);

                    // Notify the callback (MainActivity) of the location update
                    if (locationUpdateListener != null) {
                        locationUpdateListener.onLocationUpdate(geolocationLink);
                    }


                stopLocationUpdates();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);
    }

    public void stopLocationUpdates() {
        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}