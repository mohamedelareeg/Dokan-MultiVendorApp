package com.mohaa.eldokan.Controllers.activities_popup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import id.zelory.compressor.Compressor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohaa.eldokan.Auth.LoginActivity;
import com.mohaa.eldokan.Auth.RegisterActivity;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.SplashActivity;
import com.mohaa.eldokan.Controllers.fragments_home.LogoutHelper;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.MainActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.Utils.PermUtil;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.models.User;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileEditActivity extends BaseActivity {

    boolean IMAGE_STATUS = false;
    private EditText edtname;
    private String check,name,profile;
    CircleImageView image;
    ImageView upload;
    protected MediaSelector mediaSelector = new MediaSelector();
    private File new_image_file;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String user_id;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private ProgressDialog mLoginProgress;

    Toolbar toolbar;
    protected Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        // Set Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide Title
        TextView titleToolbar = findViewById(R.id.appname);
        titleToolbar.setVisibility(View.GONE);

        // Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            init_main();
        }
        else
        {
            init_main();
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
    private void init_main() {
        //Progress Dialog
        mLoginProgress = new ProgressDialog(this);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        upload=findViewById(R.id.uploadpic);
        image=findViewById(R.id.profilepic);
        edtname = findViewById(R.id.name);




        edtname.addTextChangedListener(nameWatcher);


        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String name = task.getResult().getString("name");
                String thumb_image = task.getResult().getString("thumb_image");
                edtname.setText(name);

                if(thumb_image != null)
                {
                    Glide.with(ProfileEditActivity.this)
                            .load(thumb_image) // image url
                            .apply(new RequestOptions()

                                    .override(512, 512) // resizing
                                    .centerCrop())
                            .into(image);  // imageview object
                }

                init();



            }
        });
    }
    private void logout()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String , Object > tokenMap = new HashMap<>();
        tokenMap.put("token_id" , FieldValue.delete());
        tokenMap.put("online" , timestamp.getTime());
        fStore.collection("users").document(user_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogoutHelper.signOut( ProfileEditActivity.this);
               // finish();
               // System.exit(0);
                startMainActivity();
            }
        });
    }
    private void startMainActivity() {

        try {
            Intent intent = new Intent(ProfileEditActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }catch (Exception e )
        {
            Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void init() {
        logoutButton = findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(view -> logout());
        upload.setOnClickListener(view -> mediaSelector.startChooseImageActivity(ProfileEditActivity.this, MediaSelector.CropType.Circle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                image.setImageURI(Uri.fromFile(getNew_image_file()));
            }
            catch (Exception e) {
                Toast.makeText(ProfileEditActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
        TextView button =findViewById(R.id.edit_info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO AFTER VALDATION
                if (validateName()){

                    name=edtname.getText().toString();


                    if (!TextUtils.isEmpty(name) && getNew_image_file() != null) {
                        if(validateProfile())
                        {
                            mLoginProgress.setTitle(getResources().getString(R.string.loading));
                            mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                            mLoginProgress.setCanceledOnTouchOutside(false);
                            mLoginProgress.show();

                            uploadFile_Photo(getNew_image_file() ,user_id , name );
                        }

                    }else if (!TextUtils.isEmpty(name)) {
                        mLoginProgress.setTitle(getResources().getString(R.string.loading));
                        mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();

                        uploadFile(user_id , name );
                    }
                    //Validation Success
                    //convertBitmapToString(profilePicture);

                }
            }
        });
    }
    public void uploadFile( String userId , String name ) {

        Map<String, Object> userMap_ = new HashMap<>();
        // ----------------------------------------

        userMap_.put("name", name);




        fStore.collection("users").document(userId).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mLoginProgress.dismiss();
                startActivity(new Intent(ProfileEditActivity.this, HomeActivity.class));
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mLoginProgress.dismiss();
                    }
                });
    }
    public void uploadFile_Photo(File file , String userId , String name ) {


        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);
        final String device_token = FirebaseInstanceId.getInstance().getToken();
        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(ProfileEditActivity.this)//Compressor Library
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
        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + userId + "/profile_pics/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + userId + "/profile_pics/").child(randomname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String download_thumb_uri = uri.toString();
                        // ----------------------------------------

                        Map<String, Object> userMap_ = new HashMap<>();
                        // ----------------------------------------

                        userMap_.put("name", name);
                        userMap_.put("thumb_image" ,download_thumb_uri);




                        fStore.collection("users").document(userId).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mLoginProgress.dismiss();
                                startActivity(new Intent(ProfileEditActivity.this, HomeActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mLoginProgress.dismiss();
                            }
                        });
                    }
                });

            }
        });
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
    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }


    private boolean validateProfile() {
        if (!IMAGE_STATUS)
            Toasty.info(ProfileEditActivity.this,"Select A Profile Picture", Toast.LENGTH_LONG).show();
        return IMAGE_STATUS;
    }

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






    private boolean validateName() {

        check = edtname.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }
}
