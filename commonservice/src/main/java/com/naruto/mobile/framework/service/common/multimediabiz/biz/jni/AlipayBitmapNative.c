#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <stdio.h>
#include <stdlib.h>

#ifndef _Included_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_NativeMemoryCache
#define _Included_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_NativeMemoryCache

#define JLONG_TO_PTR(j) ((void*) (intptr_t) (j))
#define PTR_TO_JLONG(p) ((jlong) (intptr_t) (p))

//#define DEBUG

#define LOG_TAG "libAlipayBitmapNative"
#ifdef DEBUG
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#else
#define LOGI(...)
#endif
#ifdef DEBUG
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define LOGE(...)
#endif

static void safe_throw_exception(JNIEnv* env, const char* msg) {
  jclass runtime_exception_class = (*env)->FindClass(env,
      "java/lang/RuntimeException");

  if (!runtime_exception_class) {
    return;
  }
  if (!(*env)->ExceptionCheck(env)) {
    (*env)->ThrowNew(env, runtime_exception_class, msg);
  }
}

/**
 * Safely unlocks bitmap's pixels even if java exception is pending
 */
static void unlock_pixels_safe(JNIEnv* env, jobject bitmap) {
  // "catch" pending exception, if any
  jthrowable stashed_exception = (*env)->ExceptionOccurred(env);
  if (stashed_exception) {
    (*env)->ExceptionClear(env);
  }

  // no pending exceptions, it is safe now to call unlockPixels
  AndroidBitmap_unlockPixels(env, bitmap);

  // rethrow exception
  if (stashed_exception) {
    // we don't expect unlockPixels to throw java exceptions, but since it takes
    // JNIEnv as a parameter we must be prepared to handle such scenario.
    // There is no way of chaining exceptions in java, so lets just print
    // unexpected exception, swallow it and rethrow the stashed one.
    if ((*env)->ExceptionCheck(env)) {
      (*env)->ExceptionDescribe(env);
      (*env)->ExceptionClear(env);
    }
    (*env)->Throw(env, stashed_exception);
  }
}

JNIEXPORT jlong JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_BitmapNativeCache_setBitmapData(JNIEnv * env, jobject obj, jobject bitmap)
{
    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0)
    {
        LOGE("AndroidBitmap_getInfo() failed, error=%d", ret);
        safe_throw_exception(env, "setBitmapData AndroidBitmap_getInfo fail");
        return 0;
    }

    ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);
    if (ret != ANDROID_BITMAP_RESULT_SUCCESS || !pixels)
    {
        LOGE("AndroidBitmap_lockPixels failed, error=%d", ret);
        safe_throw_exception(env, "setBitmapData AndroidBitmap_lockPixels fail");
        return 0;
    }

    int length = info.stride * info.height;
    void *data = malloc(length * sizeof(int));
    if (!data) {
        LOGE("malloc failed");
        unlock_pixels_safe(env, bitmap);
        safe_throw_exception(env, "could not allocate memory");
        return 0;
    }
    memcpy(data, pixels, length);

    unlock_pixels_safe(env, bitmap);
    return PTR_TO_JLONG(data);
}


JNIEXPORT void JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_BitmapNativeCache_getBitmapData(JNIEnv * env, jobject obj, jlong pointer, jobject bitmap)
{
    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if (pointer == 0) {
        LOGE("getBitmapData pointer is null !!!!!! ");
        safe_throw_exception(env, "getBitmapData pointer is null fail");
        return;
    }

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0)
    {
        LOGE("AndroidBitmap_getInfo() failed, error=%d", ret);
        safe_throw_exception(env, "getBitmapData AndroidBitmap_getInfo fail");
        return;
    }

    ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);
    if (ret != ANDROID_BITMAP_RESULT_SUCCESS || !pixels)
    {
        LOGE("AndroidBitmap_lockPixels failed, error=%d", ret);
        safe_throw_exception(env, "getBitmapData AndroidBitmap_lockPixels fail");
        return;
    }

    int length = info.stride * info.height;
    memcpy(pixels, JLONG_TO_PTR(pointer), length);

    unlock_pixels_safe(env, bitmap);
}

JNIEXPORT void JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_BitmapNativeCache_free(JNIEnv * env, jobject obj, jlong pointer)
{
    free(JLONG_TO_PTR(pointer));
}

JNIEXPORT jint JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_BitmapNativeCache_getMemTotal(JNIEnv * env, jobject obj)
{
    FILE *meminfo = fopen("/proc/meminfo", "r");

    if (meminfo == NULL)
    {
        return -1;
    }

    char line[256];
    while(fgets(line, sizeof(line), meminfo))
    {
        int ram;
        if (sscanf(line, "MemTotal: %d kB", &ram) == 1)
        {
            fclose(meminfo);
            return ram;
        }
    }

    fclose(meminfo);
    return -1;
}

JNIEXPORT jint JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_cache_fast_BitmapNativeCache_getMemFree(JNIEnv * env, jobject obj)
{
    FILE *meminfo = fopen("/proc/meminfo", "r");

    if (meminfo == NULL)
    {
        return -1;
    }

    char line[256];
    while(fgets(line, sizeof(line), meminfo))
    {
        int ram;
        if (sscanf(line, "MemFree: %d kB", &ram) == 1)
        {
            fclose(meminfo);
            return ram;
        }
    }

    fclose(meminfo);
    return -1;
}

#endif
