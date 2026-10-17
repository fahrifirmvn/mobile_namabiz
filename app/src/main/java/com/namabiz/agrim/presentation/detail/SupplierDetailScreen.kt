package com.namabiz.agrim.presentation.detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.namabiz.agrim.presentation.theme.*

@Composable
fun SupplierDetailScreen(
    slug: String,
    onNavigateBack: () -> Unit,
    onRequestQuote: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Certificates (5)", "Documents (2)")

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
                    OutlinedButton(
                        onClick = { onRequestQuote(slug) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, PrimaryDark)
                    ) {
                        Text("Request quote", color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            // Android Native Intent to WhatsApp
                            val waIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://wa.me/6281234567890?text=Hello%20Agrim%20PTE%20Ltd,%20I%20am%20interested%20in%20your%20spices%20and%20coconut%20catalog.")
                            )
                            context.startActivity(waIntent)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Outlined.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            // Top App Bar
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
                Row {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = TextPrimary)
                    }
                    IconButton(onClick = { /* Bookmark */ }) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark", tint = TextPrimary)
                    }
                }
            }

            // Hero Factory Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=800",
                    contentDescription = "Factory Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Profile Title & Verification Badges
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("eco\nAGRIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                        }
                    }

                    Surface(
                        color = BrandGreenLight,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(BrandGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verified Syndicate", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Agrim PTE Ltd", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Certified Indonesian spices and coconut ingredients, exported since 2011.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Audited on-site, Oct 2025 · Replies within ~4 hours",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("agrim.example.com ↗", fontSize = 12.sp, color = BrandGreen, fontWeight = FontWeight.SemiBold)
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = BrandGreen,
                divider = { HorizontalDivider(color = BorderLight) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) BrandGreen else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // TAB 0: OVERVIEW
                    // Card 1: Trade Terms
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Trade terms", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Text("FOB & CIF", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val terms = listOf(
                                "MOQ" to "1 × 20ft FCL (~10 MT)",
                                "Capacity" to "500 MT / month",
                                "Lead time" to "14 days",
                                "Incoterms" to "FOB / CIF",
                                "Loading port" to "Tanjung Perak, Surabaya",
                                "Payment terms" to "T/T, L/C"
                            )

                            terms.forEach { (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, fontSize = 13.sp, color = TextSecondary)
                                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 2: Export Products
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Export products", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Text("Primary Lots", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val products = listOf(
                                Triple("Black pepper", "Grade 1", "550 g/l density · Moisture max 12% · Machine cleaned"),
                                Triple("Nutmeg (ABCD grade)", "Whole", "Sound whole nutmeg · Moisture max 10% · Aflatoxin tested"),
                                Triple("Desiccated coconut", "High Fat", "Fine & Medium grade · High fat 65% min · SO2 free")
                            )

                            products.forEach { (name, tag, spec) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(44.dp),
                                        shape = CircleShape,
                                        color = LightGrayChip
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Spa, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                            Surface(color = BrandGreenLight, shape = RoundedCornerShape(4.dp)) {
                                                Text(tag, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = BrandGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(spec, fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                if (name != "Desiccated coconut") HorizontalDivider(color = BorderLight)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 3: Company
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Company", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = Color(0xFFFBF8F3),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("HQ", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                            Text("Singapore", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        }
                                        Column {
                                            Text("Processing facility", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                            Text("Surabaya, Indonesia", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Workforce", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                            Text("50–100 staff", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        }
                                        Column {
                                            Text("Heritage", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                            Text("Est. 2011 (15 yrs)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Agrim PTE Ltd orchestrates direct origin sourcing across East Java and North Maluku spice grower cooperatives. By operating captive drying and steam-sterilization infrastructure in Surabaya, the syndicate enforces tight pesticide residue monitoring and uniform moisture grading directly aligned with EU and US FDA import tolerances.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                1 -> {
                    // TAB 1: CERTIFICATES & AUDITS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Certifications & Audits (5)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Icon(Icons.Outlined.Shield, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val certList = listOf(
                                Triple("HACCP", "Valid to Mar 2027", "Issuer: SGS International"),
                                Triple("ISO 22000:2018", "Valid to Jun 2027", "Issuer: TÜV SÜD"),
                                Triple("Halal MUI", "Valid to Feb 2027", "Issuer: LPPOM MUI"),
                                Triple("GMP (Good Manufacturing)", "Valid to Nov 2026", "Issuer: BPOM Indonesia"),
                                Triple("USDA Organic", "Valid to Jan 2027", "Issuer: Control Union")
                            )

                            certList.forEach { (certName, validity, issuer) ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(certName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                        Surface(color = BrandGreenLight, shape = RoundedCornerShape(12.dp)) {
                                            Text(validity, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = BrandGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text(issuer, fontSize = 11.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📄 View document (PDF)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandGreen, modifier = Modifier.clickable {
                                        android.widget.Toast.makeText(context, "Opening certificate: $certName", android.widget.Toast.LENGTH_SHORT).show()
                                    })
                                }
                                if (certName != "USDA Organic") HorizontalDivider(color = BorderLight)
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: DOCUMENTS & CATALOGS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Catalogs & Specs (2)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Icon(Icons.Outlined.Description, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val docList = listOf(
                                Triple("Agrim_Spices_Product_Catalog_2025.pdf", "2.4 MB · Updated Oct 2025", "Complete specifications for black pepper, nutmeg, desiccated coconut"),
                                Triple("Lab_Analysis_Report_Sterilization.pdf", "1.1 MB · Updated Nov 2025", "Microbiology & heavy metals test results for export batches")
                            )

                            docList.forEach { (fileName, meta, desc) ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(40.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = BrandGreenLight
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(22.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text(meta, fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(desc, fontSize = 11.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                android.widget.Toast.makeText(context, "Viewing $fileName", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            border = BorderStroke(1.dp, BorderLight)
                                        ) {
                                            Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("View PDF", fontSize = 11.sp, color = TextPrimary)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                android.widget.Toast.makeText(context, "Downloading $fileName", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            border = BorderStroke(1.dp, BorderLight)
                                        ) {
                                            Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Download", fontSize = 11.sp, color = TextPrimary)
                                        }
                                    }
                                }
                                if (fileName.contains("Spices")) HorizontalDivider(color = BorderLight)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
