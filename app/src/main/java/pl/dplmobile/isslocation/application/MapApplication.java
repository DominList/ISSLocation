package pl.dplmobile.isslocation.application;

import android.app.Application;
import com.mapbox.mapboxsdk.Mapbox;
import pl.dplmobile.isslocation.R;

public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }
}
