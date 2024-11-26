package com.freeletics.gradle.scripts

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

private fun createQrCode(uri: String): ByteArray {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(uri, BarcodeFormat.QR_CODE, IMAGE_SIZE, IMAGE_SIZE)
    return ByteArrayOutputStream().use {
        MatrixToImageWriter.writeToStream(bitMatrix, "png", it)
        it.toByteArray()
    }
}

private const val IMAGE_SIZE = 300
