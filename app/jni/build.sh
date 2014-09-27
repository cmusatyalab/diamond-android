#!/bin/bash

set -x -e

die() { echo $*; exit -1; }

cd $(dirname "$0")

if [[ ! -d toolchain ]]; then
  $ANDROID_NDK/build/tools/make-standalone-toolchain.sh \
    --install-dir=$PWD/toolchain
fi

# Add the standalone toolchain to the path.
pathadd() { PATH="${PATH:+"$PATH:"}$1"; }
pathadd "$PWD/toolchain/bin"
command -v arm-linux-androideabi-gcc &> /dev/null || exit -1
export CC=arm-linux-androideabi-gcc
export CXX=arm-linux-androideabi-g++
export LD=arm-linux-androideabi-ld
export RANLIB=arm-linux-androideabi-ranlib
export AR=arm-linux-androideabi-ar
export CROSS_PREFIX=arm-linux-androideabi-

if [[ ! -d glib ]]; then
  git clone https://github.com/ieei/glib.git
fi
cd glib
git checkout 63c09f42414ca0214187926bf376bc8de1e71a52
mv Android.mk{,.old}
echo "TARGET_FORMAT_STRING_CFLAGS := -Wformat -Wno-error" > Android.mk
sed 's/GLIB\_BUILD\_STATIC := .*/GLIB\_BUILD\_STATIC := true/' Android.mk.old \
  >> Android.mk
echo "APP_MODULES=libglib-2.0" > Application.mk
ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=./Android.mk \
  || die "glib build failed."
cd ..

if [[ ! -d opendiamond ]]; then
  git clone https://github.com/cmusatyalab/opendiamond.git
fi
cd opendiamond
git checkout a52d4d08ec39b49d6759bdbc5068fe37b611aa7b
cd libfilter
cp ../../build-modifications/libfilter-Android.mk ./Android.mk
ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=./Android.mk \
  || die "libfilter build failed."
cd ../..

if [[ ! -d OpenCV-2.4.9-android-sdk ]]; then
  wget http://downloads.sourceforge.net/project/opencvlibrary/opencv-android/2.4.9/OpenCV-2.4.9-android-sdk.zip
  unzip OpenCV*.zip
  rm OpenCV*.zip
fi

if [[ ! -d libarchive-3.1.2.tar.gz ]]; then
  wget http://www.libarchive.org/downloads/libarchive-3.1.2.tar.gz
  tar xvfz libarchive*.gz
fi
cd libarchive-3.1.2
cd libarchive
mv archive_read_disk_posix.c{,.old}
cat > archive_read_disk_posix.c<<EOF
#ifdef __ANDROID__
#include <sys/vfs.h>
#define statvfs statfs
#define fstatvfs fstatfs
#endif
EOF
cat archive_read_disk_posix.c.old >> archive_read_disk_posix.c
cd ..
patch tar/util.c ../build-modifications/archive-util.c.diff
./configure --enable-static --host=arm-linux --without-xml2
make -j8 || die "libarchive build failed."
cd ..

if [[ ! -d libjpeg-turbo-1.3.1 ]]; then
  wget http://downloads.sourceforge.net/project/libjpeg-turbo/1.3.1/libjpeg-turbo-1.3.1.tar.gz
  tar xvfz libjpeg*.gz
  rm libjpeg*.gz
fi
cd libjpeg-turbo-1.3.1
./configure --enable-static --target=armv5te-android-gcc --host=arm-linux
make -j8 || die "jpeg-6b build failed."
cd ..

if [[ ! -d tiff-4.0.3 ]]; then
  wget ftp://ftp.remotesensing.org/pub/libtiff/tiff-4.0.3.tar.gz
  tar xvfz tiff*.gz
fi
cd tiff-4.0.3
./configure --enable-static --host=arm-linux
make -j8 || die "tiff build failed."
cd ..

if [[ ! -d libpng-1.6.12 ]]; then
  wget http://prdownloads.sourceforge.net/libpng/libpng-1.6.12.tar.gz
  tar xvfz libpng*.gz
fi
cd libpng-1.6.12
./configure --enable-static --host=arm-linux
make -j8 || die "libpng build failed."
cd ..

if [[ ! -d zlib-1.2.8 ]]; then
  wget http://zlib.net/zlib-1.2.8.tar.gz
  tar xvfz zlib*.gz
fi
cd zlib-1.2.8
sed -i -e 's/AR=ar/AR=arm-linux-androideabi-ar/' Makefile.in
patch configure ../build-modifications/zlib-configure.diff
./configure --static
make -j8 || die "zlib build failed."
cd ..

if [[ ! -d diamond-core-filters ]]; then
  git clone https://github.com/cmusatyalab/diamond-core-filters.git
fi
cd diamond-core-filters
git checkout 9c0b6b18a8d1323a2daea2e4bef7a63b78bddb3d
cd lib
cp ../../build-modifications/diamond-core-filters/lib/* .
ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=./Android.mk \
  || die "diamond-core-filters/lib build failed."
cd ../..

cd diamond-core-filters/filters
cp ../../build-modifications/diamond-core-filters/filters/base.mk .
cd img_diff
mv fil_img_diff.c{,.old}
echo "#define MIN(a,b) ((a) < (b) ? a : b)" > fil_img_diff.c
cat fil_img_diff.c.old >> fil_img_diff.c
cd ..
cd num_attr
sed -i -e 's/#include <regex\.h>//' fil_num_attr.c
cd ..
cd text_attr
sed -i -e 's/#include <regex\.h>/#include "_regex.h"/' fil_text_attr.c
cd ..
buildFilter() {
  cd $1
  cp ../../../build-modifications/diamond-core-filters/filters/$1/* .
  ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=./Android.mk \
    || die "$1 build failed."
  cd ..
}
buildFilter dog_texture
buildFilter gabor_texture
buildFilter img_diff
buildFilter null
buildFilter num_attr
buildFilter ocv_face
buildFilter rgb_histogram
buildFilter rgbimg
buildFilter shingling
buildFilter text_attr
buildFilter thumbnailer
