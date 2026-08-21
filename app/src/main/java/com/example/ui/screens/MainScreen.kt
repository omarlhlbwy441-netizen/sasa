package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.TechDarkBorder
import com.example.ui.theme.TechDarkSurface
import com.example.ui.viewmodel.SasaViewModel

@Composable
fun MainScreen(viewModel: SasaViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val githubToken by viewModel.githubToken.collectAsState()
    val repoOwner by viewModel.repoOwner.collectAsState()
    val repoName by viewModel.repoName.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()
    val geminiKey by viewModel.geminiApiKey.collectAsState()

    val repoInfo by viewModel.repoInfo.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    val commitList by viewModel.commitList.collectAsState()
    val isRepoLoading by viewModel.isRepoLoading.collectAsState()
    val repoError by viewModel.repoError.collectAsState()
    val isPushingCode by viewModel.isPushingCode.collectAsState()
    val pushResult by viewModel.pushResult.collectAsState()

    val agentLogs by viewModel.agentLogs.collectAsState()
    val isAgentThinking by viewModel.isAgentThinking.collectAsState()

    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
    val serviceLogs by viewModel.serviceLogs.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = TechDarkSurface,
                contentColor = Color.White,
                tonalElevation = androidx.compose.ui.unit.Dp(8f)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "الوكيل الذكي"
                        )
                    },
                    label = {
                        Text(
                            text = "الوكيل الذكي",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tab_agent_hub")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "استوديو التطوير"
                        )
                    },
                    label = {
                        Text(
                            text = "استوديو التطوير",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tab_dev_studio")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Source,
                            contentDescription = "مستودع GitHub"
                        )
                    },
                    label = {
                        Text(
                            text = "GitHub",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tab_github")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "الخدمات"
                        )
                    },
                    label = {
                        Text(
                            text = "الخدمات",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tab_service")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات"
                        )
                    },
                    label = {
                        Text(
                            text = "الإعدادات",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AgentHubScreen(
                    viewModel = viewModel,
                    agentLogs = agentLogs,
                    isThinking = isAgentThinking
                )
                1 -> DeveloperStudioScreen(
                    viewModel = viewModel
                )
                2 -> GitHubScreen(
                    viewModel = viewModel,
                    repoOwner = repoOwner,
                    repoName = repoName,
                    repoInfo = repoInfo,
                    userInfo = userInfo,
                    commitList = commitList,
                    isLoading = isRepoLoading,
                    repoError = repoError,
                    isPushing = isPushingCode,
                    pushResult = pushResult
                )
                3 -> BackgroundServiceScreen(
                    viewModel = viewModel,
                    isServiceRunning = isServiceRunning,
                    serviceLogs = serviceLogs
                )
                4 -> SettingsScreen(
                    viewModel = viewModel,
                    githubToken = githubToken,
                    repoOwner = repoOwner,
                    repoName = repoName,
                    serverUrl = serverUrl,
                    geminiKey = geminiKey
                )
            }
        }
    }
}
