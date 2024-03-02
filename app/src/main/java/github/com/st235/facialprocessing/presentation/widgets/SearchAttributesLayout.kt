package github.com.st235.facialprocessing.presentation.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.utils.iconRes
import github.com.st235.facialprocessing.utils.textRes
import st235.com.github.flowlayout.compose.FlowLayout
import st235.com.github.flowlayout.compose.FlowLayoutDirection

@Composable
fun SearchAttributesLayout(
    searchAttributes: Set<FaceSearchAttribute.Type>,
    modifier: Modifier = Modifier,
    onSearchAttributeClick: (FaceSearchAttribute.Type) -> Unit = {},
) {
    FlowLayout(
        direction = FlowLayoutDirection.START,
        modifier = modifier,
    ) {
        for (searchAttribute in searchAttributes) {
            SearchAttributeView(
                iconRes = searchAttribute.iconRes,
                text = stringResource(searchAttribute.textRes),
                onClick = { onSearchAttributeClick(searchAttribute) },
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun SearchAttributeView(
    @DrawableRes iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {},
) {
    val cornerRadiusPx = with(LocalDensity.current) { 16.dp.toPx() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .focusable()
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(
                    backgroundColor,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier
                .width(16.dp)
                .height(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}

