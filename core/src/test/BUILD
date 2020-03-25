# Description: IntelliJ protobuf plugin - core unit tests.
#

licenses(["notice"])  # Apache 2.0

load(
    "//build_support/testing:test_defs.bzl",
    "intellij_unit_test_suite",
)

package(default_visibility = ["//visibility:private"])

intellij_unit_test_suite(
    name = "unit_tests",
    srcs = glob(["java/**/*.java"]),
    test_package_root = "com.google.devtools.intellij.protoeditor",
    visibility = ["//visibility:public"],
    deps = [
        "//build_support:test_libs",
        "//build_support:test_plugin_api",
        "//core/src/main:core",
    ],
)
