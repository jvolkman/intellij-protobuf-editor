# Description: IntelliJ protobuf plugin - Python language support sources.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//erlang:__subpackages__"])

load(
    "//build_support/build_defs:build_defs.bzl",
    "intellij_plugin_library",
    "optional_plugin_xml",
)

java_library(
    name = "erlang",
    srcs = glob(["java/**/*.java"]),
    deps = [
        "//build_support:plugin_api",
        "//build_support:plugin_erlang_support",
        "//core",
        "//gencode",
    ],
)

optional_plugin_xml(
    name = "optional_plugin_xml",
    module = "org.jetbrains.erlang",
    plugin_xml = "resources/META-INF/erlang_optional.xml",
)

intellij_plugin_library(
    name = "plugin_library",
    optional_plugin_xmls = [":optional_plugin_xml"],
    deps = [":erlang"],
)
