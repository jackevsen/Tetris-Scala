package jackevsen

import jackevsen.fonts.FontBoxy
import indigo._
import jackevsen.model.Game
import jackevsen.renderers.GameRenderer
import jackevsen.utils._

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Tetris extends IndigoSandbox[Unit, Game] {

  val config: GameConfig =
    GameConfig.default.withViewport(550, 800)

  val animations: Set[Animation] =
    Set()

  val assets: Set[AssetType] =
    Set(FontBoxy.fontType)

  val fonts: Set[FontInfo] =
    Set(FontBoxy.fontInfo)

  val shaders: Set[Shader] =
    Set()

  def setup(
    assetCollection: AssetCollection,
    dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Game] =
    Outcome(Game.initial())

  def updateModel(
    context: FrameContext[Unit],
    model: Game
  ): GlobalEvent => Outcome[Game] = {
    case KeyboardEvent.KeyDown(keyData: Key) =>
      keyData match {
        case Key(_, " ") =>
          Outcome(
            if (!model.gameStarted && !model.gameOver) {
              model.startNewGame()
            } else if (model.gameOver) {
              model.withGameOver(false)
            } else {
              model
            }
          )
        case Key(_, "p") =>
          Outcome(
            if (model.gameStarted) {
              model.withPause(!model.paused)
            } else {
              model
            }
          )
        case _ => {
          Outcome(
            if (model.gameStarted && !model.paused) {
              InputUtils.onKeyDown(keyData, model, context.boundaryLocator, context.dice)
            } else {
              model
            }
          )
        }
      }
    case FrameTick =>
      Outcome(
        if (model.isGameStopped)
          model
        else
          GameUtils.updateGameModelByTimeout(
            model,
            context.delta,
            context.boundaryLocator,
            context.dice
          )
      )

    case _ =>
      Outcome(model)
  }

  def present(
    context: FrameContext[Unit],
    model: Game
  ): Outcome[SceneUpdateFragment] = {
    Outcome(
      SceneUpdateFragment(
        GameRenderer.renderScene(model, config.viewport)
      )
    )
  }
}
