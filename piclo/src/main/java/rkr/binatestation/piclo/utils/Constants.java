package rkr.binatestation.piclo.utils;

/**
 * Created by RKR on 02-04-2016.
 * Constants.
 */
public final class Constants {

    /**
     * The Constant that are used in volley for socket timeout
     */
    public static final int socketTimeout = 30000;//30 seconds - change to what you want


    /**
     * Content loader id
     */
    public static final int CONTENT_LOADER_CATEGORIES = 1;
    public static final int CONTENT_LOADER_PICS = 2;

    /**
     * The Constants that are used in server APIs
     */
    public static final String CATEGORIES = "categories";
    public static final String GALLERY = "gallery";
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String GALLERY_UPLOAD = "gallery/upload";
    public static final String PROFILE = "profile";
    public static final String UPDATE = "update";
    public static final String CHANGE_PASSWORD = "changepassword";
    public static final String MOBILE_REGISTER = "mobile/register";
    /**
     ***********************************************************************************************
     */

    /**
     * The Constants that are used in SharedPreferences
     */
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_FULL_NAME = "user_full_name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_CATEGORY_LAST_UPDATED_DATE = "category_last_updated_date";

    /**
     ***********************************************************************************************
     */

    /**
     * The Constants used as JSON KEY
     */
    public static final String KEY_JSON_STATUS = "status";
    public static final String KEY_JSON_MESSAGE = "message";
    public static final String KEY_JSON_DATA = "data";
    public static final String KEY_JSON_CATEGORY_ID = "categoryId";
    public static final String KEY_JSON_CATEGORY_NAME = "categoryName";
    public static final String KEY_JSON_TITLE = "title";
    public static final String KEY_JSON_FILE = "file";
    public static final String KEY_JSON_IMAGE_ID = "imageId";
    public static final String KEY_JSON_USER_ID = "userId";
    public static final String KEY_JSON_COURTESY = "courtesy";
    public static final String KEY_JSON_FULL_NAME = "fullName";

    /**
     ***********************************************************************************************
     */

    /**
     * The Constants used as Bundle KEY
     */
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_PARENT_ACTIVITY = "parent_activity";
    public static final String KEY_PICS_LIMIT = "pics_limit";
}
