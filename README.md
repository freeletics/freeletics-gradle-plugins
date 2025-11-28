# Freeletics Gradle plugins

A collection of convention plugins and build tools that are used internally at Freeletics.

- [monorepo plugins](docs/monorepo.md) which are specific to the structure of the Freeletics Android mono repository,
  provide more defaults and are more opinionated
- [base plugins](docs/plugins-common) which share the same basic configuration of the monorepo plugins but don't require
  a specific module structure. These are used internally by the monoroepo plugins and we are using these for open source
  some projects (like this one) and some secondary projects.
- scripts: a collection of ready to use command line scripts and helper that can be used from `.main.kts` scripts


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
