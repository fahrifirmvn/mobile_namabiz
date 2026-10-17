package com.namabiz.agrim.core.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.namabiz.agrim.core.model.Supplier
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfComparisonGenerator {

    /**
     * Generates a formal A4 procurement comparison report between two suppliers.
     * Page dimensions: 595 x 842 points (Standard ISO A4 @ 72 dpi)
     */
    fun generateComparisonReport(
        context: Context,
        supplierA: Supplier,
        supplierB: Supplier,
        buyerName: String = "John Doe (Global Foods Ltd)"
    ): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 35, 42)
            textSize = 10f
        }
        val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 35, 42)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(22, 78, 67) // Brand Green #164E43
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(229, 231, 235) // Border Light
            strokeWidth = 1f
        }
        val tableHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(249, 246, 240) // Warm background
        }

        var currentY = 50f
        val leftMargin = 40f
        val rightMargin = 555f
        val contentWidth = rightMargin - leftMargin

        // 1. TOP HEADER KOP
        canvas.drawText("AGRIM B2B DIRECTORY", leftMargin, currentY, headerPaint)
        currentY += 16f
        textPaint.textSize = 9f
        textPaint.color = Color.rgb(107, 114, 128)
        canvas.drawText("Global Verified Agricultural Trade Portal · Procurement Comparison Report", leftMargin, currentY, textPaint)
        currentY += 14f

        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        val dateStr = "Generated on: ${dateFormat.format(Date())}"
        val reportIdStr = "Report ID: AGRIM-CMP-${System.currentTimeMillis().toString().takeLast(6)}"
        canvas.drawText("$reportIdStr · $dateStr", leftMargin, currentY, textPaint)
        currentY += 12f
        canvas.drawText("Prepared for: $buyerName", leftMargin, currentY, textPaint)
        currentY += 16f

        // Separator bar
        paint.color = Color.rgb(22, 78, 67)
        canvas.drawRect(leftMargin, currentY, rightMargin, currentY + 3f, paint)
        currentY += 24f

        // 2. COMPARISON TABLE
        val colWidth = contentWidth / 3f
        val col1X = leftMargin
        val col2X = leftMargin + colWidth
        val col3X = leftMargin + (colWidth * 2)

        // Table Header Row Background
        canvas.drawRect(leftMargin, currentY - 14f, rightMargin, currentY + 22f, tableHeaderPaint)
        canvas.drawLine(leftMargin, currentY + 22f, rightMargin, currentY + 22f, linePaint)

        boldPaint.textSize = 10f
        boldPaint.color = Color.rgb(107, 114, 128)
        canvas.drawText("TRADE PARAMETERS", col1X + 8f, currentY + 5f, boldPaint)

        boldPaint.textSize = 11f
        boldPaint.color = Color.rgb(22, 78, 67)
        canvas.drawText(supplierA.name.take(20), col2X + 8f, currentY + 5f, boldPaint)
        canvas.drawText(supplierB.name.take(20), col3X + 8f, currentY + 5f, boldPaint)
        currentY += 36f

        // Row generator helper
        fun drawRow(label: String, valA: String, valB: String, isAlternate: Boolean = false) {
            if (isAlternate) {
                paint.color = Color.rgb(250, 250, 250)
                canvas.drawRect(leftMargin, currentY - 12f, rightMargin, currentY + 12f, paint)
            }
            canvas.drawLine(leftMargin, currentY + 12f, rightMargin, currentY + 12f, linePaint)

            boldPaint.color = Color.rgb(30, 35, 42)
            boldPaint.textSize = 9.5f
            canvas.drawText(label, col1X + 8f, currentY + 2f, boldPaint)

            textPaint.color = Color.rgb(30, 35, 42)
            textPaint.textSize = 9.5f
            canvas.drawText(valA, col2X + 8f, currentY + 2f, textPaint)
            canvas.drawText(valB, col3X + 8f, currentY + 2f, textPaint)

            currentY += 24f
        }

        drawRow("Country / Plant", "${supplierA.city}, ${supplierA.country}", "${supplierB.city}, ${supplierB.country}", false)
        drawRow("Verification Status", if (supplierA.isVerified) "Verified (On-site Audit)" else "Standard", if (supplierB.isVerified) "Verified (On-site Audit)" else "Standard", true)
        drawRow("Industry Category", supplierA.industryCategory, supplierB.industryCategory, false)
        drawRow("Min. Order Qty (MOQ)", supplierA.moq, supplierB.moq, true)
        drawRow("Monthly Capacity", supplierA.capacity, supplierB.capacity, false)
        drawRow("Estimated Lead Time", supplierA.leadTime, supplierB.leadTime, true)
        drawRow("Certifications", supplierA.tags.joinToString(", "), supplierB.tags.joinToString(", "), false)
        drawRow("Audit Record", supplierA.auditStatus, supplierB.auditStatus, true)

        currentY += 30f

        // 3. PROCUREMENT RECOMMENDATION & SIGNATURE BOX
        paint.color = Color.rgb(244, 248, 246)
        canvas.drawRoundRect(leftMargin, currentY, rightMargin, currentY + 60f, 8f, 8f, paint)

        boldPaint.color = Color.rgb(22, 78, 67)
        boldPaint.textSize = 10f
        canvas.drawText("PROCUREMENT SUMMARY", leftMargin + 14f, currentY + 20f, boldPaint)

        textPaint.color = Color.rgb(55, 65, 81)
        textPaint.textSize = 9f
        canvas.drawText(
            "Both suppliers hold verified export status. ${supplierA.name} offers faster lead time (${supplierA.leadTime}),",
            leftMargin + 14f,
            currentY + 36f,
            textPaint
        )
        canvas.drawText(
            "while ${supplierB.name} provides larger monthly volume capacity (${supplierB.capacity}).",
            leftMargin + 14f,
            currentY + 48f,
            textPaint
        )

        currentY += 90f

        // Signatures
        val signCol1 = leftMargin + 30f
        val signCol2 = rightMargin - 180f

        canvas.drawLine(signCol1, currentY + 50f, signCol1 + 140f, currentY + 50f, linePaint)
        canvas.drawLine(signCol2, currentY + 50f, signCol2 + 140f, currentY + 50f, linePaint)

        textPaint.color = Color.rgb(107, 114, 128)
        textPaint.textSize = 8.5f
        canvas.drawText("Prepared by (Procurement Sourcing)", signCol1, currentY + 64f, textPaint)
        canvas.drawText("Approved by (Purchasing Director)", signCol2, currentY + 64f, textPaint)

        // Footer
        canvas.drawLine(leftMargin, 790f, rightMargin, 790f, linePaint)
        textPaint.textSize = 8f
        canvas.drawText("Agrim Directory © 2026 · Confidential & Proprietary Trade Data · Page 1 of 1", leftMargin, 804f, textPaint)

        document.finishPage(page)

        // Save PDF to cache
        val cacheDir = File(context.filesDir, "pdf_cache").apply { mkdirs() }
        val outputFile = File(cacheDir, "comparison_report.pdf")
        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return outputFile
    }
}
