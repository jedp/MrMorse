#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <android/log.h>

#include <player/OneShotSampleSource.h>
#include <player/SimpleMultiPlayer.h>
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>

static const char *TAG = "MrMorseJNI";

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

using namespace iolib;
using namespace parselib;

static SimpleMultiPlayer player;

JNIEXPORT void JNICALL Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_setupAudioStreamNative(
        JNIEnv *env, jobject thiz, jint numChannels) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "init()");

    // we know in this case that the sample buffers are all 1-channel, 41K
    player.setupAudioStream(numChannels);
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_startAudioStreamNative(
        JNIEnv *env, jobject thiz) {
    player.startStream();
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_teardownAudioStreamNative(JNIEnv *, jobject) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "deinit()");

    // Sample buffers are all 1-channel, 44.1k.
    player.teardownAudioStream();
}

JNIEXPORT jboolean JNICALL Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_loadWavAssetNative(
        JNIEnv *env,
        jobject,
        jbyteArray bytearray,
        jint channels) {
    float pan = 0.0f; // left:-1.0f < center:0.0f > right:1.0f
    int len = env->GetArrayLength(bytearray);
    auto *buf = new unsigned char[len];
    env->GetByteArrayRegion(bytearray, 0, len, reinterpret_cast<jbyte *>(buf));

    MemInputStream stream(buf, len);

    WavStreamReader reader(&stream);
    reader.parse();

    jboolean isFormatValid = reader.getNumChannels() == channels;

    auto *sampleBuffer = new SampleBuffer();
    sampleBuffer->loadSampleData(&reader);

    auto *source = new OneShotSampleSource(sampleBuffer, pan);
    player.addSampleSource(source, sampleBuffer);

    delete[] buf;

    return isFormatValid;
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_unloadWavAssetsNative(JNIEnv *env, jobject) {
    player.unloadSampleData();
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_startTone(JNIEnv *env, jobject) {
    player.triggerDown(0);
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_stopTone(JNIEnv *env, jobject) {
    player.triggerUp(0);
}

JNIEXPORT jboolean JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_getOutputReset(JNIEnv *, jobject) {
    return player.getOutputReset();
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_clearOutputReset(JNIEnv *, jobject) {
    player.clearOutputReset();
}

JNIEXPORT void JNICALL
Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_restartStream(JNIEnv *, jobject) {
    player.resetAll();
    if (player.openStream() && player.startStream()) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream successful");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed");
    }
}

JNIEXPORT void JNICALL Java_com_jedparsons_mrmorse_audio_RealSamplePlayer_setAudioGain(
        JNIEnv *env, jobject thiz, jfloat gain) {
    player.setGain(0, gain);
}

#ifdef __cplusplus
}
#endif
