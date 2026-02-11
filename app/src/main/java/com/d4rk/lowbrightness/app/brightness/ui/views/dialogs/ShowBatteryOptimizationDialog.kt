package com.d4rk.lowbrightness.app.brightness.ui.views.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R

@Composable
fun ShowBatteryOptimizationDialog(
    onDismissRequest: () -> Unit,
    onContinue: () -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onOpenPowerSaverSettings: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.BatterySaver,
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(id = R.string.battery_optimization_dialog_title))
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.battery_optimization_dialog_message),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onOpenPowerSaverSettings,
                    modifier = Modifier
                        .padding(top = SizeConstants.SmallSize)
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.open_power_saver_settings))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDisableBatteryOptimization) {
                Text(text = stringResource(id = R.string.disable_battery_optimization))
            }
        },
        dismissButton = {
            TextButton(onClick = onContinue) {
                Text(text = stringResource(id = R.string.continue_without_changes))
            }
        }
    )
}
