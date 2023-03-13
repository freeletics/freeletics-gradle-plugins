# Freeletics Gradle plugins

A collection of convention plugins that are used internally at Freeletics. The available plugins can be separated
into 4 groups:

- [monorepo plugins](monorepo/README.md) which are specific to the structure of the Freeletics Android mono repository,
  provide more defaults and are more opinionated
- [common plugins](common/README.md) which share the same basic configuration of the monorepo plugins but don't require
  a specific module structure. We are using these for some secondary repositories.
- [root-plugin](root-plugin/README.md) which is applied to the root `build.gradle` file (can be used in combination
  with both the monorepo and the common plugins)
- [settings-plugin](settings-plugin/README.md) which is applied to `settings.gradle` file (can be used in combination 
  with both the monorepo and the common plugins)

# License

```
Copyright 2023 Freeletics GmbH.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
