# Base code for mapping between .proto and generated code (e.g., Java)

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:private"])

java_library(
    name = "gencode",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//:plugin"],
    deps = [
        "//build_support/intellij_platform_sdk:plugin_api",
        "//core",
    ],
)
