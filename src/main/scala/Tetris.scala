import fonts.FontBoxy
import indigo._
import indigo.shared.Outcome
import model.GameModel
import utils.{GraphicsUtils, InputUtils}

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Tetris extends IndigoSandbox[Unit, GameModel] {

  val fontAsset = AssetName("boxy")

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

  def initialModel(startupData: Unit): Outcome[GameModel] =
    Outcome(GameModel.initial())

  def updateModel(
    context: FrameContext[Unit],
    model: GameModel
  ): GlobalEvent => Outcome[GameModel] = {
    case ViewportResize(viewport) =>
      val gameField = model.gameField
      val size = gameField.getSize()
      gameField.setPosition(Point(viewport.center.x - size.width / 2, 50))
      Outcome(model)
    case KeyboardEvent.KeyDown(keyData: Key) =>
      keyData match {
        case Key(_, " ") =>
          Outcome(
            if (!model.gameStarted && !model.gameOver) {
              model.startNewGame()
            } else if(model.gameOver) {
              model.setGameOver(false)
            } else {
              model
            }
          )
        case Key(_, "p") =>
          Outcome(
            if (model.gameStarted) {
              model.setPaused(!model.paused)
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
      Outcome(model.update(context.delta, context.boundaryLocator, context.dice))

    case _ =>
      Outcome(model)
  }

  def present(
    context: FrameContext[Unit],
    model: GameModel
  ): Outcome[SceneUpdateFragment] = {
    Outcome(
      SceneUpdateFragment(
        GraphicsUtils.drawScene(model, config.viewport)
      )
    )
  }

}
