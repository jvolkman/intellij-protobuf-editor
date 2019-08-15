workspace(name = "protoeditor")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")
load("@bazel_tools//tools/build_defs/repo:jvm.bzl", "jvm_maven_import_external")

# Long-lived download links available at: https://www.jetbrains.com/intellij-repository/releases

# The plugin api for IntelliJ 2019.1. This is required to build IJwB,
# and run integration tests.
http_archive(
    name = "intellij_ce_2019_1",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.idea",
    sha256 = "e045751adabe2837203798270e1dc173128fe3e607e3025d4f8110c7ed4cc499",
    url = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIC/2019.1.2/ideaIC-2019.1.2.zip",
)

# The plugin api for IntelliJ 2019.2. This is required to build IJwB,
# and run integration tests.
http_archive(
    name = "intellij_ce_2019_2",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.idea192",
    sha256 = "9567f2a88c9d4c4a0495208914f07bd2dace78dad0fee31fb9f8a4adab3cc437",
    url = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIC/2019.2/ideaIC-2019.2.zip",
)

# The plugin api for IntelliJ UE 2019.1. This is required to run UE-specific
# integration tests.
http_archive(
    name = "intellij_ue_2019_1",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.ue",
    sha256 = "df6a1e6fbf77578b47163b96c83bc90a05bf043847c6e7c0bf285fe2e77d71e4",
    url = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIU/2019.1.2/ideaIU-2019.1.2.zip",
)

# The plugin api for IntelliJ UE 2019.2. This is required to run UE-specific
# integration tests.
http_archive(
    name = "intellij_ue_2019_2",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.ue192",
    sha256 = "c1a980c6eeb528ee731ed52a5821981466b9205713926748051ff08a4ce8cfaf",
    url = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIU/2019.2/ideaIU-2019.2.zip",
)

# The plugin api for CLion 2019.1. This is required to build CLwB,
# and run integration tests.
http_archive(
    name = "clion_2019_1",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.clion",
    sha256 = "6453a526fc832b3493338d5c53d976022daa6d20fb2c7e2012b440f1a8e7d313",
    url = "https://download.jetbrains.com/cpp/CLion-2019.1.3.tar.gz",
)

# The plugin api for CLion 2019.2. This is required to build CLwB,
# and run integration tests.
http_archive(
    name = "clion_2019_2",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.clion",
    sha256 = "e2d453264c2fb6dcc58e976fd5660157afd62ba5c0ee64f829407c772f7e1dcf",
    url = "https://download.jetbrains.com/cpp/CLion-2019.2.tar.gz",
)

python_build_file_content = "\n".join([
    "java_import(",
    "    name = 'python_internal',",
    "    jars = ['python-ce/lib/python-ce.jar'],",
    ")",
    "java_library(",
    "    name = 'python',",
    "    exports = [':python_internal'],",
    "    neverlink = True,",
    "    visibility = ['//visibility:public'],",
    ")",
    "java_library(",
    "    name = 'python_for_tests',",
    "    exports = [':python_internal'],",
    "    testonly = True,",
    "    visibility = ['//visibility:public'],",
    ")",
])

# Python plugin for Android Studio 3.4. Required at compile-time for python-specific features.
http_archive(
    name = "python_2018_3",
    build_file_content = python_build_file_content,
    sha256 = "095a2258f1707a8a1cd3c77f7c249d30f06cca2ca2738edba6c8befd92c0f763",
    url = "https://plugins.jetbrains.com/files/7322/58209/python-ce-2018.3.183.5912.2.zip",
)

# Python plugin for IntelliJ CE 2019.1. Required at compile-time for python-specific features.
http_archive(
    name = "python_2019_1",
    build_file_content = python_build_file_content,
    sha256 = "378002fa79623341a31bd3ac003506f04ac950d43313c8d413c6f0763826eadd",
    url = "https://plugins.jetbrains.com/files/7322/60398/python-ce-2019.1.191.6707.7.zip",
)

golang_build_file_content = "\n".join([
    "java_import(",
    "    name = 'go_internal',",
    "    jars = glob(['intellij-go/lib/*.jar']),",
    ")",
    "java_library(",
    "    name = 'go',",
    "    exports = [':go_internal'],",
    "    neverlink = True,",
    "    visibility = ['//visibility:public'],",
    ")",
    "java_library(",
    "    name = 'go_for_tests',",
    "    exports = [':go_internal'],",
    "    testonly = True,",
    "    visibility = ['//visibility:public'],",
    ")",
])

# Go plugin for IntelliJ UE. Required at compile-time for Bazel integration.
http_archive(
    name = "go_2019_1",
    build_file_content = golang_build_file_content,
    sha256 = "815f59dcd5f7db019e224cdb85e67db99c8d5deb99721a73a36f710bda64be49",
    url = "https://plugins.jetbrains.com/files/9568/62411/intellij-go-191.7141.44.205.zip",
)

# Go plugin for IntelliJ UE. Required at compile-time for Bazel integration.
http_archive(
    name = "go_2019_2",
    build_file_content = golang_build_file_content,
    sha256 = "704593665da45a6ad1dcd86f53d3959c6b0803dc0b0773b1b23864e48c9af289",
    url = "https://plugins.jetbrains.com/files/9568/66084/intellij-go-192.5728.86.268.zip",
)

# The plugin api for Android Studio 3.4. This is required to build ASwB,
# and run integration tests.
http_archive(
    name = "android_studio_3_4",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.android_studio34",
    sha256 = "35eb8c74837d1aab59229101fc91568a607ac04854a40209f7a0ba7ac0601924",
    url = "https://dl.google.com/dl/android/studio/ide-zips/3.4.2.0/android-studio-ide-183.5692245-linux.tar.gz",
)

# The plugin api for Android Studio 3.5. This is required to build ASwB,
# and run integration tests.
http_archive(
    name = "android_studio_3_5",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.android_studio",
    sha256 = "378202e844160eb498889f6f46700861b3beff48ff6df287864d8cfcb3d88947",
    url = "https://dl.google.com/dl/android/studio/ide-zips/3.5.0.17/android-studio-ide-191.5675373-linux.tar.gz",
)

# LICENSE: Common Public License 1.0
jvm_maven_import_external(
    name = "junit",
    artifact = "junit:junit:4.12",
    artifact_sha256 = "59721f0805e223d84b90677887d9ff567dc534d7c502ca903c0c2b17f05c116a",
    licenses = ["notice"],  # Common Public License 1.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "jsr305_annotations",
    artifact = "com.google.code.findbugs:jsr305:3.0.2",
    artifact_sha256 = "766ad2a0783f2687962c8ad74ceecc38a28b9f72a2d085ee438b7813e928d0c7",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "truth",
    artifact = "com.google.truth:truth:0.42",
    artifact_sha256 = "dd652bdf0c4427c59848ac0340fd6b6d20c2cbfaa3c569a8366604dbcda5214c",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "mockito",
    artifact = "org.mockito:mockito-core:1.10.19",
    artifact_sha256 = "d5831ee4f71055800821a34a3051cf1ed5b3702f295ffebd50f65fb5d81a71b8",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "objenesis",
    artifact = "org.objenesis:objenesis:1.3",
    artifact_sha256 = "dd4ef3d3091063a4fec578cbb2bbe6c1f921c00091ba2993dcd9afd25ff9444a",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "jarjar",
    artifact = "com.googlecode.jarjar:jarjar:1.3",
    artifact_sha256 = "4225c8ee1bf3079c4b07c76fe03c3e28809a22204db6249c9417efa4f804b3a7",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "auto_value",
    artifact = "com.google.auto.value:auto-value:1.6.2",
    artifact_sha256 = "edbe65a5c53e3d4f5cb10b055d4884ae7705a7cd697be4b2a5d8427761b8ba12",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "auto_value_annotations",
    artifact = "com.google.auto.value:auto-value-annotations:1.6.2",
    artifact_sha256 = "b48b04ddba40e8ac33bf036f06fc43995fc5084bd94bdaace807ce27d3bea3fb",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

jvm_maven_import_external(
    name = "error_prone_annotations",
    artifact = "com.google.errorprone:error_prone_annotations:2.3.0",
    artifact_sha256 = "524b43ea15ca97c68f10d5f417c4068dc88144b620d2203f0910441a769fd42f",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["http://central.maven.org/maven2"],
)

http_archive(
    name = "bazel_skylib",
    sha256 = "2ef429f5d7ce7111263289644d233707dba35e39696377ebab8b0bc701f7818e",
    url = "https://github.com/bazelbuild/bazel-skylib/releases/download/0.8.0/bazel-skylib.0.8.0.tar.gz",
)

# LICENSE: The Apache Software License, Version 2.0
# proto_library rules implicitly depend on @com_google_protobuf//:protoc
http_archive(
    name = "com_google_protobuf",
    sha256 = "98e615d592d237f94db8bf033fba78cd404d979b0b70351a9e5aaff725398357",
    strip_prefix = "protobuf-3.9.1",
    url = "https://github.com/protocolbuffers/protobuf/archive/v3.9.1.tar.gz",
)

http_archive(
    name = "com_google_protobuf_javalite",
    sha256 = "b04b08d31208be32aafdf5842d1b6073d527a67ff8d2cf4b17ee8f22a5273758",
    strip_prefix = "protobuf-fa08222434bc58d743e8c2cc716bc219c3d0f44e",
    url = "https://github.com/protocolbuffers/protobuf/archive/fa08222434bc58d743e8c2cc716bc219c3d0f44e.tar.gz",
)

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
protobuf_deps()

http_archive(
    name = "build_stack_rules_proto",
    urls = ["https://github.com/stackb/rules_proto/archive/d9a123032f8436dbc34069cfc3207f2810a494ee.tar.gz"],
    sha256 = "85ccc69a964a9fe3859b1190a7c8246af2a4ead037ee82247378464276d4262a",
    strip_prefix = "rules_proto-d9a123032f8436dbc34069cfc3207f2810a494ee",
)

load("@build_stack_rules_proto//python:deps.bzl", "python_proto_compile")
python_proto_compile()

http_archive(
    name = "io_bazel_rules_go",
    urls = [
        "https://storage.googleapis.com/bazel-mirror/github.com/bazelbuild/rules_go/releases/download/0.19.1/rules_go-0.19.1.tar.gz",
        "https://github.com/bazelbuild/rules_go/releases/download/0.19.1/rules_go-0.19.1.tar.gz",
    ],
    sha256 = "8df59f11fb697743cbb3f26cfb8750395f30471e9eabde0d174c3aebc7a1cd39",
)

load("@io_bazel_rules_go//go:deps.bzl", "go_rules_dependencies", "go_register_toolchains")
go_rules_dependencies()
go_register_toolchains()

# jflex for IDEA
jvm_maven_import_external(
    name = "idea_jflex",
    artifact = "org.jetbrains.intellij.deps.jflex:jflex:1.7.0-2",
    artifact_sha256 = "8a436449844a9ed4e6d371f3276f571dd116512b089ec84d478ed93ae9aa8d49",
    licenses = ["notice"],  # BSD
    server_urls = ["https://jetbrains.bintray.com/intellij-third-party-dependencies"],
    generated_rule_name = "idea_jflex_lib",
    extra_build_file_content = "\n".join([
        "java_binary(",
        "    name = 'idea_jflex',",
        "    main_class = 'jflex.Main',",
        "    runtime_deps = [",
        "        ':idea_jflex_lib',",
        "    ],",
        "    visibility = ['//visibility:public'],",
        ")",
    ])
)

http_file(
    name = "idea_jflex_skeleton",
    executable = 0,
    urls = [
        "https://raw.githubusercontent.com/JetBrains/intellij-community/123242c4b23a90f7910193139aa4d3b2979d9647/tools/lexer/idea-flex.skeleton"
    ],
    sha256 = "21231d89407d33812bda5467489aa4774bdfc285ef18623cc764aced405c0ed1",
)

http_archive(
    name = "grammar_kit",
    build_file_content = "\n".join([
        "java_import(",
        "    name = 'grammar_kit_lib',",
        "    jars = glob(['grammar-kit/lib/*.jar']),",
        "    visibility = ['//visibility:public'],",
        ")",
        "java_binary(",
        "    name = 'grammar_kit',",
        "    main_class = 'org.intellij.grammar.Main',",
        "    runtime_deps = [",
        "        ':grammar_kit_lib',",
        "        '@intellij_ce_2019_1//:sdk',",
        "    ],",
        "    visibility = ['//visibility:public'],",
        ")",
    ]),
    sha256 = "de61e6c9b645b8401f37b65f248c0df20da6c151a61d720a806f6894b38dfc84",
    url = "https://github.com/JetBrains/Grammar-Kit/releases/download/2019.1/grammar-kit-2019.1.zip",
)
