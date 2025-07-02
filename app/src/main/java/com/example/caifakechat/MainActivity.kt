package com.example.caifakechat
import androidx.compose.ui.zIndex
import android.os.Bundle
import androidx.compose.ui.graphics.graphicsLayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.draw.clip

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caifakechat.ui.theme.CAIFakeChatTheme
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Delete

@Serializable
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val id: String,
    val isTyping: Boolean = false
)

object ChatStorageHelper {
    fun saveChatToFile(context: Context, messages: List<ChatMessage>, filename: String = "chat.json") {
        try {
            val json = Json.encodeToString(messages)
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            // Handle error (log it, show user message, etc.)
            e.printStackTrace()
        }
    }

    fun loadChatFromFile(context: Context, filename: String = "chat.json"): List<ChatMessage> {
        return try {
            val json = context.openFileInput(filename).bufferedReader().use { it.readText() }
            Json.decodeFromString<List<ChatMessage>>(json)
        } catch (e: Exception) {
            // Return empty list if file doesn't exist or can't be read
            emptyList()
        }
    }
    
    fun saveImageToLocalStorage(context: Context, uri: Uri, imageType: String): String? {
        return try {
            // Create filename based on type
            val filename = "${imageType}_profile.jpg"
            
            // Remove old image if exists
            context.deleteFile(filename)
            
            // Copy new image to local storage
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            // Return the local file path
            context.getFileStreamPath(filename).absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun loadImageFromLocalStorage(context: Context, imageType: String): String? {
        return try {
            val filename = "${imageType}_profile.jpg"
            val file = context.getFileStreamPath(filename)
            if (file.exists()) {
                file.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun deleteImageFromLocalStorage(context: Context, imageType: String) {
        try {
            val filename = "${imageType}_profile.jpg"
            context.deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun isValidImageUri(context: Context, uriString: String?): Boolean {
        if (uriString == null) return false
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CAIFakeChatTheme {
                AnimeGirlChatScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CAIFakeChatTheme {
        Greeting("Android")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeGirlChatScreen() {
    var message by remember { mutableStateOf("") }
    var messages = remember { mutableStateListOf<ChatMessage>() }
    var isMuted by remember { mutableStateOf(false) }
    var typingTrigger by remember { mutableStateOf(0L) }
    var backPressCount by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var customInitialMessage by remember { mutableStateOf("Hi Sewell i am very smart and cute") }
    var tempInitialMessage by remember { mutableStateOf("") }
    var aiProfilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var userProfilePictureUri by remember { mutableStateOf<Uri?>(null) }
    
    // Get context for file operations
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // LazyListState to control scrolling
    val listState = rememberLazyListState()
    
    // Track keyboard visibility
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isKeyboardVisible = imeBottom > 0
    
    // Image picker launchers
    val aiImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Save image to local storage
            val localPath = ChatStorageHelper.saveImageToLocalStorage(context, uri, "ai")
            if (localPath != null) {
                aiProfilePictureUri = Uri.fromFile(java.io.File(localPath))
                // Save local path to preferences
                context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                    .edit()
                    .putString("ai_profile_picture", localPath)
                    .apply()
            }
        }
    }
    
    val userImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Save image to local storage
            val localPath = ChatStorageHelper.saveImageToLocalStorage(context, uri, "user")
            if (localPath != null) {
                userProfilePictureUri = Uri.fromFile(java.io.File(localPath))
                // Save local path to preferences
                context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                    .edit()
                    .putString("user_profile_picture", localPath)
                    .apply()
            }
        }
    }
    
    // Load saved profile pictures
    LaunchedEffect(Unit) {
        val savedInitialMessage = context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
            .getString("initial_message", "Hi Sewell i am very smart and cute") ?: "Hi Sewell i am very smart and cute"
        customInitialMessage = savedInitialMessage
        tempInitialMessage = savedInitialMessage
        
        // Load saved profile pictures with validation
        val savedAiProfilePicture = context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
            .getString("ai_profile_picture", null)
        if (savedAiProfilePicture != null) {
            // Try to load from local storage first
            val localPath = ChatStorageHelper.loadImageFromLocalStorage(context, "ai")
            if (localPath != null) {
                aiProfilePictureUri = Uri.fromFile(java.io.File(localPath))
            } else {
                // Clear invalid path
                context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                    .edit()
                    .remove("ai_profile_picture")
                    .apply()
            }
        }
        
        val savedUserProfilePicture = context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
            .getString("user_profile_picture", null)
        if (savedUserProfilePicture != null) {
            // Try to load from local storage first
            val localPath = ChatStorageHelper.loadImageFromLocalStorage(context, "user")
            if (localPath != null) {
                userProfilePictureUri = Uri.fromFile(java.io.File(localPath))
            } else {
                // Clear invalid path
                context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                    .edit()
                    .remove("user_profile_picture")
                    .apply()
            }
        }
    }
    
    // Load chat from file when screen starts
    LaunchedEffect(Unit) {
        val savedMessages = ChatStorageHelper.loadChatFromFile(context)
        if (savedMessages.isNotEmpty()) {
            messages.clear()
            messages.addAll(savedMessages)
        } else {
            messages.clear()
            messages.add(ChatMessage(
                text = customInitialMessage,
                isFromUser = false,
                id = "initial"
            ))
        }
    }
    
    // Save chat to file whenever messages change
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            ChatStorageHelper.saveChatToFile(context, messages)
        }
    }
    
    // Always scroll to the bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(Int.MAX_VALUE)
        }
    }
    
    // Ensure stars are visible instantly when anime girl sends a real message
    val lastMessage = messages.lastOrNull()
    LaunchedEffect(lastMessage?.id) {
        if (lastMessage != null && !lastMessage.isFromUser && !lastMessage.isTyping) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    
    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible && messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    
    // Force scroll to bottom on initial composition and when keyboard opens
    LaunchedEffect(Unit) {
        // Scroll to the very bottom immediately when the screen loads
        listState.animateScrollToItem(Int.MAX_VALUE)
    }
    
    // Scroll to bottom whenever keyboard state changes
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            delay(200) // Longer delay to ensure layout is complete
            listState.animateScrollToItem(Int.MAX_VALUE)
            // Additional scroll to ensure stars are visible
            delay(100)
            listState.animateScrollToItem(Int.MAX_VALUE)
        }
    }
    
    // Add this in the composable scope (not inside the click handler)
    LaunchedEffect(typingTrigger) {
        if (messages.isNotEmpty() && messages.last().isTyping) {
            kotlinx.coroutines.delay(1500)
            messages.removeAt(messages.lastIndex)
            messages.add(ChatMessage(
                text = "Hello",
                isFromUser = false,
                id = (System.currentTimeMillis() + 1).toString()
            ))
        }
    }
    
    // Find the last anime girl message index
    val lastAnimeGirlIndex = messages.indexOfLast { !it.isFromUser }

    val profileColor = Color(0xFF1565C0)
    val playButtonColor = Color(0xFFA0C9FF) // Light blue for play icon
    val authorTextColor = Color(0xFF97BDF0)
    val chatBubbleColor = Color(0xFF1F2226) // Anime Girl bubble
    val sentBubbleColor = Color(0xFF212529) // Sent message bubble and input
    val backgroundColor = Color(0xFF141516) // Main background and behind disclaimer
    val footerColor = Color(0xFF141516) // Footer background
    val disclaimerTextColor = Color(0xFF888888)
    val iconTint = Color(0xFFE3E2E6)
    val placeholderColor = Color(0xFF83878E)
    val statusBarColor = Color(0xFF1A1C1E)
    val topBarColor = Color(0xFF212529)

    // Set status bar color
    androidx.compose.ui.platform.LocalView.current.let { view ->
        if (!view.isInEditMode) {
            val window = (view.context as? android.app.Activity)?.window
            window?.statusBarColor = statusBarColor.toArgb()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Anime Girl",
                            color = iconTint,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 4.dp)
                            .clickable {
                                backPressCount++
                                if (backPressCount >= 3) {
                                    showSettingsDialog = true
                                    backPressCount = 0
                                }
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(22.dp),
                            tint = iconTint
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.voice),
                            contentDescription = "Mute/Unmute",
                            modifier = Modifier.size(19.dp),
                            tint = iconTint
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            painter = painterResource(R.drawable.phone),
                            contentDescription = "Call",
                            modifier = Modifier.size(19.dp),
                            tint = iconTint
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            painter = painterResource(R.drawable.brain),
                            contentDescription = "Brain",
                            modifier = Modifier.size(19.dp),
                            tint = iconTint
                        )
                        Spacer(modifier = Modifier.width(25.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .offset(x = (-8).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(profileColor)
                            )
                            if (aiProfilePictureUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(context)
                                            .data(aiProfilePictureUri)
                                            .size(200, 200)
                                            .build()
                                    ),
                                    contentDescription = "AI Profile",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape),
                                    tint = iconTint
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor
                )
            )
        },
        containerColor = backgroundColor,
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(0.dp)
                    .background(Color.Black)
            )
        },
        content = { innerPadding ->
            val density = LocalDensity.current
            val imeVisible = WindowInsets.ime.getBottom(density) > 0
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPadding)
                    .let {
                        val density = LocalDensity.current
                        val imeBottom = WindowInsets.ime.getBottom(density)
                        val customImePadding =
                            if (imeBottom > 0) (imeBottom - 20).coerceAtLeast(0) else 0
                        it.padding(bottom = with(density) { customImePadding.toDp() })
                    }
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .let {
                            val density = LocalDensity.current
                            val imeBottom = WindowInsets.ime.getBottom(density)
                            if (imeBottom > 0) {
                                it.heightIn(max = 300.dp)
                            } else {
                                it
                            }
                        },
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                // Big blank space at the top
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
                
                // Author Text
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Anime Girl and their greeting was authored by",
                            color = authorTextColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "@qwqc",
                            color = authorTextColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(if (imeVisible) 14.dp else 28.dp))
                }
                
                // Messages
                itemsIndexed(messages) { index: Int, chatMessage: ChatMessage ->
                    if (chatMessage.isFromUser) {
                        // User message
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF024474), RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 3.dp,
                                        bottomEnd = 16.dp,
                                        bottomStart = 16.dp
                                    ))
                                    .padding(vertical = 6.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = chatMessage.text,
                                    color = iconTint,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                if (userProfilePictureUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(context)
                                                .data(userProfilePictureUri)
                                                .size(200, 200)
                                                .build()
                                        ),
                                        contentDescription = "User Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User Profile Picture",
                                        modifier = Modifier.size(21.dp),
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    } else {
                        // Anime Girl message
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Profile Icon and Play Button Column
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (aiProfilePictureUri != null) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(context)
                                                        .data(aiProfilePictureUri)
                                                        .size(200, 200)
                                                        .build()
                                                ),
                                                contentDescription = "Anime Girl",
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .zIndex(1f),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Anime Girl",
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .zIndex(1f),
                                                tint = iconTint
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(profileColor)
                                                .align(Alignment.BottomCenter)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (!chatMessage.isTyping) {
                                        Icon(
                                            painter = painterResource(R.drawable.play2),
                                            contentDescription = "Play Audio",
                                            tint = playButtonColor,
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clickable { /* Play audio */ }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            chatBubbleColor,
                                            RoundedCornerShape(
                                                topStart = 3.dp,
                                                topEnd = 15.dp,
                                                bottomEnd = 15.dp,
                                                bottomStart = 15.dp
                                            )
                                        )
                                        .padding(vertical = 6.dp, horizontal = 12.dp)
                                ) {
                                    if (chatMessage.isTyping) {
                                        Box(
                                            modifier = Modifier
                                                .defaultMinSize(minWidth = 64.dp, minHeight = 28.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            BouncingDots(color = iconTint)
                                        }
                                    } else {
                                        Text(
                                            text = chatMessage.text,
                                            color = iconTint,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                            // Only show stars for the last anime girl message and not for typing
                            if (index == lastAnimeGirlIndex && !chatMessage.isTyping) {
                                Spacer(modifier = Modifier.height(0.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(4) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Star",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        if (it < 3) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Message Input Bubble with Send Button - Fixed at bottom, outside of scrollable area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(sentBubbleColor, RoundedCornerShape(24.dp))
                        .height(52.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = message,
                            onValueChange = { message = it },
                            placeholder = { Text("Message...", color = placeholderColor) },
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .padding(vertical = 0.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = iconTint,
                                unfocusedTextColor = iconTint,
                                focusedPlaceholderColor = placeholderColor,
                                unfocusedPlaceholderColor = placeholderColor,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 1,
                            singleLine = true
                        )
                        AnimatedVisibility(
                            visible = message.isBlank(),
                            enter = scaleIn(animationSpec = tween(200)),
                            exit = scaleOut(animationSpec = tween(200))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.image2),
                                contentDescription = "Attach Image",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(start = 0.dp, end = 6.dp)
                                    .clickable { /* Attach image */ },
                                tint = iconTint
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedVisibility(
                            visible = message.isBlank(),
                            enter = scaleIn(animationSpec = tween(200)),
                            exit = scaleOut(animationSpec = tween(200))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.sticker),
                                contentDescription = "Emoji",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(start = 0.dp, end = 6.dp)
                                    .clickable { /* Emoji */ },
                                tint = iconTint
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            if (message.isNotBlank()) {
                                val userMessage = ChatMessage(
                                    text = message,
                                    isFromUser = true,
                                    id = System.currentTimeMillis().toString()
                                )
                                // Add typing indicator
                                val typingMessage = ChatMessage(
                                    text = "",
                                    isFromUser = false,
                                    id = "typing",
                                    isTyping = true
                                )
                                messages.add(userMessage)
                                messages.add(typingMessage)
                                message = ""
                                typingTrigger = System.currentTimeMillis()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (message.isNotBlank()) {
                        // Circle background with send icon when text is entered
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = playButtonColor, // Same light blue as play button
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                modifier = Modifier
                                    .size(25.dp)
                                    .graphicsLayer(rotationZ = -45f), // 45 degrees rotation
                                tint = Color.Black // White icon on blue background
                            )
                        }
                    } else {
                        // Original icon when no text
                        Icon(
                            painter = painterResource(R.drawable.send),
                            contentDescription = "Send",
                            modifier = Modifier.size(20.dp),
                            tint = iconTint
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
            
            // Footer - Fixed at bottom, outside of scrollable area
            if (!imeVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(footerColor)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "This is A.I. and not a real person. Treat everything it says as fiction",
                            color = disclaimerTextColor,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(end = 2.dp),
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Keyboard Arrow Down",
                            tint = disclaimerTextColor,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
        }
    )
    
    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        color = iconTint
                    )
                    IconButton(onClick = { showSettingsDialog = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = iconTint
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Initial Message",
                        style = MaterialTheme.typography.titleMedium,
                        color = iconTint
                    )
                    TextField(
                        value = tempInitialMessage,
                        onValueChange = { tempInitialMessage = it },
                        placeholder = { Text("Enter initial message...", color = placeholderColor) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = sentBubbleColor,
                            unfocusedContainerColor = sentBubbleColor,
                            focusedTextColor = iconTint,
                            unfocusedTextColor = iconTint,
                            focusedPlaceholderColor = placeholderColor,
                            unfocusedPlaceholderColor = placeholderColor,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Profile Pictures",
                        style = MaterialTheme.typography.titleMedium,
                        color = iconTint
                    )
                    
                    // AI Profile Picture Section
                    Text(
                        text = "AI Profile Picture",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // AI Profile Picture Display
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (aiProfilePictureUri != null) {
                                AsyncImage(
                                    model = aiProfilePictureUri,
                                    contentDescription = "AI Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "AI Profile Picture",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.Center),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = { aiImagePicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select AI Profile Picture")
                            }
                            
                            if (aiProfilePictureUri != null) {
                                TextButton(
                                    onClick = {
                                        aiProfilePictureUri = null
                                        // Delete from local storage
                                        ChatStorageHelper.deleteImageFromLocalStorage(context, "ai")
                                        // Clear from preferences
                                        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                            .edit()
                                            .remove("ai_profile_picture")
                                            .apply()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Remove AI Profile Picture")
                                }
                            }
                        }
                    }
                    
                    // User Profile Picture Section
                    Text(
                        text = "User Profile Picture",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // User Profile Picture Display
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (userProfilePictureUri != null) {
                                AsyncImage(
                                    model = userProfilePictureUri,
                                    contentDescription = "User Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Profile Picture",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.Center),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = { userImagePicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select User Profile Picture")
                            }
                            
                            if (userProfilePictureUri != null) {
                                TextButton(
                                    onClick = {
                                        userProfilePictureUri = null
                                        // Delete from local storage
                                        ChatStorageHelper.deleteImageFromLocalStorage(context, "user")
                                        // Clear from preferences
                                        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                            .edit()
                                            .remove("user_profile_picture")
                                            .apply()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Remove User Profile Picture")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            messages.clear()
                            ChatStorageHelper.saveChatToFile(context, messages)
                            // Also clear profile pictures from local storage
                            ChatStorageHelper.deleteImageFromLocalStorage(context, "ai")
                            ChatStorageHelper.deleteImageFromLocalStorage(context, "user")
                            aiProfilePictureUri = null
                            userProfilePictureUri = null
                            // Clear all preferences
                            context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply()
                            showSettingsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Chat History & Reset All Settings")
                    }
                    
                    Button(
                        onClick = {
                            // Save new initial message
                            customInitialMessage = tempInitialMessage
                            context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                .edit()
                                .putString("initial_message", tempInitialMessage)
                                .apply()
                            showSettingsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = playButtonColor
                        )
                    ) {
                        Text("Save")
                    }
                }
            },
            containerColor = backgroundColor,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BouncingDots(color: Color, dotSize: Dp = 8.dp, space: Dp = 6.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val delays = listOf(0, 150, 300)
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 0..2) {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, delayMillis = delays[i], easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "dot-$i"
            )
            Box(
                Modifier
                    .size(dotSize)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .background(color, CircleShape)
            )
            if (i < 2) Spacer(Modifier.width(space))
        }
    }
}