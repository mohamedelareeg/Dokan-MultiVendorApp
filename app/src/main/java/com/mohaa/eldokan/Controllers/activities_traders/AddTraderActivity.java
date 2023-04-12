package com.mohaa.eldokan.Controllers.activities_traders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.interfaces.OnRemoveClickListener;
import com.mohaa.eldokan.models.Traders;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.SelectedContactListAdapter;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.zelory.compressor.Compressor;

public class AddTraderActivity extends BaseActivity implements OnRemoveClickListener {
    private ImageView setup_img;
    private AutoCompleteTextView trader_type;
    private TextView add_to_product;
    private String user_id;
    private com.rengwuxian.materialedittext.MaterialEditText trader_name;
    private com.rengwuxian.materialedittext.MaterialEditText trader_desc;

    private com.rengwuxian.materialedittext.MaterialEditText trader_promo;
    private com.rengwuxian.materialedittext.MaterialEditText trader_discount;
    private com.rengwuxian.materialedittext.MaterialEditText trader_phone;
    private com.rengwuxian.materialedittext.MaterialEditText trader_location;
    protected MediaSelector mediaSelector = new MediaSelector();
    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    //Picture
    private Bitmap compressedImageFile;
    private File new_image_file;

    //ProgressDialog
    private ProgressDialog mLoginProgress;


    private List<String> selectedList;
    private CardView cvSelectedContacts;
    private RecyclerView rvSelectedList;
    private SelectedContactListAdapter selectedContactsListAdapter;
    Toolbar toolbar;
    private TextView Addcategories;
    private TextView addCata;
    private LinearLayout categories_panel;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trader);
        //Progress Dialog
        /*
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
                */

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
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        //set auto complete
        categories_panel = findViewById(R.id.categort_panel);
        trader_type = (AutoCompleteTextView) findViewById(R.id.trader_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Trader_type));
        trader_type.setAdapter(adapter);
        //set spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_ip);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trader_type.setText(spinner.getSelectedItem().toString());
                trader_type.dismissDropDown();
                if (trader_type.getText().toString().equals("Others"))
                {
                    categories_panel.setVisibility(View.VISIBLE);
                }
                else
                {
                    categories_panel.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                trader_type.setText(spinner.getSelectedItem().toString());
                trader_type.dismissDropDown();
                if (trader_type.getText().toString().equals("Others"))
                {
                    categories_panel.setVisibility(View.VISIBLE);
                }
                else
                {
                    categories_panel.setVisibility(View.GONE);
                }
            }
        });
        mLoginProgress = new ProgressDialog(this);
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();
        setup_img = findViewById(R.id.trader_logo);
        trader_name = findViewById(R.id.trader_name);
        trader_desc = findViewById(R.id.trader_desc);

        trader_promo = findViewById(R.id.trader_promo);
        trader_discount = findViewById(R.id.trader_discount);
        trader_phone = findViewById(R.id.trader_phone);
        trader_location = findViewById(R.id.trader_location);
        Addcategories = findViewById(R.id.trader_cata_add);
        addCata = findViewById(R.id.add_to_list);
        addCata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberToGroup(Addcategories.getText().toString() ,selectedList , selectedList.size() + 1 );
            }
        });



        if (trader_type.getText().toString().equals("Others"))
        {
            categories_panel.setVisibility(View.VISIBLE);
        }
        else
        {
            categories_panel.setVisibility(View.GONE);
        }
        ///
        selectedList = new ArrayList<>();
        cvSelectedContacts = findViewById(R.id.cardview_selected_contacts);
        rvSelectedList = findViewById(R.id.selected_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
        rvSelectedList.setLayoutManager(layoutManager);
        rvSelectedList.setItemAnimator(new DefaultItemAnimator());
        updateSelectedContactListAdapter(selectedList, 0);
        ///
        add_to_product = findViewById(R.id.add_to_product);
        add_to_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = trader_name.getText().toString();//name
                final String desc = trader_desc.getText().toString();

                final String promo = trader_promo.getText().toString();
                final String discount = trader_discount.getText().toString();
                final String phone = trader_phone.getText().toString();
                final String location = trader_location.getText().toString();

                if (trader_type.getText().toString().equals("Others"))
                {
                    if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(desc)  &&!TextUtils.isEmpty(location) && selectedList.size()!= 0  && getNew_image_file() != null) {
                        mLoginProgress.setTitle(getResources().getString(R.string.loading));
                        mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();

                        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        String username = task.getResult().getString("username");
                                        //String thumb_image = task.getResult().getString("thumb_image");
                                        uploadFile(getNew_image_file() , name ,desc , promo,discount , phone , location ,username);
                                    }


                                }
                            }
                        });

                    }
                }
                else   {
                    if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(desc)  &&!TextUtils.isEmpty(location)  && getNew_image_file() != null) {
                        mLoginProgress.setTitle(getResources().getString(R.string.loading));
                        mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();

                        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        String username = task.getResult().getString("username");
                                        //String thumb_image = task.getResult().getString("thumb_image");
                                        uploadFile(getNew_image_file() , name ,desc  , promo,discount , phone , location ,username);
                                    }


                                }
                            }
                        });

                    }
                }

            }
        });
        setup_img.setOnClickListener(view -> mediaSelector.startChooseImageActivity(this, MediaSelector.CropType.Rectangle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                setup_img.setImageURI(Uri.fromFile(getNew_image_file()));
            }
            catch (Exception e) {
                Toast.makeText(this, "Error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void uploadFile(File file , final String user_name  , final String desc  , final String promo, final String discount , final String phone , final String location , final String owner_name) {

        String type = trader_type.getText().toString();
        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(AddTraderActivity.this)//Compressor Library
                    .setMaxWidth(256)
                    .setMaxHeight(256)
                    .setQuality(2)

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
        Map<String, String> tradersMap = convertListToMap(selectedList);

        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/traders_pics/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/traders_pics/").child(randomname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String randomname = String.valueOf(generateRandom(12));
                        String download_thumb_uri = uri.toString();
                        Traders traders = new Traders();
                        traders.setId(randomname);
                        traders.setName(user_name);
                        traders.setDesc(desc);

                        traders.setPromo(promo);
                        traders.setDiscount(discount);
                        traders.setType(trader_type.getText().toString());
                        traders.setThumb_image(download_thumb_uri);
                        traders.setPhone(phone);
                        traders.setLocation(location);
                        traders.setOwner_id(user_id);
                        traders.setOwnder_name(owner_name);
                        if (trader_type.getText().toString().equals("Others")){
                            traders.setTraders(tradersMap);
                        }
                        traders.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        /*
                        HashMap<String, Object> userMap_ = new HashMap<>();
                        userMap_.put("name", user_name);
                        userMap_.put("cost", "");
                        userMap_.put("price", price);
                        userMap_.put("type", "");
                        userMap_.put("trader", "");
                        userMap_.put("validity_start", "");
                        userMap_.put("validity_end", "");
                        userMap_.put("thumb_image", download_thumb_uri);
                        */
                        fStore.collection("traders").document(randomname).set(traders).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //fStore.collection(type).document(documentReference.getId()).set(traders);
                                    mLoginProgress.dismiss();
                                    Toast.makeText(AddTraderActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                                    Intent mIntent = new Intent(AddTraderActivity.this, HomeActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                } else {
                                    mLoginProgress.hide();
                                    String e = task.getException().getMessage();
                                    Toast.makeText(AddTraderActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                                }
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
        }
        catch (Exception e) {
            Toast.makeText(this, "Error" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }
    private Map<String, String> convertListToMap(List<String> contacts) {
        Map<String, String> members = new HashMap<>();
        for (String contact : contacts) {
            // the value "1" is a default value with no usage
            String randomname = String.valueOf(generateRandom(12));
            members.put(randomname, contact);

        }


        return members;
    }

    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }

    private void updateSelectedContactListAdapter(List<String> list, int position) {

        if (selectedContactsListAdapter == null) {
            selectedContactsListAdapter = new SelectedContactListAdapter(this, list);
            selectedContactsListAdapter.setOnRemoveClickListener(this);
            rvSelectedList.setAdapter(selectedContactsListAdapter);
        } else {
            selectedContactsListAdapter.setList(list);
            selectedContactsListAdapter.notifyDataSetChanged();
        }

        if (selectedContactsListAdapter.getItemCount() > 0) {
            cvSelectedContacts.setVisibility(View.VISIBLE);

        } else {
            cvSelectedContacts.setVisibility(View.GONE);

        }

        rvSelectedList.smoothScrollToPosition(position);
    }
    // convert the list of contact to a map of members



    private void addMemberToGroup(String contact, List<String> contactList, int position) {
        // add a contact only if it not exists
        if (!isContactAlreadyAdded(contact, contactList)) {
            // add the contact to the contact list and update the adapter
            contactList.add(contact);
        }
        //Toast.makeText(this, " " + contactList.size() + " " + contact, Toast.LENGTH_SHORT).show();
//        contactsListAdapter.addToAlreadyAddedList(contact, position);

        updateSelectedContactListAdapter(contactList, position);
    }
    // check if a contact is already added to a list
    private boolean isContactAlreadyAdded(String toCheck, List<String> mlist) {
        boolean exists = false;
        for (String contact : mlist) {
            String contactId = contact;

            if (contactId.equals(toCheck)) {
                exists = true;
                break;
            }
        }
        return exists;
    }
    @Override
    public void onRemoveClickListener(int position) {
        String contact = selectedList.get(position);
        // remove the contact only if it exists
        if (isContactAlreadyAdded(contact, selectedList)) {
            // remove the item at position from the contacts list and update the adapter
            selectedList.remove(position);

//            contactsListAdapter.removeFromAlreadyAddedList(contact);

            updateSelectedContactListAdapter(selectedList, position);
        } else {
            Snackbar.make(findViewById(R.id.coordinator),
                    getString(R.string.add_members_activity_catagories),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

}
