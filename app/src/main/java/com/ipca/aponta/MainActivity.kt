package com.ipca.aponta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ipca.aponta.ui.login.LoginScreen
import com.ipca.aponta.ui.login.LoginViewModel
import com.ipca.aponta.ui.register.RegisterScreen
import com.ipca.aponta.ui.register.RegisterViewModel
import com.ipca.aponta.ui.theme.ApontaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializamos os ViewModels
        val loginViewModel: LoginViewModel by viewModels()
        val registerViewModel: RegisterViewModel by viewModels()

        setContent {
            ApontaTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()

                // Variáveis para controlar a UI dinamicamente
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var title by remember { mutableStateOf("Aponta") }
                var isHome by remember { mutableStateOf(true) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // BARRA DE BAIXO: Esconde no Login/Register
                    bottomBar = {
                        if (currentRoute != "login" && currentRoute != "register") {
                            MyBottomBar(navController = navController)
                        }
                    },
                    // BARRA DE TOPO: Esconde no Login/Register
                    topBar = {
                        if (currentRoute != "login" && currentRoute != "register") {
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
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // --- 1. LOGIN ---
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    // Login OK -> Vai para Home
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
                                    // Registo OK -> Logout Forçado -> Vai para Login
                                    auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- 3. HOME (Notas) ---
                        composable("home") {
                            // Atualiza o estado da UI
                            isHome = true
                            title = "Minhas Notas"

                            // Conteúdo placeholder (aqui substituirás pelo NotesListScreen futuro)
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Bem-vindo às Notas!")
                            }
                        }
                    }
                }

                // --- AUTO-LOGIN ---
                // Verifica ao iniciar se já existe user
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
// COMPONENTES AUXILIARES
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