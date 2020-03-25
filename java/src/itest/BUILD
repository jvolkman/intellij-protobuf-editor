# Description: IntelliJ protobuf plugin - Java language support integration tests.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

load(
    "//build_support/build_defs:build_defs.bzl",
    "stamped_plugin_xml",
    "intellij_plugin",
)
load(
    "//build_support/testing:test_defs.bzl",
    "intellij_integration_test_suite",
)

stamped_plugin_xml(
    name = "stamped_plugin_xml",
    plugin_id = "idea.plugin.protoeditor.java",
    plugin_name = "Protocol Buffer Editor w/ Java Support",
    stamp_since_build = True,
)

intellij_plugin(
    name = "plugin_for_tests",
    plugin_xml = ":stamped_plugin_xml",
    deps = [
        "//core:plugin_library",
        "//java:plugin_library",
    ],
)

intellij_integration_test_suite(
    name = "integration_tests",
    srcs = glob(["java/**/*.java"]),
    data = glob(["resources/**/*"]) + [
        ":Proto2.proto",
        ":Proto2Java",
        ":Proto2Lite.proto",
        ":Proto2LiteJava",
        ":Proto2MultipleFiles.proto",
        ":Proto2MultipleFilesJava",
        ":Proto2OuterClass.proto",
        ":Proto2OuterClassJava",
        ":ProtoSyntax3.proto",
        ":ProtoSyntax3Java",
        ":ClashingEnumUser",
        ":ClashingMessageUser",
        ":ClashingNestedEnumUser",
        ":ClashingNestedMessageUser",
        ":NotClashingFieldUser",
    ],
    jvm_flags = ["-Dtest.resources.location=" + package_name() + "/resources/"],
    required_plugins = "idea.plugin.protoeditor.java",
    test_package_root = "idea.plugin.protoeditor",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":plugin_for_tests",
    ],
    deps = [
        "//build_support:test_plugin_api",
        "//build_support:test_plugin_java_support",
        "//build_support:test_libs",
        "//core",
        "//java",
        "//testlib:gencodeutils",
        "//testlib:test_utils",
    ],
)

# Generate some java code from protos for testing.
load("//testlib:expand_template.bzl", "expand_template")

# We fill in group fields via a place holder, instead of hardcoding it, since
# proto3 syntax doesn't allow group fields.
GROUP_FIELD_VALUE = "optional group SingleGroupField = 99 { optional int32 in_group = 100; }"

expand_template(
    name = "Proto2",
    out = "Proto2.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto2",
        "%PROTO_PACKAGE%": "com.proto.pack.api_v2",
        "%FILE_OPTIONS%": "option java_package = \"com.test.api_v2\";",
        "%SINGLE_FIELD_LABEL%": "optional",
        "%GROUP_FIELD%": GROUP_FIELD_VALUE,
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "Proto2Lib",
    srcs = [":Proto2"],
)

java_proto_library(
    name = "Proto2Lib_java_proto",
    deps = [":Proto2Lib"],
)

java_library(
    name = "Proto2Java",
    srcs = ["resources/java/Proto2User.java"],
    deps = [
        ":Proto2Lib_java_proto",
        "@com_google_protobuf//:protobuf_java",
        "@com_google_protobuf//:protobuf_lite",
    ],
)

expand_template(
    name = "Proto2Lite",
    out = "Proto2Lite.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto2",
        "%PROTO_PACKAGE%": "com.proto.pack.api_v2_lite",
        "%FILE_OPTIONS%": "option java_package = \"com.test.api_v2_lite\";",
        "%SINGLE_FIELD_LABEL%": "optional",
        "%GROUP_FIELD%": GROUP_FIELD_VALUE,
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "Proto2LiteLib",
    srcs = [":Proto2Lite"],
)

java_lite_proto_library(
    name = "Proto2LiteLib_java_proto",
    deps = [":Proto2LiteLib"],
)

java_library(
    name = "Proto2LiteJava",
    srcs = ["resources/java/Proto2LiteUser.java"],
    deps = [":Proto2LiteLib_java_proto"],
)

expand_template(
    name = "Proto2MultipleFiles",
    out = "Proto2MultipleFiles.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto2",
        "%PROTO_PACKAGE%": "com.proto.pack.api_v2_multiple",
        "%FILE_OPTIONS%": "option java_package = \"com.test.api_v2_multiple\";\n" +
                          "option java_multiple_files = true;",
        "%SINGLE_FIELD_LABEL%": "optional",
        "%GROUP_FIELD%": GROUP_FIELD_VALUE,
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "Proto2MultipleFilesLib",
    srcs = [":Proto2MultipleFiles"],
)

java_proto_library(
    name = "Proto2MultipleFilesLib_java_proto",
    deps = [":Proto2MultipleFilesLib"],
)

java_library(
    name = "Proto2MultipleFilesJava",
    srcs = ["resources/java/Proto2MultipleFilesUser.java"],
    deps = [":Proto2MultipleFilesLib_java_proto"],
)

expand_template(
    name = "Proto2OuterClass",
    out = "Proto2OuterClass.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto2",
        "%PROTO_PACKAGE%": "com.proto.pack.api_v2_outer_class",
        "%FILE_OPTIONS%": "option java_package = \"com.test.api_v2_outer_class\";\n" +
                          "option java_outer_classname = \"MyClass\";",
        "%SINGLE_FIELD_LABEL%": "optional",
        "%GROUP_FIELD%": GROUP_FIELD_VALUE,
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "Proto2OuterClassLib",
    srcs = [":Proto2OuterClass"],
)

java_proto_library(
    name = "Proto2OuterClassLib_java_proto",
    deps = [":Proto2OuterClassLib"],
)

java_library(
    name = "Proto2OuterClassJava",
    srcs = ["resources/java/Proto2OuterClassUser.java"],
    deps = [":Proto2OuterClassLib_java_proto"],
)

expand_template(
    name = "ProtoSyntax3",
    out = "ProtoSyntax3.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto3",
        "%PROTO_PACKAGE%": "com.proto.pack.syntax3",
        "%FILE_OPTIONS%": "option java_package = \"com.test.syntax3\";",
        "%SINGLE_FIELD_LABEL%": "",
        "%GROUP_FIELD%": "",
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "ProtoSyntax3Lib",
    srcs = [":ProtoSyntax3"],
)

java_proto_library(
    name = "ProtoSyntax3Lib_java_proto",
    deps = [":ProtoSyntax3Lib"],
)

java_library(
    name = "ProtoSyntax3Java",
    srcs = ["resources/java/ProtoSyntax3User.java"],
    deps = [":ProtoSyntax3Lib_java_proto"],
)

proto_library(
    name = "ClashingEnum",
    srcs = ["resources/proto/clashing_enum.proto"],
)

java_proto_library(
    name = "ClashingEnum_java_proto",
    deps = [":ClashingEnum"],
)

java_library(
    name = "ClashingEnumUser",
    srcs = ["resources/java/ClashingEnumUser.java"],
    deps = [":ClashingEnum_java_proto"],
)

proto_library(
    name = "ClashingMessage",
    srcs = ["resources/proto/clashing_message.proto"],
)

java_proto_library(
    name = "ClashingMessage_java_proto",
    deps = [":ClashingMessage"],
)

java_library(
    name = "ClashingMessageUser",
    srcs = ["resources/java/ClashingMessageUser.java"],
    deps = [":ClashingMessage_java_proto"],
)

proto_library(
    name = "ClashingNestedEnum",
    srcs = ["resources/proto/clashing_nested_enum.proto"],
)

java_proto_library(
    name = "ClashingNestedEnum_java_proto",
    deps = [":ClashingNestedEnum"],
)

java_library(
    name = "ClashingNestedEnumUser",
    srcs = ["resources/java/ClashingNestedEnumUser.java"],
    deps = [":ClashingNestedEnum_java_proto"],
)

proto_library(
    name = "ClashingNestedMessage",
    srcs = ["resources/proto/clashing_nested_message.proto"],
)

java_proto_library(
    name = "ClashingNestedMessage_java_proto",
    deps = [":ClashingNestedMessage"],
)

java_library(
    name = "ClashingNestedMessageUser",
    srcs = ["resources/java/ClashingNestedMessageUser.java"],
    deps = [":ClashingNestedMessage_java_proto"],
)

proto_library(
    name = "NotClashingField",
    srcs = ["resources/proto/not_clashing_field.proto"],
)

java_proto_library(
    name = "NotClashingField_java_proto",
    deps = [":NotClashingField"],
)

java_library(
    name = "NotClashingFieldUser",
    srcs = ["resources/java/NotClashingFieldUser.java"],
    deps = [":NotClashingField_java_proto"],
)
