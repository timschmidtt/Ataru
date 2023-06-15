package de.cewe.deskstar.util

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun Long.toLocalDateTime() = Date(this).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toEpochMillis() = atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun LocalDateTime.toLocalString() = SimpleDateFormat.getDateTimeInstance().format(Date(toEpochMillis()))