package com.example.utils

fun getNotificationCategory(appName: String, text: String, pkg: String): String {
    val appLower = appName.lowercase()
    val textLower = text.lowercase()
    val pkgLower = pkg.lowercase()

    // 1. Finance
    val financeKeywords = listOf("bank", "pay", "card", "cash", "wallet", "transfer", "receipt", "charge", "refund", "credit", "debit", "revolut", "chase", "paypal", "venmo", "crypto", "bitcoin", "stocks", "invest", "bill", "invoice", "spent", "received", "balance")
    val financePackages = listOf("finance", "bank", "wallet", "ledger", "chase", "paypal", "venmo", "cashapp", "revolut", "google.android.apps.walletnfcrel")
    if (financePackages.any { pkgLower.contains(it) } ||
        financeKeywords.any { appLower.contains(it) } ||
        financeKeywords.any { textLower.contains(it) }) {
        return "Finance 💰"
    }

    // 2. Travel & Navigation
    val travelKeywords = listOf("uber", "lyft", "grab", "bolt", "airbnb", "booking", "flight", "trip", "map", "navigation", "waze", "transit", "airline", "hotel", "travel", "gps", "cab", "taxi", "train", "metro")
    val travelPackages = listOf("uber", "lyft", "waze", "maps", "airbnb", "booking", "travel", "transit", "cab", "taxi", "train")
    if (travelPackages.any { pkgLower.contains(it) } ||
        travelKeywords.any { appLower.contains(it) } ||
        travelKeywords.any { textLower.contains(it) }) {
        return "Travel ✈️"
    }

    // 3. Social & Communication
    val socialKeywords = listOf("slack", "whatsapp", "messenger", "telegram", "instagram", "snapchat", "pinterest", "reddit", "linkedin", "facebook", "twitter", "tiktok", "viber", "discord", "signal", "skype", "hangouts", "chat", "message", "email", "gmail", "outlook")
    val socialPackages = listOf("slack", "whatsapp", "messenger", "telegram", "instagram", "snapchat", "pinterest", "reddit", "linkedin", "facebook", "twitter", "tiktok", "discord", "signal", "gmail", "email", "communication")
    if (socialPackages.any { pkgLower.contains(it) } ||
        socialKeywords.any { appLower.contains(it) } ||
        textLower.contains("message") || textLower.contains("sent you") || textLower.contains("replied") || textLower.contains("commented") || textLower.contains("new pin") || textLower.contains("dm")) {
        return "Social 💬"
    }

    // 4. Internet & Productivity
    val prodKeywords = listOf("chrome", "firefox", "safari", "opera", "edge", "github", "notion", "drive", "docs", "sheets", "slides", "calendar", "keep", "duolingo", "medium", "learning", "study", "todo", "task", "zoom", "teams", "meet", "asana", "trello", "jira")
    val prodPackages = listOf("chrome", "firefox", "browser", "github", "notion", "drive", "docs", "sheets", "calendar", "duolingo", "learning", "task", "zoom", "teams", "productivity")
    if (prodPackages.any { pkgLower.contains(it) } ||
        prodKeywords.any { appLower.contains(it) } ||
        textLower.contains("streak") || textLower.contains("pull request") || textLower.contains("commit") || textLower.contains("reminder") || textLower.contains("meeting") || textLower.contains("event")) {
        return "Internet 🌐"
    }

    // 5. Entertainment & Media
    val mediaKeywords = listOf("youtube", "spotify", "netflix", "disney", "twitch", "prime video", "hulu", "hbo", "plex", "player", "music", "podcast", "radio", "game", "xbox", "playstation", "nintendo", "steam")
    val mediaPackages = listOf("youtube", "spotify", "netflix", "twitch", "player", "music", "podcast", "vlc", "audioplayer", "video", "game")
    if (mediaPackages.any { pkgLower.contains(it) } ||
        mediaKeywords.any { appLower.contains(it) } ||
        textLower.contains("playing") || textLower.contains("listening") || textLower.contains("video") || textLower.contains("episode") || textLower.contains("song") || textLower.contains("album")) {
        return "Entertainment 🎵"
    }

    // 6. Shopping & Food
    val shopKeywords = listOf("amazon", "ebay", "shopify", "doordash", "ubereats", "delivery", "order", "shipped", "cart", "walmart", "target", "aliexpress", "temu", "shein", "groceries", "food", "restaurant", "instacart", "mercari", "etsy")
    val shopPackages = listOf("amazon", "ebay", "shopify", "doordash", "ubereats", "delivery", "shopping", "instacart", "food")
    if (shopPackages.any { pkgLower.contains(it) } ||
        shopKeywords.any { appLower.contains(it) } ||
        textLower.contains("shipped") || textLower.contains("delivered") || textLower.contains("order number") || textLower.contains("out for delivery") || textLower.contains("purchased")) {
        return "Shopping 🛍️"
    }

    return "General 📦"
}
