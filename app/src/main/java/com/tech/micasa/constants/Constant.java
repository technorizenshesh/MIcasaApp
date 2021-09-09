package com.tech.micasa.constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used as
 *
 * @author CanopusInfosystems
 * @version 1.0
 * @since 06 Jan 2020
 */
public class Constant {

    public static final String IS_USER_LOGGED_IN = "IsUserLoggedIn";
    public static final String SELECTED_LANGUAGE = "selectedLanguage";

    public static final String USER_ID = "userID";
    public static final String SELLER_ID = "sellerID";

    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;

    public interface UrlPath {
        //String BASEURL = "http://3.105.97.17/evfy/api/V1/";   // Declare Base URL.
                String BASEURL = "http://lezdash.com/micasa/webservice/";   // Declare Base URL.
        //        String PATH = "alivapp/";                  // Here is the path path of API.
        String SERVER_URL = BASEURL; //+ PATH;        // Here is API Calling Complete URL.

        /**
         * This are the End Point list of User Operation API.
         */
        String SIGN_UP_API = "signup";
        String LOGIN_API = "login";
        String FORGET_PASSWORD = "forgot_password";
        String CATEGORY_LIST ="category_list";
        String GET_SUB_CATEGORIES = "getSubcategories";
        String ADD_ITEM = "add_item";
        String GET_ITEM = "get_item";
        String GET_BANNERS = "get_banner";
        String GET_PROFILE = "get_profile";
        String GET_PRODUCT_BY_CATEGORY = "get_product_by_category";
        String GET_PRODUCT_DETAIL = "product_details";
        String GET_ALL_PRODUCT= "get_all_product";
        String UPDATE_PROFILE = "update_profile";
        String DELETE_ITEM = "delete_item";
        String UPDATE_ITEM = "update_item";
        String ADD_FAVORITE = "add_favorites";
        String CHAT_REQUEST = "chat_request";
        String GET_NOTIFICATION = "get_notification";
        String UPDATE_CHAT_STATUS = "update_chat_status";
        String INSERT_CHAT = "insert_chat";
        String GET_CHAT = "get_chat";
        String GET_CONVERSATION = "get_conversation";
        String GET_CHAT_REQUEST = "get_chat_request";
        String GET_AVAILABLE_CHAT_REQUEST = "get_available_chat_request";
        String ITEM_SEARCH = "item_search";
        String ADD_ITEM_OFFER = "add_item_offers";
        String PRIVACY_POLICY = "privacy_policy";
        String TERMS_AND_CONDITION = "terms_conditions";
        String USER_PRODUCT_BY_CATEGORY = "user_product_by_category";
        String SEARCH_ITEM_BY_CATEGORY = "search_item_by_category";
        String SOCIAL_LOGIN = "social_login";
        String CONTACT_US = "contact_info";
        String USER_ITEM_SEARCH = "user_item_search";
        String UPDATE_LANGUAGE = "change_language";
        String CHANGE_PASSWORD = "change_password";

    }

    /**
     * This Interface is contains all timeout constants.
     */


    public interface TimeOut {
        int IMAGE_UPLOAD_CONNECTION_TIMEOUT = 120;
        int IMAGE_UPLOAD_SOCKET_TIMEOUT = 120;
        int SOCKET_TIME_OUT = 60;
        int CONNECTION_TIME_OUT = 60;
    }

    /**
     * This Interface is contains all error response code.
     */
    public interface ErrorClass {
        String CODE = "code";
        String STATUS = "status";
        String MESSAGE = "message";
        String DEVELOPER_MESSAGE = "developerMessage";
    }



    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


    /**
     * This method is used to show Toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Activity context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Email Validation
     *
     * @param target
     * @return
     */
    public static boolean isValidEmail(CharSequence target) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(target);

        return matcher.matches();
    }

    /**
     * This method is used for hide keyboard.
     *
     * @param mContext
     */
    public final static void hideKeyBoard(Activity mContext) {
        // Check if no view has focus:
        View view = mContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isGPSAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static String getFormattedTime(String timeStr) {
        Date date = new Date();
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat opFormattor = new SimpleDateFormat("hh:mm aa");

        try {
            date = inputFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return opFormattor.format(date);
    }

    public static String getFormattedDate(String dateStr)
    {
        Date date = new Date();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat opFormattor = new SimpleDateFormat("dd MMM \n yyyy");

        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return opFormattor.format(date);
    }
}
