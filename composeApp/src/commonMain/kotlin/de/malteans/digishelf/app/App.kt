package de.malteans.digishelf.app

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import de.malteans.digishelf.core.presentation.main.components.CurScreen
import de.malteans.digishelf.core.presentation.main.components.CustomBookIcon
import de.malteans.digishelf.navigation.NavGraph
import de.malteans.digishelf.navigation.Route
import de.malteans.digishelf.theme.DigiShelfTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun App() {
    DigiShelfTheme {
        val scope = rememberCoroutineScope()
        
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        
        var curScreen by rememberSaveable { mutableStateOf(CurScreen.BooksOverview) }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen || curScreen != CurScreen.Legals,
                drawerContent = {
                    ModalDrawerSheet {
                        Spacer(modifier = Modifier.height(16.dp))
                        NavigationDrawerItem (
                            label = { Text("Book Overview") },
                            selected = curScreen in listOf(
                                CurScreen.BooksOverview, CurScreen.Add, CurScreen.Scanner, CurScreen.Details
                            ),
                            onClick = {
                                navController.popBackStack<Route.Books.Overview>(false)
                                scope.launch(Dispatchers.IO) { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding),
                            icon = {
                                Icon(
                                    imageVector = CustomBookIcon,
                                    contentDescription = "Book Overview"
                                )
                            },
                        )
                        NavigationDrawerItem (
                            label = { Text("Book Series") },
                            selected = curScreen == CurScreen.SeriesOverview,
                            onClick = {
                                navController.navigate(Route.NavSeries)
                                scope.launch(Dispatchers.IO) { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding),
                            icon = {
                                Icon(
                                    imageVector = CustomBookIcon,
                                    contentDescription = "Book Series Overview"
                                )
                            },
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationDrawerItem (
                            label = { Text("Settings") },
                            selected = curScreen in listOf(CurScreen.Settings, CurScreen.Trash),
                            onClick = {
                                navController.navigate(Route.NavSettings)
                                scope.launch(Dispatchers.IO) { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding),
                            icon = {
                                Icon(
                                    imageVector = if (curScreen == CurScreen.Settings) Icons.Default.Settings
                                        else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                        )
                    }
                }
            ) {
                NavGraph(
                    navController = navController,
                    openDrawer = { scope.launch(Dispatchers.IO) { drawerState.open() } },
                    setScreen = { curScreen = it },
                )
            }
        }
    }
}