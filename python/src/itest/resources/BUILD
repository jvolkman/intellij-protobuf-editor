# Description: IntelliJ protobuf plugin - Python language support integration test resources.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

load("//testlib:expand_template.bzl", "expand_template")
load("//testlib:expose_genfile.bzl", "expose_genfile")
load("@build_stack_rules_proto//python:python_proto_compile.bzl", py_proto_library = "python_proto_compile")

filegroup(
    name = "resources",
    srcs = glob(["**/*"]) + [
        ":proto2.proto",
        ":proto2_py_pb2",
        ":proto3.proto",
        ":proto3_py_pb2",
    ],
    visibility = ["//python/src/itest:__pkg__"],
)

# Generate some python code from protos for testing.

# We fill in group fields via a place holder, instead of hardcoding it, since
# proto3 syntax doesn't allow group fields.
GROUP_FIELD_VALUE = "optional group SingleGroupField = 99 { optional int32 in_group = 100; }"

expand_template(
    name = "proto2_proto",
    out = "proto2.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto2",
        "%PROTO_PACKAGE%": "com.proto.py.api_v2",
        "%FILE_OPTIONS%": "",
        "%SINGLE_FIELD_LABEL%": "optional",
        "%GROUP_FIELD%": GROUP_FIELD_VALUE,
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "proto2_lib",
    srcs = [":proto2_proto"],
)

py_proto_library(
    name = "proto2_py_pb2",
    deps = [":proto2_lib"],
)

expand_template(
    name = "proto3_proto",
    out = "proto3.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto3",
        "%PROTO_PACKAGE%": "com.proto.py.syntax3",
        "%FILE_OPTIONS%": "",
        "%SINGLE_FIELD_LABEL%": "",
        "%GROUP_FIELD%": "",
    },
    template = "//testlib:proto_template",
)

proto_library(
    name = "proto3_lib",
    srcs = [":proto3_proto"],
)

py_proto_library(
    name = "proto3_py_pb2",
    deps = [":proto3_lib"],
)
