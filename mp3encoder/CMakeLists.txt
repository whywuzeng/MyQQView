 cmake_minimum_required(VERSION 3.4.1)

 set(CMAKE_VERBOSE_MAKEFILE on)

 set(lib_src_DIR ${CMAKE_CURRENT_SOURCE_DIR})

 set(lib_build_DIR $ENV{HOME}/tmp)
 file(MAKE_DIRECTORY ${lib_build_DIR})

 add_subdirectory(${lib_src_DIR}/src/main/cpp ${lib_build_DIR}/mp3encoderJni)
# add_subdirectory(${lib_src_DIR}/src/main/cpp/libmp3_encoder ${lib_build_DIR}/mp3encodersub)