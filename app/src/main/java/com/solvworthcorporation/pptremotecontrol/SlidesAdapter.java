package com.solvworthcorporation.pptremotecontrol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.solvworthcorporation.pptremotecontrol.sockets.ServerClient;

import java.io.File;
import java.util.List;

import static com.solvworthcorporation.pptremotecontrol.Constants.MAJOR;

/**
 * Created by packard bell on 30/04/2018.
 */

public class SlidesAdapter extends RecyclerView.Adapter<SlidesAdapter.MyViewHolder> {
    private Context mContext;
    private List<Items> itemsList;
    PrefManager prefManager;
    View v;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            v = view;
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.progressbar);
        }
    }

    public SlidesAdapter(Context mContext, List<Items> itemsList) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        prefManager = new PrefManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Items item = itemsList.get(position);
        holder.title.setText(item.getName());
        if(new File(MAJOR+(1+position)+".png").exists()) {
            if(!prefManager.getCached()){
                holder.thumbnail.setImageDrawable(Drawable.createFromPath(MAJOR+(1+position)+".png"));
                holder.progressBar.setVisibility(View.GONE);
            } else {
                new GenerateDrawable(new GenerateDrawable.onCompleted() {
                    @Override
                    public void completed(boolean done, Drawable d) {
                        if (done) {
                            holder.thumbnail.setImageDrawable(d);
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }
                }).execute(holder.getAdapterPosition());
            }
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("index:"+(holder.getAdapterPosition()+1));
            }
        });

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("index:"+(holder.getAdapterPosition()+1));
            }
        });

        holder.progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("index:"+(holder.getAdapterPosition()+1));
            }
        });
    }

    private void sendCommand(String command){
        new ServerClient(mContext, false, false, new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String response) {
                if(!Utils.parsedResponse(response).equals("true")){
                    Toast.makeText(mContext,
                            "Unable to carry out operation.",
                                Toast.LENGTH_LONG).show();
                }
            }
        }).setIPAddress(prefManager.getPreviousIp()).execute(command);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private static class GenerateDrawable extends AsyncTask<Integer, Void, Drawable>{

        onCompleted completed;

        GenerateDrawable(onCompleted completed){
            this.completed = completed;
        }

        interface onCompleted{
            void completed(boolean done, Drawable d);
        }

        @Override
        protected Drawable doInBackground(Integer... position) {
            return Drawable.createFromPath(MAJOR + (position[0] + 1) + ".png");
        }

        public void onPostExecute(Drawable drawable){
            completed.completed(true, drawable);
        }
    }
}