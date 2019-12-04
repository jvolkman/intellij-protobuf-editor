# Description: IntelliJ protobuf plugin - core unit tests.
#

licenses(["notice"])  # Apache 2.0

load(
    "//testlib:test_defs.bzl",
    "intellij_unit_test_suite",
)

package(default_visibility = ["//visibility:private"])

intellij_unit_test_suite(
    name = "unit_tests",
    srcs = glob(["java/**/*.java"]),
    test_package_root = "com.google.devtools.intellij.protoeditor",
    visibility = ["//visibility:public"],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
        "//build_support/intellij_platform_sdk:test_libs",
        "//core/src/main:core",
        "@junit//jar",
    ],
)
