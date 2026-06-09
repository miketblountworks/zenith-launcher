package com.example.ui.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit
import android.util.Base64

object GeminiImageGenerator {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateWallpaper(prompt: String, outputFile: File): Boolean = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Need a valid API key
            return@withContext false
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=$apiKey"
        
        // Build JSON request manually
        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseModalities", JSONArray().apply { put("IMAGE") })
                put("imageConfig", JSONObject().apply {
                    put("aspectRatio", "9:16") // Assuming mobile screen aspect ratio
                    put("imageSize", "1K")
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("API Request failed: ${response.code} - ${response.body?.string()}")
                    return@withContext false
                }
                
                val responseBodyStr = response.body?.string() ?: return@withContext false
                val responseJson = JSONObject(responseBodyStr)
                
                val candidates = responseJson.optJSONArray("candidates") ?: return@withContext false
                val firstCandidate = candidates.optJSONObject(0) ?: return@withContext false
                val content = firstCandidate.optJSONObject("content") ?: return@withContext false
                val parts = content.optJSONArray("parts") ?: return@withContext false
                val firstPart = parts.optJSONObject(0) ?: return@withContext false
                val inlineData = firstPart.optJSONObject("inlineData") ?: return@withContext false
                
                val base64Data = inlineData.optString("data")
                if (base64Data.isNotEmpty()) {
                    val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    outputFile.writeBytes(decodedBytes)
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }
}
