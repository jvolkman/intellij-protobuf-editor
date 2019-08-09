"""A rule to gather the jspb_proto_library outputs into one file.

jspb_proto_library currently may generate different type of outputs.
If it is built with option
   --experimental_js_jspb_proto_library_generate_multi_files,
jspb_proto_library will generate a directory output, otherwise
jspb_proto_library will generate a single file.

This rule is to always generate one single file output for
jspb_proto_library. If it generates single file, then just
propagate the output, otherwise concatenate all the js outputs
into one file.
"""

def _get_single_jspb_output_impl(ctx):
    """ The implementation of the rule"""
    for src in ctx.attr.srcs:
        for file in src.files:
            if file.basename == ctx.attr.output:
                return DefaultInfo(
                    files = depset([file]),
                )
    output = ctx.actions.declare_file(ctx.attr.output)
    inputs = []
    args = ctx.actions.args()
    for src in ctx.attr.srcs:
        args.add_all(src.files, expand_directories = True)
        inputs += [file for file in src.files]
    args.use_param_file(param_file_arg = "%s", use_always = True)
    args.set_param_file_format(format = "multiline")
    ctx.actions.run_shell(
        inputs = inputs,
        arguments = [args],
        outputs = [output],
        command = "cat $(cat \"$@\") > " + output.path,
    )
    return DefaultInfo(
        files = depset([output]),
    )

get_single_jspb_output = rule(
    implementation = _get_single_jspb_output_impl,
    output_to_genfiles = True,
    attrs = {
        "srcs": attr.label_list(mandatory = True),
        "output": attr.string(mandatory = True),
    },
)
