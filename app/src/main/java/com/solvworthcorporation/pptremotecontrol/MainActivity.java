package com.solvworthcorporation.pptremotecontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.solvworthcorporation.pptremotecontrol.sockets.FTPServer;
import com.solvworthcorporation.pptremotecontrol.sockets.ServerClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.solvworthcorporation.pptremotecontrol.Constants.IMAGES;
import static com.solvworthcorporation.pptremotecontrol.Constants.IP;
import static com.solvworthcorporation.pptremotecontrol.Constants.NEXT;
import static com.solvworthcorporation.pptremotecontrol.Constants.PREV;
import static com.solvworthcorporation.pptremotecontrol.Constants.START;
import static com.solvworthcorporation.pptremotecontrol.Constants.STOP;
import static com.solvworthcorporation.pptremotecontrol.Constants.TEST_COMMAND2;


public class MainActivity extends AppCompatActivity {

    ImageButton startPresentation, up, down, left, right, stopPresentation, stopControl;
    Button controlDownUp;
    String ip;
    boolean listShow = false, cached = false;
    LinearLayout slides;
    ListView slidesList;
    AnimationSet animationSet;
    String[] titles;
    TextView noSlides;

    private RecyclerView recyclerView;
    private SlidesAdapter adapter;
    private List<Items> itemsList;


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(getIntent() != null){
            if(getIntent().getStringExtra(IP) != null){
                ip = getIntent().getStringExtra(IP);
            }
        }
        startService(new Intent(getApplicationContext(), ControlService.class));

        initAnimations();

        //declarations
        startPresentation= findViewById(R.id.start);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        stopPresentation = findViewById(R.id.stopPresentation);
        stopControl = findViewById(R.id.stopControl);
        controlDownUp = findViewById(R.id.expandUp);
        slides = findViewById(R.id.slides);
        slidesList = findViewById(R.id.slideList);
        recyclerView = findViewById(R.id.recycler_view);
        noSlides = findViewById(R.id.noSlides);
        itemsList = new ArrayList<>();

        slides.setVisibility(View.GONE);

        controlDownUp.getBackground().setLevel(5000);

        adapter = new SlidesAdapter(getApplicationContext(), itemsList);

        //listeners
        startPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(START, true);
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(PREV);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(NEXT);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(PREV);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(NEXT);
            }
        });

        stopPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build = new AlertDialog.Builder(v.getContext());
                build.setTitle("Attention");
                build.setMessage("Do you really want to end the slideshow?");
                build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendCommand(STOP);
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
        });

        stopControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build = new AlertDialog.Builder(v.getContext());
                build.setTitle("Attention");
                build.setMessage("Do you really want to disconnect Tenue Remote?");
                build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendCommand(TEST_COMMAND2);
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
        });

       controlDownUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setControls(listShow);
            }
        });

       slidesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               sendCommand("index:"+(position+1));
           }
       });

       loadView();
    }

    private void refreshSlides(String[] titles){
        if(titles != null) {
            itemsList.clear();
            for (String title : titles) {
                itemsList.add(new Items(title));
            }
            adapter = new SlidesAdapter(getApplicationContext(), itemsList);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            noSlides.setText("Presentation Slides ("+titles.length+")");
        }

    }

    public void loadView() {
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        } else {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
    }

    private void initAnimations() {
        Animation fadeIn, fadeOut;
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(fadeOut);
    }

    private void setControls(boolean listSho) {
        initAnimations();
        controlDownUp.setEnabled(true);
        if(listSho){
            slides.setAnimation(animationSet.getAnimations().get(1));
            controlDownUp.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_expand_less_black_24dp), null, null);
            slides.setVisibility(View.GONE);
            listShow= false;
        } else {
            if(titles == null){
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("Attention");
                build.setMessage("You have not started any presentation, would you like to do so now?");
                build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controlDownUp.setEnabled(false);
                        sendCommandDuplicate(START, true);
                    }
                });
                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                build.show();
            } else {
                slides.setAnimation(animationSet.getAnimations().get(0));
                refreshSlides(titles);

                if (!cached) {
                    clearAllPreviousFiles();
                    new PrefManager(getApplicationContext()).setCaached(false);
                    sendCommand(IMAGES, false, titles);
                }
                controlDownUp.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_expand_more_black_24dp), null, null);
                slides.setVisibility(View.VISIBLE);
                listShow = true;
            }
        }
    }

    private boolean clearAllPreviousFiles() {
        boolean result = false;
        File dir = new File(Constants.MAJOR);
        for(File file : dir.listFiles()){
            result = file.delete();
        }
        return result;
    }

    public void sendCommand(String command){
        new ServerClient(this, false, false, new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String response) {
                if(Utils.parsedResponse(response).equals("stopped")){
                    stopService(new Intent(getApplicationContext(), ControlService.class));
                    startActivity(new Intent(getApplicationContext(), EnterIP.class));
                    finish();
                } else {
                    if(!Utils.parsedResponse(response).equals("true")){
                        Toast.makeText(getApplicationContext(),
                                "Unable to carry out operation.",
                                Toast.LENGTH_LONG).show();

                        AlertDialog.Builder build = new AlertDialog.Builder(slides.getContext());
                        build.setTitle("Attention");
                        build.setMessage("Remote already disconnected, would you like to reconnect?");
                        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), EnterIP.class));
                            }
                        });
                        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        build.show();
                    } else if(Utils.parsedResponseSlideNames(response).length > 0){
                        titles = Utils.parsedResponseSlideNames(response);
                        slidesList.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles));
                    }
                }
            }
        }).setIPAddress(ip).execute(command);
    }

    public void sendCommandDuplicate(final String command, boolean showProgress){
        new ServerClient(this, showProgress,false, new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String response) {
                if (Utils.parsedResponse(response).equals("stopped")) {
                    stopService(new Intent(getApplicationContext(), ControlService.class));
                    startActivity(new Intent(getApplicationContext(), EnterIP.class));
                    finish();
                } else {
                    if (!Utils.parsedResponse(response).equals("true")) {
                        Toast.makeText(getApplicationContext(),
                                "Unable start presentation.",
                                Toast.LENGTH_LONG).show();
                    } else if (Utils.parsedResponseSlideNames(response).length > 0) {
                        titles = Utils.parsedResponseSlideNames(response);
                        refreshSlides(titles);
                        setControls(false);
                    }
                }
            }
        }).setIPAddress(ip).execute(command);
    }


    public void sendCommand(final String command, boolean showProgress){
        new ServerClient(this, showProgress,false, new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String response) {
                if (Utils.parsedResponse(response).equals("stopped")) {
                    stopService(new Intent(getApplicationContext(), ControlService.class));
                    startActivity(new Intent(getApplicationContext(), EnterIP.class));
                    finish();
                } else {
                    if (!Utils.parsedResponse(response).equals("true")) {
                        Toast.makeText(getApplicationContext(),
                                "Unable to carry out operation.",
                                Toast.LENGTH_LONG).show();
                        AlertDialog.Builder build = new AlertDialog.Builder(slides.getContext());
                        build.setTitle("Attention");
                        build.setMessage("Remote already disconnected, would you like to reconnect?");
                        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), EnterIP.class));
                            }
                        });
                        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        build.show();
                    } else if (Utils.parsedResponseSlideNames(response).length > 0) {
                        titles = Utils.parsedResponseSlideNames(response);
                        refreshSlides(titles);
                    }
                }
            }
        }).setIPAddress(ip).execute(command);
    }

    public void sendCommand(final String command, boolean showProgress, final String[] titles){
        new ServerClient(this, showProgress, titles,false,  new ServerClient.onFinishedProcess() {
            @Override
            public void processFinished(String response) {
                if (Utils.parsedResponse(response).equals("stopped")) {
                    stopService(new Intent(getApplicationContext(), ControlService.class));
                    startActivity(new Intent(getApplicationContext(), EnterIP.class));
                    finish();
                } else {
                    if (!Utils.parsedResponse(response).equals("true")) {
                        Toast.makeText(getApplicationContext(),
                                "Unable to carry out operation.",
                                Toast.LENGTH_LONG).show();
                        AlertDialog.Builder build = new AlertDialog.Builder(slides.getContext());
                        build.setTitle("Attention");
                        build.setMessage("Remote already disconnected, would you like to reconnect?");
                        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), EnterIP.class));
                            }
                        });
                        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        build.show();
                    } else {
                        cached = true;
                        refreshSlides(titles);
                        new PrefManager(getApplicationContext()).setCaached(true);
                    }
                }
            }
        }).setIPAddress(ip).execute(command);
    }

    public void onBackPressed(){
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Attention");
        build.setMessage("Do you really want to exit?");
        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
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
