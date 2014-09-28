#!/bin/bash

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

rm -rf $RES_DIR
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

echo "ocv_face.xml"
cp $PWD/jni/diamond-core-filters/predicates/ocv_face.xml $RES_DIR
