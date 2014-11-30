ROOT = ../../..
OPENDIAMOND := $(ROOT)/opendiamond
CV := $(ROOT)/OpenCV-2.4.9-android-sdk
GLIB := $(ROOT)/glib
LARCHIVE := $(ROOT)/libarchive-3.1.2
LJPEG := $(ROOT)/libjpeg-turbo-1.3.1
LTIFF := $(ROOT)/tiff-4.0.3
LPNG := $(ROOT)/libpng-1.6.15

LOCAL_LDLIBS := \
	$(OPENDIAMOND)/libfilter/obj/local/armeabi/libfilter.a \
	../../lib/obj/local/armeabi/libhelper.a \
	$(wildcard $(CV)/sdk/native/libs/armeabi/*.a) \
	$(wildcard $(CV)/sdk/native/libs/armeabi/*.a) \
	$(wildcard $(CV)/sdk/native/3rdparty/libs/armeabi/*.a) \
	$(LJPEG)/.libs/libjpeg.a \
	$(LTIFF)/libtiff/.libs/libtiff.a \
	$(LTIFF)/port/.libs/libport.a \
	$(LPNG)/.libs/libpng16.a \
	$(LARCHIVE)/.libs/libarchive.a \
  $(wildcard $(GLIB)/obj/local/armeabi/*.a) \
	$(ROOT)/toolchain/arm-linux-androideabi/lib/libstdc++.a \
	-llog -lz

LOCAL_C_INCLUDES := \
	../../include \
	$(LARCHIVE)/libarchive \
	$(OPENDIAMOND)/libfilter \
	$(LJPEG) \
	$(LTIFF)/libtiff \
	$(LPNG) \
	$(CV)/sdk/native/jni/include \
  $(CV)/sdk/native/jni/include/opencv2/imgproc

LOCAL_CFLAGS := -std=gnu99
