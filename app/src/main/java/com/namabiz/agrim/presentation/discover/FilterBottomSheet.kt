package com.namabiz.agrim.presentation.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namabiz.agrim.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApplyFilters: () -> Unit
) {
    var selectedSort by remember { mutableStateOf("Verified first") }
    var selectedOrigins by remember { mutableStateOf(setOf("Indonesia", "Singapore")) }
    var selectedCerts by remember { mutableStateOf(setOf("Halal", "HACCP")) }
    var selectedLeadTime by remember { mutableStateOf("14 days") }
    var onlyWithCatalogs by remember { mutableStateOf(true) }

    val origins = listOf(
        "Indonesia" to 22,
        "Singapore" to 9,
        "Vietnam" to 6,
        "India" to 7,
        "Sri Lanka" to 4
    )

    val certificates = listOf("Halal", "HACCP", "ISO 22000", "Fair Trade", "GMP", "USDA Organic")
    val leadTimes = listOf("Any", "14 days", "21 days", "30 days")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderLight)
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reset all",
                    fontSize = 13.sp,
                    color = BrandGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        selectedOrigins = emptySet()
                        selectedCerts = emptySet()
                        selectedLeadTime = "Any"
                        onlyWithCatalogs = false
                    }
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Sort by
                Text("Sort by", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Verified first", "Most popular", "A-Z").forEach { sort ->
                    val isSelected = selectedSort == sort
                    Surface(
                        modifier = Modifier.clickable { selectedSort = sort },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BrandGreen else Color.Transparent,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = sort,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(14.dp))

            // Origin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Origin", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("${selectedOrigins.size} selected", fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            origins.forEach { (country, count) ->
                val isChecked = selectedOrigins.contains(country)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedOrigins = if (isChecked) selectedOrigins - country else selectedOrigins + country
                        }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$country  ($count)", fontSize = 13.sp, color = TextPrimary)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            selectedOrigins = if (it) selectedOrigins + country else selectedOrigins - country
                        },
                        colors = CheckboxDefaults.colors(checkedColor = BrandGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(14.dp))

            // Certificates
            Text("Certificates", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                certificates.forEach { cert ->
                    val isSelected = selectedCerts.contains(cert)
                    Surface(
                        modifier = Modifier.clickable {
                            selectedCerts = if (isSelected) selectedCerts - cert else selectedCerts + cert
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BrandGreen else Color.Transparent,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = cert,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(14.dp))

            // Max Lead Time
            Text("Max lead time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                leadTimes.forEach { time ->
                    val isSelected = selectedLeadTime == time
                    Surface(
                        modifier = Modifier.clickable { selectedLeadTime = time },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BrandGreen else Color.Transparent,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = time,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Downloadable PDF Catalog Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Only suppliers with downloadable catalogs", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Show suppliers with verified PDF brochures", fontSize = 11.sp, color = TextSecondary)
                }
                Switch(
                    checked = onlyWithCatalogs,
                    onCheckedChange = { onlyWithCatalogs = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandGreen)
                )
            }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sticky Bottom CTA Surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            onApplyFilters()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Show 14 suppliers", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
