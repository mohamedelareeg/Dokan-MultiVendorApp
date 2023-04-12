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
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.models.User;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import id.zelory.compressor.Compressor;


public class PhoneRegisterActivity extends BaseActivity {

    private EditText edtname, edtpass, edtcnfpass;
    private String check,name,email,password,mobile,profile;
    CircleImageView image;
    ImageView upload;
    protected MediaSelector mediaSelector = new MediaSelector();
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;
    public static final String TAG = "MyTag";
    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    //private String user_id;
    //Picture
    private Bitmap compressedImageFile;
    private File new_image_file;
    private EditText etPhoneNumber;
    private ProgressDialog mLoginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

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
        //user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();

        upload=findViewById(R.id.uploadpic);
        image=findViewById(R.id.profilepic);
        edtname = findViewById(R.id.name);

        edtpass = findViewById(R.id.password);
        edtcnfpass = findViewById(R.id.confirmpassword);


        edtname.addTextChangedListener(nameWatcher);

        edtpass.addTextChangedListener(passWatcher);
        edtcnfpass.addTextChangedListener(cnfpassWatcher);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);



        //validate user details and register user

        TextView button =findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO AFTER VALDATION
                if (validateProfile() && validateName() && validatePass() && validateCnfPass()) {

                    name = edtname.getText().toString();

                    password = edtcnfpass.getText().toString();
                    String mobile = etPhoneNumber.getText().toString().trim();

                    if(mobile.isEmpty() || mobile.length() < 11){
                        etPhoneNumber.setError("Enter a valid mobile");
                        etPhoneNumber.requestFocus();
                        return;
                    }
                    if (!TextUtils.isEmpty(name) && getNew_image_file() != null) {
                        mLoginProgress.setTitle("Uploading Image");
                        mLoginProgress.setMessage("Please wait while we check your credentials.");
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();

                        uploadFile(getNew_image_file(), name , password , mobile);
                    }
                }
            }
        });

        upload.setOnClickListener(view -> mediaSelector.startChooseImageActivity(PhoneRegisterActivity.this, MediaSelector.CropType.Circle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                image.setImageURI(Uri.fromFile(getNew_image_file()));
            }
            catch (Exception e) {
                Toast.makeText(PhoneRegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void sendRegistrationEmail(final String name, final String emails) {



    }
    @Override
    public void onStart() {
        super.onStart();


    }
    private void convertBitmapToString(Bitmap profilePicture) {
            /*
                Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
                so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
            */
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        profile = Base64.encodeToString(array, Base64.DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            mediaSelector.handleResult(this, requestCode, resultCode, data);
            IMAGE_STATUS = true;//setting the flag
        }
        catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateProfile() {
        if (!IMAGE_STATUS)
            Toasty.info(PhoneRegisterActivity.this,"Select A Profile Picture", Toast.LENGTH_LONG).show();
        return IMAGE_STATUS;
    }
    private boolean validateCnfPass() {

        check = edtcnfpass.getText().toString();

        return check.equals(edtpass.getText().toString());
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
            }
        }

    };

    //TextWatcher for repeat Password -----------------------------------------------------

    TextWatcher cnfpassWatcher = new TextWatcher() {
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

            if (!check.equals(edtpass.getText().toString())) {
                edtcnfpass.setError("Both the passwords do not match");
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


    public void uploadFile(File file , String name , String pass , String mobile) {
        Log.d(TAG, "uploadFile");

        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);
        final String device_token = FirebaseInstanceId.getInstance().getToken();
        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(PhoneRegisterActivity.this)//Compressor Library
                    .setMaxWidth(256)
                    .setMaxHeight(256)
                    .setQuality(5)

                    .compressToBitmap(new_image_file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FilePaths filePaths = new FilePaths();
        final String randomname = UUID.randomUUID().toString();//generic randomname
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumb_data = baos.toByteArray();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + name + "/profile_pics/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + name + "/profile_pics/").child(randomname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                                String download_thumb_uri = uri.toString();
                                mLoginProgress.dismiss();
                                Intent intent = new Intent(PhoneRegisterActivity.this, VerificationCodeActivity.class);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("download_thumb_uri", download_thumb_uri);
                                intent.putExtra("name", name);
                                intent.putExtra("pass", pass);
                                startActivity(intent);
                    }
                });

            }
        });
    }


    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }











}


