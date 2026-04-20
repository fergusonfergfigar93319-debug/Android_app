@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.example.tx_ku.core.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tx_ku.feature.forum.PostDetailScreen
import com.example.tx_ku.feature.onboarding.FollowGamesScreen
import com.example.tx_ku.feature.onboarding.OnboardingScreen
import com.example.tx_ku.feature.relation.BuddyRoomScreen
import com.example.tx_ku.feature.profile.facestudio.FaceStudioScreen
import com.example.tx_ku.feature.profile.AgentPersonaScreen
import com.example.tx_ku.feature.chat.AgentChatScreen
import com.example.tx_ku.feature.profile.ProfileEditScreen
import com.example.tx_ku.feature.social.AddFriendByIdScreen
import com.example.tx_ku.feature.social.FollowingListScreen
import com.example.tx_ku.feature.social.UserDirectMessageScreen
import com.example.tx_ku.feature.auth.LoginScreen
import com.example.tx_ku.feature.auth.RegisterScreen
import com.example.tx_ku.feature.splash.SplashScreen
import com.example.tx_ku.feature.feed.EsportsCultureDetailScreen
import com.example.tx_ku.feature.feed.GameNewsDetailScreen
import com.example.tx_ku.feature.publish.MediaPublishScreen
import com.example.tx_ku.TxKuApp
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.UserAgentStore

@Composable
fun BuddyCardNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as TxKuApp
    val isLoggedIn by app.container.sessionStore.isLoggedInFlow.collectAsState(initial = false)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(isLoggedIn, currentRoute) {
        if (!isLoggedIn) return@LaunchedEffect
        if (currentRoute != Routes.LOGIN) return@LaunchedEffect
        val sessionStore = app.container.sessionStore
        sessionStore.restoreCurrentUserIfMemoryEmpty()
        UserAgentStore.loadIntoCurrentUser()
        val dest = when {
            CurrentUser.profile == null -> Routes.ONBOARDING
            !GameInterestStore.hasCompletedSelection() -> Routes.GAME_INTEREST
            else -> Routes.MAIN_TABS
        }
        navController.navigate(dest) {
            popUpTo(Routes.LOGIN) { inclusive = true }
            launchSingleTop = true
        }
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.fillMaxSize()
        ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController)
        }
        composable(Routes.GAME_INTEREST) {
            FollowGamesScreen(navController)
        }
        composable(Routes.MAIN_TABS) {
            MainTabScreen(
                navController = navController,
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedContentScope = this
            )
        }
        composable(Routes.MY_AGENT) {
            AgentPersonaScreen(navController = navController)
        }
        composable(Routes.AGENT_FACE_STUDIO) {
            FaceStudioScreen(navController = navController)
        }
        composable(
            route = Routes.AGENT_CHAT,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
            popExitTransition = { slideOutHorizontally { it } + fadeOut() }
        ) {
            AgentChatScreen(navController = navController)
        }
        composable(
            route = Routes.GAME_NEWS_DETAIL + "/{newsId}",
            arguments = listOf(navArgument("newsId") { type = NavType.StringType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            GameNewsDetailScreen(
                newsId = newsId,
                navController = navController,
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedContentScope = this
            )
        }
        composable(Routes.MEDIA_PUBLISH) {
            MediaPublishScreen(
                onBackClick = { navController.popBackStack() },
                onPublishClick = { _, _ ->
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.ESPORTS_CULTURE_DETAIL + "/{cultureId}",
            arguments = listOf(navArgument("cultureId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cultureId = backStackEntry.arguments?.getString("cultureId")
            EsportsCultureDetailScreen(cultureId = cultureId, navController = navController)
        }
        composable(Routes.PROFILE_EDIT) {
            ProfileEditScreen(navController = navController)
        }
        composable(Routes.FOLLOWING_LIST) {
            FollowingListScreen(navController = navController)
        }
        composable(Routes.ADD_FRIEND_SEARCH) {
            AddFriendByIdScreen(navController = navController)
        }
        composable(
            route = Routes.USER_DM + "/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("userId").orEmpty()
            if (uid.isBlank()) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                Box(Modifier.fillMaxSize())
            } else {
                UserDirectMessageScreen(navController = navController, peerUserId = uid)
            }
        }
        composable(Routes.POST_EDITOR) {
            com.example.tx_ku.feature.forum.PostEditorScreen(navController = navController)
        }
        composable(
            route = Routes.POST_DETAIL + "/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            PostDetailScreen(postId = postId, navController = navController)
        }
        composable(
            route = Routes.BUDDY_ROOM + "/{relationId}",
            arguments = listOf(navArgument("relationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val relationId = backStackEntry.arguments?.getString("relationId")
            BuddyRoomScreen(relationId = relationId, navController = navController)
        }
        }
    }
}
