package com.example.time_voley

import android.graphics.fonts.Font
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.time_voley.ui.theme.Time_VoleyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Time_VoleyTheme {

                Scaffold { paddingValues ->
                    homePage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homePage() {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Profile,
        BottomNavItem.Teste,
        BottomNavItem.Teste1
    )
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val topBarTitle = when (currentRoute) {
        BottomNavItem.Home.route -> "Home"
        BottomNavItem.Search.route -> "Search"
        BottomNavItem.Profile.route -> "Profile"
        BottomNavItem.Teste.route -> "Teste"
        BottomNavItem.Teste1.route -> "Teste1"
        else -> "My Application"
    }
    Scaffold(
        modifier = Modifier
            .padding(bottom = 18.dp)
            .background(Color.White)

        ,
        topBar = {
            TopAppBar(
                title = { Text(text = topBarTitle) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    if (navController.currentBackStackEntry?.destination?.route != BottomNavItem.Home.route) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, items = items)
        },
        // Define o fundo branco para o Scaffold
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) { // Define o fundo branco para o conteúdo
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    Column {
                    }
                }
                composable(BottomNavItem.Search.route) {
                    ScreenContent("Search Screen")
                }
                composable(BottomNavItem.Profile.route) {
                    ScreenContent("Profile Screen")
                }
                composable(BottomNavItem.Teste.route) {
                    ScreenContent("Notifications Screen")
                }
                composable(BottomNavItem.Teste1.route) {
                    ScreenContent("Settings Screen")
                }
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
    val minhaCor = Color(0xFF137CF1)
    BottomNavigation(
        modifier = Modifier
            .padding(bottom = 26.dp),
        backgroundColor = minhaCor,
        contentColor = Color.Black
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        tint = Color.Gray
                    )
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Evita recriar a pilha de navegação ao clicar no item novamente
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun ScreenContent(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge
    )
}
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route

}
sealed class BottomNavItem(val title: String, val icon: Int, val route: String) {
    object Home : BottomNavItem("", R.drawable.baseline_lock_24, "home")
    object Search : BottomNavItem("", R.drawable.baseline_lock_24, "search")
    object Profile : BottomNavItem("", R.drawable.baseline_lock_24, "profile")
    object Teste : BottomNavItem("", R.drawable.baseline_lock_24, "teste")
    object Teste1 : BottomNavItem("", R.drawable.baseline_lock_24, "teste1")
}


@Composable
fun scoreBoard(
) {
    var counter by remember { mutableStateOf(0) }
    var counter1 by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    Column {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(id = R.color.colum)), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.equip_1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 240.dp, top = 45.dp)
                    .graphicsLayer(rotationZ = 90f),
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 110.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = counter.toString(),
                    modifier = Modifier
                        .height(200.dp)
                        .graphicsLayer(rotationZ = 90f)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                if (dragAmount > 0) {
                                    scope.launch {
                                        counter -= 1
                                    }
                                }
                            }
                        }
                        .clickable { counter += 1 },
                    fontSize = 200.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,

                    )

            }

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(id = R.color.colum2)), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.equip_2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 240.dp, top = 45.dp)
                    .graphicsLayer(rotationZ = 90f),
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,

                )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 110.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = counter1.toString(),
                    modifier = Modifier
                        .height(200.dp)
                        .graphicsLayer(rotationZ = 90f)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                if (dragAmount > 0) {
                                    scope.launch {
                                        counter1 -= 1
                                    }
                                }
                            }
                        }
                        .clickable { counter1 += 1 },
                    fontSize = 200.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                )
            }
        }


    }
    // Botão centralizado
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(top = 342.dp, end = 200.dp), contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                counter = 0
                counter1 = 0
            },
            modifier = Modifier.graphicsLayer(rotationZ = 90f),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.Button))
        ) {
            Text(text = "Zerar")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TestePreview() {
    Time_VoleyTheme {
        homePage()
    }
}

