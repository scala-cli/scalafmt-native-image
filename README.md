[GraalVM native-image](https://www.graalvm.org/docs/getting-started/#native-images) launchers for [scalafmt](https://scalameta.org/scalafmt).

These launchers are generated on the scalafmt-native CI, and pushed [here](https://github.com/alexarchambault/scalafmt-native/releases/tag/launchers).

## Getting the launcher

### Linux

```text
$ curl -fL https://github.com/alexarchambault/scalafmt-native/releases/download/launchers/scalafmt-3.0.0-x86_64-pc-linux.gz | gzip -d > scalafmt
$ chmod +x scalafmt
$ ./scalafmt --help
```

### macOS

```text
$ curl -fL https://github.com/alexarchambault/scalafmt-native/releases/download/launchers/scalafmt-3.0.0-x86_64-apple-darwin.gz | gzip -d > scalafmt
$ chmod +x scalafmt
$ ./scalafmt --help
```

### Windows

(untested)

```text
> curl -fLo scalafmt.zip https://github.com/alexarchambault/scalafmt-native/releases/download/launchers/scalafmt-3.0.0-x86_64-pc-win32.zip
> tar -xf scalafmt.zip
> mv scalafmt-3.0.0.exe scalafmt.exe
> scalafmt --help
```
