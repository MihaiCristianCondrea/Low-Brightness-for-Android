package com.d4rk.lowbrightness.app.brightness.ui.views.cards

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.ui.views.buttons.GeneralOutlinedButton
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.model.PromotedApp

@Composable
fun PromotedAppCard(
    app: PromotedApp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SizeConstants.SmallSize + SizeConstants.ExtraTinySize),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(SizeConstants.MediumSize),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.promoted_app_title),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SizeConstants.LargeSize),
                verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize),
                ) {
                    AsyncImage(
                        model = app.iconLogo,
                        contentDescription = stringResource(
                            id = R.string.promoted_app_icon_description,
                            app.name
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = app.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val installLabel = stringResource(id = R.string.promoted_app_install)
                    GeneralOutlinedButton(
                        label = installLabel,
                        vectorIcon = Icons.Outlined.Shop,
                        iconContentDescription = stringResource(
                            id = R.string.promoted_app_install_icon_description
                        ),
                        onClick = {
                            val url =
                                "https://play.google.com/store/apps/details?id=${app.packageName}"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
