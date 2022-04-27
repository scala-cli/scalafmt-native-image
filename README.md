[GraalVM native-image](https://www.graalvm.org/docs/getting-started/#native-images) launchers for [scalafmt](https://scalameta.org/scalafmt).

_Note that Scala Native support in scalafmt is in the works. As soon as it is stable and provides launchers for Linux / macOS / Windows, the repository here should be sunset._

These launchers are generated on the scalafmt-native-image CI, and pushed [here](https://github.com/alexarchambault/scalafmt-native-image/releases/tag/launchers).

These launchers aim at being consumed by [Scala CLI](https://github.com/VirtusLab/scala-cli), but can also be useful on their own. Note that if the launcher scalafmt version and the scalafmt version in `.scalafmt.conf` don't match, the launcher will not proceed and fail loudly. Those versions have to match.

## Getting the launchers

### Linux

```text
$ curl -fL https://github.com/alexarchambault/scalafmt-native-image/releases/download/launchers/scalafmt-3.0.0-x86_64-pc-linux.gz | gzip -d > scalafmt
$ chmod +x scalafmt
$ ./scalafmt --help
```

### macOS

```text
$ curl -fL https://github.com/alexarchambault/scalafmt-native-image/releases/download/launchers/scalafmt-3.0.0-x86_64-apple-darwin.gz | gzip -d > scalafmt
$ chmod +x scalafmt
$ ./scalafmt --help
```

### Windows

(untested)

```text
> curl -fLo scalafmt.zip https://github.com/alexarchambault/scalafmt-native-image/releases/download/launchers/scalafmt-3.0.0-x86_64-pc-win32.zip
> tar -xf scalafmt.zip
> mv scalafmt-3.0.0.exe scalafmt.exe
> scalafmt --help
```

## Building the launcher locally

scalafmt-native-image is built with Mill. Generate a binary with
```text
$ ./mill show native.nativeImage
```

## Releases

Release numbers follow scalafmt versions, and need to be cut after each scalafmt release.

Typical release workflow, after a scalafmt release:
- Scala Steward opens a PR bumping scalafmt. Just merge that PR if the CI is green (if it's red, investigate why).
- create a scalafmt-native-image release from the GitHub UI (pushing a tag is not enough), with the same version number as scalafmt, prefixed with `v`.
- stop any running `master` job (these might download the new tag, and try to push binaries on their own) 
- check from GitHub actions that the release job runs fine

The release job should push binaries as assets of the newly cut release.

### If several scalafmt versions were cut since the last bump

In that case, bump the version in scalafmt-native-image step-by-step, and cut intermediate releases at each step, so that we get binaries for all intermediary scalafmt versions.
