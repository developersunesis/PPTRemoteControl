package com.solvworthcorporation.pptremotecontrol;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sunesis 04/01/2017.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;


    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences("tenue_ppt_pref", PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setPreviousIp(String input) {
        editor.putString("ip", input);
        editor.commit();
    }

    public String getPreviousIp() {
        //remember to always change this in Splash.java
        return pref.getString("ip", "");
    }

    public void setCaached(boolean input) {
        editor.putBoolean("cached", input);
        editor.commit();
    }

    public boolean getCached() {
        //remember to always change this in Splash.java
        return pref.getBoolean("cached", false);
    }
}