package utils

import kotlinx.serialization.json.Json
import org.cuongnv.consoleformatter.ConsoleColors
import java.io.File

object Constants {
    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    val outputDir = File("./../base/src/jsMain/kotlin/")
}