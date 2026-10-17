package com.namabiz.agrim.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "suppliers",
    indices = [Index("slug"), Index("country"), Index("isVerified")]
)
data class SupplierEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String,
    val logo: String?,
    val tagline: String?,
    val description: String?,
    val country: String,
    val city: String?,
    val industryCategory: String,
    val isVerified: Boolean,
    val about: String,
    val website: String?,
    val isSaved: Boolean,
    val moq: String?,
    val capacity: String?,
    val leadTime: String?,
    val tagsJson: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "documents",
    indices = [Index("supplierId")]
)
data class DocumentEntity(
    @PrimaryKey val id: Int,
    val supplierId: Int,
    val title: String,
    val type: String,
    val isPremium: Boolean,
    val downloadUrl: String,
    val localFilePath: String? = null,
    val downloadStatus: String = "NOT_DOWNLOADED",
    val fileSizeBytes: Long = 0L,
    val downloadedAt: Long? = null
)

@Entity(
    tableName = "scan_history",
    indices = [Index("scannedAt")]
)
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawPayload: String,
    val supplierSlug: String?,
    val supplierName: String?,
    val scannedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "rfqs",
    indices = [Index("status")]
)
data class RfqEntity(
    @PrimaryKey val id: Int,
    val supplierName: String,
    val productName: String,
    val volume: String,
    val status: String,
    val quoteFob: String?,
    val validUntil: String?,
    val createdAt: String
)

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY isVerified DESC, name ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE isSaved = 1 ORDER BY name ASC")
    fun getSavedSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE slug = :slug OR id = :id LIMIT 1")
    suspend fun getSupplierBySlugOrId(slug: String, id: Int): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE id IN (:ids)")
    suspend fun getSuppliersByIds(ids: List<Int>): List<SupplierEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuppliers(suppliers: List<SupplierEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity)

    @Query("UPDATE suppliers SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Int, isSaved: Boolean)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE supplierId = :supplierId")
    fun getDocumentsForSupplier(supplierId: Int): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE downloadStatus = 'DOWNLOADED'")
    fun getDownloadedDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Int): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>)

    @Query("UPDATE documents SET localFilePath = :filePath, downloadStatus = :status, downloadedAt = :timestamp WHERE id = :id")
    suspend fun updateDownloadStatus(id: Int, filePath: String?, status: String, timestamp: Long?)
}

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC")
    fun getScanHistory(): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity)
}

@Dao
interface RfqDao {
    @Query("SELECT * FROM rfqs ORDER BY id DESC")
    fun getAllRfqs(): Flow<List<RfqEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRfqs(rfqs: List<RfqEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRfq(rfq: RfqEntity)
}
