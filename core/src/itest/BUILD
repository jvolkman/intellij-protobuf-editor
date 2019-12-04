# Description: IntelliJ protobuf plugin - core integration tests.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

load(
    "//testlib:test_defs.bzl",
    "intellij_integration_test_suite",
)

# Testing

intellij_integration_test_suite(
    name = "integration_tests",
    srcs = glob(["java/**/*.java"]),
    data = glob(["resources/**/*"]),
    jvm_flags = ["-Dtest.resources.location=" + package_name() + "/resources/"],
    required_plugins = "com.google.devtools.intellij.protoeditor",
    shard_count = 1,
    test_package_root = "com.google.devtools.intellij.protoeditor",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//build_support/intellij_platform_sdk:test_libs",
        "//core:plugin_for_tests",
    ],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
        "//build_support/intellij_platform_sdk:test_libs",
        "//core",
        "//testlib:test_utils",
        "@junit//jar",
    ],
)
