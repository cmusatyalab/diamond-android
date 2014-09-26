#!/bin/bash

RES_DIR=res/raw

die() { echo $*; exit -1; }
addFilter() {
  local FULL_PATH=$PWD/jni/diamond-core-filters/filters/$1/obj/local/armeabi/$1
  [[ -f $FULL_PATH ]] || die "Unable to find filter: '$1'"
  if [[ $# == 1 ]]; then
    echo $1
    cp $FULL_PATH $RES_DIR
  else
    echo $1 '->' $2
    cp $FULL_PATH $RES_DIR/$2
  fi
}

rm -rf $RES_DIR
mkdir -p $RES_DIR

addFilter dog_texture
addFilter gabor_texture
addFilter img_diff
addFilter null null_fil
addFilter num_attr
addFilter ocv_face
addFilter rgb_histogram
addFilter rgbimg
addFilter shingling
addFilter text_attr
addFilter thumbnailer
