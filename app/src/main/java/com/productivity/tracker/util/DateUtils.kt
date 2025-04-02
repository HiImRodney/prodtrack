package com.productivity.tracker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*

object DateUtils {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a")
    private val DAY_FORMATTER = DateTimeFormatter.ofPattern("EEE")
    
    fun formatDate(date: LocalDate): String {
        return date.format(DATE_FORMATTER)
    }
    
    fun formatDateTime(dateTime: LocalDateTime): String {
        return "${dateTime.format(DATE_FORMATTER)} at ${dateTime.format(TIME_FORMATTER)}"
    }
    
    fun formatDayOfWeek(date: LocalDate): String {
        return date.format(DAY_FORMATTER)
    }
    
    fun getStartOfWeek(date: LocalDate = LocalDate.now()): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
    
    fun getEndOfWeek(date: LocalDate = LocalDate.now()): LocalDate {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }
    
    fun getDaysInCurrentWeek(): List<LocalDate> {
        val today = LocalDate.now()
        val startOfWeek = getStartOfWeek(today)
        return (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }
    
    fun getWeekRangeString(date: LocalDate = LocalDate.now()): String {
        val startOfWeek = getStartOfWeek(date)
        val endOfWeek = getEndOfWeek(date)
        return "${formatDate(startOfWeek)} - ${formatDate(endOfWeek)}"
    }
    
    fun daysBetween(start: LocalDate, end: LocalDate): Int {
        return ChronoUnit.DAYS.between(start, end).toInt()
    }
    
    fun minutesToReadableFormat(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        
        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}m"
        }
    }
    
    fun getLastNDays(n: Int): List<LocalDate> {
        val today = LocalDate.now()
        return (0 until n).map { today.minusDays(it.toLong()) }.reversed()
    }
    
    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }
    
    fun isYesterday(date: LocalDate): Boolean {
        return date == LocalDate.now().minusDays(1)
    }
}
