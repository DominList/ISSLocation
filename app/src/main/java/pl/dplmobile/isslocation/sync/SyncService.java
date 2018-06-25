package pl.dplmobile.isslocation.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;


public class SyncService extends IntentService {
    private static final String ACTION_GET_ISS_POSITION = "pl.dplmobile.isslocation.sync.action.ISS_POSITION";
    private static final String ACTION_GET_ISS_PEOPLE = "pl.dplmobile.isslocation.sync.action.ISS_PEOPLE";

    public SyncService() {
        super("SyncService");
    }


    public static void getISSPosition(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_GET_ISS_POSITION);
        context.startService(intent);
    }


    public static void getISSPeople(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_GET_ISS_PEOPLE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_ISS_POSITION.equals(action)) {
                handleActionGetPosition();
            } else if (ACTION_GET_ISS_PEOPLE.equals(action)) {
                handleActionGetPeople();
            }
        }
    }

    private void handleActionGetPosition(){
        new OkSync().performRequest(ConfigUrls.GET_POSITION_URL,
                null, new ISSPositionCallback(getApplicationContext()));
    }


    private void handleActionGetPeople(){
        new OkSync().performRequest(ConfigUrls.GET_PEOPLE_URL,
                null, new ISSPeopleCallback(getApplicationContext()));
    }
}
