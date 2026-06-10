package com.example.utils

fun evaluateMath(expr: String): String {
    try {
        val clean = expr.replace(" ", "")
        val regex = Regex("""(\d+)([+\-*/])(\d+)""")
        val match = regex.find(clean)
        if (match != null) {
            val num1 = match.groupValues[1].toDoubleOrNull() ?: return "Error"
            val num2 = match.groupValues[3].toDoubleOrNull() ?: return "Error"
            val op = match.groupValues[2]
            val res = when (op) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0.0) num1 / num2 else "DivByZero"
                else -> ""
            }
            return res.toString()
        }
        return "Enter simple math (e.g. 15 * 6)"
    } catch (_: Exception) {
        return "Error"
    }
}
