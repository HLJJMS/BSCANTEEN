package com.example.canteen;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Log.e("数据", message);//okHttp的详细日志会打印出来
    }
}
