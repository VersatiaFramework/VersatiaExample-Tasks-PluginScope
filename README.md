# VersatiaExample-Tasks-PluginScope

An injected `PluginScope` launches coroutines of your own that live as long as the plugin: here a combat tag per player, kept in `KeyedJobs` so that every hit replaces the running timer, and cancelled when the player leaves.

Part of the [Versatia examples](https://github.com/VersatiaFramework/VersatiaExamples); documentation: [Your own coroutines](https://versatiaframework.github.io/VersatiaDocs/docs/tasks#your-own-coroutines).

**Start with** [`CombatTags.kt`](src/main/kotlin/com/github/marcoral/versatia/example/pluginscope/CombatTags.kt); the configuration is [`resources/config.yml`](src/main/resources/resources/config.yml).

## What the log shows

```
(a zombie hits Steve) [VersatiaExample-Tasks-PluginScope] Steve tagged for 10s
(it hits again) [VersatiaExample-Tasks-PluginScope] Steve hit again, the tag starts over
(10 s after the last hit) [VersatiaExample-Tasks-PluginScope] Steve is safe
```

Steve reads `You are in combat for 10s; leaving now kills you` at the first hit and
`You are out of combat` when the tag ends.

## Things to try

- In survival, summon a zombie next to you (`/summon zombie ~ ~ ~2`), take one hit and walk away:
  ten seconds later the log shows `Steve is safe`. Any living entity tags you, so no second player
  is needed.
- Take a second hit before the ten seconds are over: `Steve hit again, the tag starts over`.
  `tags.launch` cancelled the running timer, so `Steve is safe` comes once, ten seconds after the
  last hit.
- Leave the server while tagged: `Steve left in combat and was killed`; `tags.cancel` returned
  true because a timer was running. Leave once you are safe and nothing happens.
- Set `duration: 30s` in `plugins/VersatiaExample-Tasks-PluginScope/config.yml` and run
  `/versatia reload combat` while tagged: the running timer survives the reload with its old
  duration, since a reload leaves the scope's coroutines alone; the next hit starts a 30s tag.

## Building

Requires JDK 21 and VersatiaStarter with what it exports (VersatiaAPI, the module APIs) and
VersatiaCatalog, which supplies every version, in your local Maven repository (`./gradlew publishToMavenLocal` in each checkout; see
[Building the framework](https://versatiaframework.github.io/VersatiaDocs/docs/contributing/building));
running it requires a Paper 1.21.4 server with VersatiaCore installed, and the
[CommandAPI](https://commandapi.jorel.dev) 12.x plugin for `/versatia reload`.

```bash
./gradlew build
```

To have the JAR copied to your server after every build, copy `local.properties.example` to
`local.properties` and set `pluginsDir`. The generated descriptor is in
`build/generated/ksp/main/kotlin`; open it to see exactly what the runtime executes.

The Versatia version is `versatiaVersion` in `gradle.properties`; `settings.gradle.kts` imports
the matching [VersatiaCatalog](https://versatiaframework.github.io/VersatiaDocs/docs/getting-started/installation#versatia-catalog),
so the build script writes `libs.versatia.starter` and `alias(libs.plugins.ksp)` instead of version
numbers.
