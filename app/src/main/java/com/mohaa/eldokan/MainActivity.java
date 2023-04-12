package com.mohaa.eldokan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mohaa.eldokan.Auth.LoginActivity;
import com.mohaa.eldokan.Controllers.BaseActivity;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends BaseActivity {
    private static int TIME_OUT = 3000; //Time to launch the another activity
    private FirebaseAuth mAuth;

    private FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    protected void onStart() {
        super.onStart();




        /*
        final ComponentName onBootReceiver = new ComponentName(getApplication().getPackageName(), MyBroadcastReceiver.class.getName());
        if(getPackageManager().getComponentEnabledSetting(onBootReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            getPackageManager().setComponentEnabledSetting(onBootReceiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
        Log.e(TAG, "Notification :  getPackageManager().setComponentEnabledSetting(onBootReceiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED");
        */



        Typeface typeface = ResourcesCompat.getFont(this, R.font.jomhuriaregular);

        TextView appname= findViewById(R.id.appname);
        appname.setTypeface(typeface);

        YoYo.with(Techniques.Bounce)
                .duration(7000)
                .playOn(findViewById(R.id.logo));

        YoYo.with(Techniques.FadeInUp)
                .duration(5000)
                .playOn(findViewById(R.id.appname));

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                sendtoHome();
            }
        }, TIME_OUT);


    }

    private void sendtoHome() {

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser != null) {
            final String device_token = FirebaseInstanceId.getInstance().getToken();//Device Token For Each Device
            String current_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());//Current_user_id

            Map<String , Object > tokenMap = new HashMap<>();//Map For put the data
            tokenMap.put("token_id" , device_token);

            fStore.collection("users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent loginIntent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(loginIntent);
                    finish();//Don't Return AnyMore TO the last page
                }
            });

        }
        else {
            Intent loginIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(loginIntent);
            finish();//Don't Return AnyMore TO the last page
        }


    }
}
