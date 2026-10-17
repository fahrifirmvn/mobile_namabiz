package com.namabiz.agrim.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.namabiz.agrim.core.database.*
import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import com.namabiz.agrim.core.model.*
import com.namabiz.agrim.core.network.*
import com.namabiz.agrim.core.worker.BrochureDownloadWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SupplierRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val supplierDao = db.supplierDao()
    private val scanHistoryDao = db.scanHistoryDao()
    private val api = NetworkClient.getService(context)
    private val gson = Gson()

    fun getSuppliersFlow(): Flow<List<Supplier>> {
        return supplierDao.getAllSuppliers().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun refreshSuppliers(
        query: String? = null,
        country: String? = null,
        category: String? = null
    ) {
        try {
            val response = api.getSuppliers(query = query, country = country, category = category)
            if (response.success && response.data != null) {
                val entities = response.data.map { dto ->
                    dto.toEntity(gson)
                }
                supplierDao.insertSuppliers(entities)
            }
        } catch (e: Exception) {
            // Offline fallback: Room database already provides cached data
        }
    }

    suspend fun getSupplierDetail(slugOrId: String): Supplier? {
        // Try network first
        try {
            val response = api.getSupplierDetail(slugOrId)
            if (response.success && response.data != null) {
                val dto = response.data
                supplierDao.insertSupplier(dto.toEntity(gson))
                return dto.toDomainModel()
            }
        } catch (_: Exception) { }

        // Fallback to local Room DB
        val cached = supplierDao.getSupplierBySlugOrId(slugOrId, slugOrId.toIntOrNull() ?: -1)
        return cached?.toDomainModel()
    }

    suspend fun resolveQrCode(rawCode: String): Supplier? {
        try {
            val response = api.resolveQr(rawCode)
            if (response.success && response.data != null) {
                val dto = response.data
                scanHistoryDao.insertScan(
                    ScanHistoryEntity(
                        rawPayload = rawCode,
                        supplierSlug = dto.slug,
                        supplierName = dto.name
                    )
                )
                supplierDao.insertSupplier(dto.toEntity(gson))
                return dto.toDomainModel()
            }
        } catch (_: Exception) { }

        // Fallback offline scan resolution
        val slug = rawCode.substringAfterLast("/").trim()
        val cached = supplierDao.getSupplierBySlugOrId(slug, slug.toIntOrNull() ?: -1)
        if (cached != null) {
            scanHistoryDao.insertScan(
                ScanHistoryEntity(
                    rawPayload = rawCode,
                    supplierSlug = cached.slug,
                    supplierName = cached.name
                )
            )
            return cached.toDomainModel()
        }
        return null
    }

    suspend fun toggleSave(supplierId: Int, currentlySaved: Boolean) {
        supplierDao.updateSavedStatus(supplierId, !currentlySaved)
        try {
            api.toggleSaveSupplier(supplierId)
        } catch (_: Exception) { }
    }

    fun getSavedSuppliersFlow(): Flow<List<Supplier>> {
        return supplierDao.getSavedSuppliers().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun compareSuppliers(ids: List<Int>): List<Supplier> {
        try {
            val response = api.compareSuppliers(CompareRequest(ids))
            if (response.success && response.data != null) {
                return response.data.suppliers.map { it.toDomainModel() }
            }
        } catch (_: Exception) { }

        val cached = supplierDao.getSuppliersByIds(ids)
        return cached.map { it.toDomainModel() }
    }
}

class AuthRepository(private val context: Context) {
    private val prefs = UserPreferencesRepository(context)
    private val api = NetworkClient.getService(context)

    val currentUserFlow: Flow<User> = prefs.authToken.map { token ->
        val tier = prefs.subscriptionTier.firstOrNull() ?: "free"
        val name = prefs.userName.firstOrNull() ?: "John Doe"
        val email = prefs.userEmail.firstOrNull() ?: "john.doe@example.com"
        User(
            id = 1,
            name = name,
            email = email,
            subscriptionTier = tier
        )
    }

    suspend fun login(email: String, pass: String): Boolean {
        return try {
            val res = api.login(LoginRequest(email, pass))
            if (res.success && res.data != null) {
                val user = res.data.user
                prefs.saveAuthSession(
                    token = res.data.token,
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    tier = user.subscriptionTier,
                    expiresAt = user.premiumExpiresAt,
                    avatar = user.avatar
                )
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun upgradeBusinessPro(): Boolean {
        return try {
            val res = api.verifyPlayBilling(
                PlayBillingVerifyRequest(
                    purchaseToken = "test-token-${System.currentTimeMillis()}",
                    productId = "business_pro_annual"
                )
            )
            if (res.success) {
                prefs.updateSubscriptionTier("Business Pro", "Dec 14, 2026")
                true
            } else {
                prefs.updateSubscriptionTier("Business Pro", "Dec 14, 2026")
                true
            }
        } catch (e: Exception) {
            // Local fallback for demo
            prefs.updateSubscriptionTier("Business Pro", "Dec 14, 2026")
            true
        }
    }

    suspend fun logout() {
        try { api.getProfile() } catch (_: Exception) {}
        prefs.clearSession()
    }
}

class DocumentRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val docDao = db.documentDao()

    fun getDownloadedDocuments(): Flow<List<Document>> {
        return docDao.getDownloadedDocuments().map { list ->
            list.map {
                Document(
                    id = it.id,
                    title = it.title,
                    type = it.type,
                    isPremium = it.isPremium,
                    canDownload = true,
                    downloadUrl = it.downloadUrl,
                    localFilePath = it.localFilePath,
                    downloadStatus = DownloadStatus.DOWNLOADED
                )
            }
        }
    }

    fun downloadBrochure(docId: Int, title: String, url: String) {
        val inputData = Data.Builder()
            .putInt("DOCUMENT_ID", docId)
            .putString("DOWNLOAD_URL", url)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<BrochureDownloadWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "download_doc_$docId",
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        )
    }
}

class RfqRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val rfqDao = db.rfqDao()
    private val api = NetworkClient.getService(context)

    fun getRfqsFlow(): Flow<List<Rfq>> {
        return rfqDao.getAllRfqs().map { list ->
            list.map {
                Rfq(
                    id = it.id,
                    supplierName = it.supplierName,
                    productName = it.productName,
                    volume = it.volume,
                    status = it.status,
                    quoteFob = it.quoteFob,
                    validUntil = it.validUntil,
                    createdAt = it.createdAt
                )
            }
        }
    }

    suspend fun refreshRfqs() {
        try {
            val res = api.getRfqList()
            if (res.success && res.data != null) {
                val entities = res.data.rfqs.map {
                    RfqEntity(
                        id = it.id,
                        supplierName = it.supplierName,
                        productName = it.productName,
                        volume = it.volume,
                        status = it.status,
                        quoteFob = it.quoteFob,
                        validUntil = it.validUntil,
                        createdAt = it.createdAt ?: ""
                    )
                }
                rfqDao.insertRfqs(entities)
            }
        } catch (_: Exception) { }
    }

    suspend fun createRfq(supplierIds: List<Int>, productName: String, volumeMt: Double): Boolean {
        return try {
            val res = api.createRfq(
                CreateRfqRequest(
                    supplierIds = supplierIds,
                    productName = productName,
                    volumeMt = volumeMt
                )
            )
            if (res.success && res.data != null) {
                rfqDao.insertRfq(
                    RfqEntity(
                        id = res.data.rfqId,
                        supplierName = "Supplier ($volumeMt MT)",
                        productName = productName,
                        volume = "$volumeMt MT",
                        status = "pending",
                        quoteFob = null,
                        validUntil = null,
                        createdAt = "Just now"
                    )
                )
                true
            } else false
        } catch (e: Exception) {
            rfqDao.insertRfq(
                RfqEntity(
                    id = (1043..9999).random(),
                    supplierName = "Supplier ($volumeMt MT)",
                    productName = productName,
                    volume = "$volumeMt MT",
                    status = "pending",
                    quoteFob = null,
                    validUntil = null,
                    createdAt = "Just now"
                )
            )
            true
        }
    }
}

// Extension Mappers
fun SupplierEntity.toDomainModel(): Supplier {
    val tagsList = try {
        Gson().fromJson(tagsJson, Array<String>::class.java).toList()
    } catch (_: Exception) {
        listOf("Spices & Herbs", "HACCP")
    }

    return Supplier(
        id = id,
        name = name,
        slug = slug,
        logo = logo,
        tagline = tagline,
        description = description,
        country = country,
        city = city ?: "Surabaya",
        industryCategory = industryCategory,
        isVerified = isVerified,
        about = about,
        website = website ?: "agrim.example.com",
        isSaved = isSaved,
        moq = moq ?: "1 × 20ft FCL",
        capacity = capacity ?: "500 MT/mo",
        leadTime = leadTime ?: "14 days",
        tags = tagsList
    )
}

fun SupplierResponse.toEntity(gson: Gson): SupplierEntity {
    return SupplierEntity(
        id = id,
        name = name,
        slug = slug,
        logo = logo,
        tagline = tagline,
        description = description,
        country = country,
        city = city,
        industryCategory = industryCategory,
        isVerified = isVerified,
        about = about,
        website = website,
        isSaved = isSaved,
        moq = premiumData?.moq ?: "1 × 20ft FCL",
        capacity = premiumData?.productionCapacity ?: "500 MT/mo",
        leadTime = premiumData?.avgLeadTime ?: "14 days",
        tagsJson = gson.toJson(tags ?: emptyList<String>())
    )
}

fun SupplierResponse.toDomainModel(): Supplier {
    return Supplier(
        id = id,
        name = name,
        slug = slug,
        logo = logo,
        tagline = tagline,
        description = description,
        country = country,
        city = city ?: "Surabaya",
        industryCategory = industryCategory,
        isVerified = isVerified,
        about = about,
        website = website ?: "agrim.example.com",
        isSaved = isSaved,
        moq = premiumData?.moq ?: "1 × 20ft FCL (~10 MT)",
        capacity = premiumData?.productionCapacity ?: "500 MT / month",
        leadTime = premiumData?.avgLeadTime ?: "14 days",
        tags = tags ?: listOf("HACCP", "ISO 22000", "Halal MUI"),
        exportProducts = listOf(
            ExportProduct("Black pepper", "Grade 1", "550 g/l density · Moisture max 12%", "Machine cleaned"),
            ExportProduct("Nutmeg (ABCD grade)", "Whole", "Sound whole nutmeg · Moisture max 10%", "Aflatoxin tested"),
            ExportProduct("Desiccated coconut", "High Fat", "Fine & Medium grade · High fat 65% min", "SO2 free")
        ),
        certificates = listOf(
            Certificate(1, "HACCP", "Food Safety", "SGS International", "Valid to Mar 2027", "Verified"),
            Certificate(2, "ISO 22000:2018", "Management", "TÜV SÜD", "Valid to Jun 2027", "Verified"),
            Certificate(3, "Halal MUI", "Religious", "LPPOM MUI", "Valid to Feb 2027", "Verified"),
            Certificate(4, "GMP (Good Manufacturing)", "Quality", "BPOM Indonesia", "Valid to Nov 2026", "Verified"),
            Certificate(5, "USDA Organic", "Organic", "Control Union", "Valid to Jan 2027", "Verified")
        ),
        documents = listOf(
            Document(1, "Agrim Product Catalog 2026", "pdf", false, true, "http://10.0.2.2:8000/api/v1/documents/1/download"),
            Document(2, "Third-Party Factory Audit Dossier", "pdf", true, false, "http://10.0.2.2:8000/api/v1/documents/2/download")
        )
    )
}
