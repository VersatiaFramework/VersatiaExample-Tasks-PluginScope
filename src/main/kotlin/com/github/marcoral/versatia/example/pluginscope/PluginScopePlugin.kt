package com.github.marcoral.versatia.example.pluginscope

import com.github.marcoral.versatia.FriendlyName
import com.github.marcoral.versatia.VersatiaPlugin

/** Registered as `combat`, so `/versatia reload combat` reloads it. */
@FriendlyName("combat")
class PluginScopePlugin : VersatiaPlugin()
