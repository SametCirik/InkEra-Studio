cmake_minimum_required(VERSION 3.10)
project(InkEraEngine)

# C++17 Standardını kullanıyoruz
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# 'inkera_engine' adında bir paylaşımlı kütüphane (.so) oluştur
add_library(inkera_engine SHARED InkEraEngine.cpp)

# Optimizasyon bayrakları (Maksimum performans için)
target_compile_options(inkera_engine PRIVATE -O3 -ffast-math)
