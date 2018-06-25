package pl.dplmobile.isslocation.sync;

import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkSync {

    private final String TAG = getClass().getSimpleName();


    public void performRequest(String url, HashMap<String, String> headers, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.url(url);

        if(headers != null)
        for(String key: headers.keySet()){
            builder.addHeader(key, headers.get(key));
        }

        Request request = builder.build();
        client.newCall(request).enqueue(callback);
    }



}