package(default_visibility = ["//visibility:public"])

#
# Version-specific targets
#
alias(
    name = "intellij_sdk",
    actual = "@idea_ue_2020_2//:plugin_sdk"
)

alias(
    name = "_java_support_internal",
    actual = "@idea_ue_2020_2//:java_support"
)

alias(
    name = "_go_support_internal",
    actual = "@go_2020_2//:plugin_sdk"
)

alias(
    name = "_python_support_internal",
    actual = "@python_2020_2//:plugin_sdk"
)

#
# Tools
#

java_binary(
    name = 'grammar_kit',
    main_class = 'org.intellij.grammar.Main',
    runtime_deps = [
        '@grammar_kit//:grammar_kit_lib',
        ':intellij_sdk',
    ],
)

java_binary(
    name = 'idea_jflex',
    main_class = 'jflex.Main',
    runtime_deps = [
        '@idea_jflex//:idea_jflex_lib',
    ],
)

alias(
    name = "idea_jflex_skeleton",
    actual = "@idea_jflex_skeleton//file",
)

java_library(
    name = "test_libs",
    testonly = 1,
    exports = [
        "@truth//jar",
        "@mockito//jar",
        "@diffutils//jar",
    ],
)

#
# Derivative targets
#

java_library(
    name = "plugin_api",
    exports = [":intellij_sdk"],
    neverlink = 1,
)

java_library(
    name = "test_plugin_api",
    exports = [":intellij_sdk"],
    testonly = 1,
)

java_library(
    name = "plugin_java_support",
    exports = [":_java_support_internal"],
    neverlink = 1,
)

java_library(
    name = "test_plugin_java_support",
    exports = [":_java_support_internal"],
    testonly = 1,
)

java_library(
    name = "plugin_go_support",
    exports = [":_go_support_internal"],
    neverlink = 1,
)

java_library(
    name = "test_plugin_go_support",
    exports = [":_go_support_internal"],
    testonly = 1,
)

java_library(
    name = "plugin_python_support",
    exports = [":_python_support_internal"],
    neverlink = 1,
)

java_library(
    name = "test_plugin_python_support",
    exports = [":_python_support_internal"],
    testonly = 1,
)
