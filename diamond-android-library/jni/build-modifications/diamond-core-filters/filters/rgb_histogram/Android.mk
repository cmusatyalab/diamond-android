LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_rgb_histo.c rgb_histo.c
LOCAL_MODULE := rgb_histogram
include $(BUILD_EXECUTABLE)
