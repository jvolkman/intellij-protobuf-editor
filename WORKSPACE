workspace(name = "protoeditor")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")
load("@bazel_tools//tools/build_defs/repo:jvm.bzl", "jvm_maven_import_external")

# Long-lived download links available at: https://www.jetbrains.com/intellij-repository/releases

# The plugin api for IntelliJ UE 2019.3. This is required to run UE-specific
# integration tests.
http_archive(
    name = "idea_ue_2019_3",
    build_file = "@//build_support/external:BUILD.idea_ue",
    sha256 = "c02e4202e94957e1e659be60ca795f9827b3069486612de8e8bc07291a9b64c4",
    strip_prefix = "idea-IU-193.6911.18",
    url = "https://download-cf.jetbrains.com/idea/ideaIU-2019.3.4-no-jbr.tar.gz",
)

# Python plugin for IntelliJ CE 2019.3. Required at compile-time for python-specific features.
http_archive(
    name = "python_2019_3",
    build_file = "@//build_support/external:BUILD.idea_python",
    sha256 = "432cc656fcfe99c25450fd9ea085b5d9dad754c478cee0059a19fac26636751f",
    url = "https://plugins.jetbrains.com/files/631/81167/python.zip",
)

# Python plugin for IntelliJ CE 2019.3. Required at compile-time for python-specific features.
http_archive(
    name = "python_ce_2019_3",
    build_file = "@//build_support/external:BUILD.idea_python_ce",
    sha256 = "53edd3c8d05151e9cb735cbbfd9c9a294426d1701be40147682529f1acd3962c",
    url = "https://plugins.jetbrains.com/files/7322/81171/python-ce.zip",
)

# Go plugin for IntelliJ UE. Required at compile-time for Bazel integration.
http_archive(
    name = "go_2019_3",
    build_file = "@//build_support/external:BUILD.idea_go",
    sha256 = "cf6a6116d49ee0a57c0fa5601d943b71c1965f2fc3e171b34fbc39816bc70c50",
    url = "https://plugins.jetbrains.com/files/9568/79857/intellij-go-193.6494.35.125.zip",
)

# jflex for IDEA
jvm_maven_import_external(
    name = "idea_jflex",
    artifact = "org.jetbrains.intellij.deps.jflex:jflex:1.7.0-2",
    artifact_sha256 = "8a436449844a9ed4e6d371f3276f571dd116512b089ec84d478ed93ae9aa8d49",
    licenses = ["notice"],  # BSD
    server_urls = ["https://jetbrains.bintray.com/intellij-third-party-dependencies"],
    generated_rule_name = "idea_jflex_lib",
)

# jflex skeleton
http_file(
    name = "idea_jflex_skeleton",
    executable = 0,
    urls = [
        "https://raw.githubusercontent.com/JetBrains/intellij-community/843d74524f93e65227cf0321b078227fa911a7c1/tools/lexer/idea-flex.skeleton"
    ],
    sha256 = "c6fa83affcbe6cec9c7345fbeccc6dfc6f32341ab9fdec42d7f2c3e008ae66d3",
)

http_archive(
    name = "grammar_kit",
    build_file = "//build_support/external:BUILD.grammar_kit",
    sha256 = "a33f9732b8bed61e509a8282e9a9ae72130ab5e7e18a449eb60cf00bb142018d",
    url = "https://github.com/JetBrains/Grammar-Kit/releases/download/2019.3/grammar-kit-2019.3.zip",
)

jvm_maven_import_external(
    name = "truth",
    artifact = "com.google.truth:truth:1.0.1",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["https://repo.maven.apache.org/maven2"],
)

jvm_maven_import_external(
    name = "diffutils",
    artifact = "com.googlecode.java-diff-utils:diffutils:1.3.0",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["https://repo.maven.apache.org/maven2"],
)

jvm_maven_import_external(
    name = "mockito",
    artifact = "org.mockito:mockito-core:1.10.19",
    artifact_sha256 = "d5831ee4f71055800821a34a3051cf1ed5b3702f295ffebd50f65fb5d81a71b8",
    licenses = ["notice"],  # Apache 2.0
    server_urls = ["https://repo.maven.apache.org/maven2"],
)

# LICENSE: The Apache Software License, Version 2.0
# proto_library rules implicitly depend on @com_google_protobuf//:protoc
http_archive(
    name = "com_google_protobuf",
    sha256 = "8e0e89a3670df2fbc2d9cedd76bb71b08027d806b81bf363ff434f5762520ed6",
    strip_prefix = "protobuf-3.11.0-rc1",
    url = "https://github.com/protocolbuffers/protobuf/archive/v3.11.0-rc1.tar.gz",
)

http_archive(
    name = "com_google_protobuf_javalite",
    sha256 = "d8b0e0ca38724ffe667c7db4803b8315690c7bb26bcf1c5b6904d0d4639495ff",
    strip_prefix = "protobuf-7b64714af67aa967dcf941df61fe5207975966be",
    url = "https://github.com/protocolbuffers/protobuf/archive/7b64714af67aa967dcf941df61fe5207975966be.tar.gz",
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
        "https://storage.googleapis.com/bazel-mirror/github.com/bazelbuild/rules_go/releases/download/v0.20.2/rules_go-v0.20.2.tar.gz",
        "https://github.com/bazelbuild/rules_go/releases/download/v0.20.2/rules_go-v0.20.2.tar.gz",
    ],
    sha256 = "b9aa86ec08a292b97ec4591cf578e020b35f98e12173bbd4a921f84f583aebd9",
)

load("@io_bazel_rules_go//go:deps.bzl", "go_rules_dependencies", "go_register_toolchains")
go_rules_dependencies()
go_register_toolchains()
