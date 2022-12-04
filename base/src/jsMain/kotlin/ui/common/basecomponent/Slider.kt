package ui.common.basecomponent

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.toModifier
import common.coerceIn
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.Draggable
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.DragEvent
import org.w3c.dom.Image
import org.w3c.dom.events.Event
import ui.styles.components.SliderComponent
import kotlin.math.roundToInt

@Deprecated("Should replace this with regular slider")
@Composable
fun Slider(
    value: Int,
    maxValue: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {

    val percent = value * 100f / maxValue.coerceAtLeast(1)
    var sliderWidth by remember { mutableStateOf(1) }
    var sliderClientOffset by remember { mutableStateOf(0.0) }

    fun updatePositionByLocation(clientX: Int) {
        if (clientX == 0) return
        if (sliderWidth == 0) return
        val offsetX = clientX.toFloat() - sliderClientOffset
        val newPosition = ((offsetX / sliderWidth) * maxValue).roundToInt().coerceIn(0, maxValue)
        if (newPosition != value) {
            onValueChange(newPosition)
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.role("slider")
        .onClick { event ->
            updatePositionByLocation(clientX = event.clientX)
        }
    ) {
        // Nesting containers to avoid a problem with paddings
        Box(
            contentAlignment = Alignment.Center,
            elementScope = {
                DisposableEffect(Unit) {
                    sliderWidth = scopeElement.offsetWidth
                    sliderClientOffset = scopeElement.getBoundingClientRect().left
                    onDispose { }
                }
            },
            modifier = Modifier.fillMaxHeight().width(100.percent - 16.px).margin(leftRight = 8.px)
        ) {
            Div(
                attrs = SliderComponent.TrackStyle.toModifier()
                    .width(percent.percent)
                    .align(Alignment.CenterStart)
                    .transition("width 0.1s ease")
                    .asAttributesBuilder()
            )

            Div(
                attrs = SliderComponent.InactiveTrackStyle.toModifier()
                    .width((100 - percent).percent)
                    .align(Alignment.CenterEnd)
                    .transition("width 0.1s ease")
                    .asAttributesBuilder()
            )

            Div(
                attrs = SliderComponent.ThumbStyle.toModifier()
                    .tabIndex(0)
                    .align(Alignment.CenterStart)
                    .margin(left = percent.percent - 8.px)
                    .transition("margin 0.1s ease")
                    .onKeyDown { event ->
                        if (event.getNormalizedKey() == "ArrowLeft") {
                            onValueChange((value - 1).coerceAtLeast(0))
                        } else if (event.getNormalizedKey() == "ArrowRight") {
                            onValueChange((value + 1).coerceAtMost(maxValue))
                        }
                    }
                    .draggable(Draggable.True)
                    .asAttributesBuilder {
                        onDragStart { event ->
                            // https://stackoverflow.com/a/49535378
                            val image = Image()
                            image.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAUEBAAAACwAAAAAAQABAAACAkQBADs="
                            event.dataTransfer?.setDragImage(image, 0, 0)
                            event.dataTransfer?.clearData()
                            event.dataTransfer?.setData(DRAG_EVENT_DATA_TYPE, "")
                        }
                        onDrag { event ->
                            updatePositionByLocation(event.clientX)
                        }
                    }
            )
        }
    }

    DisposableEffect(Unit) {
        val callback: (Event) -> Unit = { event ->
            if (event is DragEvent && event.dataTransfer?.types.orEmpty().contains(DRAG_EVENT_DATA_TYPE)) {
                event.preventDefault()
            }
        }
        document.addEventListener("dragover", callback)
        onDispose {
            document.removeEventListener("dragover", callback)
        }
    }
}

private const val DRAG_EVENT_DATA_TYPE = "thumb_data_type"