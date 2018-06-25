package pl.dplmobile.isslocation.sync;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.dplmobile.isslocation.BroadcastSender;


public class ISSPeopleCallback implements Callback {

    private final String TAG = getClass().getSimpleName();
    private BroadcastSender sender;
    private String stringResponse = null;

    public ISSPeopleCallback(Context context) {
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
                    int numberOfPeople = jsonObject.getInt("number");
                    JSONArray peopleArr = jsonObject.getJSONArray("people");
                    String people = "";
                    for (int i = 0; i < peopleArr.length(); i++) {
                        JSONObject person = peopleArr.getJSONObject(i);
                        people = people.concat(person.getString("name" ));
                        if(i != peopleArr.length() - 1) people = people.concat("\n");
                    }
                    sender.sendIssPeople(people, numberOfPeople);

                } catch(JSONException e) {

                    Log.e(TAG, "Getting data from json exception: ", e);

                }

                ;
            }

        } else {
            Log.e(TAG, "Response failed...");
        }
    }



}

