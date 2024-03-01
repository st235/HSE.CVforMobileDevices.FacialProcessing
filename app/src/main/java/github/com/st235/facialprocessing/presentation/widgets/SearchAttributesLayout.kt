package github.com.st235.facialprocessing.presentation.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.utils.iconRes
import github.com.st235.facialprocessing.utils.textRes
import st235.com.github.flowlayout.compose.FlowLayout
import st235.com.github.flowlayout.compose.FlowLayoutDirection

@Composable
fun SearchAttributesLayout(
    searchAttributes: Set<FaceSearchAttribute.Type>,
    modifier: Modifier = Modifier,
) {
    FlowLayout(
        direction = FlowLayoutDirection.START,
        modifier = modifier,
    ) {
        for (searchAttribute in searchAttributes) {
            SearchAttribute(
                iconRes = searchAttribute.iconRes,
                text = stringResource(searchAttribute.textRes),
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 4.dp)
            )
        }
    }
}
