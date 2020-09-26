# Rules for building and testing the plugin.

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
PROTO_PLUGIN_VERSION = "2.2.0"

java_library(
    name = "combined_lib",
    exports = [
        "//core",
        "//java",
        "//python",
        "//golang",
    ]
)

intellij_plugin_library(
    name = "plugin_library",
    optional_plugin_xmls = [
        "//golang:optional_plugin_xml",
        "//java:optional_plugin_xml",
        "//python:optional_plugin_xml",
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
    changelog_file = "changelog.html",
    description_file = "plugin_description.html",
    plugin_id = "idea.plugin.protoeditor",
    plugin_name = "Protocol Buffer Editor",
    stamp_since_build_major = True,
    stamp_until_build = True,
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
