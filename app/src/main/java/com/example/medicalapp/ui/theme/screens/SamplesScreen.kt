package com.example.medicalapp.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.medicalapp.R
import com.example.medicalapp.ui.theme.UserData
import com.example.medicalapp.ui.theme.UserPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.*
import java.io.File

@Composable
fun SamplesScreen(navController: NavHostController, isCamera: Boolean) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val userData by userPreferences.userData.collectAsState(initial = UserData("", "", ""))
   val locationInfo by ShareObjects.locationInfo.collectAsState()
    val items: List<Pair<Int, String>> = listOf(
        R.drawable.fecal_samples to "Fecal Sample",
        R.drawable.saliva_sample to "Saliva Sample",
        R.drawable.urine_sample to "Urine Sample",
        R.drawable.skin_samples to "Skin Sample"
    )
    var hasPermission by remember { mutableStateOf(checkPermission(context, isCamera)) }
    var capturedMediaUri by remember { mutableStateOf<Uri?>(null) }
    val mediaUri = remember { mutableStateOf(createMediaFile(context, isCamera)) }

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = if (isCamera) ActivityResultContracts.TakePicture() else ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            capturedMediaUri = mediaUri.value
            Toast.makeText(context, "Media captured successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to capture media!", Toast.LENGTH_SHORT).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required!", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF388E3C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Select Your Option",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(items) { item ->
                    Card(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            if (hasPermission) {
                                mediaUri.value = createMediaFile(context, isCamera)
                                mediaLauncher.launch(mediaUri.value)
                            } else {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = item.first),
                                contentDescription = item.second,
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = item.second)
                        }
                    }
                }
            }

            capturedMediaUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                if (isCamera) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Captured Image",
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                setVideoURI(uri)
                                start()
                            }
                        },
                        modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        sendEmail(context, uri, userData.email, userData.username, userData.phone,locationInfo)
                      capturedMediaUri = null
                      navController.navigate(Routes.SplashScreen)
                      
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = Color(0xFF64B5F6)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Send via Email",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
  LocationScreen()
}

fun createMediaFile(context: Context, isCamera: Boolean): Uri {
    val fileName =
        if (isCamera) "IMG_${System.currentTimeMillis()}.jpg" else "VID_${System.currentTimeMillis()}.mp4"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val mediaFile = File(storageDir, fileName)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", mediaFile)
}

fun checkPermission(context: Context, isCamera: Boolean): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

fun sendEmail(
  context: Context,
  fileUri: Uri,
  email: String = "abcd@Qgmail.com",
  username: String,
  phone: String,
  locationInfo: String
) {
  val locationUrl = "https://www.google.com/maps?q=$locationInfo"
  
  val emailIntent = Intent(Intent.ACTION_SEND).apply {
    type = "application/octet-stream"
    putExtra(Intent.EXTRA_EMAIL, arrayOf("donturahul@gmail.com")) // Change recipient email
    putExtra(Intent.EXTRA_SUBJECT, "Captured Media File")
    putExtra(Intent.EXTRA_TEXT, """
    Please find the attached media file.
    
    UserName: $username
    Phone Number: $phone
    Location: $locationUrl
""".trimIndent())
    
    putExtra(Intent.EXTRA_STREAM, fileUri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
    context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
}
fun getCurrentLocation(
  context: Context,
  fusedLocationClient: FusedLocationProviderClient,
  onLocationReceived: (String) -> Unit
) {
  if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    onLocationReceived("Permission not granted")
    return
  }
  
  fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
    if (location != null) {
      val latitude = location.latitude
      val longitude = location.longitude
      onLocationReceived("$latitude,$longitude")
    } else {
      onLocationReceived("Location unavailable")
    }
  }.addOnFailureListener {
    onLocationReceived("Failed to get location: ${it.message}")
  }
}


@Composable
fun LocationScreen() : Unit{
  val context = LocalContext.current
  val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
  val locationState = remember { mutableStateOf("Click to get location") }
  
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { granted ->
      if (granted) {
        getCurrentLocation(context, fusedLocationClient) { location ->
          locationState.value = location
          ShareObjects.updateLocation(location)
          
        }
      } else {
        locationState.value = "Permission denied"
        ShareObjects.updateLocation("Permission denied")
        
      }
    }
  )

  LaunchedEffect(Unit) {
    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }
  
}