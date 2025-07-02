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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Edit  
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
import kotlinx.serialization.decodeFromString
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Serializable
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val id: String,
    val isTyping: Boolean = false,
    val isTypewriterAnimated: Boolean = false
)

@Serializable
data class AIScriptData(
    val replies: List<String> = emptyList(),
    val currentIndex: Int = 0
)

@Serializable
data class AIScript(
    val name: String,
    val replies: List<String> = emptyList(),
    val currentIndex: Int = 0
)

@Serializable
data class AIScriptsCollection(
    val scripts: List<AIScript> = emptyList(),
    val currentScriptIndex: Int = 0
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

    fun saveAIScript(context: Context, script: List<String>, currentIndex: Int, filename: String = "ai_script.json") {
        try {
            val data = AIScriptData(script, currentIndex)
            val json = Json.encodeToString(data)
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun loadAIScript(context: Context, filename: String = "ai_script.json"): AIScriptData {
        return try {
            val json = context.openFileInput(filename).bufferedReader().use { it.readText() }
            Json.decodeFromString<AIScriptData>(json)
        } catch (e: Exception) {
            AIScriptData()
        }
    }
    
    fun saveAIScripts(context: Context, scripts: List<AIScript>, currentIndex: Int, filename: String = "ai_scripts.json") {
        try {
            val data = AIScriptsCollection(scripts, currentIndex)
            val json = Json.encodeToString(data)
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun loadAIScripts(context: Context, filename: String = "ai_scripts.json"): AIScriptsCollection {
        return try {
            val json = context.openFileInput(filename).bufferedReader().use { it.readText() }
            Json.decodeFromString<AIScriptsCollection>(json)
        } catch (e: Exception) {
            AIScriptsCollection()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Remove splash screen immediately - simpler approach that works on all versions
        window.setBackgroundDrawableResource(android.R.color.transparent)
        
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
    var customInitialMessage by remember { mutableStateOf("I am Daenerys Stormborn, rightful Queen of the Andals and the First Men. What do you seek from me?") }
    var tempInitialMessage by remember { mutableStateOf("") }
    var aiProfilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var userProfilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var aiScriptReplies = remember { mutableStateListOf<String>() }
    var aiScriptIndex by remember { mutableStateOf(0) }
    var aiScripts = remember { mutableStateListOf<AIScript>() }
    var currentScriptIndex by remember { mutableStateOf(0) }
    var aiDisplayName by remember { mutableStateOf("Daenerys Targaryen") }
    var greetingAuthor by remember { mutableStateOf("Daenerys Targaryen and their greeting was authored by") }
    var atQwqc by remember { mutableStateOf("@Character.Ai") }
    var keepKeyboardOpen by remember { mutableStateOf(false) }
    
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
            .getString("initial_message", "I am Daenerys Stormborn, rightful Queen of the Andals and the First Men. What do you seek from me?") ?: "I am Daenerys Stormborn, rightful Queen of the Andals and the First Men. What do you seek from me?"
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
        
        // Ensure scroll to bottom after initial message is loaded
        delay(100) // Small delay to ensure layout is complete
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
            // Additional scroll to ensure stars are visible
            delay(50)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    
    // Load AI scripts from file when screen starts
    LaunchedEffect(Unit) {
        val scriptsData = ChatStorageHelper.loadAIScripts(context)
        aiScripts.clear()
        aiScripts.addAll(scriptsData.scripts)
        currentScriptIndex = scriptsData.currentScriptIndex
        
        // Initialize with default script if none exist
        if (aiScripts.isEmpty()) {
            aiScripts.add(AIScript("Default Script", emptyList(), 0))
        }
        
        // Load current script data
        val currentScript = aiScripts.getOrNull(currentScriptIndex)
        if (currentScript != null) {
            aiScriptReplies.clear()
            aiScriptReplies.addAll(currentScript.replies)
            aiScriptIndex = currentScript.currentIndex
        }
    }
    
    // Save chat to file whenever messages change
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            ChatStorageHelper.saveChatToFile(context, messages)
        }
    }
    
    // Save AI scripts to file whenever they change
    LaunchedEffect(aiScripts, currentScriptIndex, aiScriptReplies, aiScriptIndex) {
        // Update current script with latest data
        if (aiScripts.isNotEmpty() && currentScriptIndex < aiScripts.size) {
            aiScripts[currentScriptIndex] = aiScripts[currentScriptIndex].copy(
                replies = aiScriptReplies.toList(),
                currentIndex = aiScriptIndex
            )
        }
        ChatStorageHelper.saveAIScripts(context, aiScripts.toList(), currentScriptIndex)
    }
    
    // Always scroll to the bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(Int.MAX_VALUE)
            // Additional scroll to ensure we're at the bottom
            delay(50)
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
        delay(200) // Longer delay to ensure layout is ready
        listState.animateScrollToItem(Int.MAX_VALUE)
        // Additional scroll to ensure we're at the very bottom
        delay(100)
        listState.animateScrollToItem(Int.MAX_VALUE)
        // Final scroll to ensure stars are visible
        delay(50)
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
            val aiReply = if (aiScriptIndex < aiScriptReplies.size) {
                val reply = aiScriptReplies[aiScriptIndex]
                aiScriptIndex++
                reply
            } else {
                "Not defined"
            }
            messages.add(ChatMessage(
                text = aiReply,
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

    // Load saved AI display name
    LaunchedEffect(Unit) {
        val savedName = context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
            .getString("ai_display_name", "Daenerys Targaryen") ?: "Daenerys Targaryen"
        aiDisplayName = savedName
    }
    
    // Save AI display name whenever it changes
    LaunchedEffect(aiDisplayName) {
        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
            .edit()
            .putString("ai_display_name", aiDisplayName)
            .apply()
    }

    // Load saved greeting author and @qwqc
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
        greetingAuthor = prefs.getString("greeting_author", "Daenerys Targaryen and their greeting was authored by") ?: "Daenerys Targaryen and their greeting was authored by"
        atQwqc = prefs.getString("at_qwqc", "@Character.Ai") ?: "@Character.Ai"
        keepKeyboardOpen = prefs.getBoolean("keep_keyboard_open", false)
    }
    
    // Save greeting author and @qwqc whenever they change
    LaunchedEffect(greetingAuthor, atQwqc, keepKeyboardOpen) {
        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
            .edit()
            .putString("greeting_author", greetingAuthor)
            .putString("at_qwqc", atQwqc)
            .putBoolean("keep_keyboard_open", keepKeyboardOpen)
            .apply()
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
                            text = aiDisplayName,
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
                            text = greetingAuthor,
                            color = authorTextColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = atQwqc,
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
                                            .widthIn(max = 305.dp) // Ends exactly where AI message starts (42.dp from left)
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
                                                .widthIn(max = 299.dp) // Ends exactly where user message starts (36.dp from right)
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
                                    } else if (!chatMessage.isTypewriterAnimated && chatMessage.id != "initial") {
                                        var animated by remember { mutableStateOf(false) }
                                        TypewriterText(
                                            text = chatMessage.text,
                                            color = iconTint,
                                            modifier = Modifier,
                                            typingSpeed = 30L,
                                            onAnimationEnd = {
                                                animated = true
                                                // Mark this message as animated in the messages list
                                                val idx = messages.indexOfFirst { it.id == chatMessage.id }
                                                if (idx != -1) {
                                                    messages[idx] = messages[idx].copy(isTypewriterAnimated = true)
                                                }
                                            }
                                        )
                                    } else {
                                        Text(
                                            text = chatMessage.text,
                                            color = iconTint,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                            // Only show stars for the last anime girl message, not for typing, and not for initial message
                            if (index == lastAnimeGirlIndex && !chatMessage.isTyping && chatMessage.id != "initial") {
                                    Spacer(modifier = Modifier.height(7.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        repeat(4) {
                                            Icon(
                                                painter = painterResource(R.drawable.star2),
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 8.dp)
                    ) {
                        // Refresh icon that appears when stars are shown - Floating above everything
                        val lastAnimeGirlIndex = messages.indexOfLast { !it.isFromUser }
                        val shouldShowRefresh = lastAnimeGirlIndex >= 0 && 
                            lastAnimeGirlIndex < messages.size && 
                            !messages[lastAnimeGirlIndex].isTyping && 
                            messages[lastAnimeGirlIndex].id != "initial"
                        
                        if (shouldShowRefresh) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-8).dp, y = (-40).dp)
                                    .zIndex(1000f)
                                    .size(40.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        // Refresh functionality - regenerate the last AI response
                                        if (lastAnimeGirlIndex >= 0 && lastAnimeGirlIndex < messages.size) {
                                            // Remove the last AI message
                                            messages.removeAt(lastAnimeGirlIndex)
                                            
                                            // Add typing indicator
                                            val typingMessage = ChatMessage(
                                                text = "",
                                                isFromUser = false,
                                                id = "typing",
                                                isTyping = true
                                            )
                                            messages.add(typingMessage)
                                            typingTrigger = System.currentTimeMillis()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Response",
                                    modifier = Modifier.size(32.dp),
                                    tint = iconTint
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier.align(Alignment.BottomCenter),
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
                            Spacer(modifier = Modifier.width(0.dp))
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
                                            
                                            // Hide keyboard if setting is disabled
                                            if (!keepKeyboardOpen) {
                                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                                                val currentFocus = (context as? android.app.Activity)?.currentFocus
                                                currentFocus?.let { focus ->
                                                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                                                }
                                            }
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
                                                color = playButtonColor,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.send2),
                                            contentDescription = "Send",
                                            modifier = Modifier
                                                .size(25.dp)
                                                .graphicsLayer(rotationZ = -45f)
                                                .offset(x = (-1).dp, y = 4.dp), // Shift left and down
                                            tint = Color.Black
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
                            Spacer(modifier = Modifier.width(0.dp))
                        }
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
    var settingsTabIndex by remember { mutableStateOf(0) } // 0 = Settings, 1 = AI Script
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabRow(selectedTabIndex = settingsTabIndex) {
                        Tab(selected = settingsTabIndex == 0, onClick = { settingsTabIndex = 0 }) {
                            Text("General", modifier = Modifier.padding(12.dp))
                        }
                        Tab(selected = settingsTabIndex == 1, onClick = { settingsTabIndex = 1 }) {
                            Text("AI Script", modifier = Modifier.padding(12.dp))
                        }
                    }
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
                if (settingsTabIndex == 0) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Keyboard Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Keep Keyboard Open",
                                style = MaterialTheme.typography.titleMedium,
                                color = iconTint
                            )
                            Switch(
                                checked = keepKeyboardOpen,
                                onCheckedChange = { keepKeyboardOpen = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = playButtonColor,
                                    checkedTrackColor = playButtonColor.copy(alpha = 0.5f),
                                    uncheckedThumbColor = iconTint,
                                    uncheckedTrackColor = iconTint.copy(alpha = 0.3f)
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Initial Message
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
                        
                        // AI Display Name
                        Text(
                            text = "AI Display Name",
                            style = MaterialTheme.typography.titleMedium,
                            color = iconTint
                        )
                        TextField(
                            value = aiDisplayName,
                            onValueChange = { aiDisplayName = it },
                            placeholder = { Text("Enter AI name...", color = placeholderColor) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
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
                        
                        // Greeting Author
                        Text(
                            text = "Greeting Author",
                            style = MaterialTheme.typography.titleMedium,
                            color = iconTint
                        )
                        TextField(
                            value = greetingAuthor,
                            onValueChange = { greetingAuthor = it },
                            placeholder = { Text("Enter greeting author...", color = placeholderColor) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
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
                        
                        // @qwqc
                        Text(
                            text = "@qwqc",
                            style = MaterialTheme.typography.titleMedium,
                            color = iconTint
                        )
                        TextField(
                            value = atQwqc,
                            onValueChange = { atQwqc = it },
                            placeholder = { Text("Enter @qwqc...", color = placeholderColor) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
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
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Profile Pictures Section (Compact)
                        Text(
                            text = "Profile Pictures",
                            style = MaterialTheme.typography.titleMedium,
                            color = iconTint
                        )
                        
                        // AI Profile Picture
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("AI:", color = iconTint, modifier = Modifier.width(40.dp))
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
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
                                            .size(20.dp)
                                            .align(Alignment.Center),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { aiImagePicker.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = playButtonColor)
                            ) {
                                Text("Select", color = Color.Black, fontSize = 12.sp)
                            }
                            if (aiProfilePictureUri != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        aiProfilePictureUri = null
                                        ChatStorageHelper.deleteImageFromLocalStorage(context, "ai")
                                        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                            .edit()
                                            .remove("ai_profile_picture")
                                            .apply()
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // User Profile Picture
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("User:", color = iconTint, modifier = Modifier.width(40.dp))
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
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
                                            .size(20.dp)
                                            .align(Alignment.Center),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { userImagePicker.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = playButtonColor)
                            ) {
                                Text("Select", color = Color.Black, fontSize = 12.sp)
                            }
                            if (userProfilePictureUri != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        userProfilePictureUri = null
                                        ChatStorageHelper.deleteImageFromLocalStorage(context, "user")
                                        context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                            .edit()
                                            .remove("user_profile_picture")
                                            .apply()
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                } else {
                    // AI Script Tab
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Script Selection and Management
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Scripts", style = MaterialTheme.typography.titleMedium, color = iconTint)
                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = {
                                    aiScripts.add(AIScript("New Script", emptyList(), 0))
                                    currentScriptIndex = aiScripts.size - 1
                                    aiScriptReplies.clear()
                                    aiScriptIndex = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = playButtonColor)
                            ) {
                                Text("New Script", color = Color.Black, fontSize = 12.sp)
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Script Dropdown
                        var expanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = sentBubbleColor)
                            ) {
                                Text(
                                    aiScripts.getOrNull(currentScriptIndex)?.name ?: "No Scripts",
                                    color = iconTint,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", tint = iconTint)
                            }
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(sentBubbleColor)
                            ) {
                                aiScripts.forEachIndexed { index, script ->
                                    var isRenaming by remember { mutableStateOf(false) }
                                    var newName by remember { mutableStateOf(script.name) }
                                    
                                    if (isRenaming) {
                                        DropdownMenuItem(
                                            text = { 
                                                TextField(
                                                    value = newName,
                                                    onValueChange = { newName = it },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    singleLine = true,
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent,
                                                        focusedTextColor = iconTint,
                                                        unfocusedTextColor = iconTint,
                                                        focusedPlaceholderColor = placeholderColor,
                                                        unfocusedPlaceholderColor = placeholderColor,
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent
                                                    )
                                                )
                                            },
                                            onClick = { },
                                            trailingIcon = {
                                                Row {
                                                    IconButton(
                                                        onClick = {
                                                            if (newName.isNotBlank()) {
                                                                aiScripts[index] = aiScripts[index].copy(name = newName)
                                                            }
                                                            isRenaming = false
                                                        }
                                                    ) {
                                                        Icon(Icons.Default.Check, contentDescription = "Save", tint = iconTint, modifier = Modifier.size(16.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            newName = script.name
                                                            isRenaming = false
                                                        }
                                                    ) {
                                                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Red, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            },
                                            modifier = Modifier.background(
                                                if (index == currentScriptIndex) playButtonColor.copy(alpha = 0.3f) else Color.Transparent
                                            )
                                        )
                                    } else {
                                        DropdownMenuItem(
                                            text = { 
                                                Text(script.name, color = iconTint)
                                            },
                                            onClick = {
                                                currentScriptIndex = index
                                                aiScriptReplies.clear()
                                                aiScriptReplies.addAll(script.replies)
                                                aiScriptIndex = script.currentIndex
                                                expanded = false
                                            },
                                            trailingIcon = {
                                                Row {
                                                    IconButton(
                                                        onClick = {
                                                            isRenaming = true
                                                        }
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = iconTint, modifier = Modifier.size(16.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            // Don't allow deleting the last script
                                                            if (aiScripts.size > 1) {
                                                                aiScripts.removeAt(index)
                                                                // If we deleted the current script, switch to the first available script
                                                                if (currentScriptIndex >= aiScripts.size) {
                                                                    currentScriptIndex = 0
                                                                }
                                                                // Load the current script data
                                                                val currentScript = aiScripts.getOrNull(currentScriptIndex)
                                                                if (currentScript != null) {
                                                                    aiScriptReplies.clear()
                                                                    aiScriptReplies.addAll(currentScript.replies)
                                                                    aiScriptIndex = currentScript.currentIndex
                                                                }
                                                                expanded = false
                                                            }
                                                        }
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            },
                                            modifier = Modifier.background(
                                                if (index == currentScriptIndex) playButtonColor.copy(alpha = 0.3f) else Color.Transparent
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Current Script Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Current Script", style = MaterialTheme.typography.titleMedium, color = iconTint)
                            Spacer(Modifier.weight(1f))
                            
                            // Restart from any position
                            var showRestartDialog by remember { mutableStateOf(false) }
                            Button(
                                onClick = { showRestartDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = playButtonColor)
                            ) {
                                Text("Restart from...", color = Color.Black, fontSize = 12.sp)
                            }
                            
                            if (showRestartDialog) {
                                AlertDialog(
                                    onDismissRequest = { showRestartDialog = false },
                                    title = { Text("Restart from Position", color = iconTint) },
                                    text = {
                                        Column {
                                            Text("Select position to restart from:", color = iconTint)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            LazyColumn(
                                                modifier = Modifier.heightIn(max = 200.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(aiScriptReplies.size + 1) { idx ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                aiScriptIndex = idx
                                                                showRestartDialog = false
                                                            }
                                                            .padding(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            "Position ${idx + 1}",
                                                            color = iconTint,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        if (idx < aiScriptReplies.size) {
                                                            Text(
                                                                aiScriptReplies[idx].take(30) + if (aiScriptReplies[idx].length > 30) "..." else "",
                                                                color = placeholderColor,
                                                                fontSize = 12.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showRestartDialog = false }) {
                                            Text("Cancel", color = iconTint)
                                        }
                                    },
                                    containerColor = backgroundColor
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Current Position Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Current Position: ${aiScriptIndex + 1}/${aiScriptReplies.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = placeholderColor
                            )
                            Spacer(Modifier.weight(1f))
                            if (aiScriptReplies.isNotEmpty()) {
                                Button(
                                    onClick = { aiScriptIndex = 0 },
                                    colors = ButtonDefaults.buttonColors(containerColor = playButtonColor)
                                ) {
                                    Text("Reset to 1", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Scrollable list of replies
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(aiScriptReplies.size) { idx ->
                                val reply = aiScriptReplies[idx]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (idx == aiScriptIndex) playButtonColor.copy(alpha = 0.3f) else Color.Transparent)
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (idx == aiScriptIndex) {
                                        Text("→", color = playButtonColor, modifier = Modifier.padding(end = 4.dp))
                                    } else {
                                        Spacer(Modifier.width(16.dp))
                                    }
                                    var editText by remember { mutableStateOf(reply) }
                                    var isEditing by remember { mutableStateOf(false) }
                                    if (isEditing) {
                                        TextField(
                                            value = editText,
                                            onValueChange = { editText = it },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = sentBubbleColor,
                                                unfocusedContainerColor = sentBubbleColor,
                                                focusedTextColor = iconTint,
                                                unfocusedTextColor = iconTint,
                                                focusedPlaceholderColor = placeholderColor,
                                                unfocusedPlaceholderColor = placeholderColor,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent
                                            )
                                        )
                                        IconButton(onClick = {
                                            aiScriptReplies[idx] = editText
                                            isEditing = false
                                        }) {
                                            Icon(Icons.Default.Check, contentDescription = "Save", tint = iconTint)
                                        }
                                    } else {
                                        Text(
                                            reply, 
                                            modifier = Modifier.weight(1f),
                                            color = iconTint,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        IconButton(onClick = { isEditing = true }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = iconTint)
                                        }
                                    }
                                    IconButton(onClick = { aiScriptReplies.removeAt(idx) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Add new reply section
                        var newReply by remember { mutableStateOf("") }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = newReply,
                                onValueChange = { newReply = it },
                                placeholder = { Text("Add new reply...", color = placeholderColor) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newReply.isNotBlank()) {
                                        aiScriptReplies.add(newReply)
                                        newReply = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = playButtonColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add", color = Color.Black)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                messages.clear()
                                ChatStorageHelper.saveChatToFile(context, messages)
                                // Instantly send the initial message
                                messages.add(ChatMessage(
                                    text = customInitialMessage,
                                    isFromUser = false,
                                    id = "initial"
                                ))
                                showSettingsDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Chat Only")
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
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                    
                    // Hard Reset Button with Hold Animation
                    var isHolding by remember { mutableStateOf(false) }
                    var holdProgress by remember { mutableStateOf(0f) }
                    val holdDuration = 2000L // 2 seconds in milliseconds
                    
                    LaunchedEffect(isHolding) {
                        if (isHolding) {
                            while (holdProgress < 1f) {
                                delay(16) // ~60fps
                                holdProgress += 16f / holdDuration
                            }
                            // Reset completed
                            holdProgress = 0f
                            isHolding = false
                            
                            // Clear all shared preferences
                            context.getSharedPreferences("chat_settings", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply()
                            
                            // Delete all local files
                            context.deleteFile("chat.json")
                            context.deleteFile("ai_script.json")
                            ChatStorageHelper.deleteImageFromLocalStorage(context, "ai")
                            ChatStorageHelper.deleteImageFromLocalStorage(context, "user")
                            
                            // Reset all state variables to defaults
                            customInitialMessage = "I am Daenerys Stormborn, rightful Queen of the Andals and the First Men. What do you seek from me?"
                            tempInitialMessage = "I am Daenerys Stormborn, rightful Queen of the Andals and the First Men. What do you seek from me?"
                            aiDisplayName = "Daenerys Targaryen"
                            greetingAuthor = "Daenerys Targaryen and their greeting was authored by"
                            atQwqc = "@Character.Ai"
                            aiProfilePictureUri = null
                            userProfilePictureUri = null
                            aiScriptReplies.clear()
                            aiScriptIndex = 0
                            
                            // Clear messages and add default initial message
                            messages.clear()
                            messages.add(ChatMessage(
                                text = customInitialMessage,
                                isFromUser = false,
                                id = "initial"
                            ))
                            
                            showSettingsDialog = false
                        } else {
                            holdProgress = 0f
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                color = if (isHolding) Color.Red.copy(alpha = 0.8f) else Color.Red,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)
                            ) {
                                // This will be handled by the pointer input
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isHolding = true
                                        try {
                                            awaitRelease()
                                        } finally {
                                            isHolding = false
                                        }
                                    }
                                )
                            }
                    ) {
                        // Progress indicator
                        if (isHolding) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = Color.White.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(holdProgress)
                                    .background(
                                        color = Color.White.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                        
                        // Button content
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHolding) "Hold to Reset..." else "Hard Reset (All Settings & Files)",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
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

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    typingSpeed: Long = 30L, // ms per character
    onAnimationEnd: (() -> Unit)? = null
) {
    var visibleText by remember { mutableStateOf("") }
    val finished = visibleText.length == text.length

    LaunchedEffect(text) {
        visibleText = ""
        for (i in 1..text.length) {
            visibleText = text.substring(0, i)
            delay(typingSpeed)
        }
        onAnimationEnd?.invoke()
    }
    Text(
        text = visibleText,
        modifier = modifier,
        color = color
    )
}