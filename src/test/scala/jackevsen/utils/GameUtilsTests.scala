package jackevsen.utils

import indigo.platform.assets.DynamicText
import indigo.shared.{AnimationsRegister, BoundaryLocator, FontRegister}
import jackevsen.displayObjects._
import jackevsen.model.GameField
import jackevsen.utils.GameUtils._

object BoundaryLocatorTest {
  def default: BoundaryLocator =
    new BoundaryLocator(new AnimationsRegister(), new FontRegister(), new DynamicText())
}

class GameUtilsTests extends munit.FunSuite {

  test("should return a multiple of GameUtils.cellSize") {
    assertEquals(normalizeValue(35), GameUtils.cellSize)
    assertEquals(normalizeValue(30), GameUtils.cellSize)
  }

  test("should set a piece position inside a game field") {
    val piece = PieceT.default
    val newPosition = piece.position.moveTo(GameUtils.cellSize, GameUtils.cellSize)
    val gameField = GameField.initial(10, 20)
    val boundaryLocator = BoundaryLocatorTest.default

    val updatedPiece = setPiecePosition(piece, newPosition, gameField, boundaryLocator)

    assertEquals(updatedPiece.position, newPosition)
  }
}
