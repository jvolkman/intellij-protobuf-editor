# Common code for testing links from generated code -> proto.

licenses(["notice"])  # Apache 2.0

package(default_visibility = [
    "//:plugin",
])

# .proto template for generated code tests (e.g., java <-> proto)
filegroup(
    name = "proto_template",
    srcs = ["src/main/resources/ProtoTest.prototemplate"],
)

java_library(
    name = "gencodeutils",
    testonly = True,
    srcs = glob(["src/main/java/idea/plugin/protoeditor/gencodeutils/**/*.java"]),
    deps = [
        "//build_support:test_plugin_api",
        "//build_support:test_libs",
        "//core",
    ],
)

java_library(
    name = "test_utils",
    testonly = True,
    srcs = [
        "src/main/java/idea/plugin/protoeditor/TestUtils.java",
        "src/main/java/idea/plugin/protoeditor/fixtures/PbCodeInsightFixtureTestCase.java",
    ],
    deps = [
        "//build_support:test_plugin_api",
        "//build_support:test_libs",
        "//core",
    ],
)

exports_files([
    "expand_template.bzl",
    "expose_genfile.bzl",
])
