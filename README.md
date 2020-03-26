# Protocol Buffers support for IntelliJ

![Run Tests](https://github.com/jvolkman/intellij-protobuf-editor/workflows/Run%20Tests/badge.svg) 

IntelliJ [plugin](https://plugins.jetbrains.com/plugin/14004-protocol-buffer-editor) for editing Google [Protocol Buffers](https://developers.google.com/protocol-buffers). Features include:

* Support for `proto2` and `proto3`
* Syntax highlighting
* Code completion
* Semantic analysis
* References and navigation
* Quick documentation
* Editor enhancements (completion, brace matching, etc.)
* Navigating between protobuf files and some other languages (Java, Go, Python)
* Full support for text format, both standalone and in custom options

![Editor](doc/editor.png)

This is a fork of [google/intellij-protocol-buffer-editor](https://github.com/google/intellij-protocol-buffer-editor) which was released unsupported.

# Installation

Install from the JetBrains [plugin repository](https://plugins.jetbrains.com/plugin/14004-protocol-buffer-editor), or:
* Download a [release](https://github.com/jvolkman/intellij-protobuf-editor/releases) and install manually
* [Build](#building-and-testing) from source

# Building and Testing

This project uses [Bazel](https://bazel.build/).

To build `protobuf-editor.jar`:

```
bazel build //plugin
```

To run tests:
```
bazel test //...
```

# Path Settings

By default, the collection of project source roots is used as the protobuf search path, and the protobuf descriptor and
well-known type files are provided by the plugin JAR. These paths can be customized in the editor's language settings: 

![Settings](doc/settings.png)

To customize:
* Uncheck `Configure automatically`
* Add paths that include protobuf files
  * The `Prefix` column can be used to specify an import prefix for the path. So, for example, if the path is 
    `src/protos` and the prefix is `foo/bar`, the file at `src/protos/mine.proto` would be imported as
    `foo/bar/mine.proto`.
* Organize the paths in the proper resolution order. Files found in paths at the top of the list take precedence.


# Text Format

Protobuf Text Format is most commonly used to specify long-form option values in `.proto` files. For example, as seen
in the GRPC ecosystem:

![GRPC example](doc/grpc.png)

This plugin also supports standalone text format files with a `.textproto` or `.pb`. extension. Text formant by default
does not provide a way to associate a file with its schema (a `message` in a `.proto` file). But the plugin supports
the following comments in a text proto file:

```
# proto-file: path/to/file.proto
# proto-message: SomeMessage
# proto-import: path/to/file_with_extensions.proto
# proto-import: path/to/another_file_with_extensions.proto

foo: bar
``` 

Filenames are relative to configured roots (see [Settings](#path-settings)). The `proto-message` name is scoped 
relatively to the package declared in the `proto-file` file. `proto-message` follows the same resolution rules as type 
names in `.proto` files. 
