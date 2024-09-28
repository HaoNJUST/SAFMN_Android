#include <iostream>
#include <vector>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <filesystem> // C++17中的文件系统库
#include <opencv2/core/types_c.h>
#include "net.h" // NCNN头文件
#include "opencv2/opencv.hpp" // OpenCV头文件



#define TAG "JNI_TAG"


static  ncnn::Net model;

static  ncnn::Net model2;

static int modelNum = 1;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_safmn_12_cppWrapper_SAFMNncnn_Init(JNIEnv *env, jobject thiz, jobject assetManager) {

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    {
        int ret = model.load_param(mgr,"test.ncnn.param");
        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "SafmnNcnn", "load_param_bin failed");
            return JNI_FALSE;
        }
    }

    {
        int ret = model.load_model(mgr,"test.ncnn.bin");


        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "SafmnNcnn", "load_param_bin failed");
            return JNI_FALSE;
        }
    }

    modelNum = 1;
    return JNI_TRUE;
}



extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_safmn_12_cppWrapper_SAFMNncnn_Init2(JNIEnv *env, jobject thiz, jobject assetManager) {
    // TODO: implement Init2()
    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    {
        int ret = model2.load_param(mgr,"SAFMN_L_Real_LSDIR_x4_v2.pth.ncnn.param");
        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "SafmnNcnn", "load_param_bin failed");
            return JNI_FALSE;
        }
    }

    {
        int ret = model2.load_model(mgr,"SAFMN_L_Real_LSDIR_x4_v2.pth.ncnn.bin");


        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "SafmnNcnn", "load_param_bin failed");
            return JNI_FALSE;
        }
    }
    modelNum = 2;
    return JNI_TRUE;

}




extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_safmn_12_cppWrapper_SAFMNncnn_Detect(JNIEnv *env, jobject thiz, jobject bitmap, jobject bitmap2) {

    ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bitmap, ncnn::Mat::PIXEL_RGB);

//归一化
    // 获取 Bitmap 的信息
    AndroidBitmapInfo info;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "bitmap_size", "Failed to get bitmap info");
        return nullptr;
    }

    int width = info.width;
    int height = info.height;

    // 打印 Bitmap 的宽度和高度
    __android_log_print(ANDROID_LOG_ERROR, "bitmap_size", "Bitmap width: %d, height: %d", width, height);

    float n0[3]{1 / 255., 1 / 255., 1 / 255.};
    float n1[3]{255., 255., 255.};
    float m[3]{0., 0., 0.};
    in.substract_mean_normalize(m, n0);

    if(modelNum == 1) {
        auto ex = model.create_extractor();
        ex.input(model.input_names()[0], in);
        ncnn::Mat out_mat;
        ex.extract(model.output_names()[0], out_mat);
        out_mat.substract_mean_normalize(m, n1);

        // ncnn to bitmap
        out_mat.to_android_bitmap(env, bitmap2, ncnn::Mat::PIXEL_RGB);
    } else {
        auto ex = model2.create_extractor();
        ex.input(model2.input_names()[0], in);
        ncnn::Mat out_mat;
        ex.extract(model2.output_names()[0], out_mat);
        out_mat.substract_mean_normalize(m, n1);

        // ncnn to bitmap
        out_mat.to_android_bitmap(env, bitmap2, ncnn::Mat::PIXEL_RGB);
    }


    //打印bitmap2的大小
    if (AndroidBitmap_getInfo(env, bitmap2, &info) < 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "bitmap2_size", "Failed to get bitmap info");
        return nullptr;
    }

    return bitmap2;
}

