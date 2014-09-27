LOCAL_PATH:= $(call my-dir)
GLIB_TOP:= ../../glib

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= lf_protocol.c lf_wrapper.c  lib_filter.c

LOCAL_MODULE := libfilter

LOCAL_C_INCLUDES := \
  $(GLIB_TOP) \
  $(GLIB_TOP)/glib \
  $(GLIB_TOP)/android

LOCAL_CFLAGS := -std=c99

include $(BUILD_STATIC_LIBRARY)
