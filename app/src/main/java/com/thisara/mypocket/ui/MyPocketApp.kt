package com.thisara.mypocket.ui

import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.thisara.mypocket.data.AppSettings
import com.thisara.mypocket.data.MonthBoard
import com.thisara.mypocket.data.Pocket
import com.thisara.mypocket.data.SavingsCell
import com.thisara.mypocket.ui.theme.PocketBlue
import com.thisara.mypocket.ui.theme.PocketInk
import com.thisara.mypocket.ui.theme.PocketMist
import com.thisara.mypocket.ui.theme.PocketOrange
import com.thisara.mypocket.ui.theme.PocketRose
import com.thisara.mypocket.ui.theme.PocketYellow
import java.util.Date

@Composable
fun MyPocketApp(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.completeGoogleSignIn(result.data)
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
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .safeDrawingPadding(),
        ) {
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

                state.needsPocket -> PocketSetupScreen(
                    onCreate = viewModel::createPocket,
                    onJoin = viewModel::joinPocket,
                    onSignOut = viewModel::signOut,
                )

                else -> HomeScreen(
                    state = state,
                    settings = settings,
                    onTab = viewModel::setTab,
                    onToggleCell = viewModel::toggleCell,
                    onRepairBoard = viewModel::repairCurrentBoard,
                    onReminderEnabled = viewModel::setRemindersEnabled,
                    onReminderHour = viewModel::setReminderHour,
                    onSignOut = viewModel::signOut,
                )
            }

            if (state.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.62f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PocketRose)
                }
            }
        }
    }
}

@Composable
private fun AuthGate(
    state: MainUiState,
    onMode: (AuthMode) -> Unit,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onGoogle: () -> Unit,
) {
    CenteredContent {
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
        PaletteHeader()
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "My Pocket",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = PocketInk,
            )
            Text(
                text = "A shared monthly board for saving money without messy marks or wrong totals.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSignUp) {
                Icon(Icons.Rounded.PersonAdd, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Get started")
            }
            OutlinedButton(onClick = onSignIn) {
                Icon(Icons.Rounded.Login, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Log in")
            }
        }
    }
}

@Composable
private fun PaletteHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(188.dp)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(PocketBlue),
        )
        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxWidth()
                .background(PocketYellow),
        )
        Box(
            modifier = Modifier
                .weight(0.52f)
                .fillMaxWidth()
                .background(PocketOrange),
        )
        Box(
            modifier = Modifier
                .weight(0.52f)
                .fillMaxWidth()
                .background(PocketRose),
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
            Icon(Icons.Rounded.Login, contentDescription = null)
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
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun GoogleButton(onGoogle: () -> Unit) {
    OutlinedButton(onClick = onGoogle, modifier = Modifier.fillMaxWidth()) {
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
    CenteredContent {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
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
private fun PocketSetupScreen(
    onCreate: (String) -> Unit,
    onJoin: (String) -> Unit,
    onSignOut: () -> Unit,
) {
    var pocketName by remember { mutableStateOf("Our Pocket") }
    var inviteCode by remember { mutableStateOf("") }

    CenteredContent {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                Text(
                    text = "Set up your shared pocket",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                )
            }
            item {
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Create a new pocket", fontWeight = FontWeight.Bold)
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
                SectionSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Join your partner", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = { inviteCode = it.uppercase() },
                            label = { Text("Invite code") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        OutlinedButton(onClick = { onJoin(inviteCode) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.Share, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Join pocket")
                        }
                    }
                }
            }
            item {
                TextButton(onClick = onSignOut) {
                    Text("Sign out")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: MainUiState,
    settings: AppSettings,
    onTab: (HomeTab) -> Unit,
    onToggleCell: (SavingsCell) -> Unit,
    onRepairBoard: () -> Unit,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderHour: (Int) -> Unit,
    onSignOut: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Pocket", fontWeight = FontWeight.Black)
                        Text(
                            state.pocket?.name.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = state.selectedTab == HomeTab.Board,
                    onClick = { onTab(HomeTab.Board) },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                    label = { Text("Board") },
                )
                NavigationBarItem(
                    selected = state.selectedTab == HomeTab.History,
                    onClick = { onTab(HomeTab.History) },
                    icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                    label = { Text("History") },
                )
                NavigationBarItem(
                    selected = state.selectedTab == HomeTab.Settings,
                    onClick = { onTab(HomeTab.Settings) },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when (state.selectedTab) {
                HomeTab.Board -> BoardScreen(
                    board = state.board,
                    onToggleCell = onToggleCell,
                    onRepairBoard = onRepairBoard,
                )
                HomeTab.History -> HistoryScreen(state)
                HomeTab.Settings -> SettingsScreen(
                    pocket = state.pocket,
                    settings = settings,
                    onReminderEnabled = onReminderEnabled,
                    onReminderHour = onReminderHour,
                    onSignOut = onSignOut,
                )
            }
        }
    }
}

@Composable
private fun BoardScreen(
    board: MonthBoard?,
    onToggleCell: (SavingsCell) -> Unit,
    onRepairBoard: () -> Unit,
) {
    if (board == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PocketBlue)
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

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            BoardSummary(board)
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 92.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp),
            ) {
                items(board.cells, key = { it.id }) { cell ->
                    SavingsCellTile(cell = cell, onClick = { onToggleCell(cell) })
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BoardSummary(board: MonthBoard) {
    val progress = if (board.targetTotal == 0) 0f else board.savedTotal.toFloat() / board.targetTotal

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryTile("Saved", "Rs. ${board.savedTotal}", PocketBlue, Modifier.weight(1f))
            SummaryTile("Remaining", "Rs. ${board.remainingTotal}", PocketRose, Modifier.weight(1f))
            SummaryTile("Cells", "${board.savedCount}/${board.cells.size}", PocketOrange, Modifier.weight(1f))
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = PocketBlue,
            trackColor = PocketMist,
        )
        Text(
            text = "${board.monthKey} target: Rs. ${board.targetTotal}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SummaryTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        modifier = modifier,
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StatusDot(color)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SavingsCellTile(cell: SavingsCell, onClick: () -> Unit) {
    val background = if (cell.saved) PocketBlue else Color.White
    val content = if (cell.saved) Color.White else PocketInk
    val borderColor = when (cell.amount) {
        20 -> PocketBlue
        50 -> PocketYellow
        100 -> PocketOrange
        500 -> PocketRose
        else -> PocketInk
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = if (cell.saved) 3.dp else 1.dp,
        shadowElevation = 1.dp,
        modifier = Modifier
            .aspectRatio(1.05f)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
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
                    text = if (cell.saved) cell.savedByName ?: "Saved" else "Open",
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
private fun HistoryScreen(state: MainUiState) {
    val activity = state.activity
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text("Saved history", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
        if (activity.isEmpty()) {
            item {
                EmptyState("No saved cells yet.")
            }
        } else {
            items(activity) { item ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Rs. ${item.amount}", fontWeight = FontWeight.Black)
                            Text(
                                "Saved by ${item.savedByName}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            item.savedAtMillis.formatSavedTime(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    pocket: Pocket?,
    settings: AppSettings,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderHour: (Int) -> Unit,
    onSignOut: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
        item {
            SectionSurface {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Invite code", fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            pocket?.inviteCode.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.sp,
                        )
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(pocket?.inviteCode.orEmpty()))
                            },
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Copy")
                        }
                    }
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
                    Divider()
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
                Icon(Icons.Rounded.Logout, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Sign out")
            }
        }
    }
}

@Composable
private fun SectionSurface(content: @Composable () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Surface(
        color = PocketMist,
        shape = RoundedCornerShape(8.dp),
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
private fun CenteredContent(content: @Composable () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = if (maxWidth > 720.dp) 620.dp else maxWidth)
                .fillMaxWidth(),
        ) {
            content()
        }
    }
}

private fun Long.formatSavedTime(): String {
    return DateFormat.format("MMM d, h:mm a", Date(this)).toString()
}
