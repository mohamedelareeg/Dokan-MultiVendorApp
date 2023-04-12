package com.mohaa.eldokan.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.models.User;

import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VerificationCodeActivity extends BaseActivity {

    private static final String TAG =  "VerificationCode";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";


    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //The edittext to input the code
    private AppCompatEditText editTextCode;
    private AppCompatButton btnResendCode;

    //firebase auth object
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String mobile , name , password , thumb_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        editTextCode = findViewById(R.id.etPhoneNumber_);


        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");

        //startPhoneNumberVerification("01001029570");


        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });
        btnResendCode = findViewById(R.id.btnResendCode);
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mobile, mResendToken);
            }
        });
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
               // updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                   // mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


            }
        };
        // [END phone_auth_callbacks]
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        // [START_EXCLUDE]
        if (mVerificationInProgress) {
            startPhoneNumberVerification(mobile);
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationCodeActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = getIntent();
                            name = intent.getStringExtra("name");
                            password = intent.getStringExtra("pass");
                            thumb_image = intent.getStringExtra("download_thumb_uri");
                            onAuthSuccess(task.getResult().getUser() , name , password , thumb_image);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
    private void onAuthSuccess(final FirebaseUser user  , String _name , String _password , String thumb_image) {

        Random r = new Random();
        int Low = 10;
        int High = 100;
        final int random_number = r.nextInt(High-Low) + Low;
        final String username = user.getPhoneNumber() + "@" + random_number;//get username value
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final String device_token = FirebaseInstanceId.getInstance().getToken();


        User users =  new User();
        users.setUser_id(user.getUid());
        users.setRole(0);
        users.setCredit(50);
        users.setCreated_date(timestamp.getTime());
        users.setToken_id(device_token);
        users.setUsername(username);
        users.setThumb_image(thumb_image);
        users.setRole(0);
        users.setName(_name);
        users.setPass(_password);
        users.setPhone_number(mobile);
        fStore.collection("users").document(user.getUid()).set(users);
        com.google.firebase.firestore.Query mQuery = fStore.collection("users")
                .whereEqualTo("user_id", user.getUid());//Check username

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()){//if username exists >> There's a problem

                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerificationCodeActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerificationCodeActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerificationCodeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}