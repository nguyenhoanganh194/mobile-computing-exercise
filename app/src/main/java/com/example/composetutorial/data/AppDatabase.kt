package com.example.composetutorial.data

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
class AppDatabase {

    class  UserRepository(private val userDao: UserDao){
        fun getUser(userID : Int) : User? {
            return userDao.readUserById(userID)
        }

        suspend fun addUser(user: User){
            userDao.addUser(user)
        }

        suspend fun updateUser(user:User){
            userDao.updateUser(user)
        }
    }

    @Entity (tableName = "user_data")
    data class User(
        @PrimaryKey(autoGenerate = true) val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String?,
        @ColumnInfo(name = "last_name") val lastName: String?,
        @ColumnInfo(name = "image") val image: ByteArray? = null
    )



    @Dao
    interface UserDao{

        @Query("SELECT * FROM user_data")
        fun getAllUsers(): List<User>


        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun addUser(user: User)


        @Query("SELECT * FROM user_data WHERE uid IN (:userId)")
        fun readUserById(userId: Int): User?

        @Update
        suspend fun updateUser(user : User)
}



    @Database(entities = [User::class], version = 1, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): UserDao

        companion object{
            @Volatile
            private var INSTANCE:AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase{
                val temp = INSTANCE;
                if(temp != null){
                    return temp
                }
                synchronized(this){
                    val instance= Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase ::class.java,
                        "user_data"
                    ).build()
                    INSTANCE = instance
                    return  instance
                }

            }
            data class Message(val author: String, val body: String)


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
    }


    @Entity (tableName = "user_post")
    data class Post(
        @PrimaryKey(autoGenerate = true) val uid: Int,
        @ColumnInfo(name = "image_path") val path: String,
        @ColumnInfo(name = "time") val time: String
    ){
        constructor(path : String, time : String): this(0,path, time)
    }


    @Dao
    interface PostDao{
        @Query("SELECT * FROM user_post")
        fun getAllPosts(): List<Post>
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun addPost(post: Post)


    }

    @Database(entities = [Post::class], version = 1, exportSchema = false)
    abstract class PostDatabase : RoomDatabase() {
        abstract fun postDao(): PostDao

        companion object{
            @Volatile
            private var INSTANCE:PostDatabase? = null

            fun getDatabase(context: Context): PostDatabase{
                val temp = INSTANCE;
                if(temp != null){
                    return temp
                }
                synchronized(this){
                    val instance= Room.databaseBuilder(
                        context.applicationContext,
                        PostDatabase ::class.java,
                        "user_post"
                    ).build()
                    INSTANCE = instance
                    return  instance
                }

            }

        }
    }

}