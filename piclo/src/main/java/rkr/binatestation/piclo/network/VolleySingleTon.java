package rkr.binatestation.piclo.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import rkr.binatestation.piclo.utils.Constants;


/**
 * Created by RKR on 12-11-2015.
 * VolleySingleTon.
 */
public class VolleySingleTon {
    /**
     * assignment of domain urls
     * the variable isLocal define if the project work in local domain or in live server domains
     */
    private static final String domainUrl = "http://yourdevelopmentupdate.co.uk/bookaroo/androidapi/";
    private static final String domainUrlPayment = "http://yourdevelopmentupdate.co.uk/bookaroo/paymentapi/";
    private static final String domainUrlForImage = "http://yourdevelopmentupdate.co.uk/bookaroo/frontend/web/uploads/";
    private static final String domainUrlForShareLink = "http://yourdevelopmentupdate.co.uk";

    private static final String localDomainUrl = "http://192.168.2.200/bookaroo/androidapi/";
    private static final String localDomainUrlPayment = "http://192.168.2.200/bookaroo/paymentapi/";
    private static final String localDomainUrlForImage = "http://192.168.2.200/bookaroo/frontend/web/uploads/";
    private static final String localDomainUrlShareLink = "http://192.168.2.200";

    private static Boolean isLocal = false;
    /**
     * static variable for maintain single Volley queue
     */
    private static VolleySingleTon mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleTon(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * synchronised method to ensure only single instance
     */
    public static synchronized VolleySingleTon getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleTon(context);
        }
        return mInstance;
    }

    /**
     * static method used to get the domain url for the images
     */

    public static String getDomainUrlForImage() {
        if (isLocal) {
            return localDomainUrlForImage;
        } else {
            return domainUrlForImage;
        }
    }

    /**
     * static method used to get the domain url for APIs
     */
    public static String getDomainUrl() {
        if (isLocal) {
            return localDomainUrl;
        } else {
            return domainUrl;
        }
    }

    /**
     * static method used to get the domain url for APIs
     */
    public static String getDomainUrlPayment() {
        if (isLocal) {
            return localDomainUrlPayment;
        } else {
            return domainUrlPayment;
        }
    }

    /**
     * static method used to get the domain url for the share page
     */

    public static String getDomainUrlForShareLink() {
        if (isLocal) {
            return localDomainUrlShareLink;
        } else {
            return domainUrlForShareLink;
        }
    }

    /**
     * static method used to get the single request queue
     */

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * static method used to add request to the request queue
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(Constants.socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    /**
     * static method used to get the image loader
     */

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
