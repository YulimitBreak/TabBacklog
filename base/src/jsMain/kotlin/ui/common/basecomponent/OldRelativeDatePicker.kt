package ui.common.basecomponent

import androidx.compose.runtime.Composable
import common.DateUtils
import common.coerceIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.attributes.pattern
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import ui.common.styles.MainStyle
import ui.common.styles.UtilStyle

@Composable
fun OldRelativeDatePicker(
    state: OldRelativeDatePickerState,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onStateUpdate: (OldRelativeDatePickerState) -> Unit
) {

    Div(attrs = {
        attrs?.invoke(this)
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
        }
    }) {

        when (state.mode) {
            OldRelativeDatePickerState.Mode.NONE -> Div(attrs = {
                classes(UtilStyle.centerContent)
                style {
                    height(24.px)
                }
            }) {
                Text("Never")
            }

            OldRelativeDatePickerState.Mode.SET -> Input(InputType.Date) {
                min(DateUtils.today.toString())
                value(state.rememberedDate?.toString() ?: "")
                onInput { event ->
                    onStateUpdate(state.copy(rememberedDate = event.value.takeIf { it.isNotBlank() }
                        ?.let(LocalDate::parse)))
                }
                style {
                    border(0.px)
                    height(24.px)
                }
            }


            else -> OldRelativeDateCounter(state.rememberedPeriod, state.mode) {
                onStateUpdate(state.copy(rememberedPeriod = it))
            }
        }

        Input(InputType.Range) {
            min("0")
            max(OldRelativeDatePickerState.Mode.values().size.minus(1).toString())
            style {
                width(200.px)
                property("transform", "scale(0.8)")
            }
            value(state.mode.ordinal)
            onInput {
                onStateUpdate(state.copy(mode = OldRelativeDatePickerState.Mode.values()[it.value?.toInt() ?: 0]))
            }
        }
    }

}

@Composable
private fun OldRelativeDateCounter(
    rememberedPeriod: Int,
    mode: OldRelativeDatePickerState.Mode,
    onNumberChange: (Int) -> Unit
) {

    fun choosePlural(singular: String, plural: String) = if (rememberedPeriod == 1) singular else plural

    Div(
        attrs = {
            style {
                height(24.px)
                flexDirection(FlexDirection.Row)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                display(DisplayStyle.Flex)
            }
        }
    ) {
        Button(attrs = {
            classes(MainStyle.button, MainStyle.solid)
            style {
                height(22.px)
                width(22.px)
            }
            onClick { onNumberChange((rememberedPeriod - 1).coerceIn(min = 1, max = 99)) }
        }) {
            Text("-")
        }
        TextInput(rememberedPeriod.toString()) {
            pattern("[0-9]")
            style {
                width(20.px)
                border(0.px)
                fontSize(15.px)
                textAlign("right")
            }
            onInput {
                onNumberChange((it.value.toIntOrNull() ?: rememberedPeriod).coerceIn(min = 1))
            }
        }
        Div(attrs = {
            style {
                width(48.px)
                textAlign("left")
                paddingLeft(4.px)
                paddingTop(2.px)
            }
        }) {
            when (mode) {
                OldRelativeDatePickerState.Mode.DAYS -> Text(choosePlural("day", "days"))
                OldRelativeDatePickerState.Mode.WEEKS -> Text(choosePlural("week", "weeks"))
                OldRelativeDatePickerState.Mode.MONTHS -> Text(choosePlural("month", "months"))
                OldRelativeDatePickerState.Mode.YEARS -> Text(choosePlural("year", "years"))
                else -> {}
            }
        }
        Button(attrs = {
            classes(MainStyle.button, MainStyle.solid)
            style {
                height(22.px)
                width(22.px)
            }
            onClick { onNumberChange((rememberedPeriod + 1).coerceIn(min = 1, max = 99)) }
        }) {
            Text("+")
        }
    }
}

data class OldRelativeDatePickerState(
    val mode: Mode,
    val rememberedDate: LocalDate?,
    val rememberedPeriod: Int,
) {
    enum class Mode {
        NONE,
        SET,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS,
    }

    fun toResultingDate(startingPoint: LocalDate = DateUtils.today) = when (mode) {
        Mode.NONE -> null
        Mode.SET -> rememberedDate
        Mode.DAYS -> startingPoint.plus(rememberedPeriod, DateTimeUnit.DAY)
        Mode.WEEKS -> startingPoint.plus(rememberedPeriod, DateTimeUnit.WEEK)
        Mode.MONTHS -> startingPoint.plus(rememberedPeriod, DateTimeUnit.MONTH)
        Mode.YEARS -> startingPoint.plus(rememberedPeriod, DateTimeUnit.YEAR)
    }

    val isValid get() = !(mode == Mode.NONE || (mode == Mode.SET && rememberedDate == null))

    companion object {
        fun fromInitialDate(initialDate: LocalDate?) =
            OldRelativeDatePickerState(
                if (initialDate == null) Mode.NONE else Mode.SET,
                initialDate,
                1
            )
    }
}