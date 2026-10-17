package com.namabiz.agrim.presentation.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.namabiz.agrim.core.camera.QrCodeAnalyzer
import com.namabiz.agrim.data.repository.SupplierRepository
import com.namabiz.agrim.presentation.theme.PrimaryDark
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    supplierRepository: SupplierRepository,
    onNavigateBack: () -> Unit,
    onNavigateToSupplier: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var isTorchEnabled by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isResolving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showManualInputDialog by remember { mutableStateOf(false) }
    var analyzerRef by remember { mutableStateOf<QrCodeAnalyzer?>(null) }

    fun processBarcode(rawValue: String) {
        if (isResolving) return
        isResolving = true
        coroutineScope.launch {
            try {
                val supplier = supplierRepository.resolveQrCode(rawValue)
                if (supplier != null) {
                    onNavigateToSupplier(supplier.slug)
                } else {
                    errorMessage = "Supplier not found for QR payload:\n$rawValue"
                }
            } catch (e: Exception) {
                errorMessage = "Error scanning QR: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isResolving = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    val cameraExecutor = Executors.newSingleThreadExecutor()
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val analyzer = QrCodeAnalyzer { detectedText ->
                            processBarcode(detectedText)
                        }
                        analyzerRef = analyzer

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, analyzer)
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                (ctx as? androidx.lifecycle.LifecycleOwner)
                                    ?: (context as androidx.lifecycle.LifecycleOwner),
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Failed to start camera: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Transparent overlay with cutout frame
            Canvas(modifier = Modifier.fillMaxSize()) {
                val boxSize = size.width * 0.72f
                val left = (size.width - boxSize) / 2f
                val top = (size.height - boxSize) / 2.3f

                val cutoutRect = Rect(left, top, left + boxSize, top + boxSize)
                val cutoutPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            cutoutRect,
                            CornerRadius(24.dp.toPx(), 24.dp.toPx())
                        )
                    )
                }

                clipPath(cutoutPath, clipOp = ClipOp.Difference) {
                    drawRect(color = Color(0x99000000))
                }
            }

            // Viewfinder border box
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .size(260.dp)
                        .border(3.dp, Color(0xFFE2A85C), RoundedCornerShape(24.dp))
                )
            }

            // Scanner Overlay Header & Controls
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x66000000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Scan QR Code",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            isTorchEnabled = !isTorchEnabled
                            camera?.cameraControl?.enableTorch(isTorchEnabled)
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (isTorchEnabled) Color(0xFFE2A85C) else Color(0x66000000),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isTorchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Torch",
                            tint = if (isTorchEnabled) Color.Black else Color.White
                        )
                    }
                }

                // Bottom Hint & Actions
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Align the supplier or brochure QR code inside the frame to inspect verified trade terms and certificates.",
                        color = Color(0xFFE0E0E0),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Button(
                        onClick = { showManualInputDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x44FFFFFF)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enter Slug Manually",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            // Camera Permission Denied View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Permission Required",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Agrim Directory requires camera access to scan QR codes on physical exhibition brochures and business cards.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Grant Permission", color = Color.White)
                }
            }
        }

        // Resolving / Loading Indicator
        if (isResolving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryDark)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Resolving Supplier...",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E232A),
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Matching code with database & cache",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Error Dialog
        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = {
                    errorMessage = null
                    analyzerRef?.resumeScanning()
                },
                title = { Text("Scan Result", fontWeight = FontWeight.Bold) },
                text = { Text(errorMessage ?: "") },
                confirmButton = {
                    Button(
                        onClick = {
                            errorMessage = null
                            analyzerRef?.resumeScanning()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                    ) {
                        Text("Try Again")
                    }
                }
            )
        }

        // Manual Slug Input Dialog
        if (showManualInputDialog) {
            var inputSlug by remember { mutableStateOf("agrim-pte-ltd") }
            AlertDialog(
                onDismissRequest = { showManualInputDialog = false },
                title = { Text("Manual Lookup", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            "Enter the supplier slug or QR payload URL (e.g. agrim-pte-ltd):",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = inputSlug,
                            onValueChange = { inputSlug = it },
                            label = { Text("Supplier Slug / Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showManualInputDialog = false
                            processBarcode(inputSlug.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                    ) {
                        Text("Search")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showManualInputDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
