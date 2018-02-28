package com.kkbox.sqa.recorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int mHeight;
    private int mWidth;
    private int mScreenDensity;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final String TAG = "ScreenRecorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDeviceInfo();

        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "必須同意才能啟動錄影", Toast.LENGTH_SHORT).show();

                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                initDisplayMetrics();

                Intent recorder = new Intent(this, RecorderService.class);
                recorder.putExtra("resultCode", resultCode);
                recorder.putExtra("data", data);
                recorder.putExtra("width", mWidth);
                recorder.putExtra("height", mHeight);
                recorder.putExtra("density", mScreenDensity);

                Log.i(TAG, "Sending startService()");
                startService(recorder);
            }
        }
    }

    private void initDeviceInfo() {
        String[] key = { "Manufacturer", "Brand","Model","Device", "SDK Release", "SDK Version" };
        String[] value = { Build.MANUFACTURER, Build.BRAND, Build.MODEL, Build.DEVICE, Build.VERSION.RELEASE, String.valueOf(Build.VERSION.SDK_INT) };

        ListView list = (ListView) findViewById(R.id.list);
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();

        for (int i = 0; i < key.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("key", key[i]);
            item.put("value", value[i]);
            items.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                items, R.layout.list_item, new String[]{"key", "value"},
                new int[]{R.id.key, R.id.value});
        list.setAdapter(adapter);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String command = (intent.getDataString() == null) ? "" : intent.getDataString();

        if(Intent.ACTION_RUN.equals(action) && command.equals("START")) {
            startRecorderService();
        }

        if(Intent.ACTION_RUN.equals(action) && command.equals("STOP")) {
            stopRecorderService();
            finish(); // Activity is done and should be closed.
        }
    }

    private void startRecorderService() {
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        startActivityForResult(
                mMediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    private void stopRecorderService() {
        Log.i(TAG, "Sending stopService()");
        Intent service = new Intent(this, RecorderService.class);
        stopService(service);
    }

    private void initDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mHeight = metrics.heightPixels;
        mWidth  = metrics.widthPixels;
        mScreenDensity = metrics.densityDpi;
    }
}
