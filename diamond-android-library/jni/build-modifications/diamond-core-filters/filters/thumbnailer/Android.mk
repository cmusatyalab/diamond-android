LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_thumb.c
LOCAL_MODULE := thumbnailer
include $(BUILD_EXECUTABLE)
