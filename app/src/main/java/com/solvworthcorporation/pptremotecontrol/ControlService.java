package com.solvworthcorporation.pptremotecontrol;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.solvworthcorporation.pptremotecontrol.sockets.ServerClient;

import java.util.Timer;
import java.util.TimerTask;

import static com.solvworthcorporation.pptremotecontrol.Constants.CONTROL;
import static com.solvworthcorporation.pptremotecontrol.Constants.TEST_COMMAND3;

public class ControlService extends Service {
    public static final long NOTIFY_INTERVAL = 8000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    PrefManager prefManager;

    public void onCreate(){
        super.onCreate();
        prefManager = new PrefManager(getApplicationContext());
        if(mTimer != null){
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    public void onTaskRemoved(Intent intent){
        if(mTimer != null)
        mTimer.cancel();
        super.onTaskRemoved(intent);
    }

    public ControlService() {

    }

    public int onStartCommand(Intent intent, int flags, int startId){
        createNotification();
        try {
            if (intent != null) {
                if (intent.getStringExtra(CONTROL).equals("stop")) {
                    stopSelf();
                }
            }
        } catch (NullPointerException ignored){}
        return START_STICKY;
    }

    private void createNotification() {
        NotificationCompat.Builder notification =  new NotificationCompat.Builder(this);
        Notification n = notification.setContentTitle("Tenue Remote Control")
                .setContentText("Remote connected...")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker("Remote Connected")
                .build();
        startForeground(100, n);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    new ServerClient(getApplicationContext(), false, false, new ServerClient.onFinishedProcess() {
                        @Override
                        public void processFinished(String response) {
                            //Log.e("Service: ", Utils.parsedResponse(response));
                            if(!Utils.parsedResponse(response).equals("true")){
                                mTimer.cancel();
                                stopService(new Intent(getApplicationContext(), ControlService.class));
                                Toast.makeText(getApplicationContext(), "Remote Disconnected", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).setIPAddress(prefManager.getPreviousIp()).execute(TEST_COMMAND3);
                }
            });
        }
    }
}
