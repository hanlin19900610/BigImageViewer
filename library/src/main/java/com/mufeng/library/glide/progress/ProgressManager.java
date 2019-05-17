package com.mufeng.library.glide.progress;

import android.text.TextUtils;

import com.mufeng.library.glide.SSLSocketClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author by sunfusheng on 2017/6/14.
 */
public class ProgressManager {

    private static Map<String, OnProgressListener> listenersMap =
        Collections.synchronizedMap(new HashMap<>());

    private static final ProgressResponseBody.InternalProgressListener LISTENER =
            (url, bytesRead, totalBytes) -> {
                OnProgressListener onProgressListener = getProgressListener(url);
                if (onProgressListener != null) {
                    int percentage = (int) ((bytesRead * 1f / totalBytes) * 100f);
                    boolean isComplete = percentage >= 100;
                    onProgressListener.onProgress(url, isComplete, percentage, bytesRead, totalBytes);
                    if (isComplete) {
                        removeListener(url);
                    }
                }
            };

    private ProgressManager() {

    }

    public static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response.newBuilder()
                .body(new ProgressResponseBody(request.url().toString(), LISTENER, response.body()))
                .build();
        })
            .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
            .hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        return builder.build();
    }

    public static void addListener(String url, OnProgressListener listener) {
        if (!TextUtils.isEmpty(url) && listener != null) {
            listenersMap.put(url, listener);
            listener.onProgress(url, false, 1, 0, 0);
        }
    }

    public static void removeListener(String url) {
        if (!TextUtils.isEmpty(url)) {
            listenersMap.remove(url);
        }
    }

    public static void removeAll(){
        listenersMap.clear();
    }

    public static OnProgressListener getProgressListener(String url) {
        if (TextUtils.isEmpty(url) || listenersMap == null || listenersMap.size() == 0) {
            return null;
        }

        OnProgressListener listenerWeakReference = listenersMap.get(url);
        if (listenerWeakReference != null) {
            return listenerWeakReference;
        }
        return null;
    }
}