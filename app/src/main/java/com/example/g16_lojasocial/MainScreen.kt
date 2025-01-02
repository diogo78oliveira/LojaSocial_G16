package com.example.g16_lojasocial

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

import com.example.g16_lojasocial.authentication.AuthViewModel
import com.example.g16_lojasocial.views.HomePage
import com.example.g16_lojasocial.views.SignupPage
import com.example.g16_lojasocial.views.Estatisticas
import com.example.g16_lojasocial.views.NotificationPage
import com.example.g16_lojasocial.views.Eventos
import com.example.g16_lojasocial.views.ViewsViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewsViewModel: ViewsViewModel
) {
    val isVoluntario by authViewModel.isVoluntario.observeAsState(initial = false)

    val navItemList = if (isVoluntario) {
        listOf(
            NavItem("Beneficiarios", Icons.Default.Person, 0, 0), // Maps to HomePage
            NavItem("Eventos", Icons.Default.Star, 0, 1),         // Maps to NotificationPage
            NavItem("Voluntários", Icons.Default.DateRange, 0, 3) // Maps to Eventos
        )
    } else {
        listOf(
            NavItem("Beneficiarios", Icons.Default.Person, 0, 0),
            NavItem("Eventos", Icons.Default.Star, 0, 1),
            NavItem("Registar", Icons.Default.Add, 0, 2),
            NavItem("Voluntários", Icons.Default.DateRange, 0, 3),
            NavItem("Estatisticas", Icons.Default.List, 0, 4)
        )
    }

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            BadgedBox(badge = {
                                if (navItem.badgeCount > 0)
                                    Badge { Text(text = navItem.badgeCount.toString()) }
                            }) {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = "Icon",
                                    tint = if (selectedIndex == index) Color(0xFF65c2eb) else Color.Gray
                                )
                            }
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                fontSize = 10.sp
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val selectedNavItem = navItemList[selectedIndex]

        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedScreenIndex = selectedNavItem.screenIndex,
            navController = navController,
            authViewModel = authViewModel,
            viewsViewModel = viewsViewModel,
            isVoluntario = isVoluntario

        )

    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedScreenIndex: Int,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewsViewModel: ViewsViewModel,
    isVoluntario: Boolean
) {
    when (selectedScreenIndex) {
        0 -> HomePage(navController = navController, authViewModel = authViewModel, viewsViewModel = viewsViewModel)
        1 -> Eventos()
        2 -> SignupPage(navController = navController, viewsViewModel = viewsViewModel)
        3 -> NotificationPage(isVoluntario = isVoluntario)
        4 -> Estatisticas()
    }
}
