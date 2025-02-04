package com.example.medicalapp.ui.theme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.medicalapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF388E3C)) // Blue to green gradient
                )
            ),
        contentAlignment = Alignment.TopCenter // Align the content to the top center
    ) {
        // Settings Icon in the top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings",
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.5f), shape = CircleShape)
                    .padding(8.dp)
                    .clickable { navController.navigate(Routes.UserInfoScreen) }
            )
        }


        // Column to stack "Welcome, Farmer!" and Farmer Logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter) // Align the Column at the top center
                .padding(top = 120.dp) // Adds space below the settings icon
        ) {
            // Title below the settings icon
            Text(
                text = "Welcome, Farmer!",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Farmer Logo Image below the title
            Image(
                painter = painterResource(id = R.drawable.ic_farmerlogo), // Replace with your farmer logo image
                contentDescription = "Farmer Logo",
                modifier = Modifier
                    .padding(top = 16.dp) // Adds space between title and image
                    .size(150.dp) // Adjust size as necessary
                    .clip(CircleShape) // Make the image round
                    .border(2.dp, Color.White, CircleShape) // Optional: Add a border around the round image
            )
        }

        // Column for buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomCenter) // Keep buttons at the bottom center
        ) {
            OptionButton("Take Photo", R.drawable.ic_cameralogo, Color(0xFF388E3C)) {
                navController.navigate("samples_screen/true")
            }
            OptionButton("Take Video", R.drawable.ic_video, Color(0xFF64B5F6)) {
                navController.navigate("samples_screen/false")
            }
            OptionButton("Live Classification", R.drawable.ic_live, Color(0xFFFFC107)) {
                navController.navigate("samples_screen/false")
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// OptionButton with simple design and clean contrast
@Composable
fun OptionButton(text: String, iconRes: Int, buttonColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(12.dp)
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                color = Color.White // Clean and readable text color
            )
        }
    }
}
