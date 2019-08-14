# Description: IntelliJ protobuf plugin - Python language support integration tests.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

load(
    "//build_support/testing:test_defs.bzl",
    "intellij_integration_test_suite",
)
load(
    "//build_support/build_defs:build_defs.bzl",
    "stamped_plugin_xml",
    "intellij_plugin",
)

stamped_plugin_xml(
    name = "stamped_plugin_xml",
    plugin_id = "com.google.devtools.intellij.protoeditor.python",
    plugin_name = "Protocol Buffer Editor w/ Python Support",
)

intellij_plugin(
    name = "plugin_for_tests",
    plugin_xml = ":stamped_plugin_xml",
    deps = [
        "//core:plugin_library",
        "//python:plugin_library",
    ],
)

intellij_integration_test_suite(
    name = "integration_tests",
    srcs = glob(["java/**/*.java"]),
    data = ["//python/src/itest/resources"],
    jvm_flags = [
        "-Dtest.resources.package.name=" + package_name() + "/resources",
        "-Dtest.resources.location=" + package_name() + "/resources/",
    ],
    required_plugins = "com.google.devtools.intellij.protoeditor.python",
    test_package_root = "com.google.devtools.intellij.protoeditor",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":plugin_for_tests",
        # Needed to work around one-version issue; see cr/219160374
        "//build_support/intellij_platform_sdk:test_libs",
    ],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
        "//build_support/intellij_platform_sdk:test_libs",
        "//python",
        "//testlib:gencodeutils",
        "//testlib:test_utils",
        "@junit//jar",
        "@python_2019_1//:python_for_tests",
    ],
)
