 cmake_minimum_required(VERSION 3.4.1)

 set(CMAKE_VERBOSE_MAKEFILE on)

 add_library(
         mp3encodersub

         STATIC

         Mp3Encoder.cpp
 )

 set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../distribution)

 set_target_properties(mp3encodersub
         PROPERTIES
         ARCHIVE_OUTPUT_DIRECTORY
         "${distribution_DIR}/mp3encodersub/lib/${ANDROID_ABI}")
 # copy out lib header file...
 add_custom_command(TARGET mp3encodersub POST_BUILD
         COMMAND "${CMAKE_COMMAND}" -E
         copy "${CMAKE_CURRENT_SOURCE_DIR}/Mp3Encoder.h"
         "${distribution_DIR}/mp3encodersub/include/Mp3Encoder.h"
         #                   **** the following 2 lines are for potential future debug purpose ****
         #                   COMMAND "${CMAKE_COMMAND}" -E
         #                   remove_directory "${CMAKE_CURRENT_BINARY_DIR}"
         COMMENT "Copying gmath to output directory")