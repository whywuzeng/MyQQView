 cmake_minimum_required(VERSION 3.4.1)

 set(CMAKE_VERBOSE_MAKEFILE on)

 add_library(
         mp3encoderJni

         SHARED

         mp3EnconderJni.cpp
 )

 set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../distribution)

 set_target_properties(mp3encoderJni
         PROPERTIES
         ARCHIVE_OUTPUT_DIRECTORY
         "${distribution_DIR}/mp3encoderJni/lib/${ANDROID_ABI}")