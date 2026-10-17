package com.namabiz.agrim.presentation.compare

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namabiz.agrim.presentation.paywall.UpgradePaywallBottomSheet
import com.namabiz.agrim.presentation.theme.*

@Composable
fun CompareScreen(
    onNavigateBack: () -> Unit,
    onRequestQuoteBoth: () -> Unit
) {
    var showDifferencesOnly by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val exportPdfReport: () -> Unit = {
        try {
            val supA = com.namabiz.agrim.core.model.Supplier(
                id = 1,
                name = "Agrim PTE Ltd",
                slug = "agrim-pte-ltd",
                country = "Singapore",
                city = "Surabaya Plant",
                industryCategory = "Spices & Coconut",
                isVerified = true,
                auditStatus = "Audited on-site, Oct 2025",
                moq = "1 × 20ft FCL",
                capacity = "500 MT/mo",
                leadTime = "14 days",
                tags = listOf("HACCP", "ISO 22000", "Halal MUI")
            )
            val supB = com.namabiz.agrim.core.model.Supplier(
                id = 2,
                name = "Nusantara Agro",
                slug = "nusantara-agro",
                country = "Indonesia",
                city = "Medan",
                industryCategory = "Coconut Products",
                isVerified = true,
                auditStatus = "Audited on-site, Sep 2025",
                moq = "5 MT",
                capacity = "800 MT/mo",
                leadTime = "21 days",
                tags = listOf("HACCP", "Halal MUI", "USDA Organic")
            )
            val pdfFile = com.namabiz.agrim.core.pdf.PdfComparisonGenerator.generateComparisonReport(
                context = context,
                supplierA = supA,
                supplierB = supB
            )
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share Comparison Report"))
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Export error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    if (showPaywall) {
        UpgradePaywallBottomSheet(
            onDismiss = { showPaywall = false },
            onUpgradeSuccess = {
                showPaywall = false
                exportPdfReport()
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { /* Save comparison */ },
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark", tint = TextPrimary)
                    }

                    Button(
                        onClick = onRequestQuoteBoth,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Request quote from both", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = "Compare (2)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                OutlinedButton(
                    onClick = { showPaywall = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export PDF", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            // Supplier Header Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard)
                    .border(BorderStroke(1.dp, BorderLight))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                // Supplier 1
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = BrandGreen, shape = RoundedCornerShape(6.dp), modifier = Modifier.size(34.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("AP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Agrim PTE Ltd", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        Text("Export Syndicate", fontSize = 10.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Supplier 2
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = Color(0xFF2C5E43), shape = RoundedCornerShape(6.dp), modifier = Modifier.size(34.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("NA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nusantara Agro", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        Text("Direct Producer", fontSize = 10.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            // Show differences only switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show differences only  (6 params)", fontSize = 12.sp, color = TextSecondary)
                Switch(
                    checked = showDifferencesOnly,
                    onCheckedChange = { showDifferencesOnly = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandGreen)
                )
            }

            // Comparison Sections Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Section 1: General
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("General", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("3 METRICS", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ComparisonRow(
                        label = "ORIGIN",
                        val1 = "Singapore\n(plant Surabaya)",
                        val2 = "Jakarta, Indonesia"
                    )
                    ComparisonRow(
                        label = "FOCUS PRODUCTS",
                        val1 = "Black pepper, nutmeg, coconut",
                        val2 = "Desiccated coconut, milk powder"
                    )
                    ComparisonRow(
                        label = "ESTABLISHED",
                        val1 = "2011 (15 yrs)",
                        val2 = "2016 (10 yrs)"
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Section 2: Production & scale
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Production & scale", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("5 METRICS", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ComparisonRow(
                        label = "MONTHLY CAPACITY",
                        val1 = "500 MT/mo",
                        val2 = "350 MT/mo",
                        best1 = true
                    )
                    ComparisonRow(
                        label = "MOQ (NORMALIZED)",
                        val1 = "1 × 20ft FCL (~10 MT)",
                        val2 = "5 MT",
                        best2 = true
                    )
                    ComparisonRow(
                        label = "PRODUCTION LEAD TIME",
                        val1 = "14 days",
                        val2 = "21 days",
                        best1 = true
                    )
                    ComparisonRow(
                        label = "SUPPORTED INCOTERMS",
                        val1 = "FOB / CIF",
                        val2 = "FOB only"
                    )
                    ComparisonRow(
                        label = "PRODUCT CATALOGS",
                        val1 = "📄 2 specs (PDF)",
                        val2 = "📄 1 spec (PDF)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(color = Color(0xFFF9FAFB), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Best tag indicates higher capacity, lower MOQ, or shorter lead time.", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Section 3: Certificates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Certificates", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("5 AUDITS", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ComparisonAuditRow(
                        label = "HACCP FOOD SAFETY",
                        status1 = "Verified",
                        status2 = "Verified",
                        isWarn2 = false
                    )
                    ComparisonAuditRow(
                        label = "HALAL MUI",
                        status1 = "Valid to Feb 2027",
                        status2 = "Expires in 18 days",
                        isWarn2 = true
                    )
                    ComparisonAuditRow(
                        label = "ISO 22000",
                        status1 = "Verified",
                        status2 = "— Not listed",
                        isWarn2 = false
                    )
                    ComparisonAuditRow(
                        label = "USDA ORGANIC",
                        status1 = "Verified",
                        status2 = "— Not listed",
                        isWarn2 = false
                    )
                    ComparisonAuditRow(
                        label = "FAIR TRADE CERTIFIED",
                        status1 = "— Not listed",
                        status2 = "Verified",
                        isWarn2 = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🛡 All specs audited by Malabar Board", fontSize = 11.sp, color = TextSecondary)
                Text("Trade terms", fontSize = 11.sp, color = TextSecondary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ComparisonRow(
    label: String,
    val1: String,
    val2: String,
    best1: Boolean = false,
    best2: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(val1, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, textAlign = TextAlign.Center)
                if (best1) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(color = BrandGreenLight, shape = RoundedCornerShape(10.dp)) {
                        Text("✓ Best", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(val2, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, textAlign = TextAlign.Center)
                if (best2) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(color = BrandGreenLight, shape = RoundedCornerShape(10.dp)) {
                        Text("✓ Best", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
    }
}

@Composable
fun ComparisonAuditRow(
    label: String,
    status1: String,
    status2: String,
    isWarn2: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (status1 == "Verified") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(status1, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                } else {
                    Text(status1, fontSize = 11.sp, color = if (status1.contains("—")) TextMuted else TextPrimary)
                }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (isWarn2) {
                    Surface(color = AmberBadgeBg, shape = RoundedCornerShape(6.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠ $status2", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AmberBadgeText)
                        }
                    }
                } else if (status2 == "Verified") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(status2, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                } else {
                    Text(status2, fontSize = 11.sp, color = TextMuted)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
    }
}
