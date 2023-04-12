package com.mohaa.eldokan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.Controllers.activities_popup.ProfileEditActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.Utils.PermUtil;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

import java.sql.Timestamp;
import java.util.HashMap;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "HomeActivity";


    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    public NavController navController;
    public NavigationView navigationView;

    //FireBase
    private FirebaseFirestore fStore;
    private TextView count;
    private ImageView cart;
    public final static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public final static String EXTRA_WITH_LINE_PADDING = "EXTRA_WITH_LINE_PADDING";
    int cartCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        init();
    }
    /*
    @Override
    public void onPostCreate(@Nullable @Nullable Bundle savedInstanceState, @Nullable @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();
    }
*/



    private void init() {


        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        //String data = prefs.getString("language", ""); //no id: default value
        //Toast.makeText(this, "" + data, Toast.LENGTH_SHORT).show();
        count = findViewById(R.id.count);
        //FireBase
        //mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //user_id = mAuth.getCurrentUser().getUid();
        //current_user = mAuth.getCurrentUser();
        //
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.my_drawer);

        navigationView = findViewById(R.id.navigationView);

        mToggle = new ActionBarDrawerToggle(this , drawerLayout , R.string.open , R.string.close);
        drawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartCount > 0 ) {
                    Intent loginIntent = new Intent(HomeActivity.this, CartReadyActivity.class);
                    startActivity(loginIntent);
                }
                else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.cart_is_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });







    }
    //Responsible For Adding the 3 tabs : Camera  , Home , Messages
    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        // Update Cart Count
        cartCount = OrdersBase.getInstance().getmOrders().size();

        if (cartCount > 0) {
            count.setVisibility(View.VISIBLE);
            count.setText(String.valueOf(cartCount));
        } else {
            count.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(MultiEditorActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            attemptToExitIfRoot(null);
        }


    }

    // convert the list of contact to a map of members





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.nav_orders:

                navController.navigate(R.id.orders_fragment);
                break;




            case R.id.nav_profile:

                navController.navigate(R.id.profilefragment);
                break;

        }
        return true;

    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(drawerLayout, Navigation.findNavController(this, R.id.nav_host_fragment));
    }
}
