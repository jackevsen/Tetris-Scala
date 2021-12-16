package utils

import displayObjects.pieces.Piece
import fonts.FontBoxy
import indigo.shared.config.GameViewport
import indigo.shared.datatypes._
import indigo.shared.scenegraph.{Group, SceneNode, Shape, TextBox}
import model.{GameFieldPosition, GameModel}

object GraphicsUtils {
  def drawGameField(width: Int, height: Int, cellSize: Int): Group = {
    val countVert = width + 1
    val countHorizont = height + 1
    val array = new Array[Shape](countVert + countHorizont)

    for (i <- 0 until countVert){
      array(i) = Shape.Line(Point(i * cellSize, 0), Point(i * cellSize, height * cellSize), Stroke(3, RGBA.White))
    }

    for (i <- 0 until countHorizont){
      array(i + countVert) = Shape.Line(Point(0, i * cellSize), Point(width * cellSize, i * cellSize), Stroke(3, RGBA.White))
    }

    Group(array.toList)
  }

  def drawGamePiece(piece: Piece, cellSize: Int): Group = {
    val first = piece.pathCoords.head
    val shapes: List[Shape] = piece.pathCoords.map(p => {
      Shape.Box(
        Rectangle(p * cellSize, Size(cellSize, cellSize)),
        Fill.Color(RGBA.White),
        Stroke(1, RGBA.Black)
      )
    })

    Group(shapes)
  }

  def drawText(text: String, width: Int, height: Int, fontSize: Int = 20): TextBox = {
    TextBox(
      text,
      width,
      height
    )
      .withFontFamily(FontBoxy.fontFamily)
      .withColor(RGBA.White)
      .withFontSize(Pixels(fontSize))
  }

  def drawTakenPositions(positions: List[GameFieldPosition], cellSize: Int): Group = {
    Group(
      positions.map(p => {
        val point = Point(p.column, p.row)
        Shape.Box(
          Rectangle(point * cellSize, Size(cellSize, cellSize)),
          Fill.Color(RGBA.White),
          Stroke(1, RGBA.Black)
        )
      })
    )
  }

  def drawScene(model: GameModel, viewport: GameViewport): List[SceneNode] = {
    if (!model.gameStarted && !model.gameOver) {
      List(
        Group(
          GraphicsUtils.drawText("TETRIS", viewport.width, 50, 50).moveTo(0, 30).alignCenter,
          GraphicsUtils.drawText("Control:", viewport.width, 30, 30).moveTo(0, 120).alignCenter,
          Group(
            GraphicsUtils.drawText("Arrow Right - move a figure right", 300, 25),
            GraphicsUtils.drawText("Arrow Left - move a figure left", 300, 25).moveTo(0, 30),
            GraphicsUtils.drawText("Arrow Down - speed up a figure", 300, 25).moveTo(0, 60),
            GraphicsUtils.drawText("Arrow Up - rotate a figure", 300, 25).moveTo(0, 90),
            GraphicsUtils.drawText("P - pause the game", 300, 25).moveTo(0, 120)
          ).moveTo(viewport.center.x / 2, 160),
          GraphicsUtils.drawText("Press 'Space bar' to start...", viewport.width, 35, 25).moveTo(0, 420).alignCenter,
        )
      )
    } else if (model.gameStarted) {
      var list = List(
        model.gameField.draw().moveTo(model.gameField.getPosition)
          .addChild(GraphicsUtils.drawTakenPositions(model.gameFieldModel.getTakenPositions(), model.gameField.cellSize))
          .addChild(model.currentPiece.draw()),
        GraphicsUtils.drawText(s"score: ${model.score}", viewport.width, 20).moveTo(0, 10).alignCenter,
      )

      if (model.paused) {
        list = list ::: List(GraphicsUtils.drawText("PAUSE", viewport.width, 50, 50).moveTo(0, viewport.center.y / 2 ).alignCenter)
      }

      list
    } else if (model.gameOver) {
      List(
        GraphicsUtils.drawText("Game Over", viewport.width, 45, 40).moveTo(0, 100).alignCenter,
        GraphicsUtils.drawText(s"Your score: ${model.score}", viewport.width, 30, 25).moveTo(0, 170).alignCenter,
        GraphicsUtils.drawText(s"Your best score: ${model.bestScore}", viewport.width, 30, 25).moveTo(0, 210).alignCenter,
        GraphicsUtils.drawText("Press 'Space bar' to continue...", viewport.width, 35, 25).moveTo(0, 420).alignCenter,
      )
    } else {
      List()
    }
  }
}
