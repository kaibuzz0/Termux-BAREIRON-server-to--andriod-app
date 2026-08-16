cmake_minimum_required(VERSION 3.22.1)
project(bareiron_android C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

set(SERVER_DIR "${CMAKE_CURRENT_LIST_DIR}/../../../../../server")
set(ANDROID_COMPAT_DIR "${CMAKE_CURRENT_LIST_DIR}/include")
set(GENERATED_DIR "${CMAKE_CURRENT_BINARY_DIR}/generated")
file(MAKE_DIRECTORY "${GENERATED_DIR}")

# Keep the standalone/Termux server source untouched. For Android only, generate
# a controllable entry point from main.c: failures return to Java rather than
# terminating the app process, and the infinite host loop observes a stop flag.
file(READ "${SERVER_DIR}/src/main.c" BAREIRON_MAIN_SOURCE)
string(REPLACE
    "int main (int argc, char** argv) {"
    "int bareiron_android_server_main (int argc, char** argv) {"
    BAREIRON_MAIN_SOURCE
    "${BAREIRON_MAIN_SOURCE}")
string(REPLACE
    "while (true) {"
    "while (!bareiron_android_stop_requested) {"
    BAREIRON_MAIN_SOURCE
    "${BAREIRON_MAIN_SOURCE}")
string(REPLACE
    "exit(EXIT_FAILURE);"
    "return EXIT_FAILURE;"
    BAREIRON_MAIN_SOURCE
    "${BAREIRON_MAIN_SOURCE}")
string(REPLACE
    "  printf(\"Server closed.\\n\");\n\n}"
    "  printf(\"Server closed.\\n\");\n  return EXIT_SUCCESS;\n\n}"
    BAREIRON_MAIN_SOURCE
    "${BAREIRON_MAIN_SOURCE}")
set(BAREIRON_MAIN_SOURCE
    "extern volatile int bareiron_android_stop_requested;\n${BAREIRON_MAIN_SOURCE}")
file(WRITE "${GENERATED_DIR}/main_android.c" "${BAREIRON_MAIN_SOURCE}")

file(GLOB BAREIRON_SERVER_SOURCES CONFIGURE_DEPENDS "${SERVER_DIR}/src/*.c")
list(REMOVE_ITEM BAREIRON_SERVER_SOURCES
    "${SERVER_DIR}/src/main.c"
    "${SERVER_DIR}/src/tls.c")

add_library(bareiron_android SHARED
    bareiron_jni.c
    tls_android_stub.c
    "${GENERATED_DIR}/main_android.c"
    ${BAREIRON_SERVER_SOURCES})

target_include_directories(bareiron_android PRIVATE
    "${ANDROID_COMPAT_DIR}"
    "${SERVER_DIR}/include")

target_compile_definitions(bareiron_android PRIVATE BAREIRON_ANDROID=1)
target_compile_options(bareiron_android PRIVATE -Wall -Wextra -Wno-unused-parameter)

target_link_libraries(bareiron_android m log)
