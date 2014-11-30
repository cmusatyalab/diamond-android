LOCAL_PATH := $(call my-dir)
OPENDIAMOND := ../../opendiamond
CV := ../../OpenCV-2.4.9-android-sdk
GLIB := ../../glib
LJPEG := ../../libjpeg-turbo-1.3.1
LTIFF := ../../tiff-4.0.3
LPNG := ../../libpng-1.6.12
LARCHIVE := ../../libarchive-3.1.2

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	libfilimage/examples.c \
	libfilimage/fil_image_tools.c \
	libfilimage/image_draw.c \
	libfilimage/image_tools.c \
	libfilimage/readjpeg.c \
	libfilimage/readpng.c \
	libfilimage/readtiff.c \
	libfilimage/rgb.c \
	libfilimage/readjpeg.h \
	libfilimage/readpng.h \
	libfilimage/readtiff.h \
	libocvimage/lib_ocvimage.c \
	libresults/lib_results.c \
	memstream.c \
	fmemopen.c \
	getline.c

LOCAL_LDLIBS := \
	$(OPENDIAMOND)/libfilter/obj/local/armeabi/libfilter.a \
  $(GLIB)/obj/local/armeabi/libglib-2.0.a \
	$(GLIB)/obj/local/armeabi/libgmodule-2.0.a \
	$(GLIB)/obj/local/armeabi/libgobject-2.0.a \
	$(GLIB)/obj/local/armeabi/libgthread-2.0.a \
	$(GLIB)/obj/local/armeabi/libpcre.a \
	$(CV)/obj/local/armeabi/libcv.a \
	$(CV)/obj/local/armeabi/libcvaux.a \
	$(CV)/obj/local/armeabi/libcvhighgui.a \
	$(CV)/obj/local/armeabi/libcvml.a \
	$(CV)/obj/local/armeabi/libcxcore.a \
	$(CV)/obj/local/armeabi/libopencv.a \
	$(CV)/obj/local/armeabi/libstdc++.a \
	$(LJPEG)/.libs/libjpeg.a \
	$(LTIFF)/libtiff/.libs/libtiff.a \
	$(LPNG)/.libs/libpng16.a

LOCAL_MODULE := libhelper

LOCAL_C_INCLUDES := \
	../include \
	$(OPENDIAMOND)/libfilter \
	$(LJPEG) \
	$(LTIFF)/libtiff \
	$(LPNG) \
	$(CV)/sdk/native/jni/include \
  $(LARCHIVE)/libarchive

LOCAL_CFLAGS := -std=gnu99

LOCAL_LDLIBS := -lz

include $(BUILD_STATIC_LIBRARY)
