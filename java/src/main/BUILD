# Description: IntelliJ protobuf plugin - Java language support sources.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//java:__subpackages__"])

load(
    "//build_support/build_defs:build_defs.bzl",
    "intellij_plugin_library",
    "optional_plugin_xml",
)

java_library(
    name = "java",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//build_support:plugin_api",
        "//build_support:plugin_java_support",
        "//core",
        "//gencode",
    ],
)

optional_plugin_xml(
    name = "optional_plugin_xml",
    module = "com.intellij.modules.java",
    plugin_xml = "resources/META-INF/java_optional.xml",
)

intellij_plugin_library(
    name = "plugin_library",
    optional_plugin_xmls = [":optional_plugin_xml"],
    deps = [":java"],
)
