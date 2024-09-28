package com.example.safmn_2.cppWrapper;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class SAFMNncnn {
    //加载第1个模型
    public native boolean Init(AssetManager mgr);

    //加载第2个模型
    public native boolean Init2(AssetManager mgr);

    public native Bitmap Detect(Bitmap bitmap, Bitmap bitmap2);



    static {
        System.loadLibrary("ncnn_safmn");
    }
}
