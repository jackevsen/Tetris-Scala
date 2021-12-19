package jackevsen.utils

import indigo.shared.BoundaryLocator
import indigo.shared.constants.Key
import indigo.shared.datatypes.Radians
import indigo.shared.dice.Dice
import jackevsen.model.Game

object InputUtils {
  def onKeyDown(keyData: Key, model: Game, boundaryLocator: BoundaryLocator, dice: Dice): Game = {
    val piecePosition = model.currentPiece.position

    keyData match {
      case Key(_, "ArrowUp") =>
        if (model.currentPiece.rotatable) {
          model.withCurrentPiece(
            GameUtils.setPieceRotation(
              model.currentPiece,
              model.currentPiece.rotation + Radians.PIby2,
              model.gameFieldModel,
              boundaryLocator
            )
          )
        } else {
          model
        }
      case Key(_, "ArrowDown") =>
        model.updatePosition(
          piecePosition.withY(
            piecePosition.y + GameUtils.cellSize
          ),
          boundaryLocator,
          dice
        )
      case Key(_, "ArrowLeft") =>
        model.updatePosition(
          piecePosition.withX(
            piecePosition.x - GameUtils.cellSize
          ),
          boundaryLocator,
          dice
        )
      case Key(_, "ArrowRight") =>
        model.updatePosition(
          piecePosition.withX(
            piecePosition.x + GameUtils.cellSize
          ),
          boundaryLocator,
          dice
        )
      case _ => model
    }
  }
}
