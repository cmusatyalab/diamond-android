LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_null.c image.c
LOCAL_MODULE := null_filter
include $(BUILD_EXECUTABLE)
