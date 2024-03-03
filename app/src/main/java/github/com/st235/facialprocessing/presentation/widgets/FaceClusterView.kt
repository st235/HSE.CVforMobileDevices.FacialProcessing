package github.com.st235.facialprocessing.presentation.widgets

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FaceClusterView(
    face: Bitmap,
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textSize: TextUnit = 16.sp,
    iconSize: Dp = 16.dp,
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
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Image(
            face.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .width(iconSize)
                .height(iconSize)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = textSize,
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}
