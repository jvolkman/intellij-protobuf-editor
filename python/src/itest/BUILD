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
        "//build_support:test_plugin_java_support",  # Required for some Python -> Java plugin integration
    ],
    deps = [
        "//build_support:test_plugin_api",
        "//build_support:test_plugin_python_support",
        "//build_support:test_libs",
        "//python",
        "//testlib:gencodeutils",
        "//testlib:test_utils",
    ],
)
