package com.namabiz.agrim.presentation.account

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namabiz.agrim.presentation.theme.*

@Composable
fun AccountScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit
) {
    var pushNotificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Account", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
            Text(
                "Edit",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = BrandGreen,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Profile Avatar & Info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = BorderLight
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        tint = BrandGreen,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Surface(
                    modifier = Modifier.size(26.dp),
                    shape = CircleShape,
                    color = BrandGreen,
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Edit photo", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Head of Sourcing at Global Foods Ltd", fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Jakarta, Indonesia", fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = BrandGreenLight, shape = RoundedCornerShape(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verified buyer", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Membership Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Business Pro · Annual", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Renews Dec 14, 2026", fontSize = 12.sp, color = TextSecondary)
                }
                OutlinedButton(
                    onClick = { /* Invoices */ },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Billing & invoices", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: BUSINESS
        Text("BUSINESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column {
                AccountNavRow(title = "Company details & tax ID", trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted) })
                HorizontalDivider(color = BorderLight)
                AccountNavRow(
                    title = "Sourcing preferences",
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Spices, Coconut", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: LIBRARY
        Text("LIBRARY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column {
                AccountNavRow(
                    title = "Saved suppliers",
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = LightGrayChip, shape = RoundedCornerShape(10.dp)) {
                                Text("12", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                )
                HorizontalDivider(color = BorderLight)
                AccountNavRow(
                    title = "Downloaded catalogs",
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = LightGrayChip, shape = RoundedCornerShape(10.dp)) {
                                Text("18", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                )
                HorizontalDivider(color = BorderLight)
                AccountNavRow(
                    title = "Saved comparisons",
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = LightGrayChip, shape = RoundedCornerShape(10.dp)) {
                                Text("3", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: APP SETTINGS
        Text("APP SETTINGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push notifications", fontSize = 13.sp, color = TextPrimary)
                    Switch(
                        checked = pushNotificationsEnabled,
                        onCheckedChange = { pushNotificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandGreen)
                    )
                }
                HorizontalDivider(color = BorderLight)
                AccountNavRow(title = "Password & 2FA", trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted) })
                HorizontalDivider(color = BorderLight)
                AccountNavRow(title = "Terms & privacy", trailing = { Icon(Icons.Outlined.OpenInNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp)) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign out button
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Text("Sign out", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Agrim Directory v2.4.1 (Build 890) · Jakarta",
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AccountNavRow(
    title: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, color = TextPrimary)
        trailing()
    }
}
