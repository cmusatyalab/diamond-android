LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_num_attr.c
LOCAL_MODULE := num_attr
include $(BUILD_EXECUTABLE)
