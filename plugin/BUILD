# Rules for building and testing the plugin.

load("//build_support/intellij_platform_sdk:build_defs.bzl", "select_for_ide")
load(
    "//build_support/build_defs:build_defs.bzl",
    "stamped_plugin_xml",
    "intellij_plugin",
    "intellij_plugin_library",
)
load(
    "//build_support/build_defs:intellij_plugin_debug_target.bzl",
    "intellij_plugin_debug_target",
)

package(default_visibility = ["//visibility:private"])

licenses(["notice"])  # Apache License 2.0

# Standalone plugin version.
PROTO_PLUGIN_VERSION = "1.0"

java_library(
    name = "combined_lib",
    exports = [
        "//core",
        ]
#        "//python",
#    ] + select_for_ide(
#        android_studio = [
#            "//java",
#        ],
#        clion = [],
#        default = [],
#        intellij = [
#            "//golang",
#            "//java",
#        ],
#    ),
)

intellij_plugin_library(
    name = "plugin_library",
    optional_plugin_xmls = [
#        "//golang:optional_plugin_xml",
#        "//java:optional_plugin_xml",
#        "//python:optional_plugin_xml",
    ],
    plugin_xmls = ["//core:plugin_xml"],
    deps = [":combined_lib"],
)

genrule(
    name = "version_file",
    srcs = [],
    outs = ["version"],
    cmd = "echo '%s' > $@" % PROTO_PLUGIN_VERSION,
)

stamped_plugin_xml(
    name = "standalone_plugin_xml",
    changelog_file = "CHANGELOG",
    description_file = "plugin_description",
    plugin_id = "com.google.devtools.intellij.protoeditor",
    plugin_name = "Protocol Buffer Editor",
    stamp_since_build = False,
    vendor_file = "vendor.xml",
    version_file = ":version_file",
)

intellij_plugin(
    name = "protobuf-editor",
    plugin_xml = ":standalone_plugin_xml",
    deps = [
        ":plugin_library",
    ],
)

alias(
    name = "plugin",
    actual = ":protobuf-editor",
)

intellij_plugin_debug_target(
    name = "plugin_debug_target",
    deps = [
        ":plugin",
    ],
)
