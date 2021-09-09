package com.tech.micasa.utility;

import com.tech.micasa.retrofit.models.SuccessResGetNotifications;

/**
 * Created by Ravindra Birla on 15,March,2021
 */
public interface RequestStatusInterface {

    public void myCallback(String statusId,String status,String type,String price);

}
