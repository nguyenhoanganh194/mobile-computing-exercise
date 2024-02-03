package com.example.composetutorial

// ...


import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.composetutorial.data.AppDatabase
import com.example.composetutorial.data.UserViewModel
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

import java.io.IOException

import java.io.InputStream





class MainActivity : ComponentActivity() {
    private  lateinit var mUserViewModel: UserViewModel
    private var userName = ""
    private var bitmapImage : ImageBitmap? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = AppDatabase.AppDatabase.getDatabase(applicationContext).userDao()

            initUserData(data)
        }
    }

    private suspend fun initUserData(data : AppDatabase.UserDao){
        var userData = data.readUserById(1)
        if(userData == null){
            val tempUser = AppDatabase.User(1,"Test","Test", null)
            data.addUser(tempUser)
        }
        notifyChange(data)
    }

    private suspend fun changeUserData(data : AppDatabase.UserDao, userData : AppDatabase.User){
        data.updateUser(userData)
        notifyChange(data)
    }

    private  suspend fun notifyChange(data : AppDatabase.UserDao){
        val tempUserData = data.readUserById(1)
        userName = tempUserData?.firstName.toString() + " " + tempUserData?.lastName.toString()
        try {
            val byteArray = tempUserData?.image
            if (byteArray != null) {
                val temp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                if (temp != null) {
                    bitmapImage = temp.asImageBitmap()
                }
            }
        }
        catch (e: Exception)
        {
            Log.d("MyTask", e.toString())
        }


    }


    @Preview(name = "Light Mode")
    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showBackground = true,
        name = "Dark Mode"
    )
    @Composable
    fun MyApp() {
        ComposeTutorialTheme {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "conversation") {
                composable("conversation") {
                    ConversationScreen(onNavigateToNewScreen = {
                        navController.navigate("newscreen")
                    })
                }
                composable("newscreen") {
                    NewScreen(onNavigateBack = {
                        navController.navigate("conversation"){
                            popUpTo("conversation"){
                                inclusive = true
                            }
                        }
                    })
                }
            }
        }
    }

    @Composable
    fun ConversationScreen(onNavigateToNewScreen: ()-> Unit) {
        Column {
            Button(onClick = onNavigateToNewScreen) {
                Text(text = "Change setting")
            }
            val messages = AppDatabase.AppDatabase.Companion.SampleData.conversationSample
            LazyColumn {
                items(messages) { message ->
                    MessageCard(message)
                }
            }
        }



    }

    @Composable
    fun ProfileScreen(onNavigateToFriendsList: () -> Unit)
    {


        Text("Profile")
        Button(onClick = onNavigateToFriendsList) {
            Text(text = "Go back")
        }
    }

    @Composable
    fun NewScreen(onNavigateBack: () -> Unit) {
        var firstNameText by remember { mutableStateOf(TextFieldValue("")) }
        var lastNameText by remember { mutableStateOf(TextFieldValue("")) }
        var imagePathUri by remember { mutableStateOf<Uri?>(null) }



        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                imagePathUri = uri
            }
        )
        Column {
            Text("First name")
            TextField(

                value = firstNameText,
                onValueChange = { newText ->
                    firstNameText = newText
                }
            )

            Text("Username")
            TextField(
                value = lastNameText,
                onValueChange = { newText ->
                    lastNameText = newText
                }
            )
            Button(onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Change image")
            }




            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.AppDatabase.getDatabase(applicationContext).userDao()
                    if(imagePathUri != null){
                        val iStream = contentResolver.openInputStream(imagePathUri!!)
                        if(iStream != null){
                            val inputData: ByteArray? = getBytes(iStream)
                            val data = AppDatabase.User(1,firstNameText.text, lastNameText.text, inputData)
                            Log.d("Test", "Pass")
                            changeUserData(database,data)
                        }

                    }

                }
                onNavigateBack()
            }) {
                Text("Change setting")
            }

            AsyncImage(model = imagePathUri, contentDescription = null, contentScale = ContentScale.Crop)
        }


    }



    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }




    @Composable
    fun PreviewMessageCard() {
        ComposeTutorialTheme {
            Surface {
                MessageCard(
                    msg = AppDatabase.AppDatabase.Companion.Message(
                        "Lexi",
                        "Hey, take a look at Jetpack Compose, it's great!"
                    )
                )
            }
        }
    }

    @Composable
    fun MessageCard(msg: AppDatabase.AppDatabase.Companion.Message) {
        var data by remember { mutableStateOf(userName) }


        val imageBitmap by remember { mutableStateOf(bitmapImage) }
        Row(modifier = Modifier.padding(all = 8.dp)) {
//            AsyncImage(model = imageUriz, contentDescription = null, modifier = Modifier
//                .size(40.dp)
//                .clip(CircleShape)
//                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
//            )
            DisplayImage(imageBitmap)


            Spacer(modifier = Modifier.width(8.dp))

            // We keep track if the message is expanded or not in this
            // variable
            var isExpanded by remember { mutableStateOf(false) }
            // surfaceColor will be updated gradually from one color to the other
            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            )

            // We toggle the isExpanded variable when we click on this Column


            Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                Text(
                    text = data,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                    // surfaceColor color will be changing gradually from primary to surface
                    color = surfaceColor,
                    // animateContentSize will change the Surface size gradually
                    modifier = Modifier
                        .animateContentSize()
                        .padding(1.dp)
                ) {
                    Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Composable
    fun DisplayImage(bitmap: ImageBitmap?) {
        if(bitmap != null){
            Log.d("MyTask","1")
            Image(
                painter = BitmapPainter(bitmap),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )
        }

    }
}





