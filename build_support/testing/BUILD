licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:public"])

java_library(
    name = "test_support",
    testonly = True,
    srcs = [
        "src/main/java/com/google/idea/testing/BlazeTestSystemPropertiesRule.java",
        "src/main/java/com/google/idea/testing/TestUtils.java",
    ],
    deps = [
        "//build_support:idea_plugin_api",
#        "//build_support/intellij_platform_sdk:test_libs",
#        "@jsr305_annotations//jar",
    ],
)

exports_files([
    "test_defs.bzl",
])
