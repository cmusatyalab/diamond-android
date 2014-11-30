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
The Diamond Android source is licensed under the
[Eclipse Public License v1.0][eplv1].
The following libraries have been modified as noted and are
included in the statically linked filter binary artifacts
of this repository.
These portions are copyright their respective authors with
the licenses listed.
<!--
From http://www.libjpeg-turbo.org/About/License:
4. If you are distributing only libjpeg-turbo binaries without the source, or
   if you are distributing an application that statically links with
    libjpeg-turbo, then your product documentation must include a message
    stating that "this software is based in part on the work of the Independent
    JPEG Group".
-->
This software is based in part on the work of the Independent JPEG Group.
<!-- From http://opencv.org/license.html -->
By downloading, copying, installing or using the software you agree to the
[OpenCV license](http://opencv.org/license.html).
If you do not agree to this license, do not download, install, copy
or use the software.

Project | Source Modified | License
---|---|---
[cmusatyalab/diamond-core-filters](https://github.com/cmusatyalab/diamond-core-filters) | Yes | [Eclipse Public License v1.0][eplv1]
[cmusatyalab/opendiamond](https://github.com/cmusatyalab/opendiamond) | Yes | [Eclipse Public License v1.0][eplv1]
[ivanra/getline](https://github.com/ivanra/getline) | | Public Domain
[libarchive](http://www.libarchive.org/) | Yes | [BSD 2-Clause][bsd-2]
[libjpeg-turbo](http://libjpeg-turbo.virtualgl.org/) | | [libjpeg-turbo License][lj-l], [IJG][ijg], and [libjpeg/SIMD][lj-simd]
[libpng](http://www.libpng.org/pub/png/libpng.html) | | [libpng License](http://www.libpng.org/pub/png/src/libpng-LICENSE.txt)
[libtiff](http://www.remotesensing.org/libtiff/) | | [MIT-like](http://www.remotesensing.org/libtiff/misc.html)
[memstream-0.1](http://piumarta.com/software/memstream) | | [MIT][mit]
[NimbusKit/memorymapping](https://raw.githubusercontent.com/NimbusKit/memorymapping/master/src/fmemopen.h) | | [Apache 2.0][a2]
[OpenCV](http://opencv.org/) | | [OpenCV License](http://opencv.org/license.html)

[a2]: http://www.apache.org/licenses/LICENSE-2.0.html
[eplv1]: https://www.eclipse.org/legal/epl-v10.html
[mit]: http://opensource.org/licenses/MIT
[bsd-2]: http://opensource.org/licenses/bsd-license.php
[ijg]: http://svn.code.sf.net/p/libjpeg-turbo/code/trunk/README
[lj-l]: http://svn.code.sf.net/p/libjpeg-turbo/code/trunk/README-turbo.txt
[lj-simd]: http://svn.code.sf.net/p/libjpeg-turbo/code/trunk/simd/jsimdext.inc

[case-for-cloudlets]: http://elijah.cs.cmu.edu/DOCS/satya-ieeepvc-cloudlets-2009.pdf
[diamond]: http://diamond.cs.cmu.edu/
[opencv]: http://opencv.org/
[opencv-android]: https://github.com/billmccord/OpenCV-Android
[glib]: https://developer.gnome.org/glib/
[glib-android]: https://github.com/ieei/glib/
[diamond-core-filters]: https://github.com/cmusatyalab/diamond-core-filters
[ndk-r9]: http://dl.google.com/android/ndk/android-ndk-r9-linux-x86.tar.bz2
