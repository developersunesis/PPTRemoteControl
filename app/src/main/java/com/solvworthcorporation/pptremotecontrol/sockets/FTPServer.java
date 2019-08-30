package com.solvworthcorporation.pptremotecontrol.sockets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer extends AsyncTask<Void, Void, Void> {

    private int position = 1;

    @Override
    protected Void doInBackground(Void... voids) {
        startServer();
        return null;
    }

    public void onPreExecute(){
        super.onPreExecute();
    }

    private void startServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(4241);
            Log.e("FTPServer", "Server Started");
            while (true){
                Socket socket = serverSocket.accept();
                //new ClientThread(socket, position).start();
                position = position+1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    protected void onPostExecute(Void result){
        super.onPostExecute(result);
    }
}
