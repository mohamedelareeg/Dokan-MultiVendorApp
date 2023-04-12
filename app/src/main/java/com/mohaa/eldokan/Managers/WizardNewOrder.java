package com.mohaa.eldokan.Managers;

import android.content.Context;

import com.mohaa.eldokan.models.SellProducts;


import java.io.Serializable;

public class WizardNewOrder implements Serializable {
    //    private static final String PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP =
//            "PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP";


    private SellProducts tempOrder = new SellProducts();

    // singleton
    // source : https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0
    private static volatile WizardNewOrder instance = new WizardNewOrder();
    private Context mContext;
    //private constructor.
    private WizardNewOrder() {

        //set the default mContext value equals to ChatManager.getInstance().getContext() Use ChatUI.getIntance().setContext to use another context
        //mContext = UserManager.getInstance().getContext();

        // Prevent form the reflection api.
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static WizardNewOrder getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            synchronized (WizardNewOrder.class) {
                if (instance == null) instance = new WizardNewOrder();
            }
        }

        return instance;
    }

//    // Make singleton from serialize and deserialize operation.
//    protected WizardNewGroup readResolve() {
//        return getInstance();
//    }
    // end singleton

    public SellProducts getTempOrder() {
        return tempOrder;
    }

    public void dispose() {
        clearTempUser();
    }

    private void clearTempUser() {
        tempOrder.setId("");
        tempOrder.setName("");
        tempOrder.setQuantity(0);
        tempOrder.setCost(0);
        tempOrder.setPrice(0);
        tempOrder.setSrc("");
        tempOrder.setTrader("");
        tempOrder.setValidity_start("");
        tempOrder.setValidity_end("");
        tempOrder.setType("");
        tempOrder.setThumb_image("");

    }

}
