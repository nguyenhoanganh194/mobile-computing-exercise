package com.example.composetutorial.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.composetutorial.Message

class AppDatabase {

    companion object {
        @Volatile private var instance: AppDatabase? = null


        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database-name"
            ).build()
        }


        /**
         * SampleData for Jetpack Compose Tutorial
         */
        object SampleData {
            // Sample conversation data
            val conversationSample = listOf(
                Message(
                    "Lexi",
                    "Test...Test...Test..."
                ),
                Message(
                    "Lexi",
                    """List of Android versions:
            |Android KitKat (API 19)
            |Android Lollipop (API 21)
            |Android Marshmallow (API 23)
            |Android Nougat (API 24)
            |Android Oreo (API 26)
            |Android Pie (API 28)
            |Android 10 (API 29)
            |Android 11 (API 30)
            |Android 12 (API 31)""".trim()
                ),
                Message(
                    "Lexi",
                    """I think Kotlin is my favorite programming language.
            |It's so much fun!""".trim()
                ),
                Message(
                    "Lexi",
                    "Searching for alternatives to XML layouts..."
                ),
                Message(
                    "Lexi",
                    """Hey, take a look at Jetpack Compose, it's great!
            |It's the Android's modern toolkit for building native UI.
            |It simplifies and accelerates UI development on Android.
            |Less code, powerful tools, and intuitive Kotlin APIs :)""".trim()
                ),
                Message(
                    "Lexi",
                    "It's available from API 21+ :)"
                ),
                Message(
                    "Lexi",
                    "Writing Kotlin for UI seems so natural, Compose where have you been all my life?"
                ),
                Message(
                    "Lexi",
                    "Android Studio next version's name is Arctic Fox"
                ),
                Message(
                    "Lexi",
                    "Android Studio Arctic Fox tooling for Compose is top notch ^_^"
                ),
                Message(
                    "Lexi",
                    "I didn't know you can now run the emulator directly from Android Studio"
                ),
                Message(
                    "Lexi",
                    "Compose Previews are great to check quickly how a composable layout looks like"
                ),
                Message(
                    "Lexi",
                    "Previews are also interactive after enabling the experimental setting"
                ),
                Message(
                    "Lexi",
                    "Have you tried writing build.gradle with KTS?"
                ),
            )
        }

    }
    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String?,
        @ColumnInfo(name = "last_name") val lastName: String?
    )

    @Dao
    interface UserDao{
        @Query("SELECT * FROM user")
        fun getAll(): List<User>

        @Query("SELECT * FROM user WHERE uid IN (:userIds)")
        fun loadAllByIds(userIds: IntArray): List<User>

        @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
                "last_name LIKE :last LIMIT 1")
        fun findByName(first: String, last: String): User

        @Insert
        fun insertAll(vararg users: User)

        @Delete
        fun delete(user: User)

    }

    @Database(entities = [User::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): UserDao
    }



}