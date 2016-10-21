#!/bin/bash
UPLOAD_RUNTIME=false
doUpload() {
    if [ ${UPLOAD_RUNTIME} = "true" ];
    then uploadRuntime
    else uploadExceptRuntime
    fi
}
upload() {
    rm settings.gradle
    cp settings.gradle.before settings.gradle
    ./gradlew clean assemble
    ./gradlew :core:uploadArchives
    ./gradlew :views:uploadArchives
    ./gradlew :blockcanary:uploadArchives
    ./gradlew :components:uploadArchives
    cleanGradleCache
    rm settings.gradle
    cp settings.gradle.ready settings.gradle
    ./gradlew clean
    ./gradlew :runtime:uploadArchives

    ./gradlew :noop-java:uploadArchives
    ./gradlew :noop-kotlin:uploadArchives
}
uploadExceptRuntime() {
    rm settings.gradle
    cp settings.gradle.before settings.gradle
    ./gradlew clean assemble
    ./gradlew uploadArchives
}
uploadRuntime() {
    rm settings.gradle
    cp settings.gradle.ready settings.gradle
    ./gradlew :runtime:clean
    ./gradlew :runtime:uploadArchives
}
uploadPrimary() {
    git checkout 1.0.1 -f
    doUpload
}
upload101_23() {
    git checkout v23/1.0.1 -f
    doUpload
}
upload101_22() {
    git checkout v22/1.0.1 -f
    doUpload
}
cleanGradleCache() {
    rm -rf ~/.gradle/caches/modules-2/files-2.1/com.exyui.android
}
uploadPrimary
upload101_22
upload101_23
git checkout 1.0.1
rm settings.gradle
cp settings.gradle.before settings.gradle