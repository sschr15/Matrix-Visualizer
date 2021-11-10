package sschr15.matrixvisualizer.gui

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.stage.FileChooser
import javafx.stage.Stage
import sschr15.matrixvisualizer.Color
import sschr15.matrixvisualizer.generateMatrix
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import sschr15.matrixvisualizer.cli.main as cliMain

class JavaFX : Application() {
    var matrixGenerated = false

    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/main.fxml"))
        val svgViewer = FXMLLoader.load<Parent>(javaClass.getResource("/svg.fxml"))
        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.title = "Matrix Visualizer"

        val svgStage = Stage()
        val svgScene = Scene(svgViewer)
        svgStage.scene = svgScene

        // variables
        val svgImageView = svgViewer.lookup("#image") as ImageView

        val matrixArea = root.lookup("#matrixArea") as TextArea
        val topLineSlider = root.lookup("#topLineSlider") as Slider
        val outputField = root.lookup("#outputField") as TextField
        val extraLinesField = root.lookup("#extraLinesField") as TextField
        val importButton = root.lookup("#importButton") as Button
        val generateButton = root.lookup("#generateButton") as Button
        val showSvgButton = root.lookup("#showButton") as Button
        val foregroundColor = root.lookup("#foregroundColor") as ColorPicker
        val backgroundColor = root.lookup("#backgroundColor") as ColorPicker

        showSvgButton.onMouseClicked = EventHandler {
            if (!matrixGenerated) generateButton.onMouseClicked.handle(null)
            svgImageView.image = Path("${outputField.text}.png").inputStream().use { Image(it) }
            svgStage.show()
        }

        importButton.onMouseClicked = EventHandler {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Matrix File"
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
            val newMatrix = fileChooser.showOpenDialog(primaryStage)
                ?: return@EventHandler
            matrixArea.text = newMatrix.readText()
            if (outputField.text.isEmpty() || outputField.text == "kirbtris") outputField.text = newMatrix.nameWithoutExtension

            topLineSlider.onMouseReleased.handle(null) // refresh slider
        }

        generateButton.onMouseClicked = EventHandler {
            val matrix = matrixArea.text.split("\n")
            val extraLines = extraLinesField.text.toIntOrNull() ?: 0
            val topLine = 22 - (topLineSlider.value.toInt())
            val output = outputField.text
            val foreground = Color(foregroundColor.value.red, foregroundColor.value.green, foregroundColor.value.blue, foregroundColor.value.opacity)
            val background = Color(backgroundColor.value.red, backgroundColor.value.green, backgroundColor.value.blue, backgroundColor.value.opacity)
            generateMatrix(matrix, topLine, output, extraLines, foreground, background)
            matrixGenerated = true
        }

        topLineSlider.onMouseReleased = EventHandler {
            matrixGenerated = false
            val topLine = 22 - topLineSlider.value.toInt()
            if (topLine > matrixArea.text.lines().size)
                topLineSlider.value = 23 - matrixArea.text.lines().size.toDouble()
        }

        val eventHandler = EventHandler { _: KeyEvent ->
            // reset flag for the "show image" button
            matrixGenerated = false
        }

        matrixArea.onKeyTyped = eventHandler
        extraLinesField.onKeyTyped = eventHandler
        outputField.onKeyTyped = eventHandler

        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isNotEmpty()) {
                // assume command line instead of GUI if arguments are given
                cliMain(args)
                return
            }
            launch(JavaFX::class.java)
        }
    }
}