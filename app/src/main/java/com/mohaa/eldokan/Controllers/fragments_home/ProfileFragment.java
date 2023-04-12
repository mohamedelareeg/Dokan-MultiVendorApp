package com.mohaa.eldokan.Controllers.fragments_home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mohaa.eldokan.Auth.LoginActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.Controllers.activities_dashboard.DashboardActivity;
import com.mohaa.eldokan.Controllers.activities_popup.ProfileEditActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.MainActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    int cartCount = 0;
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore fStore;
    private LinearLayout dashboard_panel;
    private LinearLayout cart_panel;
    private LinearLayout wishList_panel;
    private LinearLayout language_panel;
    private static String current_user_id;//Current_user_id>> Using Firebase Auth

    private CircleImageView profile_image;
    private TextView profile_name , profile_credit , profile_role , profile_edit;
    private RelativeLayout layout_panel;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile , container , false);

        //check Internet Connection
        new CheckInternetConnection(getContext()).checkConnection();

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        dashboard_panel = view.findViewById(R.id.dashboard_panel);
        cart_panel = view.findViewById(R.id.cart_panel);
        wishList_panel = view.findViewById(R.id.wish_list_panel);
        language_panel = view.findViewById(R.id.language_panel);


        profile_image = view.findViewById(R.id.profile_image);
        profile_name = view.findViewById(R.id.profile_name);
        profile_role = view.findViewById(R.id.profile_role);
        profile_credit = view.findViewById(R.id.profile_credit);
        profile_edit = view.findViewById(R.id.edit_profile);

        layout_panel = view.findViewById(R.id.layout_panel);

        layout_panel.setVisibility(View.INVISIBLE);
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser != null) {
            current_user_id = mAuth.getCurrentUser().getUid();//get current_user_id
            init_main();

        }


        layout_panel.setVisibility(View.VISIBLE);

        profile_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getContext(), ProfileEditActivity.class);
                startActivity(loginIntent);
            }
        });


        return view;
    }
    @Override
    public void onStart() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser == null) {
            sendtoLogin();

        }
        super.onStart();
    }
    public void sendtoLogin()
    {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();



    }

    @Override
    public void onResume() {
        //check Internet Connection

        //check Internet Connection
        new CheckInternetConnection(getContext()).checkConnection();

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser == null) {
            sendtoLogin();

        }

        super.onResume();
    }


    private void init_main() {

        fStore.collection("users").document(current_user_id).get() // Check The Database fof the document
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                String name = task.getResult().getString("name");
                                String thumb_image = task.getResult().getString("thumb_image");

                                String credit = String.valueOf(task.getResult().getLong("credit"));
                                profile_name.setText(name);
                                profile_credit.setText( getResources().getString(R.string.credit)+ credit);
                                if(thumb_image != null)
                                {
                                    Glide.with(getContext())
                                            .load(thumb_image) // image url
                                            .apply(new RequestOptions()

                                                    .override(512, 512) // resizing
                                                    .centerCrop())
                                            .into(profile_image);  // imageview object
                                }
                                long role = task.getResult().getLong("role");
                                if(role == 1 || role ==  2)
                                {
                                    dashboard_panel.setVisibility(View.VISIBLE);
                                    profile_role.setVisibility(View.VISIBLE);
                                    FirebaseMessaging.getInstance().subscribeToTopic("promotional_messages");
                                }
                                init();
                            }
                        }
                        //else = if unsuccessful
                    }
                    //on failure
                });
    }

    private void init() {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String data = prefs.getString("language", ""); //no id: default value
        cart_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartCount = OrdersBase.getInstance().getmOrders().size();

                if (cartCount > 0) {
                    Intent loginIntent = new Intent(getContext(), CartReadyActivity.class);
                    startActivity(loginIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.cart_is_empty, Toast.LENGTH_LONG).show();
                }

            }
        });
        wishList_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        language_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(data.equals("en"))
                {
                    LocaleHelper.setLocale(getActivity(), "ar");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    storeLanguageInPref("ar");
                    getActivity().finish();
                }
                else if(data.equals("ar"))
                    {
                        LocaleHelper.setLocale(getActivity(), "en");
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        storeLanguageInPref("en");
                        getActivity().finish();
                }
                else {
                    LocaleHelper.setLocale(getActivity(), "ar");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    storeLanguageInPref("ar");
                    getActivity().finish();
                }


            }
        });
        dashboard_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getContext(), DashboardActivity.class);
                startActivity(loginIntent);
            }
        });
    }
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
    //SharedPreferences  language = getActivity().getSharedPreferences("language",MODE_PRIVATE);
    private void storeLanguageInPref(String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("language", language);
        editor.apply();
    }
    //Responsible For Adding the 3 tabs : Camera  , Home , Messages

}
