# Common code for testing links from generated code -> proto.

licenses(["notice"])  # Apache 2.0

package(default_visibility = [
    "//:plugin_private",
])

# .proto template for generated code tests (e.g., java <-> proto)
filegroup(
    name = "proto_template",
    srcs = ["src/main/resources/ProtoTest.prototemplate"],
)

java_library(
    name = "gencodeutils",
    testonly = True,
    srcs = glob(["src/main/java/com/google/devtools/intellij/protoeditor/gencodeutils/**/*.java"]),
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
        "//build_support/intellij_platform_sdk:test_libs",
        "//core",
    ],
)

java_library(
    name = "test_utils",
    testonly = True,
    srcs = [
        "src/main/java/com/google/devtools/intellij/protoeditor/TestUtils.java",
        "src/main/java/com/google/devtools/intellij/protoeditor/fixtures/PbCodeInsightFixtureTestCase.java",
    ],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
        "//build_support/intellij_platform_sdk:test_libs",
        "//core",
        "@junit//jar",
    ],
)

exports_files(["expose_genfile.bzl"])
