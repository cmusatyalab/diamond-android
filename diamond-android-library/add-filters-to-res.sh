#!/bin/bash
#
#  Diamond Android - Diamond filters for the Android platform
#
#  Copyright (c) 2013-2014 Carnegie Mellon University
#  All Rights Reserved.
#
#  This software is distributed under the terms of the Eclipse Public
#  License, Version 1.0 which can be found in the file named LICENSE.
#  ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS SOFTWARE CONSTITUTES
#  RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT

RES_DIR=res/raw

die() { echo $*; exit -1; }
addFilter() {
  if [[ $# == 1 ]]; then
    local FULL_PATH=$PWD/jni/diamond-core-filters/filters/$1/obj/local/armeabi/$1
  else
    local FULL_PATH=$PWD/jni/diamond-core-filters/filters/$1/obj/local/armeabi/$2
  fi
  [[ -f $FULL_PATH ]] || die "Unable to find filter: '$1'"
  echo $1
  cp $FULL_PATH $RES_DIR
}

mkdir -p $RES_DIR

addFilter dog_texture
addFilter gabor_texture
addFilter img_diff
addFilter null null_filter
addFilter num_attr
addFilter ocv_face
addFilter rgb_histogram
addFilter rgbimg
addFilter shingling
addFilter text_attr
addFilter thumbnailer

echo "haarcascade_frontalface"
cp $PWD/jni/diamond-core-filters/predicates/ocv_face/haarcascade_frontalface.xml $RES_DIR/haarcascade_frontalface
