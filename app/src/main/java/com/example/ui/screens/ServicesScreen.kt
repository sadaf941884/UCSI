package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentRequestEntity
import com.example.data.model.PaymentTransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    user: UserEntity?,
    payments: List<PaymentTransactionEntity>,
    documents: List<DocumentRequestEntity>,
    onProcessPayment: (feeTitle: String, amountBdt: Double, gateway: String) -> Unit,
    onSubmitDocRequest: (type: String, purpose: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPaymentModal by remember { mutableStateOf(false) }
    var showDocRequestModal by remember { mutableStateOf(false) }
    var selectedReceiptForView by remember { mutableStateOf<PaymentTransactionEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("services_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = UcsiCrimson
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Financial Gateway", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Documents & Clearance", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> FinancialGatewayTab(
                user = user,
                payments = payments,
                onOpenPayModal = { showPaymentModal = true },
                onViewReceipt = { selectedReceiptForView = it }
            )
            1 -> DigitalDocumentsTab(
                documents = documents,
                onOpenRequestModal = { showDocRequestModal = true }
            )
        }
    }

    if (showPaymentModal) {
        PaymentGatewayModal(
            currentBalanceBdt = user?.balanceBdt ?: 35000.0,
            onDismiss = { showPaymentModal = false },
            onConfirmPayment = { title, amount, gateway ->
                onProcessPayment(title, amount, gateway)
                showPaymentModal = false
            }
        )
    }

    if (showDocRequestModal) {
        DocumentRequestModal(
            onDismiss = { showDocRequestModal = false },
            onConfirm = { type, purpose ->
                onSubmitDocRequest(type, purpose)
                showDocRequestModal = false
            }
        )
    }

    selectedReceiptForView?.let { receipt ->
        ReceiptDetailsModal(receipt = receipt, onDismiss = { selectedReceiptForView = null })
    }
}

@Composable
fun FinancialGatewayTab(
    user: UserEntity?,
    payments: List<PaymentTransactionEntity>,
    onOpenPayModal: () -> Unit,
    onViewReceipt: (PaymentTransactionEntity) -> Unit
) {
    val balanceBdt = user?.balanceBdt ?: 35000.0
    val balanceMyr = user?.balanceMyr ?: (balanceBdt / 27.50)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Overview Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "STUDENT TUITION ACCOUNT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "৳ ${String.format("%,.0f", balanceBdt)} BDT",
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp,
                                color = Color.White
                            )
                            Text(
                                text = "≈ RM ${String.format("%,.2f", balanceMyr)} MYR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UcsiGoldLight
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (balanceBdt > 0) Color(0xFFD97706).copy(alpha = 0.2f) else StatusSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (balanceBdt > 0) "PAYMENT DUE" else "CLEAR",
                                color = if (balanceBdt > 0) Color(0xFFFFB74D) else Color(0xFF81C995),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Official rate: 1 MYR ≈ 27.50 BDT (Updated 2026-10-02 via UCSI Finance Treasury).",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onOpenPayModal,
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pay_tuition_button")
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pay Now via bKash / Nagad / Card",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Supported Payment Gateways in Bangladesh
        item {
            Text(
                text = "Secure Local Payment Integrations",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethodBadge("bKash", Color(0xFFE2136E), "Merchant Direct", Modifier.weight(1f))
                PaymentMethodBadge("Nagad", Color(0xFFF7941D), "Direct Pay", Modifier.weight(1f))
                PaymentMethodBadge("Rocket", Color(0xFF8C3494), "DBBL", Modifier.weight(1f))
                PaymentMethodBadge("Cards", UcsiNavy, "Visa / Master", Modifier.weight(1f))
            }
        }

        // Transaction & Digital Receipt History
        item {
            Text(
                text = "Payment & Receipt History (${payments.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(payments, key = { it.id }) { payment ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewReceipt(payment) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = payment.feeTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StatusSuccess.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = payment.status,
                                    color = StatusSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(text = "Txn: ${payment.transactionId} • ${payment.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Gateway: ${payment.paymentMethod}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = UcsiNavy)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳ ${String.format("%,.0f", payment.amountBdt)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "View Receipt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UcsiCrimson
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodBadge(name: String, color: Color, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DigitalDocumentsTab(
    documents: List<DocumentRequestEntity>,
    onOpenRequestModal: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiNavyContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Digital Clearance & Documents",
                            fontWeight = FontWeight.Bold,
                            color = UcsiOnNavyContainer,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Request official transcripts, provisional certificates, NOCs & campus clearance.",
                            color = UcsiOnNavyContainer.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onOpenRequestModal,
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("request_document_button")
                    ) {
                        Text("+ Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Document Applications (${documents.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(documents, key = { it.id }) { doc ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = doc.documentType, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (doc.status) {
                                    "READY" -> StatusSuccess.copy(alpha = 0.15f)
                                    "PROCESSING" -> Color(0xFFD97706).copy(alpha = 0.15f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = doc.status,
                                    color = when (doc.status) {
                                        "READY" -> StatusSuccess
                                        "PROCESSING" -> Color(0xFFD97706)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(text = "Purpose: ${doc.purpose}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Tracking: ${doc.trackingCode} • Requested: ${doc.requestDate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = UcsiNavy)
                    }

                    if (doc.status == "READY") {
                        Button(
                            onClick = { /* digital document viewer */ },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentGatewayModal(
    currentBalanceBdt: Double,
    onDismiss: () -> Unit,
    onConfirmPayment: (String, Double, String) -> Unit
) {
    val titles = listOf("Summer Trimester Tuition Final Installment", "Lab Deposit & Tech Fee", "Semester Re-Registration Fee")
    val gateways = listOf("bKash", "Nagad", "Rocket", "Visa / Master")

    var selectedTitleIndex by remember { mutableIntStateOf(0) }
    var selectedGatewayIndex by remember { mutableIntStateOf(0) }
    var amountText by remember { mutableStateOf(currentBalanceBdt.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Institutional Fee Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Fee:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                titles.forEachIndexed { i, title ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTitleIndex = i }
                    ) {
                        RadioButton(selected = selectedTitleIndex == i, onClick = { selectedTitleIndex = i })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(title, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Amount (BDT)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Select Payment Gateway:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    gateways.forEachIndexed { i, gw ->
                        Surface(
                            onClick = { selectedGatewayIndex = i },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedGatewayIndex == i) UcsiCrimson else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = gw,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedGatewayIndex == i) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 1000.0
                    onConfirmPayment(titles[selectedTitleIndex], amount, gateways[selectedGatewayIndex])
                },
                colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson)
            ) {
                Text("Proceed to Secure Gateway", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DocumentRequestModal(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val types = listOf(
        "Official Academic Transcript",
        "Provisional Degree Certificate",
        "No Objection Certificate (NOC) for Visa",
        "Cross-Campus Transfer Clearance",
        "Bonafide Student Certificate"
    )
    var selectedTypeIndex by remember { mutableIntStateOf(0) }
    var purposeText by remember { mutableStateOf("High Commission & University Verification") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Official Document", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Document Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                types.forEachIndexed { i, type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTypeIndex = i }
                    ) {
                        RadioButton(selected = selectedTypeIndex == i, onClick = { selectedTypeIndex = i })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(type, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = purposeText,
                    onValueChange = { purposeText = it },
                    label = { Text("Purpose of Request") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(types[selectedTypeIndex], purposeText) },
                colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson)
            ) {
                Text("Submit Request", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ReceiptDetailsModal(receipt: PaymentTransactionEntity, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = UcsiCrimson)
                Spacer(modifier = Modifier.width(8.dp))
                Text("UCSI Official Digital Receipt", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ReceiptItemRow("Receipt Number", receipt.receiptNumber)
                ReceiptItemRow("Transaction ID", receipt.transactionId)
                ReceiptItemRow("Fee Description", receipt.feeTitle)
                ReceiptItemRow("Amount Paid (BDT)", "৳ ${String.format("%,.2f", receipt.amountBdt)}")
                ReceiptItemRow("Equivalent (MYR)", "RM ${String.format("%,.2f", receipt.amountMyr)}")
                ReceiptItemRow("Gateway Method", receipt.paymentMethod)
                ReceiptItemRow("Timestamp", receipt.date)
                ReceiptItemRow("Campus Office", "Banani Branch Campus, Dhaka")
                ReceiptItemRow("Status", "SETTLED / CLEARED")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = UcsiNavy)
            ) {
                Text("Close Receipt")
            }
        }
    )
}

@Composable
fun ReceiptItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
