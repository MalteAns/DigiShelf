package de.malteans.digishelf.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import de.malteans.digishelf.core.domain.SortType
import de.malteans.digishelf.core.presentation.add.AddAction
import de.malteans.digishelf.core.presentation.add.AddScreenRoot
import de.malteans.digishelf.core.presentation.add.AddViewModel
import de.malteans.digishelf.core.presentation.add.scanner.BarcodeScannerView
import de.malteans.digishelf.core.presentation.details.DetailsScreenRoot
import de.malteans.digishelf.core.presentation.overview.OverviewAction
import de.malteans.digishelf.core.presentation.overview.OverviewScreenRoot
import de.malteans.digishelf.core.presentation.overview.OverviewViewModel
import de.malteans.digishelf.core.presentation.overview.components.SearchType
import de.malteans.digishelf.core.presentation.settings.SettingsScreenRoot
import de.malteans.digishelf.core.presentation.settings.SettingsViewModel
import de.malteans.digishelf.core.presentation.settings.components.TrashScreenRoot
import de.malteans.digishelf.series.presentation.details.SeriesDetailsScreenRoot
import de.malteans.digishelf.series.presentation.overview.SeriesOverviewScreenRoot
import de.malteans.legal.presentation.navigation.LegalRoute
import de.malteans.legal.presentation.screens.ImprintScreen
import de.malteans.legal.presentation.screens.LicensesScreen
import de.malteans.legal.presentation.screens.PrivacyScreen
import digishelf.composeapp.generated.resources.Res
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit,
    setScreen: (CurScreen) -> Unit,
) {
    
    val overviewViewModel = koinViewModel<OverviewViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()

    NavHost(
        navController = navController,
        startDestination = Route.NavBooks,
        // Set default transitions to None (individual composables below override)
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        navigation<Route.NavBooks>(
            startDestination = Route.Books.Overview
        ) {
            composable<Route.Books.Overview> {
                setScreen(CurScreen.BooksOverview)
                OverviewScreenRoot(
                    viewModel = overviewViewModel,
                    openDrawer = openDrawer,
                    onAddBook = { navController.navigate(Route.Books.Add()) },
                    onAddBookWithScanner = { navController.navigate(Route.Books.Scanner) },
                    onOpenBook = { bookId -> navController.navigate(Route.Books.Details(bookId)) }
                )
            }
            composable<Route.Books.Add>(
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            ) {
                setScreen(CurScreen.Add)
                val args = it.toRoute<Route.Books.Add>() // TODO: Implement AddScreen with passed isbn
                val viewModel = koinViewModel<AddViewModel>()

                LaunchedEffect(args.isbn) {
                    if (args.isbn != null) {
                        viewModel.onAction(AddAction.OnAutoComplete(isbn = args.isbn))
                    }
                }

                AddScreenRoot(
                    viewModel = viewModel,
                    onShowScanner = { navController.navigate(Route.Books.Scanner) },
                    onShowOverview = { navController.popBackStack<Route.Books.Overview>(false) },
                    onShowBookDetail = { bookId -> navController.navigate(Route.Books.Details(bookId)) },
                )
            }
            composable<Route.Books.Scanner>(
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            ) {
                setScreen(CurScreen.Scanner)
                BarcodeScannerView(
                    onBack = { navController.popBackStack() },
                    onBarcodeScanned = { isbn -> if (isbn != null) navController.navigate(Route.Books.Add(isbn)) },
                )
            }
            composable<Route.Books.Details>(
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            ) {
                setScreen(CurScreen.Details)
                val args = it.toRoute<Route.Books.Details>()
                DetailsScreenRoot(
                    onBack = { navController.popBackStack() },
                    onAuthorSearch = { authorToSearch ->
                        overviewViewModel.onAction(
                            OverviewAction.ChangeFilterList(
                                possessionStatus = null,
                                readStatus = null,
                                sortType = SortType.AUTHOR,
                                searchType = SearchType.AUTHOR
                            ))
                        overviewViewModel.onAction(OverviewAction.SearchQueryChanged(authorToSearch))
                        navController.popBackStack<Route.Books.Overview>(false)
                    },
                    bookId = args.bookId
                )
            }
        }
        navigation<Route.NavSeries>(
            startDestination = Route.Series.Overview
        ) {
            composable<Route.Series.Overview> {
                SeriesOverviewScreenRoot(
                    openDrawer = openDrawer,
                    navigateToSeriesDetails = { seriesId -> navController.navigate(Route.Series.Details(seriesId)) }
                )
                setScreen(CurScreen.SeriesOverview)
            }
            composable<Route.Series.Details>(
                enterTransition = { slideInHorizontally { it } },
                popEnterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popExitTransition = { slideOutHorizontally { it } },
            ) {
                val seriesId = it.toRoute<Route.Series.Details>().seriesId
                SeriesDetailsScreenRoot(
                    seriesId = seriesId,
                    onBack = { navController.popBackStack() },
                    onShowBookDetails = { bookId -> navController.navigate(Route.Books.Details(bookId)) }
                )
                setScreen(CurScreen.SeriesDetails)
            }
        }
        navigation<Route.NavSettings>(
            startDestination = Route.Settings.Overview
        ) {
            composable<Route.Settings.Overview> {
                setScreen(CurScreen.Settings)
                SettingsScreenRoot(
                    viewModel = settingsViewModel,
                    openDrawer = openDrawer,
                    onTrashClicked = { navController.navigate(Route.Settings.Trash) },
                    navigateToLegalRoute = { legalRoute ->
                        navController.navigate(legalRoute)
                    }
                )
            }
            composable<Route.Settings.Trash> {
                setScreen(CurScreen.Trash)
                TrashScreenRoot(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<LegalRoute.Imprint> {
                setScreen(CurScreen.Legals)
                ImprintScreen(
                    navigateBack = { navController.popBackStack() },
                )
            }
            composable<LegalRoute.Privacy> {
                setScreen(CurScreen.Legals)
                var htmlData by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(Unit) {
                    htmlData = Res.readBytes("files/privacy_policy_de.html").decodeToString()
                }
                PrivacyScreen(
                    htmlData = htmlData,
                    navigateBack = { navController.popBackStack() },
                )
            }
            composable<LegalRoute.Licenses> {
                setScreen(CurScreen.Legals)
                val libraries by produceLibraries {
                    Res.readBytes("files/aboutlibraries.json").decodeToString()
                }
                LicensesScreen(
                    libraries = libraries,
                    navigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}