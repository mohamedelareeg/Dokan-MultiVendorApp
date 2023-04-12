package com.mohaa.eldokan.Managers;

import com.mohaa.eldokan.models.SellProducts;


import java.util.ArrayList;
import java.util.List;

public class OrdersBase {

    private List<SellProducts> mOrders;
    private static volatile OrdersBase instance = new OrdersBase();
    private OrdersBase(){
        mOrders = new ArrayList<>();
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }
    private OrdersBase(SellProducts products)
    {
        mOrders.add(products);
    }

    public List<SellProducts> getmOrders() {
        return mOrders;
    }

    public boolean InsertOrder(SellProducts products) {

        if (mOrders.size() > 0) {
            if (this.mOrders.get(Integer.parseInt(products.getId()) - 1).getName().equals(products.getName())) {
                return false;
            } else {

                this.mOrders.add(Integer.parseInt(products.getId().toString()), products);
                return true;
            }
        }
        else
        {

            this.mOrders.add(Integer.parseInt(products.getId().toString()), products);
            return true;
        }

    }

    public boolean RemoveOrder(SellProducts products) {

        if (mOrders.size() > 0) {


                //1-2-4-5 ID >> index array >>5
                 int index = mOrders.indexOf(products);
                 mOrders.remove(products);
                 /*
                for (int i = 0; i < mOrders.size() ; i++)
                {
                  if(mOrders.contains(products))
                  {
                      this.mOrders.remove(i);

                      break;
                  }

                }

                  */
                return true;

        }
        else
        {



            return true;
        }

    }

    public static OrdersBase getInstance(){
        if(instance == null)
        {
            instance = new OrdersBase();
        }
        return instance;
    }

    public void dispose() {
        clearTempUser();
    }

    private void clearTempUser() {
        mOrders.clear();

    }

}
