package com.jedparsons.mrmorse.audio

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_MEDIA
import android.media.AudioFormat
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan
import kotlin.math.sin

interface KeyOscillator {
  fun start()
  fun stop()
}

class RealKeyOscillator(
  private val coroutineScope: CoroutineScope,
  private val computationDispatcher: CoroutineDispatcher
) : KeyOscillator {

  private var isPlaying: Boolean = false
  private val sampleRateHz: Int = 44100
  private val bufferSizeBytes = AudioTrack.getMinBufferSize(
    sampleRateHz, CHANNEL_OUT_MONO, ENCODING_PCM_16BIT
  )
  private val track = AudioTrack.Builder()
    .setAudioAttributes(
      AudioAttributes.Builder()
        .setUsage(USAGE_MEDIA)
        .setContentType(CONTENT_TYPE_SONIFICATION)
        .build()
    )
    .setAudioFormat(
      AudioFormat.Builder()
        .setEncoding(ENCODING_PCM_16BIT)
        .setSampleRate(sampleRateHz)
        .setChannelMask(CHANNEL_OUT_MONO)
        .build()
    )
    .setBufferSizeInBytes(bufferSizeBytes)
    .build()
  // track.release() ?

  private val amplitude: Int = 32767
  private val frequency: Int = 700
  private val twopi: Double = 8.0 * atan(1.0)
  private val phaseIncrement = twopi * frequency / sampleRateHz

  private suspend fun playback() {
    withContext(computationDispatcher) {
      // simple sine wave generator
      val frame_out: ShortArray = ShortArray(bufferSizeBytes)
      var phase: Double = 0.0

      while (isPlaying) {
        for (i in 0 until bufferSizeBytes) {
          frame_out[i] = (amplitude * sin(phase)).toInt().toShort()
          phase += phaseIncrement
          if (phase > twopi) {
            phase -= twopi
          }
        }
        track.write(frame_out, 0, bufferSizeBytes)
      }
    }
  }

  override fun start() {
    track.play()
    isPlaying = true
    coroutineScope.launch {
      playback()
    }
  }

  override fun stop() {
    if (isPlaying) {
      isPlaying = false
      // Stop playing the audio data and release the resources
      track.stop()
    }
  }
}
