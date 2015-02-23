LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include ../base.mk
LOCAL_SRC_FILES := fil_ocv.c opencv_face_tools.c
LOCAL_MODULE := ocv_face
include $(BUILD_EXECUTABLE)
