 cmake_minimum_required(VERSION 3.4.1)

 include_directories(src/main/cpp/lame/include)
 link_directories(src/main/cpp/lame)

 add_library(
         mp3encoder

         SHARED

         src/main/cpp/mp3EnconderJni.cpp
         src/main/cpp/libmp3_encoder/Mp3Encoder.cpp
 )

 find_library( # Defines the name of the path variable that stores the
         # location of the NDK library.
         log-lib

         # Specifies the name of the NDK library that
         # CMake needs to locate.
         log )


 target_link_libraries(
         mp3encoder

         libmp3lame.a
         # Links the target library to the log library
         # included in the NDK.
         ${log-lib}
 )