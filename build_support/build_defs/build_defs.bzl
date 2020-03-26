"""Custom build macros for IntelliJ plugin handling.
"""

load(
    ":intellij_plugin.bzl",
    _intellij_plugin = "intellij_plugin",
    _intellij_plugin_library = "intellij_plugin_library",
    _optional_plugin_xml = "optional_plugin_xml",
)

load(
    ":intellij_sdk.bzl",
    _IntellijSdkInfo = "IntellijSdkInfo",
    _intellij_sdk = "intellij_sdk",
    _bundled_plugins = "bundled_plugins",
)

# Re-export these symbols
intellij_plugin = _intellij_plugin
intellij_plugin_library = _intellij_plugin_library
optional_plugin_xml = _optional_plugin_xml

intellij_sdk = _intellij_sdk
IntellijSdkInfo = _IntellijSdkInfo
bundled_plugins = _bundled_plugins


def merged_plugin_xml(name, srcs, **kwargs):
    """Merges N plugin.xml files together."""
    merge_tool = "//build_support/build_defs:merge_xml"
    native.genrule(
        name = name,
        srcs = srcs,
        outs = [name + ".xml"],
        cmd = "./$(location {merge_tool}) $(SRCS) > $@".format(
            merge_tool = merge_tool,
        ),
        tools = [merge_tool],
        **kwargs
    )

def _optstr(name, value):
    return ("--" + name) if value else ""

def stamped_plugin_xml(
        name,
        sdk = None,
        plugin_xml = None,
        plugin_id = None,
        plugin_name = None,
        stamp_since_build = False,
        stamp_since_build_major = False,
        stamp_until_build = False,
        version = None,
        version_file = None,
        changelog_file = None,
        description_file = None,
        vendor_file = None,
        **kwargs):
    """Stamps a plugin xml file with the IJ build number.

    Args:
      name: name of this target
      plugin_xml: target plugin_xml to stamp
      plugin_id: the plugin ID to stamp
      plugin_name: the plugin name to stamp
      stamp_since_build: Add build number to idea-version since-build.
      stamp_since_build_major: Use the major branch number as the since-build.
      stamp_until_build: Use idea-version until-build to limit plugin to the
          current major release.
      version: A version number to stamp.
      version_file: A file with the version number to be included.
      changelog_file: A file with the changelog to be included.
      description_file: A file containing a plugin description to be included.
      vendor_file: A file containing the vendor info to be included.
      **kwargs: Any additional arguments to pass to the final target.
    """
    stamp_tool = "//build_support/build_defs:stamp_plugin_xml"

    if not sdk:
        sdk = "//build_support:intellij_sdk"

    api_version_txt_name = name + "_api_version"
    api_version_txt(
        name = api_version_txt_name,
        sdk = sdk,
    )

    args = [
        "./$(location {stamp_tool})",
        "--api_version_txt=$(location {api_version_txt_name})",
        "{stamp_since_build}",
        "{stamp_since_build_major}",
        "{stamp_until_build}",
    ]
    srcs = [api_version_txt_name]

    if plugin_xml:
        args.append("--plugin_xml=$(location {plugin_xml})")
        srcs.append(plugin_xml)

    if version and version_file:
        fail("Cannot supply both version and version_file")

    if plugin_id:
        args.append("--plugin_id=%s" % plugin_id)

    if plugin_name:
        args.append("--plugin_name='%s'" % plugin_name)

    if version:
        args.append("--version='%s'" % version)

    if version_file:
        args.append("--version_file=$(location {version_file})")
        srcs.append(version_file)

    if changelog_file:
        args.append("--changelog_file=$(location {changelog_file})")
        srcs.append(changelog_file)

    if description_file:
        args.append("--description_file=$(location {description_file})")
        srcs.append(description_file)

    if vendor_file:
        args.append("--vendor_file=$(location {vendor_file})")
        srcs.append(vendor_file)

    cmd = " ".join(args).format(
        plugin_xml = plugin_xml,
        api_version_txt_name = api_version_txt_name,
        stamp_tool = stamp_tool,
        stamp_since_build = _optstr(
            "stamp_since_build",
            stamp_since_build,
        ),
        stamp_since_build_major = _optstr(
            "stamp_since_build_major",
            stamp_since_build_major,
        ),
        stamp_until_build = _optstr(
            "stamp_until_build",
            stamp_until_build,
        ),
        version_file = version_file,
        changelog_file = changelog_file,
        description_file = description_file,
        vendor_file = vendor_file,
    ) + "> $@"

    native.genrule(
        name = name,
        srcs = srcs,
        outs = [name + ".xml"],
        cmd = cmd,
        tools = [stamp_tool],
        **kwargs
    )


def _api_version_txt_impl(ctx):

    sdk_info = ctx.attr.sdk[IntellijSdkInfo]

    out_file = ctx.actions.declare_file(ctx.attr.name + ".txt")
    args = [
        "--application_info_jar=" + sdk_info.application_info_jar.files.to_list()[0].path,
        "--application_info_name=" + sdk_info.application_info_name,
        "--output_file=" + out_file.path,
    ]

    ctx.actions.run(
        inputs = sdk_info.application_info_jar.files,
        outputs = [out_file],
        tools = ctx.files.tool,
        arguments = args,
        executable = ctx.executable.tool,
    )

    return [
        DefaultInfo(
            files = depset([out_file]),
            runfiles = ctx.runfiles([out_file]),
        ),
    ]


api_version_txt = rule(
    _api_version_txt_impl,
    attrs = {
        "sdk": attr.label(providers = [IntellijSdkInfo]),
        "tool": attr.label(
            default = Label("//build_support/build_defs:api_version_txt"),
            executable = True,
            allow_files = True,
            cfg = "host",
        )
    }
)


repackaged_files_data = provider()

def _repackaged_files_impl(ctx):
    prefix = ctx.attr.prefix
    if prefix.startswith("/"):
        fail("'prefix' must be a relative path")
    input_files = depset()
    for target in ctx.attr.srcs:
        input_files = depset(transitive = [input_files, target.files])

    return [
        DefaultInfo(files = input_files),
        repackaged_files_data(
            files = input_files,
            prefix = prefix,
            strip_prefix = ctx.attr.strip_prefix,
        ),
    ]

_repackaged_files = rule(
    implementation = _repackaged_files_impl,
    attrs = {
        "srcs": attr.label_list(mandatory = True, allow_files = True),
        "prefix": attr.string(mandatory = True),
        "strip_prefix": attr.string(mandatory = True),
    },
)

def repackaged_files(name, srcs, prefix, strip_prefix = ".", **kwargs):
    """Assembles files together so that they can be packaged as an IntelliJ plugin.

    A cut-down version of the internal 'pkgfilegroup' rule.

    Args:
      name: The name of this target
      srcs: A list of targets which are dependencies of this rule. All output files of each of these
          targets will be repackaged.
      prefix: Where the package should install these files, relative to the 'plugins' directory.
      strip_prefix: Which part of the input file path should be stripped prior to applying 'prefix'.
          If ".", all subdirectories are stripped. If the empty string, the full package-relative path
          is used. Default is "."
      **kwargs: Any further arguments to be passed to the target
    """
    _repackaged_files(name = name, srcs = srcs, prefix = prefix, strip_prefix = strip_prefix, **kwargs)

def _strip_external_workspace_prefix(short_path):
    """If this target is sitting in an external workspace, return the workspace-relative path."""
    if short_path.startswith("../") or short_path.startswith("external/"):
        return "/".join(short_path.split("/")[2:])
    return short_path

def output_path(f, repackaged_files_data):
    """Returns the output path of a file, for a given set of repackaging parameters."""
    prefix = repackaged_files_data.prefix
    strip_prefix = repackaged_files_data.strip_prefix

    short_path = _strip_external_workspace_prefix(f.short_path).strip("/")

    if strip_prefix == ".":
        return prefix + "/" + f.basename
    if strip_prefix == "":
        return prefix + "/" + short_path

    strip_prefix = strip_prefix.strip("/")
    old_path = short_path[:-len(f.basename)].strip("/")
    if not old_path.startswith(strip_prefix):
        fail("Invalid strip_prefix '%s': path actually starts with '%s'" % (strip_prefix, old_path))

    stripped = old_path[len(strip_prefix):].strip("/")
    if stripped == "":
        return "%s/%s" % (prefix, f.basename)
    return "%s/%s/%s" % (prefix, stripped, f.basename)

def _plugin_deploy_zip_impl(ctx):
    zip_name = ctx.attr.zip_filename
    zip_file = ctx.actions.declare_file(zip_name)

    input_files = depset()
    exec_path_to_zip_path = {}
    for target in ctx.attr.srcs:
        data = target[repackaged_files_data]
        input_files = depset(transitive = [input_files, data.files])
        for f in data.files.to_list():
            exec_path_to_zip_path[f.path] = output_path(f, data)

    args = []
    args.extend(["--output", zip_file.path])
    for exec_path, zip_path in exec_path_to_zip_path.items():
        args.extend([exec_path, zip_path])
    ctx.actions.run(
        executable = ctx.executable._zip_plugin_files,
        arguments = args,
        inputs = input_files.to_list(),
        outputs = [zip_file],
        mnemonic = "ZipPluginFiles",
        progress_message = "Creating final plugin zip archive",
    )
    files = depset([zip_file])
    return struct(
        files = files,
    )

_plugin_deploy_zip = rule(
    implementation = _plugin_deploy_zip_impl,
    attrs = {
        "srcs": attr.label_list(mandatory = True, providers = []),
        "zip_filename": attr.string(mandatory = True),
        "_zip_plugin_files": attr.label(
            default = Label("//build_support/build_defs:zip_plugin_files"),
            executable = True,
            cfg = "host",
        ),
    },
)

def plugin_deploy_zip(name, srcs, zip_filename, **kwargs):
    """Packages up plugin files into a zip archive.

    Args:
      name: The name of this target
      srcs: A list of targets of type 'repackaged_files', specifying the input files and relative
          paths to include in the output zip archive.
      zip_filename: The output zip filename.
      **kwargs: Any further arguments to be passed to the target
    """
    _plugin_deploy_zip(name = name, zip_filename = zip_filename, srcs = srcs, **kwargs)
