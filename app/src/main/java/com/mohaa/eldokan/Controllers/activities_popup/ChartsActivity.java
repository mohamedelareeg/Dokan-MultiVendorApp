package com.mohaa.eldokan.Controllers.activities_popup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.models.GraphicItem;
import com.mohaa.eldokan.models.LineChartItem;
import com.mohaa.eldokan.models.Orders;
import com.mohaa.eldokan.models.PieChartItem;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.GraphicAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartsActivity extends BaseActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;

    private ArrayList<Orders> orders_list;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

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


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        orders_list = new ArrayList<>();

        getData();
        ImageButton refresh = (ImageButton) findViewById(R.id.refresh_stats_button);
        Log.i("Dashboard", "onCreateView TOP");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCharts();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        updateCharts();
    }


    public void updateCharts(){
        ListView lvMain = (ListView) findViewById(R.id.listViewStats);

        final GraphicItem[] items = new GraphicItem[6];

        float[][] cairo_values = {{0,0}, {1,0}, {2, 0}, {3,0}, {4,0}, {5,0},{6,0}};
        float[][] qal_values = {{0,0}, {1,0}, {2, 0}, {3,0}, {4,0}, {5,0},{6,0}};
        float[][] giza_values = {{0,0}, {1,0}, {2, 0}, {3,0}, {4,0}, {5,0},{6,0}};
        int cairo = 0, qal = 0, giza = 0, total;
        int count_cairo = 0 , count_giza = 0 , count_qal = 0;
        for (int x = 0 ; x < orders_list.size() ; x++)
        {
            if(orders_list.get(x).getGoverment().equals("ألقاهرة"))
            {
                cairo_values[5][1]++;
                count_cairo++;
                cairo+=orders_list.get(x).getTotal_cost();

            }
            else if(orders_list.get(x).getGoverment().equals("القليوبية"))
            {
                qal_values[5][1]++;
                count_qal++;
                qal+=orders_list.get(x).getTotal_cost();

            }
            else if(orders_list.get(x).getGoverment().equals("الجيزة"))
            {
                giza_values[5][1]++;
                count_giza++;
                giza+=orders_list.get(x).getTotal_cost();
            }
        }


        total = cairo + qal + giza;
        double cairo_chart = ((double)cairo * 100/(double)total);
        double qal_chart = ((double)qal * 100/(double)total);
        double giza_chart = ((double)giza * 100/(double)total);
        ArrayList<PieChartItem> pie_items = new ArrayList<>();
        ArrayList<PieChartItem> pie_items2 = new ArrayList<>();
        ArrayList<LineChartItem> line_items = new ArrayList<>();

        pie_items.add(new PieChartItem(cairo_chart, getResources().getColor(R.color.MD_LightGreen_300), getResources().getColor(R.color.MD_LightGreen), getString(R.string.cairo)));
        if (count_cairo > 0){
            line_items.add(new LineChartItem(cairo_values, getResources().getColor(R.color.MD_LightGreen), getString(R.string.cairo)));
        }


        pie_items.add(new PieChartItem(qal_chart, getResources().getColor(R.color.MD_LightBlue_300), getResources().getColor(R.color.MD_LightBlue), getString(R.string.qalyubia)));
        if (count_qal > 0){
            line_items.add(new LineChartItem(qal_values, getResources().getColor(R.color.MD_LightBlue), getString(R.string.qalyubia)));
        }

        pie_items.add(new PieChartItem(giza_chart, getResources().getColor(R.color.MD_Amber_300), getResources().getColor(R.color.MD_Amber), "" + getString(R.string.giza)));
        if (count_giza > 0){
            line_items.add(new LineChartItem(giza_values, getResources().getColor(R.color.MD_Amber), getString(R.string.giza)));
        }
        int pending = 0, approved = 0, deliverd = 0, total_orders;

        for (int x = 0 ; x < orders_list.size() ; x++)
        {
            if(orders_list.get(x).getState().equals("pending"))
            {

                pending++;

            }
            else if(orders_list.get(x).getState().equals("confirmed"))
            {
                approved++;

            }
            else if(orders_list.get(x).getState().equals("delivered"))
            {
                deliverd++;
            }
        }

        total_orders = pending + approved + deliverd;
        double pending_chart = ((double)pending * 100/(double)total_orders);
        double approved_chart = ((double) approved * 100/(double)total_orders);
        double deliverd_chart = ((double)deliverd * 100/(double)total_orders);

        if(pending != 0) {
            pie_items2.add(new PieChartItem(pending_chart, getResources().getColor(R.color.MD_LightGreen_300), getResources().getColor(R.color.MD_LightGreen), getString(R.string.pending)));
        }
        if (approved != 0) {
            pie_items2.add(new PieChartItem(approved_chart, getResources().getColor(R.color.MD_Red_300), getResources().getColor(R.color.MD_Red), getString(R.string.approved)));
        }
        if (deliverd != 0) {
            pie_items2.add(new PieChartItem(deliverd_chart, getResources().getColor(R.color.MD_Orange_300), getResources().getColor(R.color.MD_Orange), getString(R.string.deliverd)));
        }


        items[0] = new GraphicItem(getString(R.string.total_sales_chart), GraphicAdapter.TYPE_SEPARATOR);
        items[1] = new GraphicItem(pie_items, total, GraphicAdapter.TYPE_PIE);

        items[2] = new GraphicItem(getString(R.string.total_orders_chart), GraphicAdapter.TYPE_SEPARATOR);
        items[3] = new GraphicItem(line_items, GraphicAdapter.TYPE_LINE);

        items[4] = new GraphicItem(getString(R.string.orders_case_chart), GraphicAdapter.TYPE_SEPARATOR);
        items[5] = new GraphicItem(pie_items2, total_orders, GraphicAdapter.TYPE_PIE);

        GraphicAdapter customAdapter = new GraphicAdapter(ChartsActivity.this, R.id.text, items);
        lvMain.setAdapter(customAdapter);
    }

    public void getAllOrders()
    {

        orders_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            Query f_query = fStore.collection("orders").whereEqualTo("state" , "delivered");
            f_query.addSnapshotListener(this ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {


                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            final String TraderID = doc.getDocument().getId();
                            /*
                            double price =  Double.parseDouble(doc.getDocument().getString("price"));
                            String name =  doc.getDocument().getString("name");
                            String thumb_image =  doc.getDocument().getString("thumb_image");
                            products_list.add(new SellProducts(name, 0 , thumb_image));
                            */
                            //thumb_image
                            Orders blogPost = doc.getDocument().toObject(Orders.class).withid(TraderID);
                            orders_list.add(blogPost);


                        }
                    }


                }
            });



        }

    }

    public void getData(){
        try {
            //swipeRefreshLayout.setRefreshing(true);
            getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
