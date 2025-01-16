package com.example.senderapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecureDataProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.senderapp.secureprovider"
        const val PATH = "data"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

        private const val SECRET_KEY = "1234567890123456"
        private const val IV = "abcdefghijklmnop"
    }

    private var receivedMessage: String? = null
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, PATH, 1)
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        if (uriMatcher.match(uri) == 1) {
            val cursor = MatrixCursor(arrayOf(PATH))
            receivedMessage?.let {
                val decryptedMessage = decrypt(it)
                cursor.addRow(arrayOf(decryptedMessage))
            }
            return cursor
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (uriMatcher.match(uri) == 1) {
            val message = values?.getAsString(PATH) ?: return null
            val encryptedMessage = encrypt(message)
            receivedMessage = encryptedMessage
            context?.contentResolver?.notifyChange(uri, null)
            return uri
        }
        return null
    }

    private fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val ivParameterSpec = IvParameterSpec(IV.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encrypted = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    private fun decrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val ivParameterSpec = IvParameterSpec(IV.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        return String(cipher.doFinal(decodedData))
    }

    override fun getType(uri: Uri): String? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}

