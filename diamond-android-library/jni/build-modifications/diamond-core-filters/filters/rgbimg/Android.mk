LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_rgb.c
LOCAL_MODULE := rgbimg
include $(BUILD_EXECUTABLE)
