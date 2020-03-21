package(default_visibility = ["//:plugin"])

alias(
    name = "idea_ue_sdk",
    actual = "@idea_ue_2019_3//:sdk",
)

java_binary(
    name = 'grammar_kit',
    main_class = 'org.intellij.grammar.Main',
    runtime_deps = [
        '@grammar_kit//:grammar_kit_lib',
        ':idea_ue_sdk',
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
