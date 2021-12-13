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
        case Key(_, "p") =>
          Outcome(model.setPaused(!model.paused))
        case _ => {
          Outcome(
            if (!model.paused && !model.gameOver) {
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
    val gameField = model.gameField
    Outcome(
      SceneUpdateFragment(
        List(
          gameField.draw().moveTo(gameField.getPosition)
            .addChild(GraphicsUtils.drawTakenPositions(model.gameFieldModel.getTakenPositions(), model.gameField.cellSize))
            .addChild(model.currentPiece.draw()),
          GraphicsUtils.drawText(s"score: ${model.score}", config.viewport.width, 20).moveTo(0, 10),
        )
      )
    )
  }

}
