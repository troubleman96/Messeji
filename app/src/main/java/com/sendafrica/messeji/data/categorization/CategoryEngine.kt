package com.sendafrica.messeji.data.categorization

import android.content.Context
import com.sendafrica.messeji.data.db.dao.SenderRuleDao
import com.sendafrica.messeji.data.db.entity.SenderRule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

enum class MessageCategory(val value: String) {
    PERSON("person"),
    MONEY_OTP("money_otp"),
    PROMO("promo");

    companion object {
        fun fromValue(value: String): MessageCategory {
            return entries.find { it.value == value } ?: PERSON
        }
    }
}

data class CategoryRule(
    val type: RuleType,
    val pattern: String
)

enum class RuleType {
    SENDER_EXACT, SENDER_CONTAINS, KEYWORD, REGEX
}

@Singleton
class CategoryEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val senderRuleDao: SenderRuleDao
) {
    private var bundledRules: List<BundledSenderRule> = emptyList()
    private var bundledKeywords: List<CategoryKeyword> = emptyList()
    private var bundledRegexes: List<CategoryRegex> = emptyList()

    data class BundledSenderRule(
        val sender: String,
        val category: String,
        val matchType: String
    )

    data class CategoryKeyword(
        val keyword: String,
        val category: String
    )

    data class CategoryRegex(
        val pattern: String,
        val category: String
    )

    fun loadBundledRules() {
        try {
            val jsonString = context.assets.open("category_rules.json")
                .bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)

            val senders = json.getJSONArray("senders")
            bundledRules = mutableListOf<BundledSenderRule>().apply {
                for (i in 0 until senders.length()) {
                    val s = senders.getJSONObject(i)
                    add(
                        BundledSenderRule(
                            sender = s.getString("sender"),
                            category = s.getString("category"),
                            matchType = s.optString("match_type", "exact")
                        )
                    )
                }
            }

            val keywords = json.getJSONArray("keywords")
            bundledKeywords = mutableListOf<CategoryKeyword>().apply {
                for (i in 0 until keywords.length()) {
                    val k = keywords.getJSONObject(i)
                    add(
                        CategoryKeyword(
                            keyword = k.getString("keyword"),
                            category = k.getString("category")
                        )
                    )
                }
            }

            val regexes = json.getJSONArray("regexes")
            bundledRegexes = mutableListOf<CategoryRegex>().apply {
                for (i in 0 until regexes.length()) {
                    val r = regexes.getJSONObject(i)
                    add(
                        CategoryRegex(
                            pattern = r.getString("pattern"),
                            category = r.getString("category")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            bundledRules = emptyList()
            bundledKeywords = emptyList()
            bundledRegexes = emptyList()
        }
    }

    suspend fun categorize(
        sender: String,
        body: String,
        isContact: Boolean
    ): MessageCategory {
        if (isContact) return MessageCategory.PERSON

        val userOverride = senderRuleDao.getRule(sender)
        if (userOverride != null) {
            return MessageCategory.fromValue(userOverride.forcedCategory)
        }

        for (rule in bundledRules) {
            if (rule.category == "person") continue
            val matches = when (rule.matchType) {
                "exact" -> sender.equals(rule.sender, ignoreCase = true)
                "contains" -> sender.contains(rule.sender, ignoreCase = true)
                "starts_with" -> sender.startsWith(rule.sender, ignoreCase = true)
                else -> false
            }
            if (matches) return MessageCategory.fromValue(rule.category)
        }

        for (kw in bundledKeywords) {
            if (body.contains(kw.keyword, ignoreCase = true)) {
                return MessageCategory.fromValue(kw.category)
            }
        }

        for (rx in bundledRegexes) {
            try {
                val regex = Regex(rx.pattern, RegexOption.IGNORE_CASE)
                if (regex.containsMatchIn(body)) {
                    return MessageCategory.fromValue(rx.category)
                }
            } catch (_: Exception) { }
        }

        val alphanumericSender = sender.any { it.isLetter() } && sender.any { it.isDigit() }
        val hasUrl = body.contains(Regex("https?://\\S+", RegexOption.IGNORE_CASE))
        val hasPromoWords = listOf(
            "punguzo", "ofa", "bei nafuu", "bonyeza link",
            "tunatoa", "promo", "mshindi", "zawadi", "bonus"
        ).any { body.contains(it, ignoreCase = true) }

        if (alphanumericSender && (hasPromoWords || hasUrl)) {
            return MessageCategory.PROMO
        }

        val hasAmount = body.contains(Regex("[Tt][Ss][Hh]?\\s*[\\d,]+"))
        val hasOtpWords = listOf(
            "namba ya uthibitishaji", "otp", "msimbo wa uthibitisho",
            "verification code", "uthibitisho"
        ).any { body.contains(it, ignoreCase = true) }
        val hasNumericCode = body.contains(Regex("(?<!\\d)\\d{4,8}(?!\\d)"))
        val moneySenders = listOf("mpesa", "tigo", "airtel", "halopesa", "vodacom", "crdb", "nmb", "equity")
        val isMoneySender = moneySenders.any { sender.contains(it, ignoreCase = true) }

        if (isMoneySender || hasAmount || hasOtpWords) {
            return MessageCategory.MONEY_OTP
        }

        return MessageCategory.PERSON
    }

    suspend fun setUserOverride(sender: String, category: String) {
        senderRuleDao.upsert(
            SenderRule(
                senderPattern = sender,
                forcedCategory = category,
                createdBy = "user_override"
            )
        )
    }

    suspend fun removeUserOverride(sender: String) {
        senderRuleDao.delete(sender)
    }
}
