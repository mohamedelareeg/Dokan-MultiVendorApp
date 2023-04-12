package com.mohaa.eldokan.Managers;

import android.content.Context;

import com.mohaa.eldokan.models.Traders;


import java.io.Serializable;

public class WizardNewTrader implements Serializable {
    //    private static final String PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP =
//            "PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP";


    private Traders tempTraders = new Traders();

    // singleton
    // source : https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0
    private static volatile WizardNewTrader instance = new WizardNewTrader();
    private Context mContext;
    //private constructor.
    private WizardNewTrader() {

        //set the default mContext value equals to ChatManager.getInstance().getContext() Use ChatUI.getIntance().setContext to use another context
        //mContext = UserManager.getInstance().getContext();

        // Prevent form the reflection api.
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static WizardNewTrader getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            synchronized (WizardNewTrader.class) {
                if (instance == null) instance = new WizardNewTrader();
            }
        }

        return instance;
    }

//    // Make singleton from serialize and deserialize operation.
//    protected WizardNewGroup readResolve() {
//        return getInstance();
//    }
    // end singleton

    public Traders getTempTraders() {
        return tempTraders;
    }

    public void dispose() {
        clearTempUser();
    }





    private void clearTempUser() {
        tempTraders.setId("");
        tempTraders.setName("");
        tempTraders.setDesc("");
        tempTraders.setSpeed("");
        tempTraders.setPrice(0);
        tempTraders.setPromo("");
        tempTraders.setDiscount("");
        tempTraders.setType("");
        tempTraders.setThumb_image("");


    }

}
