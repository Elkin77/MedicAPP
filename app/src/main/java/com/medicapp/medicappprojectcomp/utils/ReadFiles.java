package com.medicapp.medicappprojectcomp.utils;

import android.content.Context;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadFiles {

    private static OkHttpClient httpClient;

    public static String readJson(Context context, String fileName) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));
        String content= "";
        String line;
        while((line=reader.readLine()) != null){
            content = content + line;
        }
        return content;
    }
    public static void fetchSvg(Context context, String url, final ImageView target) {

        if (httpClient == null) {
            // Use cache for performance and basic offline capability
            httpClient = new OkHttpClient.Builder()
                    .cache(new Cache(context.getCacheDir(), 5 * 1024 * 1014))
                    .build();
        }

        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // target.setImageDrawable(R.drawable.fallback_image);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream stream = response.body().byteStream();
                //  Sharp.loadInputStream(stream).into(target);
                stream.close();
            }
        });
    }
}
