"""Exposes a genfile by copying to output."""

def _matches_path(f, search_path):
    return f.short_path.endswith("/" + search_path)

def _expose_genfile(ctx):
    search_name = ctx.attr.genfile_orig or ctx.attr.genfile
    search_path = ctx.label.package + "/" + search_name
    for dep in ctx.attr.deps:
        for f in _get_files(dep):
            if _matches_path(f, search_path):
                ctx.actions.run_shell(
                    inputs = [f],
                    outputs = [ctx.outputs.file],
                    arguments = [f.path, ctx.outputs.file.path],
                    command = "cp $1 $2",
                )
                return []
    fail("genfile {} not found".format(search_name))

def _get_files(dep):
    if hasattr(dep, "python_proto_uncompiled_generated_sources"):
        return dep.python_proto_uncompiled_generated_sources.uncompiled_generated_sources
    else:
        return dep.files.to_list()

expose_genfile = rule(
    implementation = _expose_genfile,
    attrs = {
        "deps": attr.label_list(
            allow_empty = False,
            mandatory = True,
        ),
        "genfile": attr.string(mandatory = True),
        "genfile_orig": attr.string(mandatory = False),
    },
    outputs = {"file": "%{genfile}"},
)
