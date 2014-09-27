LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_texture.c texture_tools.c
LOCAL_MODULE := dog_texture
include $(BUILD_EXECUTABLE)
