package pl.dplmobile.isslocation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastSender {

    public static final String ACTION_ISS_LOCATION_DATA = "pl.dplmobile.isslocation.ISS_LOCATION_DATA";
    public static final String ACTION_ISS_PEOPLE_DATA = "pl.dplmobile.isslocation.ISS_PEOPLE_DATA";
    public static final String EXTRA_PEOPLE = "extra_people";
    public static final String EXTRA_NUMBER_OF_PEOPLE = "number_of_people";
    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";
    public static final String EXTRA_TIME = "extra_time";

    private LocalBroadcastManager localBroadcastManager;

    public BroadcastSender(Context context) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void sendIssLocation(float lat, float lng, long time) {
        Intent intent = new Intent(ACTION_ISS_LOCATION_DATA);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        intent.putExtra(EXTRA_TIME, time);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void sendIssPeople(String people, int numberOfPeople) {
        Intent intent = new Intent(ACTION_ISS_PEOPLE_DATA);
        intent.putExtra(EXTRA_PEOPLE, people);
        intent.putExtra(EXTRA_NUMBER_OF_PEOPLE, numberOfPeople);
        localBroadcastManager.sendBroadcast(intent);
    }
}
