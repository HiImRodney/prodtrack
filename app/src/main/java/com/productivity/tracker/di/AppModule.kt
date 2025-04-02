package com.productivity.tracker.di

import android.content.Context
import androidx.room.Room
import com.productivity.tracker.data.database.AppDatabase
import com.productivity.tracker.data.database.dao.*
import com.productivity.tracker.data.repository.*
import com.productivity.tracker.util.PointsCalculator
import com.productivity.tracker.util.StreakTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "productivity_database"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideHabitDao(database: AppDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideSkillDao(database: AppDatabase): SkillDao = database.skillDao()

    @Provides
    fun provideStepCountDao(database: AppDatabase): StepCountDao = database.stepCountDao()

    @Provides
    fun provideNidgeCardDao(database: AppDatabase): NidgeCardDao = database.nidgeCardDao()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository = TaskRepository(taskDao)

    @Provides
    @Singleton
    fun provideHabitRepository(habitDao: HabitDao): HabitRepository = HabitRepository(habitDao)

    @Provides
    @Singleton
    fun provideSkillRepository(skillDao: SkillDao): SkillRepository = SkillRepository(skillDao)

    @Provides
    @Singleton
    fun provideStepCountRepository(stepCountDao: StepCountDao): StepCountRepository = 
        StepCountRepository(stepCountDao)

    @Provides
    @Singleton
    fun provideNidgeCardRepository(nidgeCardDao: NidgeCardDao): NidgeCardRepository = 
        NidgeCardRepository(nidgeCardDao)

    @Provides
    @Singleton
    fun providePointsCalculator(): PointsCalculator = PointsCalculator()

    @Provides
    @Singleton
    fun provideStreakTracker(): StreakTracker = StreakTracker()
}
