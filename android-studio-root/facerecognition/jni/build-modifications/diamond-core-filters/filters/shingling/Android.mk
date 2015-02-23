LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_shingling.c rabin.c
LOCAL_MODULE := shingling
include $(BUILD_EXECUTABLE)
