#!/bin/bash
# build-bpfox.sh — Build Fenix with package org.bluepowerrobotics.bpfox (all ABIs).
#
# Prereqs (already set up on this machine):
#   - Python 3.12 at ~/.local/python312 (system Python 3.8 is too old for mach)
#   - Android SDK/NDK in ~/.mozbuild (installed once via mach bootstrap)
#   - mozconfig with --target=aarch64 and --with-java-bin-path=Android Studio JBR
#   - gradle/libs.versions.toml pinned to protobuf 3.21.12 (protoc 4.x needs macOS 12+)
#
# If mach says a clobber is required (e.g. after merge day / target change):
#   ./mach clobber && ./build-bpfox.sh
set -euo pipefail
cd "$(dirname "$0")"

export PATH="/Users/gaoyiwei/.local/python312/bin:$PATH"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

fetch_aar() { # resume-capable download
  local url=$1 out=$2 rc
  while :; do
    if curl -sS -L -C - --connect-timeout 30 --speed-limit 10240 --speed-time 30 -o "$out" "$url"; then
      return 0
    fi
    rc=$?
    [ $rc -eq 33 ] && rm -f "$out"
    sleep 2
  done
}

echo "==> 1/4 configure"
./mach configure

echo "==> 2/4 mach build (fetches GeckoView artifact for the mozconfig --target)"
./mach build

echo "==> 3/4 swap official all-ABI GeckoView nightly into local maven"
GV_BASE="https://maven-default-s3-serving-bucket-7i7fa3jmv07w.s3.amazonaws.com/maven2/org/mozilla/geckoview"
GV_VERSION=$(curl -sS --max-time 30 "$GV_BASE/geckoview-nightly/maven-metadata.xml" \
  | sed -n 's:.*<latest>\(.*\)</latest>.*:\1:p' | head -1)
echo "    using geckoview-nightly $GV_VERSION"
fetch_aar "$GV_BASE/geckoview-nightly/$GV_VERSION/geckoview-nightly-$GV_VERSION.aar" /tmp/gv-nightly.aar

M2="objdir-frontend/gradle/maven/org/mozilla/geckoview/geckoview-default-omni"
VDIR=$(ls -dt "$M2"/*/ 2>/dev/null | head -1)
[ -n "$VDIR" ] || { echo "ERROR: local maven geckoview dir not found — run ./mach build first" >&2; exit 1; }
AAR=$(ls "$VDIR"*.aar 2>/dev/null | head -1)
[ -n "$AAR" ] || { echo "ERROR: no .aar in $VDIR" >&2; exit 1; }
cp "$AAR" /tmp/gv-old.aar
cp /tmp/gv-nightly.aar "$AAR"
cd "$VDIR"
base=$(basename "$AAR")
md5 -q "$base" > "$base.md5"
shasum -a 1 "$base"   | awk '{print $1}' > "$base.sha1"
shasum -a 256 "$base" | awk '{print $1}' > "$base.sha256"
shasum -a 512 "$base" | awk '{print $1}' > "$base.sha512"
cd - >/dev/null
rm -rf "$HOME/.gradle/caches/modules-2/files-2.1/org.mozilla.geckoview/geckoview-default-omni"

echo "==> 4/4 gradle assembleRelease (all ABIs)"
./mach gradle -p mobile/android/fenix app:assembleRelease -PdisableOptimization

echo
echo "APKs:"
ls -lh objdir-frontend/gradle/build/mobile/android/fenix/app/outputs/apk/release/*.apk
