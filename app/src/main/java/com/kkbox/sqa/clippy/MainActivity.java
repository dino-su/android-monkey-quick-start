package com.kkbox.sqa.clippy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import junit.framework.Assert;

public class MainActivity extends AppCompatActivity {

    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaRecorder mMediaRecorder;
    private int mHeight;
    private int mWidth;
    private int mScreenDensity;
    private ToggleButton mToggleButton;
    private VirtualDisplay mVirtualDisplay;

    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final String TAG = "ScreenRecorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initRecorder();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRecorder();

                    enableWifi();
                } else {
                    stopRecorder();
                }
            }
        });
    }

    private void enableWifi() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "### Permission refused ###");
                Toast.makeText(this, "User cancelled!", Toast.LENGTH_SHORT).show();
                mToggleButton.setChecked(false);

                return;
            }

            Log.i(TAG, "### Permission granted ###");
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);

            setUpVirtualDisplay();

            Log.i(TAG, "### Staring recorder ###");
            mMediaRecorder.start();
        }
    }

    private void initRecorder() throws Exception {
        Log.i(TAG, "### Initializing recorder ###");
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mHeight = metrics.heightPixels;
        mWidth  = metrics.widthPixels;
        mScreenDensity = metrics.densityDpi;
        mMediaRecorder = new MediaRecorder();
        mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        setupRecorder();
    }

    private void setUpVirtualDisplay() {
        Log.i(TAG, "### Setup virtual display ###");
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mWidth, mHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null, null);
    }

    private void setupRecorder() throws Exception {
        Log.i(TAG, "### Setup Audio/Video Source ###");
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        Log.i(TAG, "### Setup output file format ###");
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/monkey.mp4");

        Log.i(TAG, "### Setup recorder options ###");
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoSize(mWidth, mHeight);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);

        mMediaRecorder.prepare();
    }

    private void startRecorder() {
        Log.i(TAG, "### Requesting for permission to record screen ###");
        startActivityForResult(
                mMediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    private void stopRecorder() {
        if (mVirtualDisplay == null) {
            return;
        }

        Log.i(TAG, " ### Stopping recorder ###");
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;

        Log.i(TAG, " ### Stopping virtual display ###");
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

}
