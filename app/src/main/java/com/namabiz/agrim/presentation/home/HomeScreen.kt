package com.namabiz.agrim.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.namabiz.agrim.core.model.Supplier
import com.namabiz.agrim.presentation.theme.*

@Composable
fun HomeScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToSupplier: (String) -> Unit,
    onNavigateToRfq: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    val scrollState = rememberScrollState()

    val recentlyViewed = remember {
        listOf(
            Supplier(
                id = 1,
                name = "Agrim PTE Ltd",
                slug = "agrim-pte-ltd",
                country = "Vietnam",
                city = "High Plateau",
                industryCategory = "Spices & Coconut",
                isVerified = true,
                auditStatus = "Audited on-site, Oct 2025",
                moq = "1 × 20ft FCL",
                leadTime = "14 days",
                logo = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500"
            ),
            Supplier(
                id = 2,
                name = "Nusantara Agro",
                slug = "nusantara-agro",
                country = "Indonesia",
                city = "North Sumatra",
                industryCategory = "Coconut Products",
                isVerified = true,
                auditStatus = "Audited on-site, Sep 2025",
                moq = "5 MT",
                leadTime = "21 days",
                logo = "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=500"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BorderLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        tint = BrandGreen
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Good morning,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "John Doe",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { /* Notifications */ },
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextPrimary)
                }
                IconButton(
                    onClick = onNavigateToScanner,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan QR", tint = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Business Pro Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Business Pro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = " · renews Dec 14, 2026",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "Manage",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandGreen,
                    modifier = Modifier.clickable { onNavigateToAccount() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Needs Attention Card
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "!",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = PrimaryDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Needs attention",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryDark
                        )
                    }
                    Text(
                        text = "3 PENDING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Item 1: Halal certificate expiry
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSupplier("nusantara-agro") },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFB45309))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nusantara Agro — Halal MUI certificate expires in 18 days",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = AmberBadgeBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Compliance Risk",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AmberBadgeText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Audited Oct '24",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Item 2: Quote received
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRfq() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Agrim PTE Ltd sent a quote for RFQ #1042",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "(Black pepper, 20 MT)",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.padding(start = 15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = BrandGreenLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FOB $6,420/MT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Valid 48h",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Item 3: RFQ pending with Nudge button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(TextSecondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RFQ #1041 to Nusantara Agro — no r...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                    OutlinedButton(
                        onClick = { /* Nudge action */ },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Text("Nudge", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // + New RFQ Button
        Button(
            onClick = onNavigateToRfq,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryDark,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("New RFQ", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats Summary Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight),
            onClick = onNavigateToCompare
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "12 saved",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "·",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "2 comparing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "·",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "18 catalogs downloaded",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recently Viewed Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently viewed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View catalog",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandGreen,
                modifier = Modifier.clickable { onNavigateToSupplier("agrim-pte-ltd") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recentlyViewed) { supplier ->
                Card(
                    modifier = Modifier
                        .width(240.dp)
                        .clickable { onNavigateToSupplier(supplier.slug) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(BorderLight)
                        ) {
                            AsyncImage(
                                model = supplier.logo,
                                contentDescription = supplier.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopStart),
                                color = SurfaceCard.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${supplier.country} · ${supplier.city}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = supplier.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = supplier.auditStatus,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("MOQ", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(supplier.moq, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                                Column {
                                    Text("LEAD TIME", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(supplier.leadTime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
