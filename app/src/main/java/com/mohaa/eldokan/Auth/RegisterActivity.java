package com.mohaa.eldokan.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.models.User;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import id.zelory.compressor.Compressor;


public class RegisterActivity extends BaseActivity {

    private EditText edtname, edtemail, edtpass;
    private String check,name,email,password;
    public static final String TAG = "MyTag";
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;



    private ProgressDialog mLoginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        //Progress Dialog
        mLoginProgress = new ProgressDialog(this);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();

        edtname = findViewById(R.id.name);
        edtemail = findViewById(R.id.email);
        edtpass = findViewById(R.id.password);


        edtname.addTextChangedListener(nameWatcher);
        edtemail.addTextChangedListener(emailWatcher);
        edtpass.addTextChangedListener(passWatcher);



        //validate user details and register user

        TextView button =findViewById(R.id.register);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO AFTER VALDATION
                if ( validateName() && validateEmail() && validatePass()){

                    name=edtname.getText().toString();
                    email=edtemail.getText().toString();
                    password=edtpass.getText().toString();


                    if (!TextUtils.isEmpty(name)) {
                        mLoginProgress.setTitle(getResources().getString(R.string.loading));
                        mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();

                        signUp(email  , password);
                    }
                    //Validation Success
                    //convertBitmapToString(profilePicture);

                }
            }
        });

        //Take already registered user to login page

        final TextView loginuser=findViewById(R.id.login_now);
        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        //take user to reset password




    }

    private void sendRegistrationEmail(final String name, final String emails) {



    }
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }







    private boolean validatePass() {


        check = edtpass.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
           return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {

        check = edtemail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
                return false;
        }

        return true;
    }

    private boolean validateName() {

        check = edtname.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtname.setError("Name Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edtemail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edtemail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edtemail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for pass -----------------------------------------------------

    TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtpass.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                edtemail.setError("Only @ special character allowed");
            }
        }

    };



    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    @Override
    protected void onStop () {
        super.onStop();
    }


    public void uploadFile(String userId , String name , String email) {
        Log.d(TAG, "uploadFile");


        final String device_token = FirebaseInstanceId.getInstance().getToken();

        final String randomname = UUID.randomUUID().toString();//generic randomname
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User users =  new User();
        users.setUser_id(userId);
        users.setUsername(name);
        users.setEmail(email);
        users.setCreated_date(timestamp.getTime());
        users.setName(edtname.getText().toString());
        users.setToken_id(device_token);
        users.setRole(0);
        users.setCredit(25);
        users.setStatus("i'm available.");
        fStore.collection("users").document(userId).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mLoginProgress.dismiss();
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                finish();
            }
        });
    }




    private void signUp(String email , String password) {
        Log.d(TAG, "signUp");





        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {

                            Toast.makeText(RegisterActivity.this, getString(R.string.signup_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(final FirebaseUser user) {
        //generic "in between two numbers"
        Random r = new Random();
        int Low = 10;
        int High = 100;
        final int random_number = r.nextInt(High-Low) + Low;
        final String username = usernameFromEmail(user.getEmail());//get username value



        User users =  new User();
        users.setUser_id(user.getUid());
        users.setUsername(username);
        users.setRole(0);
        fStore.collection("users").document(user.getUid()).set(users);
        com.google.firebase.firestore.Query mQuery = fStore.collection("users")
                .whereEqualTo("username", username);//Check username

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()){//if username exists >> There's a problem

                            String user_name_ex = username + random_number;//username + random_number to solve the problem
                            // Write new user
                            uploadFile(user.getUid(), user_name_ex, user.getEmail());

                        }
                        else
                        {
                            // Write new user
                            uploadFile(user.getUid(), username, user.getEmail());

                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    private boolean userNameExistsAlready(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
        com.google.firebase.firestore.Query mQuery = fStore.collection("users")
                .whereEqualTo("username", username);

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()){
                            Toast.makeText(RegisterActivity.this, "Username Exists Already!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        return false;
    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(edtemail.getText().toString())) {
            edtemail.setError(getString(R.string.required));
            result = false;
        } else {
            edtemail.setError(null);
        }

        if (TextUtils.isEmpty(edtpass.getText().toString())) {
            edtpass.setError(getString(R.string.required));
            result = false;
        } else {
            edtpass.setError(null);
        }


        return result;
    }


}


