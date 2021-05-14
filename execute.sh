#!/bin/bash
adb install -t in/app.apk
adb install -t in/test.apk

./gradlew jar
java -jar build/libs/instrumental-test-launcher-1.0.jar -a org.example.android.instrumentation.sampletest -p org.example.android.instrumentation.sampletest.test -i androidx.test.runner.AndroidJUnitRunner -t in/test.apk