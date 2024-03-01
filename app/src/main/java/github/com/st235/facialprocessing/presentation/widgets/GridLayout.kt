package github.com.st235.facialprocessing.presentation.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.max

@Composable
fun GridLayout(
    columns: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / columns

        val extraRow = if (measurables.size % columns > 0) { 1 } else { 0 }
        val rowsCount = measurables.size / columns + extraRow

        val rowHeights = IntArray(size = rowsCount)

        val maxItemHeights = if (constraints.maxHeight == Constraints.Infinity) {
            Constraints.Infinity
        } else {
            constraints.maxHeight / rowsCount
        }

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = itemWidth,
                maxHeight = maxItemHeights
            ))
        }

        placeables.forEachIndexed { index, placeable ->
            val row = index / columns
            rowHeights[row] = max(rowHeights[row], placeable.height)
        }

        val height = rowHeights.sum()
        val rowYOffsets = IntArray(size = placeables.size / columns + extraRow)

        for (i in 1 until rowHeights.size) {
            rowYOffsets[i] = rowHeights[i - 1] + rowYOffsets[i - 1]
        }

        layout(constraints.maxWidth, height) {

            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                val row = index / columns

                placeable.placeRelative(
                    x = itemWidth * column,
                    y = rowYOffsets[row],
                )
            }

        }
    }
}
