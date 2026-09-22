package com.example.data.model

import com.example.data.local.PersonEntity

enum class TransactionType(val labelArabic: String) {
    INCOME("وارد"),
    EXPENSE("منصرف")
}

enum class AppCurrency(
    val code: String,
    val displayNameArabic: String,
    val symbolArabic: String
) {
    SDG("SDG", "جنيه سوداني", "جنيه"),
    USD("USD", "دولار أمريكي", "$"),
    SAR("SAR", "ريال سعودي", "ر.س"),
    AED("AED", "درهم إماراتي", "د.إ"),
    EGP("EGP", "جنيه مصري", "ج.م"),
    EUR("EUR", "يورو", "€"),
    GBP("GBP", "جنيه إسترليني", "£");

    companion object {
        fun fromCode(code: String?): AppCurrency {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: SDG
        }
    }
}

enum class AppThemeStyle(val displayNameArabic: String) {
    CLASSIC("كلاسيكي"),
    OCEAN("محيطي"),
    EMERALD("زمردي"),
    PURPLE("أرجواني"),
    SUNSET("غروب"),
    RUBY("ياقوتي"),
    MIDNIGHT_GOLD("ذهبي ملكي"),
    FOREST("غابات");

    companion object {
        fun fromName(name: String?): AppThemeStyle {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CLASSIC
        }
    }
}

enum class AnimationEffectStyle(val displayNameArabic: String, val descriptionArabic: String) {
    SMOOTH("انسيابي وناعم", "تلاشي سلس وانتقال أنيق متزن"),
    BOUNCY("مرن ونابض", "حركة ارتدادية نابضة مفعمة بالحيوية"),
    SLIDE("انزلاقي سريع", "حركة انزلاق أفقية واضحة"),
    MINIMAL("هادئ وبسيط", "حركة خفيفة ومريحة جداً"),
    OFF("بدون حركات", "تغيير فوري بدون أي تأخير أو حركات");

    companion object {
        fun fromName(name: String?): AnimationEffectStyle {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: SMOOTH
        }
    }
}

enum class DateFilterPeriod(val labelArabic: String) {
    ALL("الكل"),
    TODAY("اليوم"),
    THIS_WEEK("هذا الأسبوع"),
    THIS_MONTH("هذا الشهر")
}

enum class TransactionSort(val labelArabic: String) {
    NEWEST("الأحدث أولاً"),
    OLDEST("الأقدم أولاً"),
    HIGHEST_AMOUNT("الأعلى مبلغاً"),
    LOWEST_AMOUNT("الأقل مبلغاً")
}

data class PersonSummary(
    val person: PersonEntity,
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double,
    val transactionCount: Int,
    val lastTimestamp: Long
)
