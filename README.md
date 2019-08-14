# Protocol Buffers support for IntelliJ

This plugin provides editor support for Protocol Buffers files, including text
format.

Note: This is an unsupported release for educational purposes and is not kept
up-to-date. It may not build correctly for you, and we are not currently
accepting pull requests.

# Features

![Editor](editor.png)

* Support for proto2 and proto3
* Syntax highlighting
* Semantic analysis
* References and linking
* Quick documentation
* Editor enhancements (completion, brace matching, etc.)
* Navigating between Java source and `.proto` definitions
* Full support for text format, both standalone and in custom options

# Building

To build `plugin.jar`:

```
bazel build //plugin
```

# Testing

The core plugin, java, and python support can be tested against IDEA Community.
Go support must be tested against IDEA Ultimate.

```
bazel test //core/...
bazel test //java/...
bazel test //python/...
bazel test //golang/... --define=ij_product=intellij-ue-latest
```
