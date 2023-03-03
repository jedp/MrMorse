package com.jedparsons.mrmorse.audio

import android.content.res.AssetManager
import android.util.Log
import androidx.annotation.FloatRange
import com.jedparsons.mrmorse.ui.KeyActivity
import java.io.IOException

interface SamplePlayer {

  /** Prepare the sample player. Do this first. */
  fun setUp(assetManager: AssetManager): Boolean

  /** Clean up after using the sample player. Do this last. */
  fun tearDown()

  /** Start playing the sample tone. */
  fun pressKey()

  /** Stop playing the sample tone. */
  fun releaseKey()

  /** Set the gain of the sample player. Legal values: 0.0 to 2.0. */
  fun setGain(@FloatRange(from = 0.0, to = 2.0) gain: Float)
}

class RealSamplePlayer : SamplePlayer {

  override fun setUp(assetManager: AssetManager): Boolean {
    Log.i(KeyActivity.TAG, "Preparing audio stream ...")
    setupAudioStreamNative(NUM_PLAY_CHANNELS)
    if (!loadWavAssets(assetManager)) {
      teardownAudioStreamNative()
      return false
    }
    startAudioStreamNative()
    Log.i(KeyActivity.TAG, "Prepared audio stream")
    return true
  }

  override fun tearDown() {
    teardownAudioStreamNative()
    unloadWavAssetsNative()
    Log.i(KeyActivity.TAG, "Cleaned up audio stream")
  }

  override fun pressKey() = startTone()

  override fun releaseKey() = stopTone()

  override fun setGain(@FloatRange(from = 0.0, to = 2.0) gain: Float) = setAudioGain(gain)

  private fun loadWavAssets(assetMgr: AssetManager): Boolean {
    var returnVal = false
    try {
      val assetFD = assetMgr.openFd(WAV_ASSET)
      val dataStream = assetFD.createInputStream()
      val dataLen = assetFD.length.toInt()
      val dataBytes = ByteArray(dataLen)
      dataStream.read(dataBytes, 0, dataLen)
      returnVal = loadWavAssetNative(dataBytes, NUM_SAMPLE_CHANNELS)
      assetFD.close()
    } catch (ex: IOException) {
      Log.i(TAG, "IOException: $ex")
    }

    Log.i(TAG, "Loaded $WAV_ASSET")
    return returnVal
  }

  private external fun setupAudioStreamNative(numChannels: Int)
  private external fun startAudioStreamNative()
  private external fun teardownAudioStreamNative()
  private external fun loadWavAssetNative(wavBytes: ByteArray, channels: Int): Boolean
  private external fun unloadWavAssetsNative()
  private external fun startTone()
  private external fun stopTone()
  private external fun setAudioGain(gain: Float)
  private external fun getOutputReset(): Boolean
  private external fun clearOutputReset()
  private external fun restartStream()

  companion object {
    const val TAG = "AudioPlayer"
    const val WAV_ASSET = "sin_700.wav"

    // Sample attributes
    const val NUM_PLAY_CHANNELS: Int = 2  // The number of channels in the player Stream.

    // Stereo Playback, set to 1 for Mono playback
    // This IS NOT the channel format of the source samples
    // (which must be mono).
    const val NUM_SAMPLE_CHANNELS: Int = 1   // All WAV resource must be mon
  }
}
