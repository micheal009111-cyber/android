Pusher Alert Android project

Pusher cluster: ap2
Channel: chat-channel
Event: new-text

The project includes a Gradle launcher so Codemagic can build it even if the Gradle wrapper files were not generated locally.

Codemagic workflow: codemagic.yaml
Build command: ./gradlew assembleDebug
Artifact: app/build/outputs/apk/debug/app-debug.apk

IMPORTANT: Never put the Pusher secret in the Android app. The Android client only needs the Pusher key and cluster.
