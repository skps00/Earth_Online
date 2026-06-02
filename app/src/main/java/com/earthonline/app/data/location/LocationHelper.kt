package com.earthonline.app.data.location

// 位置助手 — 取得最後已知位置並進行反向地理編碼（Geocoder + Nominatim 備援）

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.getSystemService
import com.earthonline.app.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// 位置助手 — 使用 Android Geocoder 與 Nominatim API 備援進行反向地理編碼
private const val TAG = "LocationHelper"

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 取得最後已知位置 — 優先 GPS，其次網路定位
    fun getLastLocation(): Location? {
        val lm = context.getSystemService<LocationManager>() ?: return null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            try {
                val loc = lm.getLastKnownLocation(provider)
                if (loc != null) return loc
            } catch (e: SecurityException) {
                Log.e(TAG, "Failed to get last known location from provider: $provider", e)
            }
        }
        return null
    }

    // 反向地理編碼 — 回傳 (顯示地址, 國家, 洲別)，Geocoder 不可用時 fallback 到 Nominatim
    @Suppress("DEPRECATION")
    fun reverseGeocode(latitude: Double, longitude: Double): Triple<String, String, String> {
        return try {
            if (!Geocoder.isPresent()) {
                return nominationFallback(latitude, longitude)
            }

            val geocoder = Geocoder(context, Locale.getDefault())
            // 過濾掉座標為 (0,0) 的無效結果
            val addresses = geocoder.getFromLocation(latitude, longitude, AppConstants.MAX_GEOCODER_RESULTS)
                ?.filter { it.latitude != 0.0 || it.longitude != 0.0 }

            val addr = selectBestAddress(addresses)
            val display = buildAddressString(addr)
            val country = addr?.countryName ?: ""
            val continent = ContinentMapper.continentOf(country)

            Triple(display, country, continent)
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder reverse geocode failed", e)
            Triple("", "", "")
        }
    }

    // 從地址列表中選出資訊最豐富的結果 — subLocality 權重最高
    private fun selectBestAddress(addresses: List<Address>?): Address? {
        if (addresses.isNullOrEmpty()) return null

        // 評分機制：欄位越詳細分數越高，subLocality 權重最高
        return addresses.maxByOrNull { addr ->
            var score = 0
            if (!addr.subLocality.isNullOrBlank()) score += 10
            if (!addr.locality.isNullOrBlank()) score += 5
            if (!addr.subAdminArea.isNullOrBlank()) score += 3
            if (!addr.adminArea.isNullOrBlank()) score += 1
            score
        }
    }

    // 從 Address 物件組裝顯示字串 — 格式為「國家, 區域」
    @Suppress("DEPRECATION")
    private fun buildAddressString(addr: Address?): String {
        if (addr == null) return ""

        val country = addr.countryName ?: ""
        val district = addr.subLocality
            ?: addr.locality
            ?: addr.subAdminArea
            ?: addr.adminArea
            ?: ""

        if (district.isBlank()) return country
        if (country.isNotBlank() && !district.contains(country)) {
            return "$country, $district"
        }
        return district
    }

    // Nominatim API 備援方案 — 當裝置 Geocoder 不可用時的網路反向地理編碼
    private fun nominationFallback(latitude: Double, longitude: Double): Triple<String, String, String> {
        return try {
            val url = java.net.URL("https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&zoom=12&addressdetails=1")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.setRequestProperty("User-Agent", "EarthOnlineApp/1.0")
            conn.connectTimeout = AppConstants.NOMINATIM_CONNECT_TIMEOUT_MS
            conn.readTimeout = AppConstants.NOMINATIM_READ_TIMEOUT_MS

            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val json = org.json.JSONObject(text)
            val addressObj = json.optJSONObject("address")
            val country = addressObj?.optString("country", "") ?: ""

            val district = addressObj?.let { obj ->
                obj.optString("suburb", "")
                    .ifBlank { obj.optString("town", "") }
                    .ifBlank { obj.optString("city", "") }
                    .ifBlank { obj.optString("district", "") }
                    .ifBlank { obj.optString("county", "") }
                    .ifBlank { obj.optString("state", "") }
            } ?: ""

            val display = buildDisplayString(country, district)
            val continent = ContinentMapper.continentOf(country)

            Triple(display, country, continent)
        } catch (e: Exception) {
            Log.e(TAG, "Nominatim fallback reverse geocode failed", e)
            Triple("", "", "")
        }
    }

    // 從國家和區域組裝顯示字串 — 若區域已含國名則不重複前綴
    private fun buildDisplayString(country: String, district: String): String {
        if (district.isBlank()) return country
        if (country.isNotBlank() && !district.contains(country)) {
            return "$country, $district"
        }
        return district
    }
}
