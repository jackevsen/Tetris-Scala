package jackevsen.utils

import jackevsen.fonts.FontBoxy
import indigo.shared.config.GameViewport
import indigo.shared.datatypes._
import indigo.shared.scenegraph.{Group, SceneNode, Shape, TextBox}
import jackevsen.model.{Game, GameFieldPosition}
import jackevsen.renderers.{GameRenderer, RenderData}

object GraphicsUtils {
  def drawGameField(width: Int, height: Int, cellSize: Int): Group = {
    val countVert = width + 1
    val countHorizont = height + 1

    val listVert: List[Shape.Line] =
      (0 until countVert).map(i => {
        Shape.Line(Point(i * cellSize, 0), Point(i * cellSize, height * cellSize), Stroke(3, RGBA.White))
      }).toList

    val listHorizont: List[Shape.Line] =
      (0 until countHorizont).map(i => {
        Shape.Line(Point(0, i * cellSize), Point(width * cellSize, i * cellSize), Stroke(3, RGBA.White))
      }).toList

    Group(listVert ::: listHorizont)
  }

  def drawGamePiece(renderData: RenderData): Group = {
    val cellSize = renderData.cellSize
    val shapes: List[Shape] = renderData.shapeCoords.map(p => {
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

  def drawScene(model: Game, viewport: GameViewport): List[SceneNode] = {
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
      val list = List(
        GameRenderer.renderGameField(model.gameField).moveTo(model.gameField.position)
          .addChild(GraphicsUtils.drawTakenPositions(model.gameFieldModel.getTakenPositions(), GameUtils.cellSize))
          .addChild(GameRenderer.renderPiece(model.currentPiece)),
        GraphicsUtils.drawText(s"score: ${model.score}", viewport.width, 20).moveTo(0, 10).alignCenter,
      )

      if (model.paused) {
        return list ::: List(GraphicsUtils.drawText("PAUSE", viewport.width, 50, 50).moveTo(0, viewport.center.y - 100).alignCenter)
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
