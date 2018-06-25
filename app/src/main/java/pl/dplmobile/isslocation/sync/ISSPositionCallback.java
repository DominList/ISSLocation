package pl.dplmobile.isslocation.sync;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.dplmobile.isslocation.BroadcastSender;


public class ISSPositionCallback implements Callback {

    private final String TAG = getClass().getSimpleName();
    private BroadcastSender sender;
    private String stringResponse = null;

    public ISSPositionCallback(Context context) {
        sender = new BroadcastSender(context);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "Request failed", e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            try {
                stringResponse = response.body().string();
                Log.i(TAG, "Response = " + stringResponse);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if(stringResponse != null) {


                try {

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    long timestamp = jsonObject.getLong("timestamp");
                    JSONObject  position = jsonObject.getJSONObject("iss_position");
                    float latitude = position.getInt("latitude");
                    float longitude = position.getInt("longitude");
                    sender.sendIssLocation( latitude, longitude, timestamp);

                } catch(JSONException e) {

                    Log.e(TAG, "Getting data from json exception: ", e);

                }
            }

        } else {
            Log.e(TAG, "Response failed...");
        }
    }



}

