package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AnimationEffectStyle
import com.example.data.model.AppCurrency
import com.example.data.model.AppThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mazen_ledger_settings")

enum class ThemeMode(val displayNameArabic: String) {
    LIGHT("فاتح"),
    DARK("داكن"),
    SYSTEM("تلقائي (حسب النظام)");

    companion object {
        fun fromName(name: String?): ThemeMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: SYSTEM
        }
    }
}

data class UserSettings(
    val isDarkMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeStyle: AppThemeStyle = AppThemeStyle.CLASSIC,
    val currency: AppCurrency = AppCurrency.SDG,
    val animationsEnabled: Boolean = true,
    val animationStyle: AnimationEffectStyle = AnimationEffectStyle.SMOOTH,
    val compactMode: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val pinCode: String = ""
)

class PreferencesDataStore(private val context: Context) {

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val THEME_STYLE = stringPreferencesKey("theme_style")
        val CURRENCY = stringPreferencesKey("currency")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val ANIMATION_STYLE = stringPreferencesKey("animation_style")
        val COMPACT_MODE = booleanPreferencesKey("compact_mode")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val PIN_CODE = stringPreferencesKey("pin_code")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: false
        val themeModeName = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val themeStyleName = preferences[PreferencesKeys.THEME_STYLE] ?: AppThemeStyle.CLASSIC.name
        val currencyCode = preferences[PreferencesKeys.CURRENCY] ?: AppCurrency.SDG.code
        val animationsEnabled = preferences[PreferencesKeys.ANIMATIONS_ENABLED] ?: true
        val animationStyleName = preferences[PreferencesKeys.ANIMATION_STYLE] ?: AnimationEffectStyle.SMOOTH.name
        val compactMode = preferences[PreferencesKeys.COMPACT_MODE] ?: false
        val isBiometricEnabled = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false
        val isAppLockEnabled = preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false
        val pinCode = preferences[PreferencesKeys.PIN_CODE] ?: ""

        UserSettings(
            isDarkMode = isDarkMode,
            themeMode = ThemeMode.fromName(themeModeName),
            themeStyle = AppThemeStyle.fromName(themeStyleName),
            currency = AppCurrency.fromCode(currencyCode),
            animationsEnabled = animationsEnabled,
            animationStyle = AnimationEffectStyle.fromName(animationStyleName),
            compactMode = compactMode,
            isBiometricEnabled = isBiometricEnabled,
            isAppLockEnabled = isAppLockEnabled,
            pinCode = pinCode
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
            preferences[PreferencesKeys.DARK_MODE] = (mode == ThemeMode.DARK)
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
            preferences[PreferencesKeys.THEME_MODE] = if (enabled) ThemeMode.DARK.name else ThemeMode.LIGHT.name
        }
    }

    suspend fun setThemeStyle(themeStyle: AppThemeStyle) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_STYLE] = themeStyle.name
        }
    }

    suspend fun setCurrency(currency: AppCurrency) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY] = currency.code
        }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun setAnimationStyle(style: AnimationEffectStyle) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANIMATION_STYLE] = style.name
            // if style is OFF, disable animations; otherwise enable
            preferences[PreferencesKeys.ANIMATIONS_ENABLED] = (style != AnimationEffectStyle.OFF)
        }
    }

    suspend fun setCompactMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COMPACT_MODE] = enabled
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setPinCode(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PIN_CODE] = pin
        }
    }
}
