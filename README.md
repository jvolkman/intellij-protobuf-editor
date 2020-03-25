# Protocol Buffers support for IntelliJ

This plugin provides editor support for Protocol Buffer files, including text
format.

This is a fork of an editor developed internally at Google and released unsupported 
[here](https://github.com/google/intellij-protocol-buffer-editor).

# Features

![Editor](doc/editor.png)

* Support for `proto2` and `proto3`
* Syntax highlighting
* Completion
* Semantic analysis
* References and navigation
* Quick documentation
* Editor enhancements (completion, brace matching, etc.)
* Navigating between protobuf files and some other languages (Java, Go, Python)
* Full support for text format, both standalone and in custom options

## Text Format

Protobuf Text Format is most commonly used to specify long-form option values in `.proto` files. For example, as seen
in the GRPC ecosystem:

![GRPC example](doc/grpc.png)

This plugin also supports standalone text format files with a `.textproto` or `.pb.` extension. Text formant by default
does not provide a way to associate a file with its schema (a `message` in a `.proto` file). But the plugin supports
the following comments in a text proto file:

```
# proto-file: path/to/file.proto
# proto-message: SomeMessage
# proto-import: path/to/file_with_extensions.proto
# proto-import: path/to/another_file_with_extensions.proto

foo: bar
``` 

Filenames are relative to configured roots (see Settings below). The `proto-message` name is scoped relatively to the package
declared in the `proto-file` file. `proto-message` follows the same resolution rules as type names in `.proto` files. 

# Building and Testing

To build `protobuf-editor.jar`:

```
bazel build //plugin
```

To run tests:
```
bazel test //...
```
