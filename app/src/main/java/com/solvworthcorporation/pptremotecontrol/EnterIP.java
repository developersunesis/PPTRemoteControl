package com.solvworthcorporation.pptremotecontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.solvworthcorporation.pptremotecontrol.sockets.ServerClient;

import static com.solvworthcorporation.pptremotecontrol.Constants.IP;
import static com.solvworthcorporation.pptremotecontrol.Constants.TEST_COMMAND;

public class EnterIP extends AppCompatActivity {

    EditText ipAddress;
    Button connect;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_ip);

        ipAddress = findViewById(R.id.ipAddress);
        connect = findViewById(R.id.connect);
        prefManager = new PrefManager(this);
        if(!prefManager.getPreviousIp().isEmpty())
            ipAddress.setText(prefManager.getPreviousIp());
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ipAddress.getText().toString().isEmpty() && ipAddress.getText().toString().contains(".")){
                    startConnect(ipAddress.getText().toString());
                } else{
                    Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void startConnect(final String ip){
        new ServerClient(this, true, true, new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String response) {
                if(Utils.parsedResponse(response).equals("true")){
                    Toast.makeText(getApplicationContext(),
                            "Remote Connected.",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(IP, ip);
                    prefManager.setPreviousIp(ip);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Unable to connect remote.\nPlease double check the IP Address",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).setIPAddress(ip).execute(TEST_COMMAND);
    }

    public void onBackPressed(){
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Attention");
        build.setMessage("Do you really want to exit?");
        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EnterIP.super.onBackPressed();
            }
        });
        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        build.show();
    }
}
