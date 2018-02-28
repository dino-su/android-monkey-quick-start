package com.kkbox.sqa.monkey;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
public class CalculatorTest {
    private UiDevice device =  UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    private static final String APP_PACKAGE = "com.android.calculator2";
    private static final int TIMEOUT = 5000;

    @Test
    public void start() {
        // grant recording permission
        device.wait(Until.findObject(By.res("android", "button1")), TIMEOUT).click();

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