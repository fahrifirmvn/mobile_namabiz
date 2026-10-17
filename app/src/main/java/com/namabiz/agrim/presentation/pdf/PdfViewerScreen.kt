package com.namabiz.agrim.presentation.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.namabiz.agrim.core.database.AppDatabase
import com.namabiz.agrim.presentation.theme.BackgroundLight
import com.namabiz.agrim.presentation.theme.PrimaryDark
import com.namabiz.agrim.presentation.theme.SurfaceCard
import com.namabiz.agrim.presentation.theme.TextMuted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    docId: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var docTitle by remember { mutableStateOf("Product Catalog") }
    var pdfBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var localFileRef by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(docId) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            val doc = db.documentDao().getDocumentById(docId)
            docTitle = doc?.title ?: "Document #$docId"

            val targetFile = if (doc?.localFilePath != null && File(doc.localFilePath).exists()) {
                File(doc.localFilePath)
            } else {
                File(context.filesDir, "pdf_cache/catalog_${docId}.pdf")
            }

            if (targetFile.exists() && targetFile.length() > 0) {
                localFileRef = targetFile
                try {
                    val pfd = ParcelFileDescriptor.open(targetFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(pfd)
                    val count = renderer.pageCount
                    val bitmaps = mutableListOf<Bitmap>()

                    for (i in 0 until count) {
                        val page = renderer.openPage(i)
                        val width = page.width * 2
                        val height = page.height * 2
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmaps.add(bitmap)
                        page.close()
                    }
                    renderer.close()
                    pfd.close()

                    withContext(Dispatchers.Main) {
                        pdfBitmaps = bitmaps
                        isLoading = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Cannot render PDF: ${e.message}"
                        isLoading = false
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    errorMessage = "Document file has not been downloaded yet. Tap download from supplier profile to cache for offline viewing."
                    isLoading = false
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = docTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark,
                            maxLines = 1
                        )
                        Text(
                            text = if (pdfBitmaps.isNotEmpty()) "${pdfBitmaps.size} pages • Offline Cache" else "Offline Document",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryDark
                        )
                    }
                },
                actions = {
                    if (localFileRef != null && localFileRef!!.exists()) {
                        IconButton(onClick = {
                            try {
                                val uri: Uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    localFileRef!!
                                )
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share PDF Catalog"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share PDF",
                                tint = PrimaryDark
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryDark)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading document pages...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Document Offline Cache",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(pdfBitmaps) { pageIndex, pageBitmap ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Image(
                                        bitmap = pageBitmap.asImageBitmap(),
                                        contentDescription = "Page ${pageIndex + 1}",
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Page ${pageIndex + 1} of ${pdfBitmaps.size}",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
