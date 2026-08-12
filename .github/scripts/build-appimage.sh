#!/bin/bash
# Builds a Linux AppImage from the jpackage image using quick-sharun.
# Designed to run inside an archlinux container with the repo mounted at /work.
set -e

echo "==> [AppImage] Updating Arch container"
pacman -Syu --noconfirm --needed

echo "==> [AppImage] Installing dependencies"
pacman -S --noconfirm --needed \
	base-devel \
	wget \
	xorg-server-xvfb \
	strace \
	patchelf \
	libpulse \
	libxkbcommon \
	wayland

echo "==> [AppImage] Installing jpackage image to /usr/lib/shatteredpd"
rm -rf /usr/lib/shatteredpd
cp -a "/work/desktop/build/jpackage/Easier Pixel Dungeon" /usr/lib/shatteredpd
chmod -R u+w /usr/lib/shatteredpd

echo "==> [AppImage] Fetching quick-sharun"
wget -qO /work/quick-sharun.sh \
	https://raw.githubusercontent.com/pkgforge-dev/Anylinux-AppImages/refs/heads/main/useful-tools/quick-sharun.sh
chmod +x /work/quick-sharun.sh

echo "==> [AppImage] Preparing bin wrapper"
mkdir -p /work/AppDir/bin
cat > /work/AppDir/bin/shatteredpd <<'EOF'
#!/bin/sh
APPIMAGE_APPDIR="${APPDIR:-$(cd "$(dirname "$0")"/.. && pwd)}"
export LD_LIBRARY_PATH="$APPIMAGE_APPDIR/lib${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"
exec "$APPIMAGE_APPDIR/lib/shatteredpd/bin/Easier Pixel Dungeon" "$@"
EOF
chmod +x /work/AppDir/bin/shatteredpd

./get-debloated-pkgs.sh --add-common --prefer-nano

echo "==> [AppImage] Deploying with quick-sharun"
cd /work
DESKTOP=/work/.github/assets/shatteredpd.desktop \
ICON=/work/desktop/src/main/assets/icons/icon_256.png \
APPDIR=/work/AppDir \
OUTPATH=/work \
OUTPUT_APPIMAGE=1 \
STRACE_BINARY='Easier Pixel Dungeon' \
STRACE_TIME=10 \
DEPLOY_OPENGL=1 \
DEPLOY_PULSE=1 \
./quick-sharun.sh /usr/lib/shatteredpd

echo "==> [AppImage] Done"
ls -lah /work/*.AppImage
