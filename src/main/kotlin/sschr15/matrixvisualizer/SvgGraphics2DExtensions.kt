package sschr15.matrixvisualizer

import org.jfree.svg.SVGGraphics2D

fun SVGGraphics2D.fillRect(x: Number, y: Number, width: Number, height: Number, scaling: Number = 1) {
    val x1 = x.toDouble() * scaling.toDouble()
    val y1 = y.toDouble() * scaling.toDouble()
    val width1 = width.toDouble() * scaling.toDouble()
    val height1 = height.toDouble() * scaling.toDouble()
    fillRect(x1.toInt(), y1.toInt(), width1.toInt(), height1.toInt())
}