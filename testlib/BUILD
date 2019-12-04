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
        "src/main/java/com/google/idea/testing/BlazeTestSystemPropertiesRule.java",
        "src/main/java/com/google/idea/testing/TestUtils.java",
    ],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
        "//build_support/intellij_platform_sdk:test_libs",
        "//core",
        "@junit//jar",
        "@jsr305_annotations//jar",
    ],
)

java_library(
    name = "test_support",
    testonly = True,
    srcs = [
        "src/main/java/com/google/idea/testing/BlazeTestSystemPropertiesRule.java",
        "src/main/java/com/google/idea/testing/TestUtils.java",
    ],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api_for_tests",
#        "//build_support/intellij_platform_sdk:test_libs",
        "@jsr305_annotations//jar",
    ],
)

exports_files([
    "expand_template.bzl",
    "expose_genfile.bzl",
    "test_defs.bzl",
])
