LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_texture.c texture_tools.c
LOCAL_MODULE := perceptual_hash
include $(BUILD_EXECUTABLE)
