package github.com.st235.facialprocessing.presentation.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun GridButton(
    @DrawableRes iconRes: Int,
    text: String,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(backgroundColor),
    ) {
        Icon(painterResource(iconRes), contentDescription = null)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, color = textColor)
    }
}