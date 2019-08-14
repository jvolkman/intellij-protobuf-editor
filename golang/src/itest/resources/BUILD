# Description: IntelliJ protobuf plugin - Go language support integration test resources.
#

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

load("//testlib:expand_template.bzl", "expand_template")
load("//testlib:expose_genfile.bzl", "expose_genfile")
load(":defs.bzl", "pb_go_library", "pb_go_proto_library", "resources_import_prefix", "resources_package_name")

filegroup(
    name = "resources",
    srcs = [
        ".testdata",
        "proto2.pb.go",
        "proto2.proto",
        "proto2_user.go",
        "proto3.pb.go",
        "proto3.proto",
        "proto3_user.go",
    ],
    visibility = ["//golang/src/itest:__pkg__"],
)

exports_files(["META-INF/golang_optional.xml"])

# We fill in group fields via a place holder, instead of hardcoding it, since
# proto3 syntax doesn't allow group fields.
GROUP_FIELD_VALUE = "optional group SingleGroupField = 99 { optional int32 in_group = 100; }"

expand_template(
    name = "proto2",
    out = "proto2.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto2",
        "%PROTO_PACKAGE%": "com.proto.test.golang.proto2",
        "%FILE_OPTIONS%": "option go_package = 'proto2_go_proto';",
        "%SINGLE_FIELD_LABEL%": "optional",
        "%GROUP_FIELD%": GROUP_FIELD_VALUE,
    },
    template = "//testlib:proto_template",
)

expand_template(
    name = "proto2_user",
    out = "proto2_user.go",
    substitutions = {
        "%PROTO_PACKAGE_NAME%": resources_import_prefix() + resources_package_name(),
    },
    template = "proto2_user.go.tmpl",
)

proto_library(
    name = "proto2_proto",
    srcs = [":proto2.proto"],
)

pb_go_proto_library(
    name = "proto2_go_proto",
    genfile = "proto2.pb.go",
    proto = ":proto2_proto",
)

pb_go_library(
    name = "proto2_user_lib",
    srcs = ["proto2_user.go"],
    deps = [":proto2_go_proto"],
)

expand_template(
    name = "proto3",
    out = "proto3.proto",
    substitutions = {
        "%SYNTAX_VALUE%": "proto3",
        "%PROTO_PACKAGE%": "com.proto.test.golang.proto3",
        "%FILE_OPTIONS%": "option go_package = 'proto3_go_proto';",
        "%SINGLE_FIELD_LABEL%": "",
        "%GROUP_FIELD%": "",
    },
    template = "//testlib:proto_template",
)

expand_template(
    name = "proto3_user",
    out = "proto3_user.go",
    substitutions = {
        "%PROTO_PACKAGE_NAME%": resources_import_prefix() + resources_package_name(),
    },
    template = "proto3_user.go.tmpl",
)

proto_library(
    name = "proto3_proto",
    srcs = [":proto3.proto"],
)

pb_go_proto_library(
    name = "proto3_go_proto",
    genfile = "proto3.pb.go",
    proto = ":proto3_proto",
)

pb_go_library(
    name = "proto3_user_lib",
    srcs = ["proto3_user.go"],
    deps = [":proto3_go_proto"],
)

exports_files(["defs.bzl"])
