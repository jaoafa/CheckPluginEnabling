# CheckPluginEnabling

Use GitHub Actions to actually run the plugin on the PaperMC server and check if it runs without errors for one minute.

```yaml
name: PaperServerTest

on: [ push, pull_request ]

jobs:
  main:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v1

      - name: Set up JDK 17
        uses: actions/setup-java@v1
        with:
          java-version: 17

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
        uses: jaoafa/CheckPluginEnabling@v1
        with:
          plugin-name: CheckPluginEnabling
          minecraft-version: 1.18
```
