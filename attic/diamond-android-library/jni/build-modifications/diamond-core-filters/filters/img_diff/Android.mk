LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_img_diff.c
LOCAL_MODULE := img_diff
include $(BUILD_EXECUTABLE)
