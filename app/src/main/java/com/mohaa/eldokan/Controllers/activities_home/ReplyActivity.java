package com.mohaa.eldokan.Controllers.activities_home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.models.Reply;
import com.mohaa.eldokan.models.Traders;
import com.mohaa.eldokan.models.User;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.ReplyRecyclerAdapter;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Mohamed El Sayed
 */
public class ReplyActivity extends BaseActivity implements TextWatcher , View.OnClickListener{
    //=========== RecycleView =============
    private ReplyRecyclerAdapter replyRecyclerAdapter;
    private List<Reply> replyList;
    private List<User> user_list;
    private List<Traders> products_list;
    //=========== FireBase =============
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    //=========== Others =============
    private String blog_post_id;
    private String comment_post_id;
    //=========== VIews =============
    private EditText commentEditText;
    @Nullable
    private ScrollView scrollView;
    private Button sendButton;
    private ProgressBar commentsProgressBar;
    private RecyclerView commentsRecyclerView;
    private TextView warningCommentsTextView;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        //=========== FireBase =============
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        //=========== ExtraIntent =============
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        comment_post_id = getIntent().getStringExtra("comment_id");
        type = getIntent().getStringExtra("type");
        initViews();
        setViews();
        sharedPreferences();
        updateRecycleView();
        loadReplys();
        sendButton.setOnClickListener(this);
        commentEditText.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private void loadReplys() {
        //mUserDatabase.keepSynced(true);
        Query f_query = firebaseFirestore.collection("traders/" + blog_post_id + "/Comments/"+comment_post_id+"/Reply").orderBy("timestamp", Query.Direction.ASCENDING);

        f_query.addSnapshotListener(ReplyActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            final String replyid = doc.getDocument().getId();
                            Reply comments = doc.getDocument().toObject(Reply.class).withid(replyid);
                            replyList.add(comments);
                            String blogUserID = doc.getDocument().getString("user_id");
                            firebaseFirestore.collection("users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            String username = task.getResult().getString("username");
                                            String thumb_image = task.getResult().getString("thumb_image");
                                            HashMap<String, Object> userMap_ = new HashMap<>();
                                            userMap_.put("username", username);
                                            userMap_.put("thumb_image", thumb_image);
                                            firebaseFirestore.collection("traders").document(blog_post_id)
                                                    .collection("Comments").document(comment_post_id).collection("Reply").document(replyid).update(userMap_);
                                        }


                                    }
                                }
                            });
                            replyRecyclerAdapter.notifyDataSetChanged();


                        }
                    }

                }

            }
        });
    }

    private void updateRecycleView() {
        products_list = new ArrayList<>();
        user_list = new ArrayList<>();
        //RecyclerView Firebase List
        replyList = new ArrayList<>();
        replyRecyclerAdapter = new ReplyRecyclerAdapter(replyList , user_list , products_list );
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(replyRecyclerAdapter);
    }

    private void sharedPreferences() {
        SharedPreferences prefs_c = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor_c = prefs_c.edit();
        editor_c.putString("comment_post_id", comment_post_id); //InputString: from the EditText
        editor_c.commit();
        SharedPreferences prefs_ = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor_ = prefs_.edit();
        editor_.putString("blog_post_id", blog_post_id); //InputString: from the EditText
        editor_.commit();
    }

    private void setViews() {

    }

    private void initViews() {
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        scrollView = findViewById(R.id.scrollView);
        commentEditText = findViewById(R.id.commentEditText);
        commentsProgressBar = findViewById(R.id.commentsProgressBar);
        warningCommentsTextView = findViewById(R.id.warningCommentsTextView);
        sendButton = findViewById(R.id.sendButton);
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        sendButton.setEnabled(charSequence.toString().trim().length() > 0);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sendButton:
            {
                sendComment();
                break;
            }
            default:
                break;
        }
    }

    private void sendComment() {
        String comment_message = commentEditText.getText().toString();

        if(!TextUtils.isEmpty(comment_message)) {
            commentEditText.setText("");
            firebaseFirestore.collection("users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            final String username = task.getResult().getString("username");
                            final String thumb_image = task.getResult().getString("thumb_image");

                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> commentsMap_ = new HashMap<>();
                            commentsMap_.put("message", comment_message);
                            commentsMap_.put("user_id", current_user_id);
                            commentsMap_.put("timestamp", timestamp.getTime());
                            commentsMap_.put("username", username);
                            commentsMap_.put("thumb_image", thumb_image);
                            firebaseFirestore.collection("traders/" + blog_post_id + "/Comments/" + comment_post_id + "/Reply").add(commentsMap_).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
