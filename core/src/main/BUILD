# Description: IntelliJ protobuf plugin - core sources.
#

licenses(["notice"])  # Apache 2.0

load(
    "//build_support/build_defs:build_defs.bzl",
    "intellij_plugin",
    "intellij_plugin_library",
    "stamped_plugin_xml",
)

package(default_visibility = ["//core:__subpackages__"])

# Main build rules
genrule(
    name = "generated_parser",
    srcs = glob(["bnf/*.bnf"]),
    outs = ["generated-parser.srcjar"],
    cmd = """
        set -e
        WORK=`pwd`
        mkdir $(@D)/gen
        $(location //build_support:grammar_kit) $(@D)/gen $(SRCS)
        cd $(@D)/gen
        find * -type f | xargs $$WORK/$(location @bazel_tools//tools/zip:zipper) c $$WORK/$(@D)/generated-parser.srcjar
        cd $$WORK
        rm -rf $(@D)/gen
        """,
    tools = [
        "@bazel_tools//tools/zip:zipper",
        "//build_support:grammar_kit",
    ],
    visibility = ["//visibility:private"],
)

genrule(
    name = "generated_lexer",
    srcs = glob(["jflex/*.flex"]) + [
        "@idea_jflex_skeleton//file",
    ],
    outs = ["generated-lexer.srcjar"],
    cmd = """
        set -e
        WORK=`pwd`
        SKEL=`echo $(SRCS) | tr ' ' '\n' | grep 'skeleton' | tr '\n' ' '`
        GRAMMARS=`echo $(SRCS) | tr ' ' '\n' | grep '\.flex$$' | tr '\n' ' '`

        mkdir $(@D)/gen
        $(location //build_support:idea_jflex) \
           -skel $$SKEL \
           -d $(@D)/gen $$GRAMMARS
        cd $(@D)/gen
        find * -type f | xargs $$WORK/$(location @bazel_tools//tools/zip:zipper) c $$WORK/$(@D)/generated-lexer.srcjar
        rm -rf $(@D)/gen
        """,
    tools = [
        "@bazel_tools//tools/zip:zipper",
        "//build_support:idea_jflex",
    ],
    visibility = ["//visibility:private"],
)

java_library(
    name = "resources",
    resources = glob(
        ["resources/**"],
        exclude = [
            "resources/META-INF/**",
            "resources/update_include_files.sh",
        ],
    ),
    visibility = ["//visibility:private"],
)

java_library(
    name = "core",
    srcs = glob(
        ["java/**/*.java"],
    ) + [
        ":generated_lexer",
        ":generated_parser",
    ],
    deps = [
        ":resources",
        "//build_support:idea_plugin_api",
    ],
)

filegroup(
    name = "plugin_xml",
    srcs = ["resources/META-INF/plugin.xml"],
)

intellij_plugin_library(
    name = "plugin_library",
    plugin_xmls = ["resources/META-INF/plugin.xml"],
    deps = [":core"],
)

stamped_plugin_xml(
    name = "stamped_plugin_xml",
    plugin_id = "com.google.devtools.intellij.protoeditor",
    plugin_name = "Protocol Buffer Editor (base)",
    stamp_since_build = True,
    visibility = ["//visibility:private"],
)

intellij_plugin(
    name = "plugin_for_tests",
    testonly = 1,
    plugin_xml = ":stamped_plugin_xml",
    deps = [":plugin_library"],
)
