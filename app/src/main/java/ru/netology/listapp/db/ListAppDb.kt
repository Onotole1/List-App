package ru.netology.listapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.listapp.db.dao.PostDao
import ru.netology.listapp.db.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class ListAppDb : RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        private var instance: ListAppDb? = null

        fun getInstance(contextProvider: () -> Context): ListAppDb {
            return instance ?: synchronized(this) {
                instance ?: create(contextProvider().applicationContext).also { instance = it }
            }
        }

        private fun create(context: Context): ListAppDb = Room.databaseBuilder(
            context,
            ListAppDb::class.java,
            "db"
        )
            .build()
    }
}
