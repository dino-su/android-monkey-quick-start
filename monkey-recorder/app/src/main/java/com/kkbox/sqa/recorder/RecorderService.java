package com.kkbox.sqa.recorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class RecorderService extends Service {
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;
    private static final String TAG = "ScreenRecorder";
    public static boolean RECORDING;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting RecorderService");
        mResultCode = intent.getIntExtra("resultCode", -1);
        mResultData = intent.getParcelableExtra("data");
        mScreenWidth = intent.getIntExtra("width", 1920);
        mScreenHeight = intent.getIntExtra("height", 1080);
        mScreenDensity = intent.getIntExtra("density", 1);

        mMediaProjection =  createMediaProjection();
        mMediaRecorder = createMediaRecorder();
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
        RECORDING = true;

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping RecorderService");
        RECORDING = false;

        if (mVirtualDisplay == null) {
            return;
        }

        mMediaRecorder.stop();
        mMediaRecorder.reset();

        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private MediaProjection createMediaProjection() {
        return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(mResultCode, mResultData);
    }

    private MediaRecorder createMediaRecorder() {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/recorder.mp4");
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(mScreenWidth * mScreenHeight);
        mediaRecorder.setVideoFrameRate(30);

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return mediaRecorder;
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }
}
