package dev.versatia.example.pluginscope

import dev.versatia.FriendlyName
import dev.versatia.VersatiaPlugin

/** Registered as `combat`, so `/versatia reload combat` reloads it. */
@FriendlyName("combat")
class PluginScopePlugin : VersatiaPlugin()
