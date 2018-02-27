package com.kkbox.sqa.monkey;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MonkeyTest {
    private UiDevice device =  UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    private static final String APP_PACKAGE = "com.skysoft.kkbox.android";
    private static final int TIMEOUT = 10000;

    @Test
    public void start() {
        // start now
//        launchRecorder();
//        device.wait(Until.hasObject(By.pkg("com.kkbox.sqa.monkey").depth(0)), TIMEOUT);
        device.wait(Until.findObject(By.text("Start now")), TIMEOUT).click();

        // login
        lunachApp();
        this.device.wait(Until.findObject(By.res("com.skysoft.kkbox.android:id/button_login")), TIMEOUT).click();
        this.device.wait(Until.findObject(By.res("com.skysoft.kkbox.android:id/button_login_with_email")), TIMEOUT).click();
        this.device.wait(Until.findObject(By.res("com.skysoft.kkbox.android:id/text_uid")), TIMEOUT).setText("demo171018@gmail.com");
        this.device.wait(Until.findObject(By.res("com.skysoft.kkbox.android:id/text_password")), TIMEOUT).setText("1234");
        this.device.wait(Until.findObject(By.res("com.skysoft.kkbox.android:id/button_login")), TIMEOUT).click();
        this.device.wait(Until.hasObject(By.res("com.skysoft.kkbox.android:id/toolbar")), TIMEOUT);
    }

    private void launchRecorder() {
        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        this.device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), TIMEOUT);

        // Launch the blueprint app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage("com.kkbox.sqa.monkey");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        intent.setAction(Intent.ACTION_RUN);
        intent.setData(Uri.parse("START"));
        context.startActivity(intent);

        // Wait for the app to appear
        this.device.wait(Until.hasObject(By.pkg("com.kkbox.sqa.monkey").depth(0)), TIMEOUT);
    }

    private void lunachApp() {
        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        this.device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), TIMEOUT);

        // Launch the blueprint app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        this.device.wait(Until.hasObject(By.pkg(APP_PACKAGE).depth(0)), TIMEOUT);
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

}