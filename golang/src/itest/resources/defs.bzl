"""Some macros for building go test data."""

load("//testlib:expose_genfile.bzl", "expose_genfile")
load("@io_bazel_rules_go//go:def.bzl", "go_library")
load("@io_bazel_rules_go//proto:def.bzl", "go_proto_library")

def pb_go_proto_library(name, proto, genfile, visibility = None):
    go_proto_library(
        name = name,
        proto = proto,
        importpath = native.package_name() + "/" + name,
        compilers = ["@io_bazel_rules_go//proto:gogo_proto"],
        visibility = visibility,
    )
    native.filegroup(
        name = name + "_src",
        srcs = [":" + name],
        output_group = "go_generated_srcs",
    )
    expose_genfile(
        name = name + "_exposed_src",
        genfile = genfile,
        genfile_orig = name + "/" + genfile,
        deps = [":" + name + "_src"],
    )

def pb_go_library(**kwargs):
    importpath = native.package_name() + "/" + kwargs["name"]
    go_library(importpath = importpath, **kwargs)

def resources_package_name():
    name = native.package_name()
    if not name.endswith("/resources"):
        name = name + "/resources"
    return name

def resources_import_prefix():
    return ""
