package com.namabiz.agrim.core.network

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AgrimApiService {

    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthData>

    @POST("v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthData>

    @POST("v1/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): ApiResponse<AuthData>

    @GET("v1/auth/user")
    suspend fun getProfile(): ApiResponse<UserData>

    @GET("v1/dashboard/home")
    suspend fun getHomeDashboard(): ApiResponse<DashboardResponse>

    @GET("v1/suppliers")
    suspend fun getSuppliers(
        @Query("q") query: String? = null,
        @Query("country") country: String? = null,
        @Query("category") category: String? = null,
        @Query("certification_id") certId: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): ApiResponse<List<SupplierResponse>>

    @GET("v1/suppliers/{idOrSlug}")
    suspend fun getSupplierDetail(@Path("idOrSlug") idOrSlug: String): ApiResponse<SupplierResponse>

    @GET("v1/suppliers/qr-resolve")
    suspend fun resolveQr(@Query("code") code: String): ApiResponse<SupplierResponse>

    @POST("v1/suppliers/compare")
    suspend fun compareSuppliers(@Body request: CompareRequest): ApiResponse<CompareResponse>

    @POST("v1/suppliers/{id}/toggle-save")
    suspend fun toggleSaveSupplier(@Path("id") id: Int): ApiResponse<ToggleSaveResponse>

    @GET("v1/collection/saved")
    suspend fun getSavedSuppliers(): ApiResponse<List<SupplierResponse>>

    @GET("v1/rfq/list")
    suspend fun getRfqList(): ApiResponse<RfqListResponse>

    @POST("v1/rfq/create")
    suspend fun createRfq(@Body request: CreateRfqRequest): ApiResponse<CreateRfqResponse>

    @Streaming
    @GET("v1/documents/{id}/download")
    suspend fun downloadDocument(@Path("id") id: Int): Response<ResponseBody>

    @POST("v1/billing/verify-play-purchase")
    suspend fun verifyPlayBilling(@Body request: PlayBillingVerifyRequest): ApiResponse<BillingVerifyResponse>
}

// Request & Response DTOs
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val errors: Any? = null
)

data class LoginRequest(
    val email: String,
    val password: String,
    @SerializedName("device_name") val deviceName: String = "Android-Native"
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("device_name") val deviceName: String = "Android-Native"
)

data class GoogleAuthRequest(
    val email: String,
    val name: String,
    val avatar: String? = null,
    @SerializedName("device_name") val deviceName: String = "Android-Google-Auth"
)

data class AuthData(
    val user: UserData,
    val token: String
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("subscription_tier") val subscriptionTier: String,
    @SerializedName("premium_expires_at") val premiumExpiresAt: String? = null,
    @SerializedName("is_premium") val isPremium: Boolean = false,
    val avatar: String? = null
)

data class DashboardResponse(
    val membership: MembershipDto,
    @SerializedName("quick_stats") val quickStats: QuickStatsDto,
    @SerializedName("needs_attention") val needsAttention: List<AttentionItemDto>,
    @SerializedName("recently_viewed") val recentlyViewed: List<SupplierResponse>
)

data class MembershipDto(
    val tier: String,
    @SerializedName("renews_date") val renewsDate: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class QuickStatsDto(
    val saved: Int,
    val comparing: Int,
    @SerializedName("catalogs_downloaded") val catalogsDownloaded: Int
)

data class AttentionItemDto(
    val id: String,
    val type: String,
    val title: String,
    val tag: String? = null,
    val details: String? = null,
    val badge: String? = null,
    @SerializedName("valid_until") val validUntil: String? = null,
    @SerializedName("action_label") val actionLabel: String,
    @SerializedName("supplier_slug") val supplierSlug: String
)

data class SupplierResponse(
    val id: Int,
    val name: String,
    val slug: String,
    val logo: String? = null,
    val tagline: String? = null,
    val description: String? = null,
    val country: String,
    val city: String? = null,
    @SerializedName("industry_category") val industryCategory: String,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    val about: String = "",
    val website: String? = null,
    @SerializedName("is_saved") val isSaved: Boolean = false,
    @SerializedName("premium_data") val premiumData: PremiumDataDto? = null,
    val tags: List<String>? = null,
    val certifications: List<CertDto>? = null,
    val documents: List<DocDto>? = null
)

data class PremiumDataDto(
    @SerializedName("is_locked") val isLocked: Boolean,
    @SerializedName("founded_year") val foundedYear: Int? = null,
    @SerializedName("company_size") val companySize: String? = null,
    @SerializedName("production_capacity") val productionCapacity: String? = null,
    val moq: String? = null,
    @SerializedName("avg_lead_time") val avgLeadTime: String? = null,
    @SerializedName("carbon_footprint_report") val carbonFootprintReport: String? = null
)

data class CertDto(
    val id: Int,
    val name: String,
    val category: String? = null,
    val status: String? = null,
    @SerializedName("is_premium_field") val isPremiumField: Boolean = false,
    @SerializedName("is_locked") val isLocked: Boolean = false
)

data class DocDto(
    val id: Int,
    val title: String,
    val type: String,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("can_download") val canDownload: Boolean,
    @SerializedName("download_url") val downloadUrl: String,
    @SerializedName("external_url") val externalUrl: String? = null
)

data class CompareRequest(
    @SerializedName("supplier_ids") val supplierIds: List<Int>
)

data class CompareResponse(
    val suppliers: List<SupplierResponse>,
    @SerializedName("compared_count") val comparedCount: Int
)

data class ToggleSaveResponse(
    @SerializedName("is_saved") val isSaved: Boolean,
    @SerializedName("supplier_id") val supplierId: Int
)

data class RfqListResponse(
    val rfqs: List<RfqDto>
)

data class RfqDto(
    val id: Int,
    @SerializedName("supplier_name") val supplierName: String,
    @SerializedName("product_name") val productName: String,
    val volume: String,
    val status: String,
    @SerializedName("quote_fob") val quoteFob: String? = null,
    @SerializedName("valid_until") val validUntil: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class CreateRfqRequest(
    @SerializedName("supplier_ids") val supplierIds: List<Int>,
    @SerializedName("product_name") val productName: String,
    @SerializedName("volume_mt") val volumeMt: Double,
    @SerializedName("target_incoterm") val targetIncoterm: String = "FOB",
    val notes: String? = null
)

data class CreateRfqResponse(
    @SerializedName("rfq_id") val rfqId: Int,
    val status: String,
    @SerializedName("suppliers_contacted") val suppliersContacted: Int,
    val message: String
)

data class PlayBillingVerifyRequest(
    @SerializedName("purchase_token") val purchaseToken: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("order_id") val orderId: String? = null
)

data class BillingVerifyResponse(
    val user: UserData,
    val subscription: SubscriptionDetailsDto
)

data class SubscriptionDetailsDto(
    val tier: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("expires_at") val expiresAt: String? = null,
    val status: String
)
