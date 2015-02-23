LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := \
	fil_gab_texture.cc \
	gabor.cc \
	gabor_filter.cc \
	gabor_tools.cc
LOCAL_MODULE := gabor_texture
include $(BUILD_EXECUTABLE)
