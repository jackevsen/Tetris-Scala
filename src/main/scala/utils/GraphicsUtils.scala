package utils

import displayObjects.pieces.Piece
import fonts.FontBoxy
import indigo.shared.datatypes._
import indigo.shared.scenegraph.{Group, Shape, TextBox}
import model.GameFieldPosition

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
      .alignCenter
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
}
