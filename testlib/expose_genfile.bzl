"""Exposes a genfile by copying to output."""

def _expose_genfile(ctx):
    output_name = ctx.attr.genfile
    output_path = ctx.label.package + "/" + output_name
    for dep in ctx.attr.deps:
        for f in _get_files(dep):
            if f.short_path == output_path:
                ctx.actions.run_shell(
                    inputs = [f],
                    outputs = [ctx.outputs.file],
                    arguments = [f.path, ctx.outputs.file.path],
                    command = "cp $1 $2",
                )
                return []
    fail("genfile {} not found".format(output_name))

def _get_files(dep):
    if hasattr(dep, "python_proto_uncompiled_generated_sources"):
        return dep.python_proto_uncompiled_generated_sources.uncompiled_generated_sources
    else:
        return dep.files

expose_genfile = rule(
    implementation = _expose_genfile,
    attrs = {
        "deps": attr.label_list(
            allow_empty = False,
            mandatory = True,
        ),
        "genfile": attr.string(mandatory = True),
    },
    outputs = {"file": "%{genfile}"},
)
