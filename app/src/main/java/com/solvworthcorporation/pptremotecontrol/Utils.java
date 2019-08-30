package com.solvworthcorporation.pptremotecontrol;

import android.content.Context;
import android.util.Log;

import com.solvworthcorporation.pptremotecontrol.sockets.ServerClient;


class Utils {

    static String sendMessage(Context context, String ip, String command){
        final String[] response = {""};
        new ServerClient(context, true, false, new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String yes) {
                response[0] = yes;
            }
        }).setIPAddress(ip).execute(command);
        return parsedResponse(response[0]);
    }

    static String parsedResponse(String r){
        try {
            Log.e("PARSED", r.substring(r.indexOf("command:{")+9, r.indexOf("}")));
            return r.substring(r.indexOf("command:{")+9, r.indexOf("}"));
        } catch (StringIndexOutOfBoundsException ex){
            return r;
        }
    }

    static String[] parsedResponseSlideNames(String r){
        try {
            Log.e("Slides", r.substring(r.indexOf("\nslides:[")+9, r.indexOf("]")));
            return r.substring(r.indexOf("\nslides:[")+9, r.indexOf("]")).split(";");
        } catch (StringIndexOutOfBoundsException ex){
            ex.printStackTrace();
            return new String[0];
        }
    }

    static String parsedImageResponse(String r){
        try {
            return r.substring(r.indexOf("sc:{")+4, r.indexOf("}:end"));
        } catch (StringIndexOutOfBoundsException ex){
            return r;
        }
    }
}
