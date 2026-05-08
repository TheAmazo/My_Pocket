package com.thisara.mypocket.ui

import android.net.Uri
import android.graphics.BitmapFactory
import android.text.format.DateFormat
import android.util.Base64
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.thisara.mypocket.R
import com.thisara.mypocket.data.AppSettings
import com.thisara.mypocket.data.AvatarCropper
import com.thisara.mypocket.data.MonthBoard
import com.thisara.mypocket.data.MonthSummary
import com.thisara.mypocket.data.Pocket
import com.thisara.mypocket.data.SavingsCell
import com.thisara.mypocket.data.canToggle
import com.thisara.mypocket.data.isLocked
import com.thisara.mypocket.data.sortRank
import com.thisara.mypocket.ui.theme.PocketBlue
import com.thisara.mypocket.ui.theme.PocketElectric
import com.thisara.mypocket.ui.theme.PocketOrange
import com.thisara.mypocket.ui.theme.PocketRose
import com.thisara.mypocket.ui.theme.PocketTheme
import com.thisara.mypocket.ui.theme.PocketYellow
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

private val GlassCardShape = RoundedCornerShape(24.dp)
private val GlassCompactShape = RoundedCornerShape(20.dp)
private val GlassPillShape = RoundedCornerShape(100.dp)

@Composable
fun MyPocketApp(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingAvatarUri by remember { mutableStateOf<Uri?>(null) }
    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.completeGoogleSignIn(result.data)
    }
    val googleAccountDeleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.completeGoogleAccountDeletion(result.data)
    }
    val profilePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        pendingAvatarUri = uri
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.needsEmailVerification) {
        if (state.needsEmailVerification) {
            viewModel.refreshVerificationSilently()
        }
    }

    DisposableEffect(lifecycleOwner, state.needsEmailVerification) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && state.needsEmailVerification) {
                viewModel.refreshVerificationSilently()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(appBackgroundBrush())
                .padding(padding)
                .safeDrawingPadding(),
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                when {
                    !state.isSignedIn -> AuthGate(
                        state = state,
                        onMode = viewModel::setAuthMode,
                        onSignIn = viewModel::signIn,
                        onSignUp = viewModel::signUp,
                        onGoogle = {
                            viewModel.googleSignInIntent()?.let(googleLauncher::launch)
                        },
                    )

                    state.needsEmailVerification -> EmailVerificationScreen(
                        email = state.user?.email.orEmpty(),
                        onSendAgain = viewModel::sendVerificationEmail,
                        onRefresh = viewModel::refreshVerification,
                        onSignOut = viewModel::signOut,
                    )

                    state.needsPocket || state.showPocketPicker -> PocketSelectorScreen(
                        pockets = state.pockets,
                        currentPocketId = state.pocket?.id,
                        onSelect = viewModel::switchPocket,
                        onCreate = viewModel::createPocket,
                        onRename = viewModel::renamePocket,
                        onDelete = viewModel::deletePocket,
                        onSignOut = viewModel::signOut,
                    )

                    else -> HomeScreen(
                        state = state,
                        settings = settings,
                        onTab = viewModel::setTab,
                        onShowPockets = viewModel::showPocketPicker,
                        onToggleCell = viewModel::toggleCell,
                        onRepairBoard = viewModel::repairCurrentBoard,
                        onReminderEnabled = viewModel::setRemindersEnabled,
                        onReminderHour = viewModel::setReminderHour,
                        onDarkModeEnabled = viewModel::setDarkModeEnabled,
                        onSignOut = viewModel::signOut,
                        onPreviousSummaryYear = viewModel::previousSummaryYear,
                        onNextSummaryYear = viewModel::nextSummaryYear,
                        onOpenSummaryMonth = viewModel::openSummaryMonth,
                        onCloseSummaryMonth = viewModel::closeSummaryMonth,
                        onPickProfilePhoto = {
                            profilePhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        onDeleteProfilePhoto = viewModel::deleteProfilePhoto,
                        onUpdateDisplayName = viewModel::updateDisplayName,
                        onChangePassword = viewModel::changePassword,
                        onDeleteAccountWithPassword = viewModel::deleteAccountWithPassword,
                        onDeleteGoogleAccount = {
                            viewModel.googleAccountDeleteIntent()?.let(googleAccountDeleteLauncher::launch)
                        },
                    )
                }
            }

            if (state.loading) {
                val style = PocketTheme.colors
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(style.loadingOverlay),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            pendingAvatarUri?.let { uri ->
                AvatarCropDialog(
                    sourceUri = uri,
                    onDismiss = { pendingAvatarUri = null },
                    onSave = { zoom, offsetX, offsetY ->
                        pendingAvatarUri = null
                        viewModel.updateProfilePhoto(
                            uri = uri,
                            zoom = zoom,
                            offsetX = offsetX,
                            offsetY = offsetY,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun appBackgroundBrush(): Brush {
    val style = PocketTheme.colors
    return Brush.verticalGradient(
        colors = style.backgroundGradient,
    )
}

@Composable
private fun AuthGate(
    state: MainUiState,
    onMode: (AuthMode) -> Unit,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onGoogle: () -> Unit,
) {
    CenteredContent(scrollable = true) {
        when (state.authMode) {
            AuthMode.Landing -> LandingScreen(
                onSignUp = { onMode(AuthMode.SignUp) },
                onSignIn = { onMode(AuthMode.SignIn) },
            )

            AuthMode.SignIn -> SignInScreen(
                onBack = { onMode(AuthMode.Landing) },
                onSignIn = onSignIn,
                onGoogle = onGoogle,
                onCreateAccount = { onMode(AuthMode.SignUp) },
            )

            AuthMode.SignUp -> SignUpScreen(
                onBack = { onMode(AuthMode.Landing) },
                onSignUp = onSignUp,
                onGoogle = onGoogle,
                onHaveAccount = { onMode(AuthMode.SignIn) },
            )
        }
    }
}

@Composable
private fun LandingScreen(onSignUp: () -> Unit, onSignIn: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth(),
    ) {
        AppIconMark(size = 112.dp)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "My Pocket",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "A personal monthly board for saving money without messy marks or wrong totals.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth < 360.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Rounded.PersonAdd, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Get started")
                    }
                    OutlinedButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.AutoMirrored.Rounded.Login, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Log in")
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onSignUp) {
                        Icon(Icons.Rounded.PersonAdd, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Get started")
                    }
                    OutlinedButton(onClick = onSignIn) {
                        Icon(Icons.AutoMirrored.Rounded.Login, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Log in")
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIconMark(size: Dp) {
    val style = PocketTheme.colors
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    style.appIconGradient,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "My Pocket",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SignInScreen(
    onBack: () -> Unit,
    onSignIn: (String, String) -> Unit,
    onGoogle: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthPanel(title = "Log in", onBack = onBack) {
        EmailField(value = email, onValueChange = { email = it })
        PasswordField(label = "Password", value = password, onValueChange = { password = it })
        Button(
            onClick = { onSignIn(email, password) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.AutoMirrored.Rounded.Login, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Log in")
        }
        GoogleButton(onGoogle)
        TextButton(onClick = onCreateAccount, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Create an account")
        }
    }
}

@Composable
private fun SignUpScreen(
    onBack: () -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onGoogle: () -> Unit,
    onHaveAccount: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AuthPanel(title = "Create account", onBack = onBack) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        EmailField(value = email, onValueChange = { email = it })
        PasswordField(label = "Password", value = password, onValueChange = { password = it })
        PasswordField(label = "Confirm password", value = confirm, onValueChange = { confirm = it })
        Button(
            onClick = { onSignUp(name, email, password, confirm) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Rounded.PersonAdd, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Sign up")
        }
        GoogleButton(onGoogle)
        TextButton(onClick = onHaveAccount, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("I already have an account")
        }
    }
}

@Composable
private fun AuthPanel(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        AppIconMark(size = 72.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun EmailField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun GoogleButton(onGoogle: () -> Unit) {
    OutlinedButton(onClick = onGoogle, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text("Continue with Google")
    }
}

@Composable
private fun EmailVerificationScreen(
    email: String,
    onSendAgain: () -> Unit,
    onRefresh: () -> Unit,
    onSignOut: () -> Unit,
) {
    CenteredContent(scrollable = true) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            AppIconMark(size = 72.dp)
            StatusDot(color = PocketYellow)
            Text(
                text = "Verify your email",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "We sent a verification link to $email. Open the link, then refresh here.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onRefresh) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Refresh")
                }
                OutlinedButton(onClick = onSendAgain) {
                    Text("Send again")
                }
            }
            TextButton(onClick = onSignOut) {
                Text("Use another account")
            }
        }
    }
}

@Composable
private fun PocketSelectorScreen(
    pockets: List<Pocket>,
    currentPocketId: String?,
    onSelect: (String) -> Unit,
    onCreate: (String) -> Unit,
    onRename: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onSignOut: () -> Unit,
) {
    val style = PocketTheme.colors
    var pocketName by remember { mutableStateOf("My Pocket") }
    var renameTarget by remember { mutableStateOf<Pocket?>(null) }
    var renameName by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<Pocket?>(null) }
    var deleteConfirmation by remember { mutableStateOf("") }

    CenteredContent {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                AppIconMark(size = 72.dp)
            }
            item {
                Text(
                    text = "Your pockets",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (pockets.isNotEmpty()) {
                items(pockets, key = { it.id }) { pocket ->
                    Surface(
                        color = if (pocket.id == currentPocketId) style.glassSelected else style.glassStrong,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = GlassCardShape,
                        border = BorderStroke(
                            1.dp,
                            if (pocket.id == currentPocketId) style.glassStrokeStrong else style.glassStroke,
                        ),
                        tonalElevation = 0.dp,
                        shadowElevation = 14.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(pocket.id) },
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pocket.name, fontWeight = FontWeight.Black)
                                Text("Personal savings pocket", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(
                                onClick = {
                                    renameTarget = pocket
                                    renameName = pocket.name
                                },
                            ) {
                                Icon(Icons.Rounded.Edit, contentDescription = "Rename pocket", tint = PocketBlue)
                            }
                            IconButton(
                                onClick = {
                                    deleteTarget = pocket
                                    deleteConfirmation = ""
                                },
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete pocket", tint = PocketRose)
                            }
                            if (pocket.id == currentPocketId) {
                                Icon(Icons.Rounded.Check, contentDescription = null, tint = PocketBlue)
                            }
                        }
                    }
                }
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (pockets.isEmpty()) "Create your first pocket" else "Create another pocket",
                            fontWeight = FontWeight.Bold,
                        )
                        OutlinedTextField(
                            value = pocketName,
                            onValueChange = { pocketName = it },
                            label = { Text("Pocket name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Button(onClick = { onCreate(pocketName) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Create pocket")
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PocketRose,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Sign out")
                }
            }
        }
    }

    renameTarget?.let { pocket ->
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("Rename pocket") },
            text = {
                OutlinedTextField(
                    value = renameName,
                    onValueChange = { renameName = it },
                    label = { Text("Pocket name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (renameName.isBlank()) Text("Name cannot be blank.")
                    },
                )
            },
            confirmButton = {
                Button(
                    enabled = renameName.isNotBlank(),
                    onClick = {
                        onRename(pocket.id, renameName)
                        renameTarget = null
                    },
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text("Cancel")
                }
            },
        )
    }

    deleteTarget?.let { pocket ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete pocket?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This permanently deletes all savings cells, monthly data, and summaries in ${pocket.name}.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = deleteConfirmation,
                        onValueChange = { deleteConfirmation = it },
                        label = { Text("Type delete") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = deleteConfirmation == "delete",
                    onClick = {
                        onDelete(pocket.id)
                        deleteTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PocketRose,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Delete pocket")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: MainUiState,
    settings: AppSettings,
    onTab: (HomeTab) -> Unit,
    onShowPockets: () -> Unit,
    onToggleCell: (SavingsCell) -> Unit,
    onRepairBoard: () -> Unit,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderHour: (Int) -> Unit,
    onDarkModeEnabled: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onPreviousSummaryYear: () -> Unit,
    onNextSummaryYear: () -> Unit,
    onOpenSummaryMonth: (String) -> Unit,
    onCloseSummaryMonth: () -> Unit,
    onPickProfilePhoto: () -> Unit,
    onDeleteProfilePhoto: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onDeleteAccountWithPassword: (String) -> Unit,
    onDeleteGoogleAccount: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 720.dp
        if (useRail) {
            Row(Modifier.fillMaxSize()) {
                HomeNavigationRail(selectedTab = state.selectedTab, onTab = onTab)
                HomeScaffold(
                    state = state,
                    settings = settings,
                    showBottomBar = false,
                    onTab = onTab,
                    onShowPockets = onShowPockets,
                    onToggleCell = onToggleCell,
                    onRepairBoard = onRepairBoard,
                    onReminderEnabled = onReminderEnabled,
                    onReminderHour = onReminderHour,
                    onDarkModeEnabled = onDarkModeEnabled,
                    onSignOut = onSignOut,
                    onPreviousSummaryYear = onPreviousSummaryYear,
                    onNextSummaryYear = onNextSummaryYear,
                    onOpenSummaryMonth = onOpenSummaryMonth,
                    onCloseSummaryMonth = onCloseSummaryMonth,
                    onPickProfilePhoto = onPickProfilePhoto,
                    onDeleteProfilePhoto = onDeleteProfilePhoto,
                    onUpdateDisplayName = onUpdateDisplayName,
                    onChangePassword = onChangePassword,
                    onDeleteAccountWithPassword = onDeleteAccountWithPassword,
                    onDeleteGoogleAccount = onDeleteGoogleAccount,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            HomeScaffold(
                state = state,
                settings = settings,
                showBottomBar = true,
                onTab = onTab,
                onShowPockets = onShowPockets,
                onToggleCell = onToggleCell,
                onRepairBoard = onRepairBoard,
                onReminderEnabled = onReminderEnabled,
                onReminderHour = onReminderHour,
                onDarkModeEnabled = onDarkModeEnabled,
                onSignOut = onSignOut,
                onPreviousSummaryYear = onPreviousSummaryYear,
                onNextSummaryYear = onNextSummaryYear,
                onOpenSummaryMonth = onOpenSummaryMonth,
                onCloseSummaryMonth = onCloseSummaryMonth,
                onPickProfilePhoto = onPickProfilePhoto,
                onDeleteProfilePhoto = onDeleteProfilePhoto,
                onUpdateDisplayName = onUpdateDisplayName,
                onChangePassword = onChangePassword,
                onDeleteAccountWithPassword = onDeleteAccountWithPassword,
                onDeleteGoogleAccount = onDeleteGoogleAccount,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    state: MainUiState,
    settings: AppSettings,
    showBottomBar: Boolean,
    onTab: (HomeTab) -> Unit,
    onShowPockets: () -> Unit,
    onToggleCell: (SavingsCell) -> Unit,
    onRepairBoard: () -> Unit,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderHour: (Int) -> Unit,
    onDarkModeEnabled: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onPreviousSummaryYear: () -> Unit,
    onNextSummaryYear: () -> Unit,
    onOpenSummaryMonth: (String) -> Unit,
    onCloseSummaryMonth: () -> Unit,
    onPickProfilePhoto: () -> Unit,
    onDeleteProfilePhoto: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onDeleteAccountWithPassword: (String) -> Unit,
    onDeleteGoogleAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            if (state.selectedTab != HomeTab.History) {
                HomeTopBar(
                    pocketName = state.pocket?.name.orEmpty(),
                    onShowPockets = onShowPockets,
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                HomeNavigationBar(selectedTab = state.selectedTab, onTab = onTab)
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            HomeTabContent(
                state = state,
                settings = settings,
                onToggleCell = onToggleCell,
                onRepairBoard = onRepairBoard,
                onReminderEnabled = onReminderEnabled,
                onReminderHour = onReminderHour,
                onDarkModeEnabled = onDarkModeEnabled,
                onSignOut = onSignOut,
                onPreviousSummaryYear = onPreviousSummaryYear,
                onNextSummaryYear = onNextSummaryYear,
                onOpenSummaryMonth = onOpenSummaryMonth,
                onCloseSummaryMonth = onCloseSummaryMonth,
                onPickProfilePhoto = onPickProfilePhoto,
                onDeleteProfilePhoto = onDeleteProfilePhoto,
                onUpdateDisplayName = onUpdateDisplayName,
                onChangePassword = onChangePassword,
                onDeleteAccountWithPassword = onDeleteAccountWithPassword,
                onDeleteGoogleAccount = onDeleteGoogleAccount,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(pocketName: String, onShowPockets: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onShowPockets) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Pockets")
            }
        },
        title = {
            Column {
                Text("My Pocket", fontWeight = FontWeight.Black)
                Text(
                    pocketName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

@Composable
private fun HomeNavigationBar(selectedTab: HomeTab, onTab: (HomeTab) -> Unit) {
    val style = PocketTheme.colors
    NavigationBar(
        containerColor = style.navigationGlass,
        tonalElevation = 0.dp,
    ) {
        HomeTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTab(tab) },
                icon = { Icon(tab.icon(), contentDescription = null) },
                label = { Text(tab.label()) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = PocketRose,
                    indicatorColor = PocketElectric.copy(alpha = 0.34f),
                    unselectedIconColor = style.textMuted,
                    unselectedTextColor = style.textMuted,
                ),
            )
        }
    }
}

@Composable
private fun HomeNavigationRail(selectedTab: HomeTab, onTab: (HomeTab) -> Unit) {
    val style = PocketTheme.colors
    NavigationRail(containerColor = style.navigationGlass) {
        Spacer(Modifier.height(12.dp))
        HomeTab.entries.forEach { tab ->
            NavigationRailItem(
                selected = selectedTab == tab,
                onClick = { onTab(tab) },
                icon = { Icon(tab.icon(), contentDescription = null) },
                label = { Text(tab.label()) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = PocketRose,
                    indicatorColor = PocketElectric.copy(alpha = 0.34f),
                    unselectedIconColor = style.textMuted,
                    unselectedTextColor = style.textMuted,
                ),
            )
        }
    }
}

@Composable
private fun HomeTabContent(
    state: MainUiState,
    settings: AppSettings,
    onToggleCell: (SavingsCell) -> Unit,
    onRepairBoard: () -> Unit,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderHour: (Int) -> Unit,
    onDarkModeEnabled: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onPreviousSummaryYear: () -> Unit,
    onNextSummaryYear: () -> Unit,
    onOpenSummaryMonth: (String) -> Unit,
    onCloseSummaryMonth: () -> Unit,
    onPickProfilePhoto: () -> Unit,
    onDeleteProfilePhoto: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onDeleteAccountWithPassword: (String) -> Unit,
    onDeleteGoogleAccount: () -> Unit,
) {
    when (state.selectedTab) {
        HomeTab.Board -> BoardScreen(
            board = state.board,
            todayKey = state.todayKey,
            onToggleCell = onToggleCell,
            onRepairBoard = onRepairBoard,
        )
        HomeTab.History -> SummaryScreen(
            state = state,
            onPreviousYear = onPreviousSummaryYear,
            onNextYear = onNextSummaryYear,
            onOpenMonth = onOpenSummaryMonth,
            onBackFromMonth = onCloseSummaryMonth,
        )
        HomeTab.Settings -> SettingsScreen(
            pocket = state.pocket,
            settings = settings,
            onReminderEnabled = onReminderEnabled,
            onReminderHour = onReminderHour,
            onDarkModeEnabled = onDarkModeEnabled,
            onSignOut = onSignOut,
        )
        HomeTab.Profile -> ProfileScreen(
            state = state,
            onPickPhoto = onPickProfilePhoto,
            onDeletePhoto = onDeleteProfilePhoto,
            onUpdateDisplayName = onUpdateDisplayName,
            onChangePassword = onChangePassword,
            onDeleteAccountWithPassword = onDeleteAccountWithPassword,
            onDeleteGoogleAccount = onDeleteGoogleAccount,
        )
    }
}

private fun HomeTab.label(): String {
    return if (this == HomeTab.History) "Summary" else name
}

private fun HomeTab.icon() = when (this) {
    HomeTab.Board -> Icons.Rounded.Home
    HomeTab.History -> Icons.Rounded.History
    HomeTab.Settings -> Icons.Rounded.Settings
    HomeTab.Profile -> Icons.Rounded.AccountCircle
}

@Composable
private fun BoardScreen(
    board: MonthBoard?,
    todayKey: String,
    onToggleCell: (SavingsCell) -> Unit,
    onRepairBoard: () -> Unit,
) {
    if (board == null) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            SectionSurface {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CircularProgressIndicator(color = PocketBlue, modifier = Modifier.size(34.dp))
                    Text(
                        "Preparing monthly board",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "If this stays here, tap retry to create or reload this pocket's savings cells.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = onRepairBoard) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Retry")
                    }
                }
            }
        }
        return
    }

    if (board.cells.isEmpty()) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            SectionSurface {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StatusDot(PocketYellow)
                    Text(
                        "Monthly board is not ready",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "The pocket loaded, but the 30 savings cells are missing. Tap retry to create them.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = onRepairBoard) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Retry")
                    }
                }
            }
        }
        return
    }

    val sortedCells = remember(board.cells, todayKey) {
        board.cells.sortedWith(
            compareBy<SavingsCell> { it.sortRank(todayKey) }
                .thenBy { it.index },
        )
    }
    val gridState = rememberLazyGridState()
    val orderSignature = remember(sortedCells, todayKey) {
        sortedCells.joinToString(separator = "|") { "${it.id}:${it.sortRank(todayKey)}" }
    }

    LaunchedEffect(orderSignature) {
        gridState.scrollToItem(0)
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxWidth < 360.dp
        val horizontalPadding = if (compact) 12.dp else 16.dp
        val cellMinSize = when {
            maxWidth < 360.dp -> 86.dp
            maxWidth < 600.dp -> 104.dp
            maxWidth < 900.dp -> 124.dp
            else -> 150.dp
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = cellMinSize),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                TodayBanner(todayKey)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                BoardSummary(board)
            }
            items(sortedCells, key = { "${it.sortRank(todayKey)}-${it.id}" }) { cell ->
                SavingsCellTile(
                    cell = cell,
                    todayKey = todayKey,
                    onClick = { onToggleCell(cell) },
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TodayBanner(todayKey: String) {
    val style = PocketTheme.colors
    Surface(
        color = style.glassStrong,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = GlassCardShape,
        border = BorderStroke(1.dp, style.glassStroke),
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Today", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(todayKey, fontWeight = FontWeight.Black)
            }
            StatusDot(PocketYellow)
        }
    }
}

@Composable
private fun BoardSummary(board: MonthBoard) {
    val style = PocketTheme.colors
    val progress = if (board.targetTotal == 0) 0f else board.savedTotal.toFloat() / board.targetTotal

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SummaryMetricRow(
            metrics = listOf(
                SummaryMetric("Saved", "Rs. ${board.savedTotal}", PocketBlue),
                SummaryMetric("Remaining", "Rs. ${board.remainingTotal}", PocketRose),
                SummaryMetric("Cells", "${board.savedCount}/${board.cells.size}", PocketOrange),
            ),
        )
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(GlassPillShape),
            color = PocketBlue,
            trackColor = style.progressTrack,
        )
        Text(
            text = "${board.monthKey} target: Rs. ${board.targetTotal}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private data class SummaryMetric(
    val label: String,
    val value: String,
    val color: Color,
)

@Composable
private fun SummaryMetricRow(metrics: List<SummaryMetric>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        if (maxWidth < 390.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                metrics.forEach { metric ->
                    SummaryTile(
                        label = metric.label,
                        value = metric.value,
                        color = metric.color,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                metrics.forEach { metric ->
                    SummaryTile(
                        label = metric.label,
                        value = metric.value,
                        color = metric.color,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    val style = PocketTheme.colors
    Surface(
        color = style.glassStrong,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = GlassCompactShape,
        border = BorderStroke(1.dp, style.glassStroke),
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        modifier = modifier,
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusDot(color)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SavingsCellTile(cell: SavingsCell, todayKey: String, onClick: () -> Unit) {
    val style = PocketTheme.colors
    val locked = cell.isLocked(todayKey)
    val canClick = cell.canToggle(todayKey)
    val background = when {
        locked -> style.lockedCell
        cell.saved -> style.savedCell
        else -> style.openCell
    }
    val content = when {
        locked -> style.lockedCellContent
        cell.saved -> style.savedCellContent
        else -> style.openCellContent
    }
    val borderColor = when (cell.amount) {
        20 -> PocketBlue
        50 -> PocketYellow
        100 -> PocketOrange
        500 -> PocketRose
        else -> MaterialTheme.colorScheme.outline
    }

    Surface(
        color = background,
        contentColor = content,
        shape = GlassCompactShape,
        border = BorderStroke(
            1.dp,
            if (cell.saved) style.glassStrokeStrong else style.glassStroke.copy(alpha = if (locked) 0.42f else 0.9f),
        ),
        tonalElevation = 0.dp,
        shadowElevation = if (cell.saved) 14.dp else 10.dp,
        modifier = Modifier
            .aspectRatio(1.05f)
            .clickable(enabled = canClick, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(borderColor),
            )
            Text(
                text = "Rs. ${cell.amount}",
                color = content,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (cell.saved) Icons.Rounded.Check else Icons.Rounded.Close,
                    contentDescription = null,
                    tint = content.copy(alpha = 0.82f),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = when {
                        locked -> "Locked"
                        cell.saved -> "Saved today"
                        else -> "Open"
                    },
                    color = content.copy(alpha = 0.82f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SummaryScreen(
    state: MainUiState,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    onOpenMonth: (String) -> Unit,
    onBackFromMonth: () -> Unit,
) {
    val monthKey = state.selectedSummaryMonthKey
    if (monthKey != null) {
        MonthDetailScreen(
            state = state,
            monthKey = monthKey,
            onBack = onBackFromMonth,
        )
    } else {
        SummaryYearOverview(
            state = state,
            onPreviousYear = onPreviousYear,
            onNextYear = onNextYear,
            onOpenMonth = onOpenMonth,
        )
    }
}

@Composable
private fun SummaryYearOverview(
    state: MainUiState,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    onOpenMonth: (String) -> Unit,
) {
    val monthKeys = remember(state.selectedSummaryYear) { monthKeysForYear(state.selectedSummaryYear) }
    val summaries = monthKeys.mapNotNull { state.summaryForMonth(it) }
    val savedTotal = summaries.sumOf { it.savedTotal }
    val targetTotal = summaries.sumOf { it.targetTotal }
    val missedTotal = (targetTotal - savedTotal).coerceAtLeast(0)
    val completedCells = summaries.sumOf { it.savedCount }
    val totalCells = summaries.sumOf { it.cellCount }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxWidth < 360.dp
        val sidePadding = adaptiveSidePadding(maxWidth)
        val monthMinSize = when {
            maxWidth < 380.dp -> 150.dp
            maxWidth < 720.dp -> 170.dp
            else -> 220.dp
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = monthMinSize),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp),
            contentPadding = PaddingValues(horizontal = sidePadding, vertical = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Summary",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onPreviousYear) {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Previous year")
                        }
                        Text(
                            "${state.selectedSummaryYear}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                        )
                        IconButton(onClick = onNextYear) {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Next year")
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SummaryMetricRow(
                    metrics = listOf(
                        SummaryMetric("Saved", "Rs. $savedTotal", PocketBlue),
                        SummaryMetric("Missed", "Rs. $missedTotal", PocketRose),
                        SummaryMetric("Cells", "$completedCells/$totalCells", PocketOrange),
                    ),
                )
            }
            items(monthKeys, key = { it }) { key ->
                MonthCard(
                    monthKey = key,
                    summary = state.summaryForMonth(key),
                    onClick = { onOpenMonth(key) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MonthCard(
    monthKey: String,
    summary: MonthSummary?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = PocketTheme.colors
    val saved = summary?.savedTotal ?: 0
    val missed = summary?.missedTotal ?: 0
    val savedCount = summary?.savedCount ?: 0
    val cellCount = summary?.cellCount ?: 0
    val progress = if (cellCount == 0) 0f else savedCount.toFloat() / cellCount.toFloat()

    Surface(
        color = style.glassStrong,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = GlassCardShape,
        border = BorderStroke(1.dp, style.glassStroke),
        tonalElevation = 0.dp,
        shadowElevation = 14.dp,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(monthKey.monthName(short = true), fontWeight = FontWeight.Black)
                StatusDot(if (savedCount > 0) PocketBlue else PocketYellow)
            }
            Text("Saved Rs. $saved", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Missed Rs. $missed", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text("$savedCount/$cellCount cells", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(GlassPillShape),
                color = PocketBlue,
                trackColor = style.progressTrack,
            )
        }
    }
}

@Composable
private fun MonthDetailScreen(
    state: MainUiState,
    monthKey: String,
    onBack: () -> Unit,
) {
    val summary = state.selectedMonthSummary
    val savedCells = remember(state.selectedMonthSavedCells, state.board, monthKey) {
        val detailCells = state.selectedMonthSavedCells
        val currentBoardCells = state.board
            ?.takeIf { it.monthKey == monthKey }
            ?.cells
            .orEmpty()
            .filter { it.saved }
        (detailCells + currentBoardCells)
            .distinctBy { it.id }
            .sortedByDescending { it.savedAtMillis ?: 0L }
    }
    val cellsByDay = remember(savedCells) {
        savedCells.groupBy { it.savedDayKey.orEmpty() }
    }
    val monthDays = remember(monthKey) { dayKeysForMonth(monthKey) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val sidePadding = adaptiveSidePadding(maxWidth)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = sidePadding, vertical = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            monthKey.monthName(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(monthKey, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                SummaryMetricRow(
                    metrics = listOf(
                        SummaryMetric("Saved", "Rs. ${summary?.savedTotal ?: 0}", PocketBlue),
                        SummaryMetric("Missed", "Rs. ${summary?.missedTotal ?: 0}", PocketRose),
                        SummaryMetric("Cells", "${summary?.savedCount ?: 0}/${summary?.cellCount ?: 0}", PocketOrange),
                    ),
                )
            }
            if (state.selectedMonthLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PocketBlue)
                    }
                }
            } else {
                items(monthDays, key = { it }) { dayKey ->
                    MonthDayCard(
                        dayKey = dayKey,
                        cells = cellsByDay[dayKey].orEmpty(),
                    )
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MonthDayCard(dayKey: String, cells: List<SavingsCell>) {
    val style = PocketTheme.colors
    val savedTotal = cells.sumOf { it.amount }
    Surface(
        color = if (cells.isEmpty()) style.glass else style.glassStrong,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = GlassCardShape,
        border = BorderStroke(1.dp, if (cells.isEmpty()) style.glassStroke.copy(alpha = 0.72f) else style.glassStroke),
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text(dayKey.dayTitle(), fontWeight = FontWeight.Black)
                    Text(
                        dayKey,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    if (savedTotal == 0) "Rs. 0" else "Rs. $savedTotal",
                    fontWeight = FontWeight.Black,
                    color = if (savedTotal == 0) MaterialTheme.colorScheme.onSurfaceVariant else PocketBlue,
                )
            }
            if (cells.isEmpty()) {
                Text("No savings", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                cells.forEach { cell ->
                    SavedCellLine(cell)
                }
            }
        }
    }
}

@Composable
private fun SavedCellLine(cell: SavingsCell) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("Rs. ${cell.amount}", fontWeight = FontWeight.SemiBold)
            Text(
                "Saved by ${cell.savedByName ?: "You"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(
            cell.savedAtMillis?.formatSavedTime() ?: "No time",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ProfileScreen(
    state: MainUiState,
    onPickPhoto: () -> Unit,
    onDeletePhoto: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onDeleteAccountWithPassword: (String) -> Unit,
    onDeleteGoogleAccount: () -> Unit,
) {
    val user = state.user ?: return
    var showNameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val sidePadding = adaptiveSidePadding(maxWidth)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = sidePadding, vertical = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            item {
                SectionSurface {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AvatarImage(photoData = user.photoData, size = 118.dp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onPickPhoto) {
                                Text(if (user.photoData == null) "Add photo" else "Edit photo")
                            }
                            if (user.photoData != null) {
                                OutlinedButton(onClick = onDeletePhoto) {
                                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = PocketRose)
                                    Spacer(Modifier.size(6.dp))
                                    Text("Delete photo")
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                user.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                            )
                            IconButton(onClick = { showNameDialog = true }) {
                                Icon(Icons.Rounded.Edit, contentDescription = "Edit name", tint = PocketBlue)
                            }
                        }
                        Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Account history", fontWeight = FontWeight.Black)
                        HistoryLine("Account created", user.createdAtMillis?.formatFullDateTime() ?: "Not available")
                        HistoryLine("Last login", user.lastLoginAtMillis?.formatFullDateTime() ?: "Not available")
                    }
                }
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Account settings", fontWeight = FontWeight.Black)
                        if (user.isGoogleUser) {
                            EmptyState("Password is managed by Google.")
                        } else {
                            OutlinedButton(onClick = { showPasswordDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Change password")
                            }
                        }
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PocketRose,
                                contentColor = Color.White,
                            ),
                        ) {
                            Icon(Icons.Rounded.Delete, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Delete account")
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showNameDialog) {
        EditNameDialog(
            currentName = user.name,
            onDismiss = { showNameDialog = false },
            onSave = {
                onUpdateDisplayName(it)
                showNameDialog = false
            },
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onSave = { current, new, confirm ->
                onChangePassword(current, new, confirm)
                showPasswordDialog = false
            },
        )
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            isGoogleUser = user.isGoogleUser,
            onDismiss = { showDeleteDialog = false },
            onDeleteWithPassword = {
                onDeleteAccountWithPassword(it)
                showDeleteDialog = false
            },
            onDeleteGoogleAccount = {
                onDeleteGoogleAccount()
                showDeleteDialog = false
            },
        )
    }
}

@Composable
private fun AvatarImage(photoData: String?, size: Dp) {
    val style = PocketTheme.colors
    val avatarBitmap = remember(photoData) { photoData?.decodeAvatarBitmap() }
    Surface(
        color = style.glassStrong,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = CircleShape,
        border = BorderStroke(1.dp, style.glassStroke),
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        modifier = Modifier.size(size),
    ) {
        if (avatarBitmap != null) {
            Image(
                bitmap = avatarBitmap,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    tint = PocketBlue,
                    modifier = Modifier.size(size * 0.78f),
                )
            }
        }
    }
}

@Composable
private fun AvatarCropDialog(
    sourceUri: Uri,
    onDismiss: () -> Unit,
    onSave: (Float, Float, Float) -> Unit,
) {
    val context = LocalContext.current
    val style = PocketTheme.colors
    val sourceBitmap = remember(sourceUri) {
        runCatching { AvatarCropper.loadBitmap(context, sourceUri) }.getOrNull()
    }
    var zoom by remember(sourceUri) { mutableStateOf(1f) }
    var offsetX by remember(sourceUri) { mutableStateOf(0f) }
    var offsetY by remember(sourceUri) { mutableStateOf(0f) }
    val previewBitmap = remember(sourceBitmap, zoom, offsetX, offsetY) {
        sourceBitmap?.let {
            AvatarCropper.cropToAvatarBitmap(
                source = it,
                zoom = zoom,
                offsetX = offsetX,
                offsetY = offsetY,
            ).asImageBitmap()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crop avatar") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                if (previewBitmap == null) {
                    EmptyState("Could not load the selected photo.")
                } else {
                    Surface(
                        color = style.glassStrong,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, style.glassStrokeStrong),
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .size(220.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        Image(
                            bitmap = previewBitmap,
                            contentDescription = "Avatar crop preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    CropSlider(
                        label = "Zoom",
                        value = zoom,
                        valueRange = 1f..3f,
                        onValueChange = { zoom = it },
                    )
                    CropSlider(
                        label = "Horizontal",
                        value = offsetX,
                        valueRange = -1f..1f,
                        onValueChange = { offsetX = it },
                    )
                    CropSlider(
                        label = "Vertical",
                        value = offsetY,
                        valueRange = -1f..1f,
                        onValueChange = { offsetY = it },
                    )
                }
            }
        },
        confirmButton = {
            Button(enabled = previewBitmap != null, onClick = { onSave(zoom, offsetX, offsetY) }) {
                Text("Save photo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun CropSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}

private fun String.decodeAvatarBitmap(): ImageBitmap? {
    return runCatching {
        val encoded = substringAfter(",", this)
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    }.getOrNull()
}

@Composable
private fun HistoryLine(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
    }
}

@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember(currentName) { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(enabled = name.isNotBlank(), onClick = { onSave(name) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Use your current password first, then enter and confirm the new password.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                PasswordField("Current password", currentPassword) { currentPassword = it }
                PasswordField("New password", newPassword) { newPassword = it }
                PasswordField("Confirm new password", confirmPassword) { confirmPassword = it }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(currentPassword, newPassword, confirmPassword) },
            ) {
                Text("Update password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun DeleteAccountDialog(
    isGoogleUser: Boolean,
    onDismiss: () -> Unit,
    onDeleteWithPassword: (String) -> Unit,
    onDeleteGoogleAccount: () -> Unit,
) {
    var confirmation by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    val canDelete = confirmation == "delete" && (isGoogleUser || currentPassword.isNotBlank())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete account?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "This permanently deletes your account, avatar, pockets, boards, and savings history.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!isGoogleUser) {
                    PasswordField("Current password", currentPassword) { currentPassword = it }
                } else {
                    EmptyState("You will confirm this with Google before the account is deleted.")
                }
                OutlinedTextField(
                    value = confirmation,
                    onValueChange = { confirmation = it },
                    label = { Text("Type delete") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                enabled = canDelete,
                onClick = {
                    if (isGoogleUser) onDeleteGoogleAccount() else onDeleteWithPassword(currentPassword)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PocketRose,
                    contentColor = Color.White,
                ),
            ) {
                Text(if (isGoogleUser) "Continue with Google" else "Delete account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun SettingsScreen(
    pocket: Pocket?,
    settings: AppSettings,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderHour: (Int) -> Unit,
    onDarkModeEnabled: (Boolean) -> Unit,
    onSignOut: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val sidePadding = adaptiveSidePadding(maxWidth)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = sidePadding, vertical = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Current pocket", fontWeight = FontWeight.Bold)
                        Text(
                            pocket?.name.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.sp,
                        )
                    }
                }
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Settings, contentDescription = null)
                                Spacer(Modifier.size(8.dp))
                                Text("Dark mode", fontWeight = FontWeight.Bold)
                            }
                            Switch(checked = settings.darkModeEnabled, onCheckedChange = onDarkModeEnabled)
                        }
                        Text(
                            if (settings.darkModeEnabled) "Deep glass mode is active." else "Light card mode is active.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Notifications, contentDescription = null)
                                Spacer(Modifier.size(8.dp))
                                Text("Daily reminder", fontWeight = FontWeight.Bold)
                            }
                            Switch(checked = settings.remindersEnabled, onCheckedChange = onReminderEnabled)
                        }
                        HorizontalDivider()
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Reminder time")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onReminderHour(settings.reminderHour - 1) }) {
                                    Icon(Icons.Rounded.Remove, contentDescription = "Earlier")
                                }
                                Text("${settings.reminderHour.toString().padStart(2, '0')}:00")
                                IconButton(onClick = { onReminderHour(settings.reminderHour + 1) }) {
                                    Icon(Icons.Rounded.Add, contentDescription = "Later")
                                }
                            }
                        }
                    }
                }
            }
            item {
                OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Sign out")
                }
            }
        }
    }
}

@Composable
private fun SectionSurface(content: @Composable () -> Unit) {
    val style = PocketTheme.colors

    Surface(
        color = style.glassStrong,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = GlassCardShape,
        border = BorderStroke(1.dp, style.glassStroke),
        tonalElevation = 0.dp,
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    val style = PocketTheme.colors

    Surface(
        color = style.glass,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = GlassCardShape,
        border = BorderStroke(1.dp, style.glassStroke.copy(alpha = 0.72f)),
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(18.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatusDot(color: Color) {
    Box(
        Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun CenteredContent(scrollable: Boolean = false, content: @Composable () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        val contentModifier = Modifier
            .widthIn(max = if (maxWidth > 720.dp) 620.dp else maxWidth)
            .fillMaxWidth()

        if (scrollable) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = contentModifier) {
                    content()
                }
            }
        } else {
            Box(modifier = contentModifier) {
                content()
            }
        }
    }
}

private fun adaptiveSidePadding(maxWidth: Dp, maxContentWidth: Dp = 920.dp): Dp {
    val defaultPadding = if (maxWidth < 360.dp) 12.dp else 16.dp
    return if (maxWidth > maxContentWidth + defaultPadding * 2) {
        (maxWidth - maxContentWidth) / 2
    } else {
        defaultPadding
    }
}

private fun Long.formatSavedTime(): String {
    return DateFormat.format("MMM d, h:mm a", Date(this)).toString()
}

private fun Long.formatFullDateTime(): String {
    return DateFormat.format("MMM d, yyyy h:mm a", Date(this)).toString()
}

private fun monthKeysForYear(year: Int): List<String> {
    return (1..12).map { month -> "$year-${month.toString().padStart(2, '0')}" }
}

private fun dayKeysForMonth(monthKey: String): List<String> {
    val yearMonth = runCatching { YearMonth.parse(monthKey) }.getOrNull() ?: return emptyList()
    return (1..yearMonth.lengthOfMonth()).map { day ->
        "$monthKey-${day.toString().padStart(2, '0')}"
    }
}

private fun String.monthName(short: Boolean = false): String {
    val parts = split("-")
    val year = parts.getOrNull(0)?.toIntOrNull() ?: return this
    val month = parts.getOrNull(1)?.toIntOrNull() ?: return this
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return "${Month.of(month).getDisplayName(style, Locale.US)} $year"
}

private fun String.dayTitle(): String {
    val day = split("-").getOrNull(2)?.toIntOrNull() ?: return this
    return "Day ${day.toString().padStart(2, '0')}"
}
