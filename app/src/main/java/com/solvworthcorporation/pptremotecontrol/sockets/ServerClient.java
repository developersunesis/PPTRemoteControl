package com.solvworthcorporation.pptremotecontrol.sockets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.solvworthcorporation.pptremotecontrol.Constants.IMAGES;

public class ServerClient extends AsyncTask<String, Void, String> {


    private Context context;
    private String ip;
    private ProgressDialog progressDialog;
    boolean showDialog;
    boolean timeOut;
    String[] titles;
    private int port = 2522;

    public interface  onFinishedProcess{
        void processFinished(String response);
    }

    public onFinishedProcess onFinishedProcess;

    public ServerClient(Context ctx, boolean s, boolean setTimeout, onFinishedProcess onFinishedProcess){
        context = ctx;
        progressDialog = new ProgressDialog(context);
        showDialog = s;
        timeOut = setTimeout;
        this.onFinishedProcess = onFinishedProcess;
    }

    public ServerClient(Context ctx, boolean s, String[] titles, boolean setTimeout, onFinishedProcess onFinishedProcess){
        context = ctx;
        progressDialog = new ProgressDialog(context);
        showDialog = s;
        timeOut = setTimeout;
        this.titles = titles;
        this.onFinishedProcess = onFinishedProcess;
    }

    public ServerClient(Context ctx, boolean s, int port, boolean setTimeout, onFinishedProcess onFinishedProcess){
        context = ctx;
        progressDialog = new ProgressDialog(context);
        showDialog = s;
        timeOut = setTimeout;
        this.port = port;
        this.onFinishedProcess = onFinishedProcess;
    }

    public ServerClient(Context ctx, String i, boolean s, onFinishedProcess onFinishedProcess){
        context = ctx;
        ip = i;
        showDialog = s;
        progressDialog = new ProgressDialog(context);
        this.onFinishedProcess = onFinishedProcess;
    }

    public ServerClient setIPAddress(String i){
        ip = i;
        return this;
    }

    public void onPreExecute(){
        if(titles != null){
            progressDialog.setMessage("Preparing slides...");
        } else {
            progressDialog.setMessage("Processing...");
        }
        progressDialog.setCancelable(false);
        if(showDialog)
            progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String response;
        try {
            Socket clientSocket = new Socket();

            if(timeOut) {
                clientSocket.connect(new InetSocketAddress(ip, port), 5000);
            } else {
                clientSocket.connect(new InetSocketAddress(ip, port), 10000);
            }

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            InputStream inStream = clientSocket.getInputStream();
            //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(inStream));
            byte[] data = (strings[0]+";").getBytes();
            outToServer.write(data, 0, data.length);
            outToServer.flush();

            if(strings[0].equals(IMAGES)){
                if(titles !=null){
                    for(int i = 0; i< titles.length; i++){
                        try {
                            Socket clientFTPSocket = new Socket(ip, (4241+i));
                            Log.e("Route: ", ""+(4241+i));
                            clientFTPSocket.setSoTimeout(10000);
                            new ClientThread(clientFTPSocket.getInputStream(), (i+1)).start();
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            response = IOUtils.toString(inStream, "UTF-8");
            Log.e("SERVER RESPONSE", response);
            //clientSocket.close();
        } catch (IOException ex) {
            response = "null";
        } catch (NullPointerException ex){
            response = "null";
        }
        return response;
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        onFinishedProcess.processFinished(result);
    }
}
