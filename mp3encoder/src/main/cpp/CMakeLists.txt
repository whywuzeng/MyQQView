 cmake_minimum_required(VERSION 3.4.1)

 set(CMAKE_VERBOSE_MAKEFILE on)

# include_directories(/lame/include)
# link_directories(/lame)
# link_libraries(mp3lame)

 set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../distribution)

 message(STATUS "execute CMakeLists")
 # 创建一个静态库 lib_gmath 直接引用libgmath.a
 add_library(mp3lame STATIC IMPORTED)
 set_target_properties(mp3lame PROPERTIES IMPORTED_LOCATION  "${CMAKE_SOURCE_DIR}/src/main/cpp/lame/libmp3lame.a")
 message(STATUS "execute CMakeLists ${CMAKE_SOURCE_DIR}")
# D:/move/MyQQView/mp3encoder
# D:\move\MyQQView\mp3encoder\src\main\cpp\lame\libmp3lame.a
 # build application's shared lib
 set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")



 add_library(
         mp3encoderJni

         SHARED

         mp3EnconderJni.cpp
         ./libmp3_encoder/Mp3Encoder.cpp
 )

 set_target_properties(mp3encoderJni
         PROPERTIES
         ARCHIVE_OUTPUT_DIRECTORY
         "${distribution_DIR}/mp3encoderJni/lib/${ANDROID_ABI}")

 target_link_libraries(
         mp3encoderJni

         mp3lame
 )