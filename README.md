# _Repository archived_

This repository has been archived due to the termination of the Minecraft Server service of jao Minecraft Server.  
In principle, the contents of this repository will not be maintained in the future.

The content in this repository will continue to be released under the license specified in [LICENSE](LICENSE). It is possible that the content may become private without prior notice.

---

# CheckPluginEnabling

Use GitHub Actions to actually run the plugin on the PaperMC server and check if it runs without errors for one minute.

Will **NOT WORK** with the following Minecraft
versions: `1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12, 1.12.1, 1.12.2, 1.13-pre7, 1.13`

See [paper-versions.json](paper-versions.json) for a list of supported Minecraft versions.

```yaml
name: PaperServerTest

on: [ push, pull_request ]

jobs:
  main:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v1

      - name: Set up JDK 11
        uses: actions/setup-java@v1
        with:
          java-version: 11

      - name: Cache local Maven repository
        uses: actions/cache@v2
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build with Maven
        run: mvn -B package --file pom.xml

      - name: Test
        uses: jaoafa/CheckPluginEnabling@v2.4.0
        with:
          plugin-name: CheckPluginEnabling
          minecraft-version: [ "1.13.1", "1.14", "1.15", "1.16.1", "1.17", "1.18", "1.19", "1.20" ]
```
