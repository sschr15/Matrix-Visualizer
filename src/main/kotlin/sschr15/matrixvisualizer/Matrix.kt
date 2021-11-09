package sschr15.matrixvisualizer

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.jfree.svg.SVGGraphics2D
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.io.path.writeText

object MatrixConstants {
    /**
     * Units per matrix cell.
     */
    const val SCALING = 100.0

    /**
     * Thickness of borders relative to the matrix cell.
     */
    const val BORDER_SIZE = 0.1

    /**
     * Thickness of white lines relative to the matrix cell.
     */
    const val THICKNESS = 0.1
}

/**
 * Generate a matrix image from [matrix] and save it to [filename].svg and [filename].png
 */
fun generateMatrix(
    matrix: List<String>,
    heightAbove: Int,
    filename: String = "kirbtris",
    extraLinesBelow: Int = 1
) {
    val height = matrix.size + extraLinesBelow
    val width = matrix.map { it.length }.maxOrNull() ?: 0
    val fixedMatrix = matrix.map { it.padEnd(width, ' ') }.toMutableList()

    for (i in 0..extraLinesBelow) {
        fixedMatrix.add("".padStart(width, '#'))
    }

    val border = MatrixConstants.BORDER_SIZE / 2
    val borderPlusThickness = border + MatrixConstants.THICKNESS
    val errorFactor = 0.01 // fix for rounding errors creating seams in SVG images

    val svg = SVGGraphics2D(
        (width + 2) * MatrixConstants.SCALING,
        (height + 2) * MatrixConstants.SCALING
    )

    svg.color = Color.BLACK
    svg.fillRect(0, 0, width + 2, height + 2, MatrixConstants.SCALING)

    svg.color = Color.WHITE
    for (y in 0 until height) for (x in 0 until width) {
        val char = fixedMatrix[y][x]
        if (char != ' ') {
            svg.fillRect(x + 1, y + 1, 1 + errorFactor, 1 + errorFactor, MatrixConstants.SCALING)
        }
    }

    svg.color = Color.BLACK
    // draw horizontal borders
    for (y in 0..height) for (x in 0 until width) {
        val isBorder = when (y) {
            0 -> fixedMatrix[y][x] != ' '
            height -> fixedMatrix[y - 1][x] != ' '
            else -> fixedMatrix[y - 1][x] != fixedMatrix[y][x]
        }

        if (isBorder) {
            svg.fillRect(x + 1 - border, y + 1 - border, 1 + 2 * border, 2 * border, MatrixConstants.SCALING)
        }
    }

    // draw vertical borders
    for (y in 0 until height) for (x in 0..width) {
        val isBorder = when (x) {
            0 -> fixedMatrix[y][x] != ' '
            width -> fixedMatrix[y][x - 1] != ' '
            else -> fixedMatrix[y][x - 1] != fixedMatrix[y][x]
        }

        if (isBorder) {
            svg.fillRect(x + 1 - border, y + 1 - border, 2 * border, 1 + 2 * border, MatrixConstants.SCALING)
        }
    }

    // draw interiors to make them outlined and not filled
    for (y in 0..height) for (x in 0 until width) {
        if (fixedMatrix[y][x] != ' ') {
            val defaultSize = 1 - 2 * borderPlusThickness
            // single mino at least
            svg.fillRect(x + 1 + borderPlusThickness, y + 1 + borderPlusThickness, defaultSize, defaultSize, MatrixConstants.SCALING)
            var connected = false
            // try above
            for (y2 in y - 1 downTo 0) {
                when (fixedMatrix[y][x]) {
                    fixedMatrix[y2][x] -> {
                        connected = true
                        break
                    }
                    '#' -> break
                }
            }
            if (connected) svg.fillRect(x + 1 + borderPlusThickness, y + 1 - errorFactor, defaultSize, 0.5 + errorFactor, MatrixConstants.SCALING)
            connected = false
            // try below
            for (y2 in (y + 1)..height) {
                when (fixedMatrix[y][x]) {
                    fixedMatrix[y2][x] -> {
                        connected = true
                        break
                    }
                    '#' -> break
                }
            }
            if (connected) svg.fillRect(x + 1 + borderPlusThickness, y + 1.5, defaultSize, 0.5 + errorFactor, MatrixConstants.SCALING)
            connected = false
            // try left
            for (x2 in x - 1 downTo 0) {
                when (fixedMatrix[y][x]) {
                    fixedMatrix[y][x2] -> {
                        connected = true
                        break
                    }
                    '#' -> break
                }
            }
            if (connected) svg.fillRect(x + 1 - errorFactor, y + 1 + borderPlusThickness, 0.5 + errorFactor, defaultSize, MatrixConstants.SCALING)
            connected = false
            // try right
            for (x2 in (x + 1) until width) {
                when (fixedMatrix[y][x]) {
                    fixedMatrix[y][x2] -> {
                        connected = true
                        break
                    }
                    '#' -> break
                }
            }
            if (connected) svg.fillRect(x + 1.5, y + 1 + borderPlusThickness, 0.5 + errorFactor, defaultSize, MatrixConstants.SCALING)

            // eliminate "dots" in 2x2 blocks
            if (x > 0 && y > 0) {
                connected = true
                for (y2 in (y - 1)..y) for (x2 in (x - 1)..x) {
                    if (fixedMatrix[y2][x2] != fixedMatrix[y][x]) {
                        connected = false
                        break
                    }
                }
                if (connected) svg.fillRect(x + 0.5, y + 0.5, 1, 1, MatrixConstants.SCALING)
            }

        }
    }

    // edges
    svg.color = Color.WHITE
    // left
    svg.fillRect(1 - borderPlusThickness, heightAbove + 1 + borderPlusThickness, MatrixConstants.THICKNESS, height - heightAbove - 2 * border, MatrixConstants.SCALING)
    // right
    svg.fillRect(width + 1 + border, heightAbove + 1 + borderPlusThickness, MatrixConstants.THICKNESS, height - heightAbove - 2 * border, MatrixConstants.SCALING)

    // write to file
    val svgFile = Path("$filename.svg")
    svgFile.writeText(svg.svgDocument)

    // png as well
    val pngFile = Path("$filename.png")
    pngFile.outputStream().use {
        val input = TranscoderInput(svgFile.toUri().toURL().toString())
        val output = TranscoderOutput(it)
        val transcoder = PNGTranscoder()
        transcoder.transcode(input, output)
    }
}