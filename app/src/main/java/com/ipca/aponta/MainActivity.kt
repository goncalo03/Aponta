package com.ipca.aponta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

// --- IMPORTS DOS TEUS ECRÃS E VIEWMODELS ---
// Certifica-te que os ficheiros existem nas pastas corretas
import com.ipca.aponta.ui.theme.ApontaTheme
import com.ipca.aponta.ui.login.LoginScreen
import com.ipca.aponta.ui.login.LoginViewModel
import com.ipca.aponta.ui.register.RegisterScreen
import com.ipca.aponta.ui.register.RegisterViewModel
import com.ipca.aponta.ui.home.HomeScreen
import com.ipca.aponta.viewmodels.NotesViewModel
import com.ipca.aponta.ui.notes.AddEditNoteScreen
import com.ipca.aponta.ui.notes.AddEditNoteViewModel
import com.ipca.aponta.ui.notes.NoteDetailScreen
import com.ipca.aponta.ui.profile.ProfileScreen
import com.ipca.aponta.ui.profile.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializamos os ViewModels de Autenticação ao nível da Activity
        val loginViewModel: LoginViewModel by viewModels()
        val registerViewModel: RegisterViewModel by viewModels()

        setContent {
            ApontaTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()

                // Variáveis para controlar a navegação e UI
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var title by remember { mutableStateOf("Aponta") }
                var isHome by remember { mutableStateOf(true) }

                // Lógica de Visibilidade das Barras:
                // Escondemos em todos os ecrãs que já têm o seu próprio design/Scaffold
                val showBars = currentRoute != "login" &&
                        currentRoute != "register" &&
                        currentRoute != "home" &&
                        currentRoute != "profile" &&
                        currentRoute?.startsWith("note_detail") != true &&
                        currentRoute?.startsWith("add_note") != true

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    // BARRA DE BAIXO (Só aparece se showBars for true - raramente neste caso)
                    bottomBar = {
                        if (showBars) {
                            MyBottomBar(navController = navController)
                        }
                    },

                    // BARRA DE TOPO (Só aparece se showBars for true)
                    topBar = {
                        if (showBars) {
                            MyTopBar(
                                topBarTitle = title,
                                isHomeScreen = isHome,
                                onLogout = {
                                    auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->

                    // O NavHost gere a troca de páginas
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        // Usamos padding 0 na Home para o fundo preto ocupar tudo
                        modifier = Modifier.padding(if (showBars) innerPadding else PaddingValues(0.dp))
                    ) {

                        // --- 1. LOGIN ---
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // --- 2. REGISTO ---
                        composable("register") {
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = {
                                    auth.signOut() // Logout forçado após criar conta
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- 3. HOME (LISTA) ---
                        composable("home") {
                            isHome = true
                            title = "Minhas Notas"

                            val notesViewModel: NotesViewModel = viewModel()

                            HomeScreen(
                                viewModel = notesViewModel,
                                onAddNoteClick = {
                                    navController.navigate("add_note/new")
                                },
                                onNoteClick = { noteId ->
                                    navController.navigate("note_detail/$noteId")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                }
                            )
                        }

                        // --- 4. DETALHES DA NOTA ---
                        composable("note_detail/{noteId}") { backStackEntry ->
                            isHome = false
                            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""

                            // Reutilizamos AddEditNoteViewModel para leitura/apagar
                            val detailViewModel: AddEditNoteViewModel = viewModel()

                            NoteDetailScreen(
                                noteId = noteId,
                                viewModel = detailViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToEdit = { id ->
                                    navController.navigate("add_note/$id")
                                }
                            )
                        }

                        // --- 5. ADICIONAR / EDITAR NOTA ---
                        composable("add_note/{noteId}") { backStackEntry ->
                            isHome = false
                            val noteId = backStackEntry.arguments?.getString("noteId")

                            val addEditViewModel: AddEditNoteViewModel = viewModel()

                            AddEditNoteScreen(
                                noteId = noteId,
                                viewModel = addEditViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- 6. PERFIL DE UTILIZADOR ---
                        composable("profile") {
                            isHome = false
                            val profileViewModel: ProfileViewModel = viewModel()

                            ProfileScreen(
                                viewModel = profileViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    // Ao fazer logout no perfil, voltamos ao login
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }

                // --- AUTO-LOGIN (Verificação Inicial) ---
                LaunchedEffect(Unit) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------
// COMPONENTES AUXILIARES (UI Genérica)
// -----------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    topBarTitle: String,
    isHomeScreen: Boolean,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = { Text(topBarTitle) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = onLogout) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Sair")
            }
        }
    )
}

@Composable
fun MyBottomBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Notas") },
            selected = true,
            onClick = { navController.navigate("home") }
        )
    }
}