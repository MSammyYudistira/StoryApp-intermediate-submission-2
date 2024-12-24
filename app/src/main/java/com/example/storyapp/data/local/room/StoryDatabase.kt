package com.example.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.data.local.room.dao.KeyDao
import com.example.storyapp.data.local.room.dao.StoryDao
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.data.local.room.entity.KeyEntity

@Database(
    entities = [StoryEntity::class, KeyEntity::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun getListStoryDetailDao(): StoryDao
    abstract fun getRemoteKeysDao(): KeyDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}