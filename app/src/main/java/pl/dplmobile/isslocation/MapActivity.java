package pl.dplmobile.isslocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pl.dplmobile.isslocation.sync.SyncService;


public class MapActivity extends AppCompatActivity {

    private  final String TAG = getClass().getSimpleName();
    private MapView mapView;
    private MapboxMap map;
    private TextView lastSyncView;
    private Marker issMarker;
    private boolean mapLoaded;
    private float lat = 52.153053f, lng = 21.022716f;
    private long lastSyncTime = -1L;
    private int numberOfPeople = 0;
    private String peopleText = "";
    private LocalBroadcastManager broadcastManager;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastSender.ACTION_ISS_LOCATION_DATA)) {
                lat = intent.getFloatExtra(BroadcastSender.EXTRA_LAT, 0f);
                lng = intent.getFloatExtra(BroadcastSender.EXTRA_LNG, 0f);
                lastSyncTime = intent.getLongExtra(BroadcastSender.EXTRA_TIME, -1L);
            } else if(intent.getAction().equals(BroadcastSender.ACTION_ISS_PEOPLE_DATA)) {
                numberOfPeople = intent.getIntExtra(BroadcastSender.EXTRA_NUMBER_OF_PEOPLE, numberOfPeople);
                peopleText = intent.getStringExtra(BroadcastSender.EXTRA_PEOPLE);
            }
        }
    };


    private MapboxMap.OnCameraMoveListener cameraListener = new MapboxMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            LatLng location = new LatLng(lat, lng);
            if (issMarker == null) issMarker = map.addMarker(buildNewMarkerFromLatLng(location));
            else {
                updateMarker(location);
            }
            map.getMarkerViewManager().updateMarkerViewsPosition();
            map.getMarkerViewManager().invalidateViewMarkersInVisibleRegion();
        }
    };

    private Handler handler = new Handler();
    private Runnable timer = new Runnable() {
        @Override
        public void run() {

            SyncService.getISSPosition(getApplicationContext());
            SyncService.getISSPeople(getApplicationContext());

            lastSyncTime = Calendar.getInstance().getTimeInMillis();
            onNewIssPositionReceived(lat, lng);
            getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putString("iss_location", "")
                    .apply();
            lat = lat + 0.001f;
            lng = lng - 0.01f;
            handler.postDelayed(this, 4000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        lastSyncView = findViewById(R.id.last_sync_box);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapLoaded = false;
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                mapLoaded = true;
                showLastSyncInfo();
                map.addOnCameraMoveListener(cameraListener);
                handler.postDelayed(timer, 4000);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        lat = preferences.getFloat("latitude", lat);
        lng = preferences.getFloat("longitude", lng);
        lastSyncTime = preferences.getLong("lastSync", 0);
        peopleText = preferences.getString("people", "");
        numberOfPeople = preferences.getInt("numberOfPeople", 0);
        showLastSyncInfo();
        broadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastSender.ACTION_ISS_LOCATION_DATA));
        broadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastSender.ACTION_ISS_PEOPLE_DATA));
        if(mapLoaded) handler.postDelayed(timer, 4000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        handler.removeCallbacks(timer);
        broadcastManager.unregisterReceiver(receiver);
        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putFloat("latitude", lat)
                .putFloat("longitude", lng)
                .putLong("lastSync", lastSyncTime)
                .putString("people", peopleText)
                .putInt("numberOfPeople", numberOfPeople)
                .apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void onNewIssPositionReceived(float latitude, float longitude) {
        LatLng location = new LatLng(latitude, longitude);
        animateCameraToPosition(location);
        showLastSyncInfo();
    }

    private void animateCameraToPosition(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(8f)
                        .tilt(0f)
                        .build()
        ));
    }

    private MarkerOptions buildNewMarkerFromLatLng(LatLng latLng){
        return new MarkerOptions()
                .position(latLng)
                .title(getMarkerTitle())
                .snippet(peopleText)
                .setIcon(IconFactory.getInstance(getApplicationContext()).fromResource(R.mipmap.ic_empty_pointer));
    }

    private void updateMarker(LatLng location){
        issMarker.setPosition(location);
        issMarker.setSnippet(peopleText);
        issMarker.setTitle(getMarkerTitle());
    }

    private void showLastSyncInfo(){
        if(lastSyncTime > 0) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String date = format.format(new java.util.Date(lastSyncTime));
            String text = (getString(R.string.last_sync_text)).concat(" ").concat(date);
            lastSyncView.setText(text);
        } else
            lastSyncView.setText("");
    }

    private String getMarkerTitle() {
        return getString(R.string.there_are_text)
                .concat(" "+numberOfPeople+" ")
                .concat(getString(R.string.people_on_iss));
    }
}
