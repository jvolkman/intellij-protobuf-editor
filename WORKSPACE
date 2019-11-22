workspace(name = "protoeditor")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")
load("@bazel_tools//tools/build_defs/repo:jvm.bzl", "jvm_maven_import_external")

# Long-lived download links available at: https://www.jetbrains.com/intellij-repository/releases

# The plugin api for IntelliJ 2019.2. This is required to build IJwB,
# and run integration tests.
http_archive(
    name = "intellij_ce_2019_2",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.idea192",
    sha256 = "fed481dfbd44a0717ab544c4c09c8bf5c037c50e39897551abeec81328cda9f7",
    url = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIC/2019.2.4/ideaIC-2019.2.4.zip",
)

# The plugin api for IntelliJ 2019.3. This is required to build IJwB,
# and run integration tests.
http_archive(
    name = "intellij_ce_2019_3",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.idea192",
    sha256 = "5bd69fc7588f0845facc7a0cf6c6b55d63217b19d8687e3cc6d7405e24a0db79",
    strip_prefix = "idea-IC-193.4386.10",
    url = "https://download.jetbrains.com/idea/ideaIC-193.4386.10.tar.gz",
)

# The plugin api for IntelliJ UE 2019.2. This is required to run UE-specific
# integration tests.
http_archive(
    name = "intellij_ue_2019_2",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.ue192",
    sha256 = "cc6f68dedf6b0bc7b7d8f5be812b216605991ad5ab014147e0cd74cd2b9ea4ae",
    url = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIU/2019.2.4/ideaIU-2019.2.4.zip",
)

# The plugin api for IntelliJ UE 2019.3. This is required to run UE-specific
# integration tests.
http_archive(
    name = "intellij_ue_2019_3",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.ue192",
    sha256 = "5bbaeaa580ae38622b8c6e9188551726f7d82fccde8ef1743cb141d902235e66",
    strip_prefix = "idea-IU-193.4386.10",
    url = "https://download.jetbrains.com/idea/ideaIU-193.4386.10.tar.gz",
)

# The plugin api for CLion 2019.2. This is required to build CLwB,
# and run integration tests.
http_archive(
    name = "clion_2019_2",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.clion",
    sha256 = "aedec47a538c9a2b654c30719ed7f0111af0a708a2aeb2a9f36a1c0767841a5c",
    url = "https://download.jetbrains.com/cpp/CLion-2019.2.5.tar.gz",
)

# The prerelease plugin api for CLion 2019.3. This is required to build CLwB,
# and run integration tests.
http_archive(
    name = "clion_2019_3",
    build_file = "@//build_support/intellij_platform_sdk:BUILD.clion193",
    sha256 = "ddfa2bc7ab0d316463f66146e56394de9258260f82123d74b825e6e56e556c35",
    url = "https://download.jetbrains.com/cpp/CLion-193.5233.10.tar.gz",
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

# Python plugin for IntelliJ CE 2019.2. Required at compile-time for python-specific features.
http_archive(
    name = "python_2019_2",
    build_file_content = python_build_file_content,
    sha256 = "c0d970d4b8034fbe1a1c705a59e2d6321ec032ae38c65535493dc1ec5c8aeec5",
    url = "https://plugins.jetbrains.com/files/7322/66012/python-ce.zip",
)

# Python plugin for IntelliJ CE 2019.3. Required at compile-time for python-specific features.
http_archive(
    name = "python_2019_3",
    build_file_content = python_build_file_content,
    sha256 = "21b2bd88c594bc58d8e8062c845be3bee965fc4dff2da9521158a6da3ab5b825",
    url = "https://plugins.jetbrains.com/files/7322/70397/python-ce.zip",
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
    name = "go_2019_2",
    build_file_content = golang_build_file_content,
    sha256 = "c19195a5979a0d5361ab9d1e82beeeb40c6f9eebaae504d07790a9f3afee4478",
    url = "https://plugins.jetbrains.com/files/9568/68228/intellij-go-192.6603.23.335.zip",
)

# Go plugin for IntelliJ UE. Required at compile-time for Bazel integration.
http_archive(
    name = "go_2019_3",
    build_file_content = golang_build_file_content,
    sha256 = "b80126de5f2011e506943cbc1959f2af14d206a000812ee67ffef3c2d59380ef",
    url = "https://plugins.jetbrains.com/files/9568/70301/intellij-go-193.4386.1.538.zip",
)

# The plugin api for Android Studio 3.5. This is required to build ASwB,
# and run integration tests.
http_archive(
    name = "android_studio_3_5",
    build_file = "@//intellij_platform_sdk:BUILD.android_studio",
    sha256 = "94fc392a148480a67299d83c1faaabc56db27188194748433534cf8b5ca4dd29",
    url = "https://dl.google.com/dl/android/studio/ide-zips/3.5.1.0/android-studio-ide-191.5900203-linux.tar.gz",
)

http_archive(
    name = "android_studio_3_6",
    build_file = "@//intellij_platform_sdk:BUILD.android_studio36",
    sha256 = "c1e98b7cf5abe56d7e1206d7284c1ff3ed6f2db8cfd56033a1617d09b2c9de73",
    url = "https://dl.google.com/dl/android/studio/ide-zips/3.6.0.16/android-studio-ide-192.5994180-linux.tar.gz",
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
        "        '@intellij_ce_2019_3//:sdk',",
        "    ],",
        "    visibility = ['//visibility:public'],",
        ")",
    ]),
    sha256 = "a33f9732b8bed61e509a8282e9a9ae72130ab5e7e18a449eb60cf00bb142018d",
    url = "https://github.com/JetBrains/Grammar-Kit/releases/download/2019.3/grammar-kit-2019.3.zip",
)
