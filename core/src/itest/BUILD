# Description: IntelliJ protobuf plugin - core integration tests.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

load(
    "//build_support/testing:test_defs.bzl",
    "intellij_integration_test_suite",
)

# Testing

intellij_integration_test_suite(
    name = "integration_tests",
    srcs = glob(["java/**/*.java"]),
    data = glob(["resources/**/*"]),
    jvm_flags = ["-Dtest.resources.location=" + package_name() + "/resources/"],
    required_plugins = "idea.plugin.protoeditor",
    shard_count = 1,
    test_package_root = "idea.plugin.protoeditor",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//core:plugin_for_tests",
    ],
    deps = [
        "//build_support:test_libs",
        "//build_support:test_plugin_api",
        "//core",
        "//testlib:test_utils",
    ],
)
