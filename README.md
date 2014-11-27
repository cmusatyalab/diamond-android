TODO - Create diagram.

Mobile devices have powerful processors and streaming and processing
video in real time is becoming a reality by leveraging
resource-rich cloudlets, as described in
["The Case for VM-Based Cloudlets in Mobile Computing"][case-for-cloudlets].
Real-time video processing is becoming a reality,
and filtering video frames can be helpful to identify objects in frames.
**This project provides an image and video frame filtering library for
Android and Glass.**
For example, an application can say "only send video frames with a
brick wall like in these examples because I'm only interested in
overlaying images on top of brick walls."

These filters are provided by the [Diamond][diamond] project,
which provides interactive search of non-indexed data.
This repository compiles the C filters from the
[cmusatyalab/diamond-core-filters][diamond-core-filters] repository
for ARM and statically links their dependencies.

# Face Detection Example.
TODO: Mention that the OpenCV Android library is likely a better
choice because it's implemented in java, but this
is meant to show the capabilities of DiamondAndroid.

# Obtaining ARM filters.
TODO

## Downloading Binaries.
TODO - Upload

## Building filters with the Android NDK.
The current

TODO: Run `add-res-to-raw.sh`.

# Contributing.
TODO

# Licensing
TODO - Licenses of dependencies.

TODO:
We also used the following C implementations of functions we
couldn't otherwise resolve.

https://raw.githubusercontent.com/NimbusKit/memorymapping/master/src/fmemopen.h
http://www.opensource.apple.com/source/cvs/cvs-19/cvs/lib/getline.c?txt
http://piumarta.com/software/memstream/memstream-0.1/memstream.c


[case-for-cloudlets]: http://elijah.cs.cmu.edu/DOCS/satya-ieeepvc-cloudlets-2009.pdf
[diamond]: http://diamond.cs.cmu.edu/
[opencv]: http://opencv.org/
[opencv-android]: https://github.com/billmccord/OpenCV-Android
[glib]: https://developer.gnome.org/glib/
[glib-android]: https://github.com/ieei/glib/
[diamond-core-filters]: https://github.com/cmusatyalab/diamond-core-filters
[ndk-r9]: http://dl.google.com/android/ndk/android-ndk-r9-linux-x86.tar.bz2
