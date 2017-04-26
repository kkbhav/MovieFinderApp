package com.example.bhavesh.moviefinder.util;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MainApplication extends Application
{
    Context context;

    private static RequestQueue queue;

    private static Gson gson;

    @Override
    public void onCreate() {

        super.onCreate();

        queue = Volley.newRequestQueue(getApplicationContext());
        context = getApplicationContext();

        /**
         * UIL initialization
         */
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory().imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading()
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

    }

    public static RequestQueue getRequestQueue()
    {

        return queue;
    }

}
