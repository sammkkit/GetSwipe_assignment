package com.samkit.swipeassignment

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.samkit.swipeassignment.presentation.addProduct.AddProductScreen
import com.samkit.swipeassignment.presentation.addProduct.AddProductViewModel
import com.samkit.swipeassignment.presentation.productList.ProductListScreen
import com.samkit.swipeassignment.ui.theme.SwipeAssignmentTheme
import com.samkit.swipeassignment.util.AirplaneModeBroadcastReciever
import com.samkit.swipeassignment.util.PermissionDialog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    lateinit var airplaneModeBroadcastReciever: AirplaneModeBroadcastReciever
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        airplaneModeBroadcastReciever = AirplaneModeBroadcastReciever()
        IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED).also {
            registerReceiver(airplaneModeBroadcastReciever,it)
        }
        setContent {
            SwipeAssignmentTheme {
                Surface {
                    NotificationPermissionRequester()
                    val addProductViewModel: AddProductViewModel = koinViewModel()
                    val sheetState = rememberModalBottomSheetState(
                        skipPartiallyExpanded = false
                    )
                    val scope = rememberCoroutineScope()
                    var showBottomSheet by remember { mutableStateOf(false) }

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState
                        ) {
                            AddProductScreen(
                                onProductAdded = {
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                            addProductViewModel.resetState()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    ProductListScreen(
                        onAddProductClick = {
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun NotificationPermissionRequester() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val activity = (context as? ComponentActivity) ?: return

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                // If permission is denied, and we can't ask again, show the settings dialog
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showSettingsDialog = true
                }
            }
        }
    )

    // Check permission status when the composable enters the composition
    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            // If the user has denied it before, show a rationale
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showRationaleDialog = true
            } else {
                // Otherwise, it's the first time or they permanently denied it. Just ask.
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Show the rationale dialog when needed
    if (showRationaleDialog) {
        PermissionDialog(
            title = "Notification Permission",
            text = "To notify you about pending product syncs and updates, please allow notifications.",
            confirmButtonText = "Continue",
            onConfirm = {
                showRationaleDialog = false
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onDismiss = { showRationaleDialog = false }
        )
    }

    if (showSettingsDialog) {
        PermissionDialog(
            title = "Permission Required",
            text = "You have permanently disabled notifications. To enable them, please go to the app settings.",
            confirmButtonText = "Open Settings",
            onConfirm = {
                showSettingsDialog = false
                // Intent to open the app's specific settings screen
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }
}
