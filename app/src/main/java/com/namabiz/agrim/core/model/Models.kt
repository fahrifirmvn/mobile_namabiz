package com.namabiz.agrim.core.model

data class Supplier(
    val id: Int,
    val name: String,
    val slug: String,
    val logo: String? = null,
    val tagline: String? = null,
    val description: String? = null,
    val country: String,
    val city: String? = null,
    val industryCategory: String,
    val isVerified: Boolean = false,
    val about: String = "",
    val website: String? = null,
    val isSaved: Boolean = false,
    val auditStatus: String = "Audited on-site, Oct 2025",
    val responseTime: String = "Replies within ~4 hours",
    val moq: String = "1 × 20ft FCL",
    val capacity: String = "500 MT/mo",
    val leadTime: String = "14 days",
    val incoterms: String = "FOB / CIF",
    val loadingPort: String = "Tanjung Perak, Surabaya",
    val paymentTerms: String = "T/T, L/C",
    val workforce: String = "50–100 staff",
    val established: String = "2011 (15 yrs)",
    val tags: List<String> = emptyList(),
    val exportProducts: List<ExportProduct> = emptyList(),
    val certificates: List<Certificate> = emptyList(),
    val documents: List<Document> = emptyList()
)

data class ExportProduct(
    val name: String,
    val grade: String,
    val spec: String,
    val tag: String
)

data class Certificate(
    val id: Int,
    val name: String,
    val category: String = "Food Safety",
    val issuer: String = "SGS International",
    val validity: String = "Valid to Mar 2027",
    val status: String = "Verified",
    val isLocked: Boolean = false,
    val documentUrl: String? = null
)

data class Document(
    val id: Int,
    val title: String,
    val type: String,
    val isPremium: Boolean,
    val canDownload: Boolean,
    val downloadUrl: String,
    val localFilePath: String? = null,
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED
)

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    FAILED
}

data class Rfq(
    val id: Int,
    val supplierName: String,
    val productName: String,
    val volume: String,
    val status: String,
    val quoteFob: String? = null,
    val validUntil: String? = null,
    val createdAt: String = ""
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val subscriptionTier: String = "free",
    val premiumExpiresAt: String? = null,
    val avatar: String? = null,
    val roleTitle: String = "Head of Sourcing at Global Foods Ltd",
    val location: String = "Jakarta, Indonesia"
) {
    val isPremium: Boolean
        get() = subscriptionTier.equals("premium", ignoreCase = true) || subscriptionTier.equals("business pro", ignoreCase = true)
}

data class HomeDashboardData(
    val membershipTier: String,
    val membershipRenews: String,
    val savedCount: Int,
    val comparingCount: Int,
    val catalogsDownloaded: Int,
    val needsAttention: List<AttentionItem>,
    val recentlyViewed: List<Supplier>
)

data class AttentionItem(
    val id: String,
    val type: String,
    val title: String,
    val tag: String? = null,
    val subtitle: String? = null,
    val badge: String? = null,
    val validHours: String? = null,
    val actionLabel: String,
    val supplierSlug: String
)
