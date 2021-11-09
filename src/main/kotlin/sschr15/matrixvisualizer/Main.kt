package sschr15.matrixvisualizer

import kotlin.io.path.Path
import kotlin.io.path.readLines

fun main() {
    println("If no input is provided, an example matrix will be created instead with fake inputs.")
    print("Enter the path to the file: ")
    val matrixLocation = (readLine() ?: "").ifEmpty {
        println("Enter the path to the file: matrix.txt")
        println("Enter the path to the output file: example")
        println("Enter the number of extra rows above the matrix: 2")
        println("Enter the number of extra rows below the matrix (default 1): 1")
        println("Generating")
        exampleMatrix()
        return
    }

    print("Enter the path to the output file: ")
    val outputFileName = readLine()!!
    print("Enter the number of extra rows above the matrix: ")
    val extraRowsAbove = readLine()!!.toInt()
    print("Enter the number of extra rows below the matrix (default 1): ")
    val extraRowsBelow = readLine()!!.let { if (it.isEmpty()) 1 else it.toInt() }

    println("Generating")
    generateMatrix(Path(matrixLocation).readLines(), extraRowsAbove, outputFileName, extraRowsBelow)
}

fun main(args: Array<String>) {
    if (args.size < 3) {
        main()
        return
    }
    val outputFilename = args[0]
    val heightAbove = args[1].toInt()
    val matrix = Path(args[2]).readLines()

    generateMatrix(matrix, heightAbove, outputFilename)
}

fun exampleMatrix() {
    val filename = "example"
    val heightAbove = 2
    val matrix = listOf(
        "###EEDD###",
        "###CEDD###",
        "###CBBB###",
        "###CBAA###",
        "###CAA####",
        "####E#####"
    )

    generateMatrix(matrix, heightAbove, filename)
}
