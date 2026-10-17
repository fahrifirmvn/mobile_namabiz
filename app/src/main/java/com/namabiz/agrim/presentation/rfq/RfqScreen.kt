package com.namabiz.agrim.presentation.rfq

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namabiz.agrim.core.model.Rfq
import com.namabiz.agrim.data.repository.RfqRepository
import com.namabiz.agrim.data.repository.SupplierRepository
import com.namabiz.agrim.presentation.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RfqViewModel(
    private val rfqRepository: RfqRepository,
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val _rfqs = MutableStateFlow<List<Rfq>>(emptyList())
    val rfqs: StateFlow<List<Rfq>> = _rfqs.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        loadRfqs()
    }

    private fun loadRfqs() {
        viewModelScope.launch {
            rfqRepository.getRfqsFlow().collect { list ->
                if (list.isEmpty()) {
                    // Seed initial demo RFQs if empty
                    rfqRepository.createRfq(listOf(1), "Cloves Lalpari (CG3)", 50.0)
                    rfqRepository.createRfq(listOf(2), "Nutmeg ABCD High Grade", 25.0)
                }
                _rfqs.value = list
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            rfqRepository.refreshRfqs()
            _isRefreshing.value = false
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setShowCreateDialog(show: Boolean) {
        _showCreateDialog.value = show
    }

    fun submitNewRfq(productName: String, volumeMt: Double) {
        viewModelScope.launch {
            rfqRepository.createRfq(
                supplierIds = listOf(1),
                productName = productName,
                volumeMt = volumeMt
            )
            _showCreateDialog.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfqScreen(
    viewModel: RfqViewModel,
    onNavigateToSupplier: (String) -> Unit
) {
    val context = LocalContext.current
    val rfqs by viewModel.rfqs.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()

    val filteredList = remember(rfqs, filter) {
        when (filter) {
            "Action Required" -> rfqs.filter { it.status.lowercase() == "quoted" }
            "Pending" -> rfqs.filter { it.status.lowercase() == "pending" }
            "Closed" -> rfqs.filter { it.status.lowercase() == "closed" }
            else -> rfqs
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "RFQ & Quotations",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                        Text(
                            text = "Requests for quotation & trade bids",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = PrimaryDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowCreateDialog(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = { Text("New RFQ", fontWeight = FontWeight.SemiBold, color = Color.White) },
                containerColor = PrimaryDark,
                contentColor = Color.White
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Action Required", "Pending", "Closed").forEach { item ->
                    val isSelected = filter == item
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(item) },
                        label = { Text(item, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryDark,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceCard,
                            labelColor = TextDark
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.RequestQuote,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No RFQ found",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Create a new quotation request to receive verified supplier pricing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        RfqCard(
                            rfq = item,
                            onWhatsAppClick = {
                                val message = "Halo ${item.supplierName}, menindaklanjuti RFQ #${item.id} untuk produk ${item.productName} (${item.volume}) di Agrim Directory."
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/6281234567890?text=${Uri.encode(message)}")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateRfqDialog(
                onDismiss = { viewModel.setShowCreateDialog(false) },
                onSubmit = { product, volume ->
                    viewModel.submitNewRfq(product, volume)
                }
            )
        }
    }
}

@Composable
fun RfqCard(
    rfq: Rfq,
    onWhatsAppClick: () -> Unit
) {
    val isQuoted = rfq.status.lowercase() == "quoted"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: RFQ # and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RFQ #${rfq.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryDark,
                    fontWeight = FontWeight.Bold
                )

                val badgeBg = if (isQuoted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                val badgeText = if (isQuoted) Color(0xFF2E7D32) else Color(0xFFF57F17)
                val statusLabel = if (isQuoted) "QUOTATION IN" else "AWAITING BID"

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = statusLabel,
                        color = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product & Volume
            Text(
                text = rfq.productName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            val supplierNameDisplay = rfq.supplierName?.ifBlank { "Agrim PTE Ltd" } ?: "Agrim PTE Ltd"
            Text(
                text = "Supplier: $supplierNameDisplay • Volume: ${rfq.volume}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isQuoted && rfq.quoteFob != null) {
                // Quoted Price Highlight
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF4F8F6)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Quoted Price (FOB)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = rfq.quoteFob,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDark
                            )
                        }

                        if (!rfq.validUntil.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = PrimaryDark
                                )
                                Text(
                                    text = rfq.validUntil,
                                    fontSize = 12.sp,
                                    color = PrimaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onWhatsAppClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Accept & WA", fontSize = 13.sp, color = Color.White)
                    }
                }
            } else {
                Text(
                    text = "Awaiting quotation from supplier. Typical turnaround time is 24-48 hours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun CreateRfqDialog(
    onDismiss: () -> Unit,
    onSubmit: (productName: String, volumeMt: Double) -> Unit
) {
    var productName by remember { mutableStateOf("Cloves Lalpari (CG3)") }
    var volumeInput by remember { mutableStateOf("50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Request for Quotation", fontWeight = FontWeight.Bold, color = PrimaryDark)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Request verified pricing and shipping FOB quotes directly from suppliers.",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = volumeInput,
                    onValueChange = { volumeInput = it },
                    label = { Text("Volume (Metric Tons)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val vol = volumeInput.toDoubleOrNull() ?: 10.0
                    onSubmit(productName, vol)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark,
                    contentColor = Color.White
                )
            ) {
                Text("Send Request", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = SurfaceCard
    )
}
