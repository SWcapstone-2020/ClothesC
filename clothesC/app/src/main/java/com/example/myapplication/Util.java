package com.example.myapplication;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

import java.net.URLConnection;

public class Util {

    public Util(Activity activity){
        /**/
    }

    public static void showToast(Activity activity,String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
    public static final String INTENT_PATH = "path";
    public static final String INTENT_MEDIA = "media";

    public static final int GALLERY_IMAGE = 0;
    public static final int GALLERY_VIDEO = 1;


    public static boolean isStorageUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/posts");
    }

    public static boolean isProfile(String url, String id){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/profileImage/"+id);
    }
    public static boolean isClothes(String url, String kind){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/"+kind);
    }

    public static boolean isItemUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/outer");
    }


    public static boolean isShirtUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/shirt");
    }

    public static boolean isPantUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/pant");
    }

    public static boolean isEtcUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/etc");
    }

    public static boolean isShoesUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/clothesc-ver1.appspot.com/o/shoes");
    }
    public static String storageUrlToName(String url){
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length - 1];
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }



}
