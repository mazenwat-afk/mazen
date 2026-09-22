package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PersonEntity
import com.example.data.local.ThemeMode
import com.example.data.local.TransactionEntity
import com.example.data.local.UserSettings
import com.example.data.model.AnimationEffectStyle
import com.example.data.model.AppCurrency
import com.example.data.model.AppThemeStyle
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.theme.ClassicPrimaryLight
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ForestPrimaryLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MidnightGoldPrimaryLight
import com.example.ui.theme.OceanPrimaryLight
import com.example.ui.theme.PurplePrimaryLight
import com.example.ui.theme.RubyPrimaryLight
import com.example.ui.theme.SunsetPrimaryLight
import com.example.util.DataBackupManager

enum class SettingsSection(
    val titleArabic: String,
    val subtitleArabic: String,
    val icon: ImageVector
) {
    APPEARANCE("المظهر", "الوضع الفاتح والداكن، ألوان الواجهة، والوضع المضغوط", Icons.Default.Palette),
    CURRENCY("العملة", "اختيار العملة الافتراضية ورموز المبالغ", Icons.Default.CurrencyExchange),
    DATA("البيانات", "تصدير نسخة احتياطية، استيراد بيانات، أو مسح الحسابات", Icons.Default.Storage),
    APP_BEHAVIOR("التطبيق", "تأثيرات الحركة وسلاسة الانتقالات", Icons.Default.Settings),
    ABOUT("حول التطبيق", "معلومات الإصدار والمطور", Icons.Default.Info)
}

@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    allPersons: List<PersonEntity> = emptyList(),
    allTransactions: List<TransactionEntity> = emptyList(),
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onDarkModeChange: (Boolean) -> Unit = {},
    onThemeStyleChange: (AppThemeStyle) -> Unit,
    onCurrencyChange: (AppCurrency) -> Unit,
    onAnimationStyleChange: (AnimationEffectStyle) -> Unit = {},
    onCompactModeChange: (Boolean) -> Unit,
    onBiometricChange: (Boolean) -> Unit = {},
    onImportData: (List<PersonEntity>, List<TransactionEntity>, (Int, Int) -> Unit, (String) -> Unit) -> Unit = { _, _, _, _ -> },
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf<SettingsSection?>(null) }

    BackHandler(enabled = activeSection != null) {
        activeSection = null
    }

    AnimatedContent(
        targetState = activeSection,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "settings_subpage_anim",
        modifier = modifier.fillMaxSize()
    ) { section ->
        if (section == null) {
            SettingsMainMenu(
                userSettings = userSettings,
                onSelectSection = { activeSection = it }
            )
        } else {
            when (section) {
                SettingsSection.APPEARANCE -> SettingsAppearancePage(
                    userSettings = userSettings,
                    onThemeModeChange = onThemeModeChange,
                    onThemeStyleChange = onThemeStyleChange,
                    onCompactModeChange = onCompactModeChange,
                    onBack = { activeSection = null }
                )
                SettingsSection.CURRENCY -> SettingsCurrencyPage(
                    userSettings = userSettings,
                    onCurrencyChange = onCurrencyChange,
                    onBack = { activeSection = null }
                )
                SettingsSection.DATA -> SettingsDataPage(
                    persons = allPersons,
                    transactions = allTransactions,
                    onImportData = onImportData,
                    onClearAllData = onClearAllData,
                    onBack = { activeSection = null }
                )
                SettingsSection.APP_BEHAVIOR -> SettingsAppBehaviorPage(
                    userSettings = userSettings,
                    onAnimationStyleChange = onAnimationStyleChange,
                    onBack = { activeSection = null }
                )
                SettingsSection.ABOUT -> SettingsAboutPage(
                    onBack = { activeSection = null }
                )
            }
        }
    }
}

@Composable
private fun SettingsMainMenu(
    userSettings: UserSettings,
    onSelectSection: (SettingsSection) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_main_menu"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "menu_header") {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)) {
                Text(
                    text = "الإعدادات",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "خصص المظهر، العملة، والنسخ الاحتياطي بكل بساطة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(SettingsSection.entries.toTypedArray(), key = { it.name }) { section ->
            SettingsSectionCard(
                section = section,
                badgeText = when (section) {
                    SettingsSection.APPEARANCE -> userSettings.themeStyle.displayNameArabic
                    SettingsSection.CURRENCY -> userSettings.currency.code
                    SettingsSection.APP_BEHAVIOR -> userSettings.animationStyle.displayNameArabic
                    else -> null
                },
                onClick = { onSelectSection(section) }
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    section: SettingsSection,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_item_${section.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = section.titleArabic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = section.subtitleArabic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SECTION 1: المظهر (Appearance)
// -------------------------------------------------------------
@Composable
private fun SettingsAppearancePage(
    userSettings: UserSettings,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemeStyleChange: (AppThemeStyle) -> Unit,
    onCompactModeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_appearance_page"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "sub_header") {
            SubPageHeader(
                title = "المظهر",
                subtitle = "الوضع الفاتح والداكن وألوان الثيم ونمط العرض",
                onBack = onBack
            )
        }

        // Theme Mode (Light / Dark / System)
        item(key = "theme_mode_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نمط الإضاءة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اختر الوضع الفاتح أو الداكن أو التبديل التلقائي مع الهاتف:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ThemeMode.entries.forEach { mode ->
                        val isSelected = userSettings.themeMode == mode
                        Surface(
                            onClick = { onThemeModeChange(mode) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .testTag("theme_mode_${mode.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onThemeModeChange(mode) }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = mode.displayNameArabic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Color Themes
        item(key = "color_theme_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ألوان وثيم التطبيق",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اختر اللون الرئيسي المفضل لديك للواجهات والبطاقات:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    val themesList = listOf(
                        Triple(AppThemeStyle.CLASSIC, "كلاسيكي", ClassicPrimaryLight),
                        Triple(AppThemeStyle.OCEAN, "محيطي", OceanPrimaryLight),
                        Triple(AppThemeStyle.EMERALD, "زمردي", EmeraldPrimaryLight),
                        Triple(AppThemeStyle.PURPLE, "أرجواني", PurplePrimaryLight),
                        Triple(AppThemeStyle.SUNSET, "غروب", SunsetPrimaryLight),
                        Triple(AppThemeStyle.RUBY, "ياقوتي", RubyPrimaryLight),
                        Triple(AppThemeStyle.MIDNIGHT_GOLD, "ذهبي ملكي", MidnightGoldPrimaryLight),
                        Triple(AppThemeStyle.FOREST, "غابات", ForestPrimaryLight)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        themesList.chunked(2).forEach { rowThemes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowThemes.forEach { (style, name, color) ->
                                    val isSelected = userSettings.themeStyle == style
                                    Surface(
                                        onClick = { onThemeStyleChange(style) },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) color.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) color else Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("theme_picker_${style.name}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Text(
                                                text = name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Compact Mode
        item(key = "compact_mode_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsSwitchRow(
                        title = "الوضع المضغوط",
                        subtitle = "تصغير الهوامش لعرض عدد أكبر من البطاقات",
                        icon = Icons.Default.DensityMedium,
                        checked = userSettings.compactMode,
                        onCheckedChange = onCompactModeChange,
                        testTag = "compact_mode_switch"
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SECTION 2: العملة (Currency)
// -------------------------------------------------------------
@Composable
private fun SettingsCurrencyPage(
    userSettings: UserSettings,
    onCurrencyChange: (AppCurrency) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_currency_page"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "sub_header") {
            SubPageHeader(
                title = "العملة",
                subtitle = "حدد العملة الافتراضية لحساباتك ورموز المبالغ",
                onBack = onBack
            )
        }

        item(key = "currency_list_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AppCurrency.entries.forEach { currency ->
                        val isSelected = userSettings.currency == currency
                        Surface(
                            onClick = { onCurrencyChange(currency) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("currency_option_${currency.code}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onCurrencyChange(currency) }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${currency.code} — ${currency.displayNameArabic}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "رمز العملة: ${currency.symbolArabic}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = currency.symbolArabic,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SECTION 3: البيانات (Data - Safe Export & Import)
// -------------------------------------------------------------
@Composable
private fun SettingsDataPage(
    persons: List<PersonEntity>,
    transactions: List<TransactionEntity>,
    onImportData: (List<PersonEntity>, List<TransactionEntity>, (Int, Int) -> Unit, (String) -> Unit) -> Unit,
    onClearAllData: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // SAF Export Launcher (wrapped safely)
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                val json = DataBackupManager.generateBackupJson(persons, transactions)
                val success = DataBackupManager.writeJsonToUri(context, uri, json)
                if (success) {
                    Toast.makeText(context, "تم حفظ النسخة الاحتياطية بنجاح ✅", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "حدث خطأ أثناء حفظ الملف", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "تعذر حفظ الملف: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // SAF Import Launcher (wrapped safely)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val jsonString = DataBackupManager.readJsonFromUri(context, uri)
                if (jsonString != null) {
                    val backup = DataBackupManager.parseBackupJson(jsonString)
                    onImportData(
                        backup.persons,
                        backup.transactions,
                        { pCount, tCount ->
                            Toast.makeText(
                                context,
                                "تم استيراد $pCount حساب و $tCount حركة بنجاح 🎉",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "تعذر قراءة محتوى الملف", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "ملف النسخة غير صالح أو معطوب", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_data_page"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "sub_header") {
            SubPageHeader(
                title = "البيانات",
                subtitle = "تصدير نسخة احتياطية آمنة، استيراد الحسابات، أو مسح البيانات",
                onBack = onBack
            )
        }

        // Export Card
        item(key = "export_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IncomeGreen.copy(alpha = 0.12f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تصدير نسخة احتياطية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "احفظ ملفاً يشمل كل (${persons.size}) حساب و (${transactions.size}) حركة بأمان",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Button: Direct Share / Save (Zero Crash Guarantee)
                    Button(
                        onClick = {
                            val json = DataBackupManager.generateBackupJson(persons, transactions)
                            val result = DataBackupManager.shareBackupFile(context, json)
                            result.onFailure {
                                Toast.makeText(context, "تعذر مشاركة الملف: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("export_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مشاركة أو حفظ في الجهاز (مباشر)", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Secondary Button: SAF CreateDocument with safe fallback
                        OutlinedButton(
                            onClick = {
                                try {
                                    val fileName = DataBackupManager.createBackupFileName()
                                    exportLauncher.launch(fileName)
                                } catch (e: ActivityNotFoundException) {
                                    // Fallback to share
                                    val json = DataBackupManager.generateBackupJson(persons, transactions)
                                    DataBackupManager.shareBackupFile(context, json)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "خطأ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_saf_btn")
                        ) {
                            Text("تحديد مسار الحفظ", fontSize = 13.sp)
                        }

                        // Copy JSON to clipboard
                        OutlinedButton(
                            onClick = {
                                val json = DataBackupManager.generateBackupJson(persons, transactions)
                                val copied = DataBackupManager.copyToClipboard(context, json)
                                if (copied) {
                                    Toast.makeText(context, "تم نسخ محتوى النسخة للحافظة بنجاح 📋", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_copy_btn")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ النص", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Import Card
        item(key = "import_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "استيراد نسخة احتياطية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "استعادة الحسابات والمعاملات من ملف احتياطي سابق",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            try {
                                importLauncher.launch(arrayOf("application/json", "*/*"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "تعذر فتح منتقي الملفات في هذا الجهاز", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("import_file_btn")
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اختيار ملف النسخة من الهاتف", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val clipText = DataBackupManager.readFromClipboard(context)
                            if (clipText.isNullOrBlank()) {
                                Toast.makeText(context, "الحافظة فارغة! انسخ نص النسخة أولاً", Toast.LENGTH_SHORT).show()
                            } else {
                                try {
                                    val backup = DataBackupManager.parseBackupJson(clipText)
                                    onImportData(
                                        backup.persons,
                                        backup.transactions,
                                        { pCount, tCount ->
                                            Toast.makeText(context, "تم استيراد $pCount حساب و $tCount حركة من الحافظة 🎉", Toast.LENGTH_LONG).show()
                                        },
                                        { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "النص الموجود في الحافظة ليس نسخة احتياطية صالحة", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_clipboard_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("لصق واستيراد من الحافظة", fontSize = 13.sp)
                    }
                }
            }
        }

        // Danger Zone: Clear Data
        item(key = "clear_data_card") {
            Card(
                onClick = { showClearConfirmDialog = true },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clear_all_data_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مسح كافة البيانات والحسابات",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                        Text(
                            text = "حذف جميع الحسابات وسجلات الوارد والمنصرف بالكامل",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = null,
                        tint = ExpenseRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        DeleteConfirmationDialog(
            title = "مسح كافة البيانات؟",
            message = "تحذير: هذا الإجراء سيقوم بحذف جميع الحسابات والمعاملات المالية نهائياً. نوصي بتصدير نسخة احتياطية أولاً.",
            confirmButtonText = "نعم، احذف الكل",
            onConfirm = {
                onClearAllData()
                showClearConfirmDialog = false
                Toast.makeText(context, "تم مسح البيانات بالكامل", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showClearConfirmDialog = false }
        )
    }
}

// -------------------------------------------------------------
// SECTION 4: التطبيق (App Behavior)
// -------------------------------------------------------------
@Composable
private fun SettingsAppBehaviorPage(
    userSettings: UserSettings,
    onAnimationStyleChange: (AnimationEffectStyle) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_app_behavior_page"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "sub_header") {
            SubPageHeader(
                title = "سلوك التطبيق",
                subtitle = "تخصيص الحركات وتأثيرات التنقل بين الصفحات",
                onBack = onBack
            )
        }

        item(key = "animation_styles_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نمط الحركة والانتقال",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اختر سلاسة الحركة عند التبديل بين التبويبات والصفحات:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    AnimationEffectStyle.entries.forEach { style ->
                        val isSelected = userSettings.animationStyle == style
                        Surface(
                            onClick = { onAnimationStyleChange(style) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .testTag("anim_style_${style.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onAnimationStyleChange(style) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = style.displayNameArabic,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = style.descriptionArabic,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SECTION 5: حول التطبيق (About)
// -------------------------------------------------------------
@Composable
private fun SettingsAboutPage(
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_about_page"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "sub_header") {
            SubPageHeader(
                title = "حول التطبيق",
                subtitle = "معلومات الإصدار والرؤية والمطور",
                onBack = onBack
            )
        }

        item(key = "about_card") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Mz",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.SansSerif
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "محاسبي Mz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "الإصدار 2.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "محاسبيMZ",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "تطبيقك البسيط عشان ترتب حساباتك، تعرف عليك شنو وليك شنو، وتتابع مصروفاتك ومعاملاتك بسهولة.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "بواسطة Mazen Omer 😎✌️",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حالة التشغيل",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "محلي 100% بدون إنترنت",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPONENTS
// -------------------------------------------------------------
@Composable
private fun SubPageHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("settings_back_btn")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
