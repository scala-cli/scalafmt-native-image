import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.1.4`
import $ivy.`io.github.alexarchambault.mill::mill-native-image::0.1.19`
import $ivy.`io.github.alexarchambault.mill::mill-native-image-upload:0.1.19`

import de.tobiasroeser.mill.vcs.version._
import io.github.alexarchambault.millnativeimage.NativeImage
import io.github.alexarchambault.millnativeimage.upload.Upload
import mill._
import mill.scalalib._

def scalafmtVersion = "3.5.6"

trait ScalafmtNativeImage extends ScalaModule with NativeImage {
  def scalaVersion = "2.13.8"

  def nativeImageClassPath = T{
    runClasspath()
  }
  def nativeImageOptions = T{
    super.nativeImageOptions() ++ Seq(
      "--no-fallback"
    )
  }
  def nativeImagePersist = System.getenv("CI") != null
  def nativeImageGraalVmJvmId = "graalvm-java17:22.0.0"
  def nativeImageName = "scalafmt"
  def ivyDeps = super.ivyDeps() ++ Seq(
    ivy"org.scalameta::scalafmt-cli:$scalafmtVersion"
  )
  def nativeImageMainClass = "org.scalafmt.cli.Cli"

  def nameSuffix = ""
  def copyToArtifacts(directory: String = "artifacts/") = T.command {
    val _ = Upload.copyLauncher(
      nativeImage().path,
      directory,
      "scalafmt",
      compress = true,
      suffix = nameSuffix
    )
  }
}

object native extends ScalafmtNativeImage

def csDockerVersion = "2.1.0-M5-18-gfebf9838c"

object `native-static` extends ScalafmtNativeImage {
  def nameSuffix = "-static"
  def buildHelperImage = T {
    os.proc("docker", "build", "-t", "scala-cli-base-musl:latest", ".")
      .call(cwd = os.pwd / "musl-image", stdout = os.Inherit)
    ()
  }
  def nativeImageDockerParams = T{
    buildHelperImage()
    Some(
      NativeImage.linuxStaticParams(
        "scala-cli-base-musl:latest",
        s"https://github.com/coursier/coursier/releases/download/v$csDockerVersion/cs-x86_64-pc-linux.gz"
      )
    )
  }
  def writeNativeImageScript(scriptDest: String, imageDest: String = "") = T.command {
    buildHelperImage()
    super.writeNativeImageScript(scriptDest, imageDest)()
  }
}

object `native-mostly-static` extends ScalafmtNativeImage {
  def nameSuffix = "-mostly-static"
  def nativeImageDockerParams = Some(
    NativeImage.linuxMostlyStaticParams(
      "ubuntu:18.04", // TODO Pin that?
      s"https://github.com/coursier/coursier/releases/download/v$csDockerVersion/cs-x86_64-pc-linux.gz"
    )
  )
}


def publishVersion = T{
  val state = VcsVersion.vcsState()
  if (state.commitsSinceLastTag > 0) {
    val versionOrEmpty = state.lastTag
      .filter(_ != "latest")
      .map(_.stripPrefix("v"))
      .flatMap { tag =>
        val idx = tag.lastIndexOf(".")
        if (idx >= 0) Some(tag.take(idx + 1) + (tag.drop(idx + 1).toInt + 1).toString + "-SNAPSHOT")
        else None
      }
      .getOrElse("0.0.1-SNAPSHOT")
    Some(versionOrEmpty)
      .filter(_.nonEmpty)
      .getOrElse(state.format())
  } else
    state
      .lastTag
      .getOrElse(state.format())
      .stripPrefix("v")
}

def upload(directory: String = "artifacts/") = T.command {
  val version = publishVersion()

  val path = os.Path(directory, os.pwd)
  val launchers = os.list(path).filter(os.isFile(_)).map { path =>
    path.toNIO -> path.last
  }
  val ghToken = Option(System.getenv("UPLOAD_GH_TOKEN")).getOrElse {
    sys.error("UPLOAD_GH_TOKEN not set")
  }
  val (tag, overwriteAssets) =
    if (version.endsWith("-SNAPSHOT")) ("launchers", true)
    else ("v" + version, false)

  Upload.upload("scala-cli", "scalafmt-native-image", ghToken, tag, dryRun = false, overwrite = overwriteAssets)(launchers: _*)
}
