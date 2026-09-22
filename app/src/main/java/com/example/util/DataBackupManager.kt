package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.PersonEntity
import com.example.data.local.TransactionEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupData(
    val persons: List<PersonEntity>,
    val transactions: List<TransactionEntity>,
    val exportedAt: Long = System.currentTimeMillis(),
    val appVersion: String = "2.0.0"
)

object DataBackupManager {

    fun generateBackupJson(
        persons: List<PersonEntity>,
        transactions: List<TransactionEntity>
    ): String {
        val root = JSONObject()
        root.put("app", "محاسبي Mz")
        root.put("version", "2.0.0")
        root.put("exportedAt", System.currentTimeMillis())

        val personsArray = JSONArray()
        for (p in persons) {
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("name", p.name)
            pObj.put("phone", p.phone)
            pObj.put("note", p.note)
            pObj.put("createdAt", p.createdAt)
            personsArray.put(pObj)
        }
        root.put("persons", personsArray)

        val txArray = JSONArray()
        for (t in transactions) {
            val tObj = JSONObject()
            tObj.put("id", t.id)
            tObj.put("personId", t.personId)
            tObj.put("personName", t.personName)
            tObj.put("title", t.title)
            tObj.put("amount", t.amount)
            tObj.put("type", t.type)
            tObj.put("category", t.category)
            tObj.put("note", t.note)
            tObj.put("timestamp", t.timestamp)
            txArray.put(tObj)
        }
        root.put("transactions", txArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)
        val persons = mutableListOf<PersonEntity>()
        val transactions = mutableListOf<TransactionEntity>()

        val personsArray = root.optJSONArray("persons")
        if (personsArray != null) {
            for (i in 0 until personsArray.length()) {
                val obj = personsArray.getJSONObject(i)
                persons.add(
                    PersonEntity(
                        id = obj.optLong("id", 0L),
                        name = obj.optString("name", "بدون اسم"),
                        phone = obj.optString("phone", ""),
                        note = obj.optString("note", ""),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        val txArray = root.optJSONArray("transactions")
        if (txArray != null) {
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = obj.optLong("id", 0L),
                        personId = obj.optLong("personId", 0L),
                        personName = obj.optString("personName", ""),
                        title = obj.optString("title", "عملية"),
                        amount = obj.optDouble("amount", 0.0),
                        type = obj.optString("type", "INCOME"),
                        category = obj.optString("category", "عام"),
                        note = obj.optString("note", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        }

        return BackupData(
            persons = persons,
            transactions = transactions,
            exportedAt = root.optLong("exportedAt", System.currentTimeMillis()),
            appVersion = root.optString("version", "2.0.0")
        )
    }

    /**
     * Creates a temporary JSON file and launches the system share sheet.
     * This allows saving to Downloads/Drive/WhatsApp/Files without crashing
     * on any Android device.
     */
    fun shareBackupFile(context: Context, json: String): Result<String> {
        return runCatching {
            val backupDir = File(context.cacheDir, "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val fileName = createBackupFileName()
            val backupFile = File(backupDir, fileName)
            FileOutputStream(backupFile).use { fos ->
                OutputStreamWriter(fos).use { writer ->
                    writer.write(json)
                    writer.flush()
                }
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, backupFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "نسخة احتياطية - محاسبي Mz")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "حفظ أو مشاركة النسخة الاحتياطية").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            fileName
        }
    }

    fun writeJsonToUri(context: Context, uri: Uri, json: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { os ->
                OutputStreamWriter(os).use { writer ->
                    writer.write(json)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun readJsonFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun copyToClipboard(context: Context, text: String): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("نسخة محاسبي Mz", text)
            clipboard.setPrimaryClip(clip)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun readFromClipboard(context: Context): String? {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).text?.toString()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun createBackupFileName(): String {
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
        return "mazen_ledger_backup_${formatter.format(Date())}.json"
    }
}
