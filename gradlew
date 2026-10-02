#!/bin/sh
set -e

GRADLE_VERSION="8.7"
GRADLE_HOME="$HOME/.gradle/pusher-gradle/gradle-$GRADLE_VERSION"
GRADLE_ZIP="$HOME/.gradle/pusher-gradle/gradle-$GRADLE_VERSION-bin.zip"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  mkdir -p "$HOME/.gradle/pusher-gradle"
  if [ ! -f "$GRADLE_ZIP" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$GRADLE_ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -O "$GRADLE_ZIP"
    else
      echo "Neither curl nor wget is available." >&2
      exit 1
    fi
  fi
  rm -rf "$GRADLE_HOME"
  mkdir -p "$GRADLE_HOME"
  if command -v unzip >/dev/null 2>&1; then
    unzip -q "$GRADLE_ZIP" -d "$HOME/.gradle/pusher-gradle"
  else
    echo "unzip is not available." >&2
    exit 1
  fi
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
