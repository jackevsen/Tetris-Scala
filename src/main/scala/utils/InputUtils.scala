package utils

import indigo.shared.BoundaryLocator
import indigo.shared.constants.Key
import indigo.shared.datatypes.Radians
import indigo.shared.dice.Dice
import model.GameModel

object InputUtils {
  def onKeyDown(keyData: Key, model: GameModel, boundaryLocator: BoundaryLocator, dice: Dice): GameModel = {
    val piecePosition = model.currentPiece.getPosition

    keyData match {
      case Key(_, "ArrowUp") =>
        GameUtils.setPieceRotation(
          model.currentPiece,
          model.currentPiece.getRotation + Radians.PIby2,
          model.gameField,
          boundaryLocator
        )
        model
      case Key(_, "ArrowDown") =>
        model.updatePosition(
          piecePosition.withY(
            piecePosition.y + model.currentPiece.cellSize
          ),
          boundaryLocator,
          dice
        )
      case Key(_, "ArrowLeft") =>
        model.updatePosition(
          piecePosition.withX(
            piecePosition.x - model.currentPiece.cellSize
          ),
          boundaryLocator,
          dice
        )
      case Key(_, "ArrowRight") =>
        model.updatePosition(
          piecePosition.withX(
            piecePosition.x + model.currentPiece.cellSize
          ),
          boundaryLocator,
          dice
        )
      case _ => model
    }
  }
}
