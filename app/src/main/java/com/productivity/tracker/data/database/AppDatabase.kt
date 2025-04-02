package com.productivity.tracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.productivity.tracker.data.database.dao.*
import com.productivity.tracker.data.database.entity.*
import com.productivity.tracker.util.DataConverters

@Database(
    entities = [
        Task::class,
        Habit::class,
        HabitLog::class,
        Skill::class,
        SkillLog::class,
        StepCount::class,
        NidgeCard::class,
        StreakRecord::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun skillDao(): SkillDao
    abstract fun stepCountDao(): StepCountDao
    abstract fun nidgeCardDao(): NidgeCardDao
}
