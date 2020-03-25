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
        "//build_support:test_libs",
        "//build_support:test_plugin_api",
#        "@jsr305_annotations//jar",
    ],
)

exports_files([
    "test_defs.bzl",
])
