#!/usr/bin/env bash

## Init
mkdir -p output/

## Install Helper
./gradlew installDebug installDebugAndroidTest

##  START Recording
adb shell am start -n com.kkbox.sqa.monkey/.MainActivity -a android.intent.action.RUN -d START

## Login App
adb shell am instrument -w -e class com.kkbox.sqa.monkey.MonkeyTest#start com.kkbox.sqa.monkey.test/android.support.test.runner.AndroidJUnitRunner

## Run Monkey
adb shell monkey -p com.skysoft.kkbox.android -v 2000 > output/monkey.log
sleep 8

## STOP Recording
adb shell am start -n com.kkbox.sqa.monkey/.MainActivity -a android.intent.action.RUN -d STOP

## Screenshot
adb shell screencap /sdcard/monkey.png

## Acquiring Logs
adb bugreport > output/bugreport.log
adb pull /sdcard/monkey.png output/
adb pull /sdcard/monkey.mp4 output/
