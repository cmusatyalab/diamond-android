**Warning:** This library is pre-alpha, not under active development,
and not ready for production.

![](https://github.com/cmusatyalab/diamond-android/raw/master/images/overview.png)

[Javadoc][javadoc]

Mobile devices have powerful processors and streaming and processing
video in real time is becoming a reality by leveraging
resource-rich cloudlets, as described in
["The Case for VM-Based Cloudlets in Mobile Computing"][case-for-cloudlets].
Real-time video processing is becoming a reality,
and filtering video frames can be helpful to identify objects in frames.
This project provides an image and video frame filtering library for
Android and Glass.
For example, an augmented reality application can say "only send video
frames with a brick wall like in these examples because I'm only interested in
overlaying images on top of brick walls."

These filters are provided by the [Diamond][diamond] project,
which provides interactive search of non-indexed data.
This repository compiles the C filters from the
[cmusatyalab/diamond-core-filters][diamond-core-filters] repository
for ARM and statically links their dependencies.

# Face Detection Example
![](https://github.com/cmusatyalab/diamond-android/raw/master/images/face-detection-example.png)

[examples/face-detection](https://github.com/cmusatyalab/diamond-android/tree/master/android-studio-root/facerecognition)
is an example Android application to illustrate the usage of a
Diamond filter with an Android application filtering video
in real time for faces.
The APK of this application is available as
`facerecognition-release-unsigned.apk` from [the releases][releases].

The Diamond face detection filter is implemented in
[filters/ocv_face](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/ocv_face)
of [cmusatyalab/diamond-core-filters][diamond-core-filters]
and detects faces using the [OpenCV](http://opencv.org/) C library.
An alternate to using a Diamond filters is to use the
[OpenCV Android library](http://opencv.org/platforms/android.html),
which uses the JNI to interact with the native library.

## Initialization
Applications interact with the filter binaries through `Filter` objects.
The face detection example uses the `rgbimg` filter to convert images
into RGB format and the `ocv_face` filter to detect faces.
See the [Filter Javadoc][javadoc] for more information about creating filters.

```Java
String[] faceFilterArgs = {"1.2", "24", "24", "1", "2"};
InputStream ocvXmlIS = context.getResources().openRawResource(R.raw.haarcascade_frontalface);
Filter rgbFilter, faceFilter;
try {
    rgbFilter = new Filter(R.raw.rgbimg, context, "RGB", null, null);
    byte[] ocvXml = IOUtils.toByteArray(ocvXmlIS);
    faceFilter = new Filter(R.raw.ocv_face, context, "OCVFace",
    faceFilterArgs, ocvXml);
} catch (IOException e1) {
    Log.e(TAG, "Unable to create filter subprocess.");
    e1.printStackTrace();
    return;
}
```

## Using Filters
Video frames are passed to the `isFace` function as jpg images,
which uses the filters to detect faces.
Applications communicate with filters through the `process` function,
which uses a map for communication and returns a double.
The face recognition filter returns 1.0 if a face is detected
ad 0.0 otherwise.
Further details are described in the [Filter Javadoc][javadoc].

```Java
private boolean isFace(byte[] jpegImage, Filter rgbFilter, Filter faceFilter) throws IOException, FilterException {
    final Map<String,byte[]> m = new HashMap<String,byte[]>();
    Log.d(TAG, "Sending JPEG image to RGB filter.");
    Log.d(TAG, "JPEG image size: " + String.valueOf(jpegImage.length) + " bytes.");

    m.put("", jpegImage);
    rgbFilter.process(m);
    byte[] rgbImage = m.get("_rgb_image.rgbimage");

    Log.d(TAG, "Obtained RGB image from RGB filter.");
    Log.d(TAG, "RGB image size: " + String.valueOf(rgbImage.length) + " bytes.");

    Log.d(TAG, "Sending RGB image to OCV face filter.");
    double faceRecognized = faceFilter.process(m);
    return Math.abs(faceRecognized-1.0d) < 1E-6;
}
```

# Filters provided
This repository compiles the following filters from
[cmusatyalab/diamond-core-filters][diamond-core-filters]
for ARM.
The arguments to the filters can be found in the
[filter sources](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters).

Filter | Description
---|---
[dog_texture](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/dog_texture) | [Wikipedia: Difference of Gaussians](https://en.wikipedia.org/wiki/Difference_of_Gaussians)
[gabor_texture](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/gabor_texture) | [Wikipedia: Gabor filter](https://en.wikipedia.org/wiki/Gabor_filter)
[img_diff](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/img_diff) | Pixel-wise image difference.
[null](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/null) | Null filter (for testing)
[ocv_face](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/ocv_face) | OpenCV face detection.
[rgb_histogram](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/rgb_histogram) | [Wikipedia: Color histogram](https://en.wikipedia.org/wiki/Color_histogram)
[rgbimg](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/rgbimg) | Tries to convert numerous image formats into RGB.
[shingling](https://github.com/cmusatyalab/diamond-core-filters/tree/master/filters/shingling) | Fingerprint images based on sliding windows.

# Filter performance
The [performance](https://github.com/cmusatyalab/diamond-android/tree/master/android-studio-root/performance)
application runs filter on a sample data set
to estimate how long the filters will take to run in production.
The runtime may vary depending on the device, CPU load,
and the number of sample images are used.

## Configuring
### Image set on the device
Extract and move `public-domain-landscapes.tgz` from [the releases][releases]
into `/sdcard/` of the device.

```
tar xvfz public-domain-landscapes.tgz
adb push public-domain-landscapes /sdcard/public-domain-landscapes
```

### Filter examples in the application resources
Move and rename `public-domain-landscape-cloud-filters.zip` (as a zip archive)
from [the releases][releases] to `res/raw/filter_zip` of
the `performance` application.

## Results
[TODO, #33](https://github.com/cmusatyalab/diamond-android/issues/33)

# Obtaining ARM Filters
Filters should be located in the `res/raw` directory of your
Android application and can be obtained as binaries
in `arm-filters.tgz` from [the releases][releases].
The following portion describes how to build the filters
from source with the NDK and integrate with gradle in Android Studio.

## Building filters from source with the Android NDK
This section was ported from a pre-Android Studio into gradle
and does not use all of gradle and Android Studio's dependency
management solutions.

[build.gradle](https://github.com/cmusatyalab/diamond-android/blob/master/android-studio-root/facerecognition/build.gradle)
in the facerecognition application will invoke the NDK if
an arm filter is not present with the following task and dependency.

```Gradle
task buildJni(type: Exec) {
  commandLine 'sh', './jni/build.sh'
  commandLine 'sh', './add-filters-to-res.sh'
}

compile files("src/main/res/raw/dog_texture") {
  builtBy 'buildJni'
}
```

The [build.sh](https://github.com/cmusatyalab/diamond-android/blob/master/android-studio-root/facerecognition/jni/build.sh)
script in the
[diamond-android-library/jni](https://github.com/cmusatyalab/diamond-android/tree/master/android-studio-root/facerecognition/jni)
directory will automatically download libraries from source,
apply [source modifications](https://github.com/cmusatyalab/diamond-android/tree/master/android-studio-root/facerecognition/jni/build-modifications),
and build static filter executables.
The only prerequisite to running `build.sh` is to install the
[Android NDK](https://developer.android.com/tools/sdk/ndk/index.html)
and set the `ANDROID_NDK` environment variable to the installation path.
`build.sh` will create a
[standalone toolchain](http://www.kandroid.org/ndk/docs/STANDALONE-TOOLCHAIN.html)
for cross compiling the ARM applications.
The prebuilt filters use the Android NDK version `r9d` on OSX,
and [#31](https://github.com/cmusatyalab/diamond-android/issues/31)
describes a possible bug if the filters are compiled from Linux.
Once the filters are built, the
[add-res-to-raw.sh](https://github.com/cmusatyalab/diamond-android/blob/master/android-studio-root/facerecognition/add-filters-to-res.sh),
script will extract the binaries into `res/raw` of the
library application, which should be moved into `res/raw`
of applications using Diamond Android.

# Licensing
The Diamond Android source is copyright Carnegie Mellon University
and licensed under the [Eclipse Public License v1.0][eplv1].
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
[glib](https://developer.gnome.org/glib/) | Yes | [LGPL][lgpl3]
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
[lgpl3]: https://www.gnu.org/licenses/lgpl.html
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

[releases]: https://github.com/cmusatyalab/diamond-android/releases/
[javadoc]: https://github.com/cmusatyalab/diamond-android/issues/20
