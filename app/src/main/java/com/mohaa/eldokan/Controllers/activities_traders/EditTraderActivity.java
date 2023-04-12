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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.Utils.ProductsUI;
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

public class EditTraderActivity extends BaseActivity implements OnRemoveClickListener {
    private ImageView setup_img;

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
    private String catagerios_id;
    private String catagerios_Name;
    //ProgressDialog
    private ProgressDialog mLoginProgress;

    Toolbar toolbar;
    private List<String> selectedList;
    private CardView cvSelectedContacts;
    private RecyclerView rvSelectedList;
    private SelectedContactListAdapter selectedContactsListAdapter;
    private Traders traders_list;

    private String blog_post_id;
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
        setContentView(R.layout.activity_edit_trader);
        //Progress Dialog
        /*
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
                */
        //set auto complete

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
        traders_list = (Traders) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_TRADERS_LIST);
        blog_post_id = getIntent().getStringExtra(ProductsUI.BUNDLE_TRADERS_LIST_ID);

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

        Query f_query = fStore.collection("traders").whereEqualTo("name" , traders_list.getName());
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String groupID = doc.getDocument().getId();
                        setTraders_list(doc.getDocument().toObject(Traders.class).withid(groupID));
                        initCata(getTraders_list() , groupID);

                    }
                }
            }
        });



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
                if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(desc)  &&!TextUtils.isEmpty(location) && getNew_image_file() != null) {
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
                else if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(desc)  &&!TextUtils.isEmpty(location) ) {
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
                                    uploadFile( name ,desc  , promo,discount , phone , location ,username);
                                }


                            }
                        }
                    });

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

    private void initCata(Traders traders_list, String groupID) {
        //removeMemberFromChatGroup(traders_list ,"هدوم");
        /*
        for (int i = 0 ; i < traders_list.getMembersList().size() ; i++) {
            catagerios_Name = traders_list.getMembersList().get(i).getName();
            catagerios_id = traders_list.getMembersList().get(i).getUser_id();
            addMemberToGroup(catagerios_Name ,selectedList , selectedList.size() + 1 );
        }

         */
        //addMemberToGroup(Addcategories.getText().toString() ,selectedList , selectedList.size() + 1 );
        initLoad();
    }

    private void initLoad() {


        trader_name.setText(traders_list.getName());
        trader_desc.setText(traders_list.getDesc());

        trader_promo.setText(traders_list.getPromo());
        trader_discount.setText(traders_list.getDiscount());
        trader_phone.setText(traders_list.getPhone());
        trader_location.setText(traders_list.getLocation());

        Glide.with(this)
                .load(traders_list.getThumb_image()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_photo) // any placeholder to load at start
                        .error(R.drawable.ic_photo)  // any image in case of error
                        .override(512, 512) // resizing
                        .centerCrop())
                .into(setup_img);  // imageview object
    }

    public void uploadFile(File file , final String user_name  , final String desc , final String promo,final String discount , final String phone , final String location , final String owner_name) {


        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(EditTraderActivity.this)//Compressor Library
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


                        String download_thumb_uri = uri.toString();
                        HashMap<String, Object> userMap_ = new HashMap<>();
                        userMap_.put("name", user_name);
                        userMap_.put("desc", desc);

                        userMap_.put("promo", promo);
                        userMap_.put("discount", discount);
                        userMap_.put("phone", phone);
                        userMap_.put("location", location);
                        userMap_.put("thumb_image", download_thumb_uri);
                        //userMap_.put("traders", tradersMap);

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
                        fStore.collection("traders").document(blog_post_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                mLoginProgress.dismiss();
                                Toast.makeText(EditTraderActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                                Intent mIntent = new Intent(EditTraderActivity.this, HomeActivity.class);
                                startActivity(mIntent);
                                finish();
                            } else {
                                mLoginProgress.hide();
                                String e = task.getException().getMessage();
                                Toast.makeText(EditTraderActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                            }
                            }
                        });
                    }
                });

            }
        });

    }

    public void uploadFile( final String user_name  , final String desc  , final String promo,final String discount , final String phone , final String location , final String owner_name) {



        HashMap<String, Object> userMap_ = new HashMap<>();
        userMap_.put("name", user_name);
        userMap_.put("desc", desc);

        userMap_.put("promo", promo);
        userMap_.put("discount", discount);
        userMap_.put("phone", phone);
        userMap_.put("location", location);

        fStore.collection("traders").document(blog_post_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mLoginProgress.dismiss();

                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(EditTraderActivity.this, HomeActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    mLoginProgress.hide();
                    String e = task.getException().getMessage();
                    Toast.makeText(EditTraderActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                }
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
            for(Map.Entry entry: traders_list.getTraders().entrySet()) {
                if (contact.equals(entry.getValue())) {
                    String old_ID = String.valueOf(entry.getKey());
                    members.put(old_ID, contact);
                }
                else if (!contact.equals(entry.getValue()))
                {
                    String randomname = String.valueOf(generateRandom(12));
                    members.put(randomname, contact);
                }

            }


            // the value "1" is a default value with no usage


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
        //removeMemberFromChatGroup(traders_list,contact);
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

    public void removeMemberFromChatGroup(Traders traders , String cata_name) {

        mLoginProgress.setTitle("Updating Categories");
        mLoginProgress.setMessage("Please wait while we check your credentials.");
        mLoginProgress.setCanceledOnTouchOutside(false);
        mLoginProgress.show();

        String toRemove_user_id;
        for(Map.Entry entry: traders_list.getTraders().entrySet()){
            if(cata_name.equals(entry.getValue())){
                toRemove_user_id = String.valueOf(entry.getKey());
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                if (traders.getTraders().containsKey(toRemove_user_id)) {
                    // remove from firebase app reference
                    //appGroupsNode.child("/" + groupId + "/members/" + toRemove.getGroupId()).removeValue();
                    //toRemove_user_id
                    // remove member from local group
                    traders.getTraders().remove(toRemove_user_id);
                    //chatGroup.getMembersList().remove(toRemove_user_id);
                    HashMap<String , Object> remove_member_update = new HashMap<>();
                    remove_member_update.put("traders" , traders.getTraders());
                    fStore.collection("traders").document(blog_post_id).update(remove_member_update);


                }
                break; //breaking because its one to one map
            }

        }
        mLoginProgress.dismiss();

    }

    public void addMembersToChatGroup(Traders chatGroup , Map<String, String> toAdd) {

        mLoginProgress.setTitle("Updating Categories");
        mLoginProgress.setMessage("Please wait while we check your credentials.");
        mLoginProgress.setCanceledOnTouchOutside(false);
        mLoginProgress.show();
        Map<String, String> chatGroupMembers = chatGroup.getTraders();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        // add the news member to the existing members
        // the map automatically override existing keys
        chatGroupMembers.putAll(toAdd);
        //chatGroupMembers.put(currentUserId, current_user_name);
        HashMap<String , Object> add_member_update = new HashMap<>();
        add_member_update.put("traders" , chatGroup.getTraders());
        fStore.collection("traders").document(blog_post_id).update(add_member_update);

        mLoginProgress.dismiss();

    }

    public Traders getTraders_list() {
        return traders_list;
    }

    public void setTraders_list(Traders traders_list) {
        this.traders_list = traders_list;
    }
}
