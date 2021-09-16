import $ivy.`io.github.alexarchambault.mill::mill-native-image_mill0.9:0.1.10`
import $ivy.`io.github.alexarchambault.mill::mill-native-image-upload:0.1.10`

import io.github.alexarchambault.millnativeimage.NativeImage
import io.github.alexarchambault.millnativeimage.upload.Upload
import mill._
import mill.scalalib._

def scalafmtVersion = "3.0.2"

object native extends ScalaModule with NativeImage {
  def scalaVersion = "2.13.6"
  def nativeImagePersist = System.getenv("CI") != null
  def nativeImageGraalVmJvmId = "graalvm-java11:21.2.0"
  def nativeImageName = "scalafmt"
  def ivyDeps = super.ivyDeps() ++ Seq(
    ivy"org.scalameta::scalafmt-cli:$scalafmtVersion"
  )
  def nativeImageClassPath = T{
    runClasspath()
  }
  def nativeImageMainClass = "org.scalafmt.cli.Cli"

  def copyToArtifacts(directory: String = "artifacts/") = T.command {
    val _ = Upload.copyLauncher(
      nativeImage().path,
      directory,
      s"scalafmt-$scalafmtVersion",
      compress = true
    )
  }
}

def upload(directory: String = "artifacts/") = T.command {
  val version = scalafmtVersion

  val path = os.Path(directory, os.pwd)
  val launchers = os.list(path).filter(os.isFile(_)).map { path =>
    path.toNIO -> path.last
  }
  val ghToken = Option(System.getenv("UPLOAD_GH_TOKEN")).getOrElse {
    sys.error("UPLOAD_GH_TOKEN not set")
  }
  val (tag, overwriteAssets) = ("launchers", false)
  Upload.upload("alexarchambault", "scalafmt-native", ghToken, tag, dryRun = false, overwrite = overwriteAssets)(launchers: _*)
}
