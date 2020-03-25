IntellijSdkInfo = provider(fields = ["java_info", "application_info_jar", "application_info_name", "bundled_plugins_java_info"])


def _intellij_sdk_impl(ctx):
    java_info = ctx.attr.sdk_lib[JavaInfo]

    return [
        java_info,
        IntellijSdkInfo(
            java_info = java_info,
            application_info_jar = ctx.attr.application_info_jar,
            application_info_name = ctx.attr.application_info_name,
            bundled_plugins_java_info = ctx.attr.bundled_plugins[JavaInfo],
        ),
    ]


intellij_sdk = rule(
    implementation = _intellij_sdk_impl,
    attrs = {
        "sdk_lib": attr.label(providers = [JavaInfo]),
        "bundled_plugins": attr.label(providers = [JavaInfo]),
        "application_info_jar": attr.label(),
        "application_info_name": attr.string(),
    },
)


def _bundled_plugins_impl(ctx):
    sdk_info = ctx.attr.sdk[IntellijSdkInfo]
    java_info = sdk_info.bundled_plugins_java_info
    return [
        java_info,
    ]


bundled_plugins = rule(
    implementation = _bundled_plugins_impl,
    attrs = {
        "sdk": attr.label(providers = [IntellijSdkInfo])
    }
)
