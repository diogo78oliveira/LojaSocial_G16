package com.example.g16_lojasocial

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

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
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFFFFFF),
                modifier = Modifier
                    .shadow(16.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .border(1.dp, Color(0xFFE4E7EC), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFFFFFFF)),
                        icon = {
                            BadgedBox(badge = {
                                if (navItem.badgeCount > 0)
                                    Badge(containerColor = Color(0xFFFFFFFF)) {
                                        Text(text = navItem.badgeCount.toString())
                                    }
                            }) {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = "Icon",
                                    tint = if (selectedIndex == index) Color(0xFF004EBB) else Color(0xFF8C98AB)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                fontSize = 10.sp,
                                color = if (selectedIndex == index) Color(0xFF004EBB) else Color(0xFF8C98AB)
                            )
                        },
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
        1 -> Eventos(isVoluntario = isVoluntario, viewsViewModel = viewsViewModel)
        2 -> SignupPage(navController = navController, viewsViewModel = viewsViewModel)
        3 -> NotificationPage(isVoluntario = isVoluntario, viewsViewModel = viewsViewModel)
        4 -> Estatisticas()
    }
}
