package com.namabiz.agrim.presentation.paywall

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namabiz.agrim.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradePaywallBottomSheet(
    onDismiss: () -> Unit,
    onUpgradeSuccess: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf("annual") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderLight)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Close & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandGreenLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("BUSINESS PRO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Export this comparison as a branded PDF",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Access full certified producer ledgers, lab compliance dossiers, and instant RFQ exports.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Blurred Teaser Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9FAFB))
                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .blur(5.dp)
                ) {
                    Text("INSTITUTIONAL TRADE METRICS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Milling Capacity: 1,500 Tons/Month", fontSize = 11.sp)
                    Text("Verified MOQ: 1 FCL (14 MT)", fontSize = 11.sp)
                    Text("Factory Lead Time: 11 Calendar Days", fontSize = 11.sp)
                }

                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceCard,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unlock verified supplier metrics", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checklist
            val benefits = listOf(
                "Full operating data: capacity, MOQ, lead time",
                "Compliance and audit documents",
                "Unlimited catalog downloads",
                "Branded PDF comparison exports and direct WhatsApp contact"
            )

            benefits.forEach { benefit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(benefit, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Option 1: Annual Plan
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "annual" },
                shape = RoundedCornerShape(14.dp),
                color = if (selectedPlan == "annual") Color(0xFFF6FAF7) else SurfaceCard,
                border = BorderStroke(
                    if (selectedPlan == "annual") 2.dp else 1.dp,
                    if (selectedPlan == "annual") BrandGreen else BorderLight
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPlan == "annual",
                        onClick = { selectedPlan = "annual" },
                        colors = RadioButtonDefaults.colors(selectedColor = BrandGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Annual — \$29/mo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("Billed \$348/year · save 25%", fontSize = 12.sp, color = TextSecondary)
                    }
                    Surface(color = BrandGreenLight, shape = RoundedCornerShape(6.dp)) {
                        Text("BEST VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Monthly Plan
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "monthly" },
                shape = RoundedCornerShape(14.dp),
                color = if (selectedPlan == "monthly") Color(0xFFF6FAF7) else SurfaceCard,
                border = BorderStroke(
                    if (selectedPlan == "monthly") 2.dp else 1.dp,
                    if (selectedPlan == "monthly") BrandGreen else BorderLight
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPlan == "monthly",
                        onClick = { selectedPlan = "monthly" },
                        colors = RadioButtonDefaults.colors(selectedColor = BrandGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Monthly — \$39/mo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("Cancel anytime", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("Standard", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CTA Button
            Button(
                onClick = {
                    onUpgradeSuccess()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark,
                    contentColor = Color.White
                )
            ) {
                Text("Upgrade to Business Pro", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🛡 14-day money-back guarantee · Secure checkout",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Restore purchases", fontSize = 11.sp, color = TextSecondary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline, modifier = Modifier.clickable { })
                Text("  ·  ", fontSize = 11.sp, color = TextSecondary)
                Text("Contact sales for teams", fontSize = 11.sp, color = TextSecondary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline, modifier = Modifier.clickable { })
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
