def _strip_virtual_import_impl(ctx):
    def_info = ctx.attr.file[DefaultInfo]

    result = []
    for file in def_info.files.to_list():
        if '_virtual_imports' in file.path:
            outfile = ctx.actions.declare_file(file.basename)
            result.append(outfile)
            ctx.actions.run_shell(
                outputs=[outfile],
                inputs=[file],
                command="cp '{}' '{}'".format(file.path, outfile.path)
            )
        else:
            result.append(file)

    return [
        DefaultInfo(files=depset(result))
    ]


# Strips the '_virtual_imports' path prefix from generated Python protobuf source that results from
# using generated .proto files.
strip_virtual_import = rule(
    _strip_virtual_import_impl,
    attrs = {
        "file": attr.label(providers = [DefaultInfo])
    }
)
