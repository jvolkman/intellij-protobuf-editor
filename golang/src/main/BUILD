# Description: IntelliJ protobuf plugin - Go language support sources.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//golang:__subpackages__"])

load(
    "//build_support/build_defs:build_defs.bzl",
    "intellij_plugin_library",
    "optional_plugin_xml",
)

java_library(
    name = "golang",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//build_support/intellij_platform_sdk:jsr305",
        "//build_support/intellij_platform_sdk:plugin_api",
        "//core",
        "//gencode",
        "@go_2019_3//:go",
    ],
)

optional_plugin_xml(
    name = "optional_plugin_xml",
    module = "org.jetbrains.plugins.go",
    plugin_xml = "resources/META-INF/golang_optional.xml",
)

intellij_plugin_library(
    name = "plugin_library",
    optional_plugin_xmls = [":optional_plugin_xml"],
    deps = [":golang"],
)
