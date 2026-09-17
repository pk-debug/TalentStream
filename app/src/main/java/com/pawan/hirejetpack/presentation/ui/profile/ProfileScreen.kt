package com.pawan.hirejetpack.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import com.pawan.hirejetpack.data.ThemeRepository
import com.pawan.hirejetpack.presentation.state.LoginUiState
import com.pawan.hirejetpack.presentation.state.LoginViewModel
import com.pawan.hirejetpack.presentation.ui.components.InitialsAvatar

/**
 * [ProfileScreenContent] — the Profile tab's body: gradient header +
 * info card + theme toggle + logout button.
 */
@Composable
fun ProfileScreenContent(
    viewModel: LoginViewModel,
    onLogout: () -> Unit,
    onAnalyticsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by ThemeRepository.isDarkMode.collectAsState()
    val user = (uiState as? LoginUiState.Success)?.user

    Column(modifier = Modifier.fillMaxSize()) {
        // ... (Gradient header remains the same)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                InitialsAvatar(
                    name = user?.name ?: "N A",
                    size = 88.dp,
                    backgroundColor = Color.White,
                    textColor = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = user?.name ?: "N/A",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(user?.role ?: "N/A") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ProfileInfoRow(label = "Email", value = user?.email ?: "N/A")
                Spacer(modifier = Modifier.height(14.dp))
                ProfileInfoRow(label = "Role", value = user?.role ?: "N/A")
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Dark Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Dark Mode",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { ThemeRepository.toggleDarkMode() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // ... (rest of the buttons)
        Button(
            onClick = onAnalyticsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(androidx.compose.material.icons.Icons.Default.TrendingUp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Market Insights (Flutter)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

/**
 * [ProfileInfoRow] — label on top (muted), value below (prominent).
 */
@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}