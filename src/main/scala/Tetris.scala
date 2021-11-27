import indigo._
import indigo.platform.assets.AssetCollection
import indigo.shared.{FrameContext, Outcome, Startup}
import indigo.shared.animation.Animation
import indigo.shared.assets.AssetType
import indigo.shared.config.GameConfig
import indigo.shared.datatypes.FontInfo
import indigo.shared.dice.Dice
import indigo.shared.events.GlobalEvent
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.shader.Shader

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Tetris extends IndigoSandbox[Unit, Unit] {

  val magnification = 3

  val config: GameConfig =
    GameConfig.default.withMagnification(magnification)

  val animations: Set[Animation] =
    Set()

  val assets: Set[AssetType] =
    Set()

  val fonts: Set[FontInfo] =
    Set()

  val shaders: Set[Shader] =
    Set()

  def setup(
    assetCollection: AssetCollection,
    dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(
    context: FrameContext[Unit],
    model: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(())

  def present(
    context: FrameContext[Unit],
    model: Unit
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)

}
