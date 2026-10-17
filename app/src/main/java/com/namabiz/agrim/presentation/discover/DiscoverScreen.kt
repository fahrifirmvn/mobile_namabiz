package com.namabiz.agrim.presentation.discover

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCompare: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedCompareIds by remember { mutableStateOf(setOf(1, 2)) }

    val categories = listOf("ALL", "SPICES & HERBS", "COCONUT PRODUCTS", "PACKAGING")

    val suppliers = remember {
        listOf(
            Supplier(
                id = 1,
                name = "Agrim PTE Ltd",
                slug = "agrim-pte-ltd",
                country = "Singapore",
                city = "plant in Surabaya",
                industryCategory = "Spices & Coconut",
                isVerified = true,
                auditStatus = "Audited on-site, Oct 2025",
                moq = "1 × 20ft FCL",
                capacity = "500 MT/mo",
                leadTime = "14 days",
                tags = listOf("HACCP", "ISO 22000", "Halal MUI", "+2"),
                logo = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=600"
            ),
            Supplier(
                id = 2,
                name = "Nusantara Agro",
                slug = "nusantara-agro",
                country = "Jakarta",
                city = "Indonesia",
                industryCategory = "Coconut Products",
                isVerified = true,
                auditStatus = "Audited on-site, Sep 2025",
                moq = "5 MT",
                capacity = "350 MT/mo",
                leadTime = "21 days",
                tags = listOf("HACCP", "Halal MUI", "Fair Trade"),
                logo = "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600"
            ),
            Supplier(
                id = 3,
                name = "Ceylon Harvest",
                slug = "ceylon-harvest",
                country = "Colombo",
                city = "Sri Lanka",
                industryCategory = "Spices",
                isVerified = false,
                auditStatus = "Verification pending (audit scheduled)",
                moq = "2 MT",
                capacity = "80 MT/mo",
                leadTime = "25 days",
                tags = listOf("USDA Organic"),
                logo = "https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=600"
            )
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            onDismiss = { showFilterSheet = false },
            onApplyFilters = { /* Filters applied */ }
        )
    }

    val filteredSuppliers = remember(suppliers, selectedCategory, searchQuery) {
        suppliers.filter { supplier ->
            val matchesCategory = when (selectedCategory) {
                "ALL" -> true
                "SPICES & HERBS" -> supplier.industryCategory.contains("Spices", ignoreCase = true)
                "COCONUT PRODUCTS" -> supplier.industryCategory.contains("Coconut", ignoreCase = true)
                "PACKAGING" -> supplier.industryCategory.contains("Packaging", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    supplier.name.contains(searchQuery, ignoreCase = true) ||
                    supplier.country.contains(searchQuery, ignoreCase = true) ||
                    (supplier.city?.contains(searchQuery, ignoreCase = true) == true) ||
                    supplier.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(WarmBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimary)
                        }
                        Text(
                            text = "Agrim Directory",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmarks", tint = TextPrimary)
                    }
                }
            }

            // Search Bar & Filter Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        placeholder = { Text("Search product, supplier or certifica", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .size(52.dp)
                            .background(SurfaceCard, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Outlined.Tune, contentDescription = "Filter", tint = TextPrimary)
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier.clickable { selectedCategory = category },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) BrandGreen else SurfaceCard,
                            border = if (isSelected) null else BorderStroke(1.dp, BorderLight)
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Count & Sort Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${filteredSuppliers.size} suppliers", fontSize = 13.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.clickable { showFilterSheet = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sort: Verified first", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Supplier Cards
            items(filteredSuppliers) { supplier ->
                val isCompared = selectedCompareIds.contains(supplier.id)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column {
                        // Hero Image with Bookmark & Initials Avatar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            AsyncImage(
                                model = supplier.logo,
                                contentDescription = supplier.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { /* Bookmark */ },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .size(36.dp)
                                    .background(SurfaceCard.copy(alpha = 0.9f), CircleShape)
                            ) {
                                Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }
                            // Initials Avatar
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 14.dp, bottom = 10.dp)
                                    .size(46.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = BrandGreen
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val initials = supplier.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                                    Text(
                                        text = initials,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Info Body
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = supplier.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${supplier.country} · ${supplier.city}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Verification / Audit Badge
                            if (supplier.isVerified) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = supplier.auditStatus,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandGreen
                                    )
                                }
                            } else {
                                Surface(color = AmberBadgeBg, shape = RoundedCornerShape(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AmberBadgeText))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(supplier.auditStatus, fontSize = 11.sp, color = AmberBadgeText, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats 3 Columns (MOQ, CAPACITY, LEAD TIME)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("MOQ", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(supplier.moq, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Column {
                                    Text("CAPACITY", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(supplier.capacity, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Column {
                                    Text("LEAD TIME", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(supplier.leadTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Certification Chips
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                supplier.tags.forEach { tag ->
                                    Surface(
                                        color = LightGrayChip,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons: "View profile" + "+" (add to compare)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onNavigateToDetail(supplier.slug) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryDark,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("View profile", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                }

                                IconButton(
                                    onClick = {
                                        selectedCompareIds = if (isCompared) {
                                            selectedCompareIds - supplier.id
                                        } else {
                                            selectedCompareIds + supplier.id
                                        }
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(if (isCompared) BrandGreen else Color.Transparent, RoundedCornerShape(10.dp))
                                        .border(1.dp, if (isCompared) BrandGreen else BorderLight, RoundedCornerShape(10.dp))
                                ) {
                                    if (isCompared) {
                                        Icon(Icons.Default.Check, contentDescription = "Compared", tint = Color.White)
                                    } else {
                                        Icon(Icons.Default.Add, contentDescription = "Add to compare", tint = TextPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "2 catalogs (PDF)",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { onNavigateToDetail(supplier.slug) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Compare Bar at bottom
        if (selectedCompareIds.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            Surface(color = BrandGreen, shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("AP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Surface(color = Color(0xFF2C5E43), shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("NA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Compare (${selectedCompareIds.size} selected)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = onNavigateToCompare,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Compare now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    }
                }
            }
        }
    }
}
