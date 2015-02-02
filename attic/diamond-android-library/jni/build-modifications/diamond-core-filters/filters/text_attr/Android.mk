LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_text_attr.c regex.c
LOCAL_MODULE := text_attr
include $(BUILD_EXECUTABLE)
