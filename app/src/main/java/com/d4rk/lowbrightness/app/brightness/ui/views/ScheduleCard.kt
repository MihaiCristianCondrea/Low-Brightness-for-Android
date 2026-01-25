package com.d4rk.lowbrightness.app.brightness.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.d4rk.android.libs.apptoolkit.core.ui.views.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.views.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.views.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.fragmentActivity
import com.d4rk.lowbrightness.app.brightness.domain.services.SchedulerService
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleCard() {
    val context = LocalContext.current
    val appContext = context.applicationContext

    // ✅ Use Compose resources (no LocalContext.getString inside effects)
    val remainingToLightenLabel = stringResource(R.string.time_remaining_to_lighten_label)
    val remainingToDarkenLabel = stringResource(R.string.time_remaining_to_darken_label)

    val initialStart = remember { SchedulerService.getCalendarForStart(appContext) }
    val initialEnd = remember { SchedulerService.getCalendarForEnd(appContext) }

    var enabled by remember { mutableStateOf(SchedulerService.isEnabled(appContext)) }
    var startHour by remember { mutableIntStateOf(initialStart.get(Calendar.HOUR_OF_DAY)) }
    var startMinute by remember { mutableIntStateOf(initialStart.get(Calendar.MINUTE)) }
    var endHour by remember { mutableIntStateOf(initialEnd.get(Calendar.HOUR_OF_DAY)) }
    var endMinute by remember { mutableIntStateOf(initialEnd.get(Calendar.MINUTE)) }

    var remaining by remember { mutableStateOf("") }

    LaunchedEffect(
        enabled,
        startHour,
        startMinute,
        endHour,
        endMinute,
        remainingToLightenLabel,
        remainingToDarkenLabel,
    ) {
        if (!enabled) {
            remaining = ""
            return@LaunchedEffect
        }

        while (isActive) {
            val now = Calendar.getInstance()

            val start = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val end = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val startMinutesOfDay = startHour * 60 + startMinute
            val endMinutesOfDay = endHour * 60 + endMinute
            val crossesMidnight = endMinutesOfDay <= startMinutesOfDay

            if (crossesMidnight) {
                // end is "tomorrow"
                end.add(Calendar.DAY_OF_YEAR, 1)

                // if it's after midnight but before today's end-time, we're inside the interval started yesterday
                val endToday = (end.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
                if (now.before(start) && now.before(endToday)) {
                    start.add(Calendar.DAY_OF_YEAR, -1)
                    end.add(Calendar.DAY_OF_YEAR, -1)
                }
            }

            val nowMs = now.timeInMillis
            val startMs = start.timeInMillis
            val endMs = end.timeInMillis

            val isInInterval = nowMs in startMs..<endMs

            remaining = if (isInInterval) {
                val diff = (endMs - nowMs).coerceAtLeast(0L)
                "${remainingToLightenLabel}: ${formatDurationHms(diff)}"
            } else {
                val nextStart = (start.clone() as Calendar).apply {
                    if (nowMs >= endMs) add(Calendar.DAY_OF_YEAR, 1)
                }
                val diff = (nextStart.timeInMillis - nowMs).coerceAtLeast(0L)
                "${remainingToDarkenLabel}: ${formatDurationHms(diff)}"
            }

            delay(1_000)
        }
    }

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SizeConstants.SmallSize + SizeConstants.ExtraTinySize)
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
                    text = stringResource(id = R.string.schedule),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {
                Text(
                    text = stringResource(id = R.string.summary_scheduler),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        if (enabled) {
                            SchedulerService.disable(appContext)
                            remaining = ""
                        } else {
                            SchedulerService.enable(appContext)
                        }
                        enabled = !enabled
                    },
                    modifier = Modifier
                        .padding(top = SizeConstants.SmallSize + SizeConstants.ExtraTinySize)
                        .align(Alignment.CenterHorizontally)
                        .animateContentSize()
                        .bounceClick()
                ) {
                    Icon(
                        modifier = Modifier.size(SizeConstants.ButtonIconSize),
                        imageVector = Icons.Outlined.PowerSettingsNew,
                        contentDescription = null
                    )
                    ButtonIconSpacer()
                    Text(
                        text = if (enabled) stringResource(id = R.string.disable_scheduler)
                        else stringResource(id = R.string.enable_scheduler),
                        modifier = Modifier.animateContentSize()
                    )
                }

                AnimatedVisibility(
                    visible = enabled,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Text(
                            modifier = Modifier.padding(top = SizeConstants.SmallSize + SizeConstants.ExtraTinySize),
                            text = stringResource(id = R.string.enabled_only_during_this_interval),
                            textAlign = TextAlign.Center
                        )

                        val activity = context.fragmentActivity

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = SizeConstants.SmallSize + SizeConstants.ExtraTinySize)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val dlg = TimePickerDialog.newInstance({ _, h, m, _ ->
                                        startHour = h
                                        startMinute = m
                                        SchedulerService.setFrom(appContext, h, m)
                                        SchedulerService.evaluateSchedule(appContext)
                                    }, startHour, startMinute, true)

                                    activity?.let { dlg.show(it.supportFragmentManager, "from") }
                                },
                                modifier = Modifier.weight(1f).bounceClick()
                            ) {
                                Icon(
                                    modifier = Modifier.size(SizeConstants.ButtonIconSize),
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null
                                )
                                ButtonIconSpacer()
                                Text(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute))
                            }

                            SmallHorizontalSpacer()

                            OutlinedButton(
                                onClick = {
                                    val dlg = TimePickerDialog.newInstance({ _, h, m, _ ->
                                        endHour = h
                                        endMinute = m
                                        SchedulerService.setTo(appContext, h, m)
                                        SchedulerService.evaluateSchedule(appContext)
                                    }, endHour, endMinute, true)

                                    activity?.let { dlg.show(it.supportFragmentManager, "to") }
                                },
                                modifier = Modifier.weight(1f).bounceClick()
                            ) {
                                Icon(
                                    modifier = Modifier.size(SizeConstants.ButtonIconSize),
                                    imageVector = Icons.Outlined.TimerOff,
                                    contentDescription = null
                                )
                                ButtonIconSpacer()
                                Text(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute))
                            }
                        }

                        if (remaining.isNotEmpty()) {
                            Text(
                                text = remaining,
                                modifier = Modifier.padding(top = SizeConstants.SmallSize + SizeConstants.ExtraTinySize),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDurationHms(durationMillis: Long): String {
    val totalSeconds = (durationMillis / 1_000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}
