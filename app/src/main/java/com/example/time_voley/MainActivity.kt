package com.example.time_voley

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DrawerDefaults.backgroundColor
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.dataStore
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.time_voley.ui.theme.Time_VoleyTheme
import com.example.time_voley.ui.theme.data.getSavedTeams
import com.example.time_voley.ui.theme.data.saveTeams
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Time_VoleyTheme {

                Scaffold { paddingValues ->
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val showBottomBar = remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (showBottomBar.value) {
                BottomNavigationBar(
                    navController,
                    listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Search,
                        BottomNavItem.teste,
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash_screen",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("splash_screen"){
                showBottomBar.value = false
                SplashScreen(navController)
            }

            composable("home_page") {
                showBottomBar.value = true
                homePage(navController)
            }
            composable(BottomNavItem.Search.route) {
                showBottomBar.value = false
                scoreBoard(navController)
            }
            composable(BottomNavItem.teste.route) {
                showBottomBar.value = true
                Resultado(navController, "", 2)
            }
            composable(
                route = "resultado/{timesSorteados}/{tamanhoDaEquipe}",
                arguments = listOf(
                    navArgument("timesSorteados") { type = NavType.StringType },
                    navArgument("tamanhoDaEquipe") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val timesStringEncoded = backStackEntry.arguments?.getString("timesSorteados") ?: ""
                val tamanhoDaEquipe = backStackEntry.arguments?.getInt("tamanhoDaEquipe") ?: 0
                val timesString = URLDecoder.decode(timesStringEncoded, "UTF-8")
                Resultado(navController, timesString, tamanhoDaEquipe)
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {

    val systemUiController = rememberSystemUiController()
    HideSystemUI()

    LaunchedEffect(Unit) {
        systemUiController.isSystemBarsVisible = false
        systemUiController.isNavigationBarVisible = false
    }

    // Usando LaunchedEffect para iniciar um atraso e navegar
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(4000) // Espera 2 segundos
        navController.navigate("home_page") {
            popUpTo("splash_screen") { inclusive = true } // Remove a Splash da pilha de navegação
        }
    }

    // Layout da Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colum2)), // Cor de fundo azul
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Ícone ou Logo
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.volleyball_icon_icons_com_67205),
                contentDescription = "",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .background(colorResource(id = R.color.colum2), CircleShape)
            )
            // Texto de Boas-Vindas
            Text(
                text = "Voley Time",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun homePage(navController: NavHostController) {
    var participantesTexto by remember { mutableStateOf(TextFieldValue("")) }
    var participantes by remember { mutableStateOf(listOf<String>()) }
    var tamanhoTime by remember { mutableStateOf(2) }
    var timesSorteados by remember { mutableStateOf<List<List<String>>?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, top = 95.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ícone e instruções
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.volleyball_icon_icons_com_67205),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
                .background(colorResource(id = R.color.colum2), CircleShape)
        )
        Text(
            "Adicione os Jogadores:",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        // Campo para adicionar jogadores
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BasicTextField(
                value = participantesTexto,
                onValueChange = { participantesTexto = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .border(1.dp, colorResource(id = R.color.colum2))
                    .padding(8.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    val novosParticipantes = participantesTexto.text
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    if (novosParticipantes.isNotEmpty()) {
                        participantes = participantes + novosParticipantes
                        participantesTexto = TextFieldValue("")
                    }
                },
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.colum2)),
            ) {
                Text("Adicionar")
            }
        }
        Text("Participantes: ${participantes.joinToString(", ")}")

        // Selecione a modalidade
        Text(
            "Selecione a Modalidade:",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 3, 4).forEach { tamanho ->
                Button(
                    onClick = { tamanhoTime = tamanho },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.colum2))
                ) {
                    Text("$tamanho pessoas")
                }
            }
        }
        Text("Tamanho escolhido: $tamanhoTime pessoas por time")

        // Botão para sortear times
        Button(
            onClick = {
                timesSorteados = sortearTimes(participantes, tamanhoTime)
                timesSorteados?.let { times ->
                    CoroutineScope(Dispatchers.IO).launch {
                        saveTeams(context, times)
                    }
                    val timesString = times.flatten().joinToString(",")
                    val encodedString = URLEncoder.encode(timesString, "UTF-8")
                    navController.navigate("resultado/$encodedString/$tamanhoTime")
                }
            },
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.colum2)),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Sortear Times")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Resultado(navController: NavController, timesString: String, tamanhoDaEquipe: Int) {
    val context = LocalContext.current
    var times by remember { mutableStateOf<List<List<String>>>(emptyList()) }

    LaunchedEffect(Unit) {
        getSavedTeams(context).collect { savedTeams ->
            if (savedTeams.isNotEmpty()) { // Agora funciona porque você tem a lista concreta
                times = savedTeams
            } else if (timesString.isNotEmpty()) {
                val participantes = timesString.split(",").map { it.trim() }
                times = participantes.chunked(tamanhoDaEquipe)
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .padding(bottom = 18.dp)
            .background(Color.White),
        topBar = {
            TopAppBar(
                title = { Text(text = "Times Sorteados") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, top = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(times.size) { index ->
                    TimeCard(time = times[index], numeroDoTime = index + 1)
                }
            }
        }
    }
}

// Função para sortear os times
fun sortearTimes(participantes: List<String>, tamanhoTime: Int): List<List<String>> {
    val participantesEmbaralhados = participantes.shuffled()
    val times = mutableListOf<List<String>>()

    for (i in participantesEmbaralhados.indices step tamanhoTime) {
        val time = participantesEmbaralhados.subList(
            i,
            minOf(i + tamanhoTime, participantesEmbaralhados.size)
        )
        times.add(time)
    }

    return times
}

@Composable
fun TimeCard(time: List<String>, numeroDoTime: Int) {
    // Defina as duas cores desejadas
    val corPar = colorResource(id = R.color.colum)// Azul claro
    val corImpar = colorResource(id = R.color.colum2) // Amarelo claro

    // Escolha a cor com base no número do time
    val corDeFundo = if (numeroDoTime % 2 == 0) corPar else corImpar

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Envolvendo o conteúdo do Card em um Box para aplicar a cor de fundo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(corDeFundo) // Aplica a cor de fundo desejada
                .padding(16.dp) // Padding interno para o conteúdo
        ) {
            Column {
                Text(
                    "$numeroDoTime˚ Time ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 130.dp)
                )
                time.forEach { participante ->
                    Text(participante,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp,start = 20.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
        BottomNavigation(
            modifier = Modifier
                .padding(bottom = 44.dp),
            backgroundColor = colorResource(id = R.color.colum2),
            contentColor = Color.Black
        ) {
            val currentRoute = currentRoute(navController)
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            modifier = Modifier
                                .width(30.dp)
                                .height(44.dp)
                                .padding(top = 6.dp),
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            tint = Color.Black
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
    object Home : BottomNavItem("Sorteador", R.drawable.play_luck_game_gambling_dice_icon_225890, "home")
    object Search : BottomNavItem("Placar", R.drawable.football_scoreboard_icon_133804, "search")
    object teste : BottomNavItem("Times", R.drawable.clipboard_29594, "teste")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun scoreBoard(navController: NavController) {
    HideSystemUI()
    var counter by remember { mutableStateOf(0) }
    var counter1 by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.isSystemBarsVisible = false
        systemUiController.isNavigationBarVisible = false
    }

    Scaffold(
        modifier = Modifier
            .padding(bottom = 18.dp)
            .background(Color.Black),

        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(start = 350.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .graphicsLayer(rotationZ = 90f),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.colum),
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(id = R.color.colum)),
                contentAlignment = Alignment.Center
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
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.colum2)),
                contentAlignment = Alignment.Center
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
                .padding(top = 368.dp, end = 100.dp),
            contentAlignment = Alignment.Center
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
}
//@Composable
//fun scoreBoard(navController: NavController) {
//    var counter by remember { mutableStateOf(0) }
//    var counter1 by remember { mutableStateOf(0) }
//    val scope = rememberCoroutineScope()
//    val systemUiController = rememberSystemUiController()
//
//    LaunchedEffect(Unit){
//        systemUiController.isSystemBarsVisible = false
//        systemUiController.isNavigationBarVisible = false
//    }
//
//    Column {
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .weight(1f)
//                .fillMaxHeight()
//                .background(colorResource(id = R.color.colum)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = stringResource(R.string.equip_1),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 240.dp, top = 45.dp)
//                    .graphicsLayer(rotationZ = 90f),
//                fontSize = 32.sp,
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 110.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//
//                Text(
//                    text = counter.toString(),
//                    modifier = Modifier
//                        .height(200.dp)
//                        .graphicsLayer(rotationZ = 90f)
//                        .pointerInput(Unit) {
//                            detectVerticalDragGestures { change, dragAmount ->
//                                change.consume()
//                                if (dragAmount > 0) {
//                                    scope.launch {
//                                        counter -= 1
//                                    }
//                                }
//                            }
//                        }
//                        .clickable { counter += 1 },
//                    fontSize = 200.sp,
//                    color = Color.White,
//                    fontFamily = FontFamily.Serif,
//                    fontWeight = FontWeight.Bold,
//
//                    )
//
//            }
//
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .weight(1f)
//                .fillMaxHeight()
//                .background(colorResource(id = R.color.colum2)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = stringResource(R.string.equip_2),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 240.dp, top = 45.dp)
//                    .graphicsLayer(rotationZ = 90f),
//                fontSize = 32.sp,
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//
//                )
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 110.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    text = counter1.toString(),
//                    modifier = Modifier
//                        .height(200.dp)
//                        .graphicsLayer(rotationZ = 90f)
//                        .pointerInput(Unit) {
//                            detectVerticalDragGestures { change, dragAmount ->
//                                change.consume()
//                                if (dragAmount > 0) {
//                                    scope.launch {
//                                        counter1 -= 1
//                                    }
//                                }
//                            }
//                        }
//                        .clickable { counter1 += 1 },
//                    fontSize = 200.sp,
//                    color = Color.White,
//                    fontFamily = FontFamily.Serif,
//                    fontWeight = FontWeight.Bold,
//                )
//            }
//        }
//
//
//    }
//    // Botão centralizado
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.Transparent)
//            .padding(top = 310.dp, end = 100.dp), contentAlignment = Alignment.Center
//    ) {
//        Button(
//            onClick = {
//                counter = 0
//                counter1 = 0
//            },
//            modifier = Modifier.graphicsLayer(rotationZ = 90f),
//            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.Button))
//        ) {
//            Text(text = "Zerar")
//        }
//    }
//
//}

@Composable
fun HideSystemUI() {
    val context = LocalContext.current
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = (context as? ComponentActivity)?.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.apply {
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window?.insetsController?.show(WindowInsets.Type.systemBars())
            } else {
                @Suppress("DEPRECATION")
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestePreview() {
    Time_VoleyTheme {
        AppNavigation()
    }
}

