package jackevsen.model

import indigo.shared.datatypes.Point

case class RenderData(center: Point, shapeCoords: List[Point], cellSize: Int, pivotPoint: Point)
