# 指定cmake的最低版本
cmake_minimum_required (VERSION 2.8)


set(ANDROID_CPP_FEATURES "rtti exceptions")

# 项目名称
project(ncnn_safmn)

# 设置ncnn库的搜索路径
set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn-20240410-android/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

# 搜索openCV库，一旦找到可以使用变量OpenCV_INCLUDE_DIRS、OpenCV_LIBS
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv-mobile-4.9.0-android/sdk/native/jni)
find_package(OpenCV REQUIRED)

# 添加 OpenCV 头文件路径
include_directories(${OpenCV_INCLUDE_DIRS})




# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 输出 OpenCV 库的路径，以便检查
#message("OpenCV include dirs: ${OpenCV_INCLUDE_DIRS}")
#message("OpenCV libs: ${OpenCV_LIBS}")

add_library(ncnn_safmn SHARED SAFMN.cpp)

# 创建一个名为 ncnn_safmn 的共享库（SHARED 表示它是一个动态库，会生成 .so 文件）。这个库会将 SAFMN.cpp 文件编译为动态库。
# 这行代码最终会生成 libncnn_safmn.so 文件。

# 执行编译目标需要链接的库
target_link_libraries(ncnn_safmn ncnn ${OpenCV_LIBS})

# 将 ncnn 库和 OpenCV 库链接到 ncnn_safmn 库中。也就是说，编译生成的 libncnn_safmn.so 文件将包含对 ncnn 和 OpenCV 库的依赖。

