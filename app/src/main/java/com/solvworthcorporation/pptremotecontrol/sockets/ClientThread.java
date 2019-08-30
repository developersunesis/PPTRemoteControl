package com.solvworthcorporation.pptremotecontrol.sockets;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.solvworthcorporation.pptremotecontrol.Constants.MAJOR;

public class ClientThread extends Thread {

    InputStream in;
    private OutputStream out = null;
    private int position;

    ClientThread(InputStream in, int position){
        this.in = in;
        this.position = position;
        Log.e("FTPServer", "ServerClient Started");
    }

    @Override
    public void run(){
        try{
            out = new FileOutputStream(MAJOR+position+".png");
            byte[] bytes = new byte[16*1024];
            int count;
            while((count = in.read(bytes)) != -1){
                out.write(bytes, 0, count);
            }
            Log.e("FTPServer", "ServerClient Done");
            out.flush();
            out.close();
            in.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
