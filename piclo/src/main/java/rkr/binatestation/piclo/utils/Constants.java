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
     * The Constants that are used in server APIs
     */
    public static final String GOOGLE_PLACE_SEARCH_PREFIX = "https://maps.googleapis.com/maps/api/place/autocomplete/json?components=country:uk&input=";
    public static final String GOOGLE_PLACE_SEARCH_POSTFIX = "&key=AIzaSyCUny5m0EdXze8EzSKUyj_2dAzaUL8z0gs";
    public static final String GOOGLE_PLACE_DETAIL_PREFIX = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    public static final String GOOGLE_PLACE_DETAIL_POSTFIX = "&key=AIzaSyCUny5m0EdXze8EzSKUyj_2dAzaUL8z0gs";
    public static final String PAY_PAL_URL_PREFIX = "https://www.sandbox.paypal.com/webapps/adaptivepayment/flow/pay?paykey=";
    public static final String PAY_PAL_URL_POSTFIX = "&expType=mini";
    public static final String ADD_BLOCK = "addblock";
    public static final String BUSINESS_CATEGORY = "businesscategory";
    public static final String DEAL = "deal";
    public static final String ADD_DEAL = "adddeal";
    public static final String UPDATE_DEALS = "updatedeals";
    public static final String SERVICE = "service";
    public static final String EMPLOYEES = "employees";
    public static final String OPEN_FOR_BIDING = "openforbiding";
    public static final String OPEN_FOR_BIDING_UPDATE = "openforbiding_update";
    public static final String FEELING_DETAILS = "feelingdetails";
    public static final String SERVICES_LIST = "serviceslist";
    public static final String LOCATIONS = "locations";
    public static final String DURATIONS = "durations";
    public static final String PROMOTION = "promotion";
    public static final String ADD_PROMOTION = "addpromotion";
    public static final String PROMOTION_UPDATE = "promotionupdate";
    public static final String CATEGORY = "category";
    public static final String EMPLOYEES_LIST = "employeeslist";
    public static final String SERVICE_OFFER = "serviceoffer";
    public static final String IMAGE_UPLOAD = "imageupload";
    public static final String ADD_SERVICE = "addservice";
    public static final String SELECT_BID = "selectbid";
    public static final String VIEW_BIDS = "viewbids";
    public static final String BOOKING_IN_SEARCH = "booking_in_search";
    public static final String SPECIFIC_TIME = "specific_time";
    public static final String EMPLOYEE_AVAILABLE = "employee_avilable";
    public static final String BOOK_NOW = "booknow";
    public static final String APPOINTMENT = "appoinment";
    public static final String USER_APPOINTMENTS = "userappoinments";
    public static final String CHANGE_PASSWORD = "changepassword";
    public static final String CONTACT = "contact";
    public static final String ADD_APPOINTMENT = "addappoinment";
    public static final String SAVE_APPOINTMENT = "saveappoinment";
    public static final String DASHBOARD = "dashboard";
    public static final String ACTION = "action";
    public static final String LIST_DEALS = "listdeals";
    public static final String LIST_PROMOTION = "listpromotion";
    public static final String REMOVE_DEAL = "removedeal";
    public static final String REMOVE_PROMOTION = "removepromotion";
    public static final String BLOCK_PROMOTION = "blockpromotion";
    public static final String UNBLOCK_PROMOTION = "unblockpromotion";
    public static final String DIARY_LISTING = "diarylisting";
    public static final String OPENING_TIMES = "openingtimes";
    public static final String OPENING_UPDATE = "openingupdate";
    public static final String FAQ = "faq";
    public static final String FAVOURITE_LIST = "favouritelist";
    public static final String FEEDBACK = "feedback";
    public static final String USER_HISTORY = "userhistory";
    public static final String HOW = "how";
    public static final String LOGIN = "login";
    public static final String SOCIAL_SIGN_UP = "socialsignup";
    public static final String CLIENTS = "clients";
    public static final String CLIENTS_SEARCH = "clientssearch";
    public static final String PRIVACY = "privacy";
    public static final String SIGN_UP_DETAILS = "signupdetails";
    public static final String BUSINESS_UPDATE = "businessupdate";
    public static final String USER_DETAILS = "userdetails";
    public static final String BUSINESS = "business";
    public static final String FEELING_LUCKY = "feeling_lucky";
    public static final String FEELING_SEARCH = "feelingsearch";
    public static final String DEL_REQUESTS = "del_requests";
    public static final String ADD_BID = "addbid";
    public static final String PROFILE_REVIEWS = "profilereviews";
    public static final String ADD_REVIEW = "addreview";
    public static final String USER_REVIEWS = "userreviews";
    public static final String SIGN_UP = "signup";
    public static final String BUSINESS_DAILY_REPORT = "businessdailyreport";
    public static final String BUSINESS_MONTHLY_REPORT = "businessmonthlyreport";
    public static final String MOST_POPULAR = "mostpopular";
    public static final String SEARCH_SERVICES = "searchservices";
    public static final String ADD_FAVOURITE = "addfavourite";
    public static final String SERVICES = "services";
    public static final String IMAGE_DELETE = "imagedelete";
    public static final String SERVICE_OFFERED = "serviceoffered";
    public static final String CATEGORY_SAVE = "categorysave";
    public static final String LOGIN_CHECK = "logincheck";
    public static final String TERMS = "terms";
    public static final String CLIENT = "client";
    public static final String CHECKOUT = "checkout";
    public static final String PROMO_VALIDATION = "promo_validation";
    public static final String SERVICE_CANCEL = "servicecancel";
    public static final String REFUND = "refund";
    public static final String BOOKING_RESCHEDULE = "booking_reschedule";
    public static final String RESCHEDULE = "reschedule";
    public static final String SERVICE_EMPLOYEE = "serviceemployee";
    public static final String REMOVE_PRICE = "removeprice";
    public static final String APPOINTMENT_SYNC = "appoinmentsync";
    public static final String APPOINTMENT_SYNC_SAVE = "appoinmentsyncsave";
    /**
     ***********************************************************************************************
     */

    /**
     * The Constants that are used in SharedPreferences
     */
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_IMAGE = "user_image";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_FIRST_NAME = "user_first_name";
    public static final String KEY_USER_LAST_NAME = "user_last_name";
    public static final String KEY_IS_SEARCH_FILTER_APPLICABLE = "is_search_filter_applicable";
    public static final String KEY_SEARCH_DATE = "search_date";
    public static final String KEY_SEARCH_SERVICE = "search_service";
    public static final String KEY_SEARCH_LOCATION_NAME = "search_location_name";
    public static final String KEY_SEARCH_LOCATION_LATITUDE = "search_location_latitude";
    public static final String KEY_SEARCH_LOCATION_LONGITUDE = "search_location_longitude";
    public static final String KEY_SEARCH_TIME = "search_time";
    public static final String KEY_SERVICE_LIST_IS_GRID = "service_list_is_grid";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_CLIENT_SEARCH_USERNAME = "client_search_username";
    public static final String KEY_CLIENT_SEARCH_SERVICE = "client_search_service";
    public static final String KEY_CLIENT_SEARCH_LOCATION_LATITUDE = "client_search_location_latitude";
    public static final String KEY_CLIENT_SEARCH_LOCATION_LONGITUDE = "client_search_location_longitude";
    public static final String KEY_CLIENT_SEARCH_LOCATION_NAME = "client_search_location_name";
    public static final String KEY_CLIENT_SEARCH_RADIUS = "client_search_radius";
    public static final String KEY_CLIENT_SEARCH_AGE = "client_search_age";
    public static final String KEY_CLIENT_SEARCH_DATE = "client_search_date";
    public static final String KEY_IS_CLIENT_SEARCH_FILTER_APPLICABLE = "is_client_search_filter_applicable";

    /**
     ***********************************************************************************************
     */

}
