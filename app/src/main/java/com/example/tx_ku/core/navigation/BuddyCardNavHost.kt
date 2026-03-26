package com.example.tx_ku.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tx_ku.feature.forum.PostDetailScreen
import com.example.tx_ku.feature.onboarding.FollowGamesScreen
import com.example.tx_ku.feature.onboarding.OnboardingScreen
import com.example.tx_ku.feature.relation.BuddyRoomScreen
import com.example.tx_ku.feature.profile.AgentPersonaScreen
import com.example.tx_ku.feature.chat.AgentChatScreen
import com.example.tx_ku.feature.profile.ProfileEditScreen
import com.example.tx_ku.feature.social.AddFriendByIdScreen
import com.example.tx_ku.feature.social.FollowingListScreen
import com.example.tx_ku.feature.social.UserDirectMessageScreen
import com.example.tx_ku.feature.auth.LoginScreen
import com.example.tx_ku.feature.auth.RegisterScreen
import com.example.tx_ku.feature.splash.SplashScreen

@Composable
fun BuddyCardNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            MainTabScreen(navController = navController)
        }
        composable(Routes.MY_AGENT) {
            AgentPersonaScreen(navController = navController)
        }
        composable(Routes.AGENT_CHAT) {
            AgentChatScreen(navController = navController)
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
