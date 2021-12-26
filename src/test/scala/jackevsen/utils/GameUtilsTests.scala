package jackevsen.utils

import indigo.platform.assets.DynamicText
import indigo.shared.{AnimationsRegister, BoundaryLocator, FontRegister}
import indigo.shared.datatypes.{Point, Radians, Rectangle, Size}
import indigo.shared.time.Seconds
import jackevsen.displayObjects._
import jackevsen.model.{Game, GameFieldPosition}
import jackevsen.utils.GameUtils._

object BoundaryLocatorTest {
  def default: BoundaryLocator =
    new BoundaryLocator(new AnimationsRegister(), new FontRegister(), new DynamicText())
}

class GameUtilsTests extends munit.FunSuite {

  test("should return a multiple of cellSize") {
    assertEquals(normalizeValue(cellSize + 5), cellSize)
    assertEquals(normalizeValue(cellSize - 2), cellSize)
    assertNotEquals(normalizeValue(15), cellSize)
  }

  test("should set a piece position inside the game field") {
    val piece = PieceT.default
    val newPosition = piece.position.moveTo(cellSize, cellSize)
    val game = Game.initial()
    val boundaryLocator = BoundaryLocatorTest.default

    val updatedPiece = setPiecePosition(piece, newPosition, game, boundaryLocator)

    assertEquals(updatedPiece.position, newPosition)
  }

  test("should not set a piece position outside the game field") {
    val piece = PieceT.default
    val newPosition = piece.position.moveTo(-cellSize, -cellSize)
    val game = Game.initial()
    val boundaryLocator = BoundaryLocatorTest.default

    val updatedPiece = setPiecePosition(piece, newPosition, game, boundaryLocator)

    assertNotEquals(updatedPiece.position, newPosition)
    assertEquals(updatedPiece.position, Point(0, 0))
  }

  test("should set a piece rotation") {
    val piece = PieceT.default
    val newRotation = piece.rotation + Radians.PIby2
    val game = Game.initial()
    val boundaryLocator = BoundaryLocatorTest.default

    val updatedPiece = setPieceRotation(piece, newRotation, game, boundaryLocator)

    assertEquals(updatedPiece.rotation, newRotation)
  }

  test("should update a piece position after rotation not to render it outside of the game field") {
    val piece = PieceT.default
    val prevPosition = piece.position // Point(0, 0)
    val newRotation = piece.rotation + Radians.PIby2
    val game = Game.initial()
    val boundaryLocator = BoundaryLocatorTest.default

    val updatedPiece = setPieceRotation(piece, newRotation, game, boundaryLocator)

    assertNotEquals(updatedPiece.position, prevPosition)
    assertEquals(updatedPiece.position, Point(cellSize, 0))
  }

  test("should return a piece fragments coordinates inside the game field grid ") {
    val piece = PieceT.default
    val game = Game.initial()
    val boundaryLocator = BoundaryLocatorTest.default

    // schematic representation of a piece in game field grid
    //   0 1 2
    // 0 x - -
    // 1 x x -
    // 2 x - -

    assertEquals(
      getPieceFragmentsPositions(piece, boundaryLocator),
      List(
        GameFieldPosition(0, 0),
        GameFieldPosition(1, 0),
        GameFieldPosition(2, 0),
        GameFieldPosition(1, 1),
      )
    )

    val updatedPiece = setPieceRotation(
      piece,
      piece.rotation + Radians.PIby2,
      game,
      boundaryLocator
    )

    // schematic representation of a piece in game field grid
    //   0 1 2
    // 0 - - -
    // 1 x x x
    // 2 - x -

    assertEquals(
      getPieceFragmentsPositions(updatedPiece, boundaryLocator),
      List(
        GameFieldPosition(1, 2),
        GameFieldPosition(1, 1),
        GameFieldPosition(1, 0),
        GameFieldPosition(2, 1),
      )
    )
  }

  test("should find and set position to a piece that is free in game field grid") {
    val piece = PieceT.default
      .withPosition(Point(cellSize, cellSize * 16)) // 1 col, 16 row

    val gameInitial = Game.initial()

    val game = gameInitial
      .withGameField(
        gameInitial.gameField
          .withTakenPositions(
            List(
              GameFieldPosition(16, 0),
              GameFieldPosition(17, 0),
              GameFieldPosition(18, 0),
              GameFieldPosition(19, 0),
            )
          )
      )
    val boundaryLocator = BoundaryLocatorTest.default

    // schematic representation of a piece in game field grid
    //    0 1 2
    // 14 - - -
    // 15 - - -
    // 16 o x -
    // 17 o x x
    // 18 o x -
    // 19 o - -

    // all positions are free for a piece
    assertEquals(
      findAndSetFreePositions(piece, game.gameField, boundaryLocator).position,
      piece.position
    )

    val updatedPiece = setPieceRotation(
      piece,
      piece.rotation + Radians.PIby2,
      game,
      boundaryLocator
    )

    // schematic representation of a piece in game field grid
    //    0 1 2
    // 14 - - -
    // 15 x x x
    // 16 o x -
    // 17 o - -
    // 18 o - -
    // 19 o - -

    // for some fragments positions are taken, need to move the piece up
    assertEquals(
      findAndSetFreePositions(updatedPiece, game.gameField, boundaryLocator).position,
      Point(32, 448)
    )
  }

  test("should return a game field size") {
    assertEquals(getGameFieldSize(10, 20), Size(cellSize * 10, cellSize * 20))
  }

  test("should update model by delta") {
    val game = Game.initial()
    val boundaryLocator = BoundaryLocatorTest.default
    val dice = getDice()
    val delta = Seconds(0.1)

    val model = updateGameModelByTimeout(game, delta, boundaryLocator, dice)

    assertEquals(model.curentDelta, delta.toFloat)

    val updatedModel = updateGameModelByTimeout(game, Seconds(1), boundaryLocator, dice)

    assertEquals(updatedModel.curentDelta, 0.toFloat)
    assertEquals(updatedModel.currentPiece.position, updatedModel.currentPiece.position.withY(32))
  }

  test("should update model") {
    val game = Game.initial()
      .withCurrentPiece(PieceT.default)
    val boundaryLocator = BoundaryLocatorTest.default
    val dice = getDice()
    val newPosition =  Point(32, 32)

    val model = updateGameModel(game, newPosition, boundaryLocator, dice)

    assertEquals(model.currentPiece.position, newPosition)

    val endOfGameFieldPosition =  Point(32, cellSize * 17)
    val updatedModel = updateGameModel(game, endOfGameFieldPosition, boundaryLocator, dice)

    assertNotEquals(updatedModel.currentPiece, game.currentPiece)
    assertNotEquals(updatedModel.gameField.cellsList, game.gameField.cellsList)
  }

  test("should remove filled rows, calculate score and increase movement interval") {
    val gameInitial = Game.initial()

    val takenPositions = (0 to gameInitial.gameField.width - 1).map(index => {
      GameFieldPosition(gameInitial.gameField.height - 1, index)
    }).toList

    val game = gameInitial
      .withGameField(gameInitial.gameField.withTakenPositions(takenPositions))
    val boundaryLocator = BoundaryLocatorTest.default
    val dice = getDice()

    val model = processNextPieceUpdate(game,dice, boundaryLocator)

    assertEquals(
      model.gameField.cellsList,
      List.fill(model.gameField.width * model.gameField.height)(false)
    )

    assertEquals(model.score, scoreForItem * model.gameField.width)

    assertNotEquals(model.movementInterval, gameInitial.movementInterval)
  }

  test("should end current game if the game field is filled") {
    val gameInitial = Game.initial()

    val game = gameInitial
      .withGameField(
        gameInitial.gameField
          .withCellsList(
            List.fill(gameInitial.gameField.width * gameInitial.gameField.height)(true)
          )
      )

    val boundaryLocator = BoundaryLocatorTest.default
    val dice = getDice()

    val model = processNextPieceUpdate(game,dice, boundaryLocator)

    assertEquals(model.gameOver, true)
  }

  test("should set a new random piece") {
    val game = Game.initial()
      .withCurrentPiece(PieceT.default)
    val dice = getDice()

    val model = setRandomPiece(game, dice)

    assertNotEquals(game.currentPiece, model.currentPiece)
  }

  test("should calculate movement interval") {
    assertEquals(calculateMovementInterval(100), 0.9.toFloat)
    assertEquals(calculateMovementInterval(200), 0.8.toFloat)
  }
}
