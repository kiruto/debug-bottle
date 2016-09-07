package com.exyui.android.debugbottle.components

import java.util.regex.Pattern

/**
 * Created by yuriel on 8/17/16.
 */
internal object SearchableListViewHelper {

    fun getPattern(str: String): Pattern {
        var patternStr = ".*"
        for (c in str) {
            patternStr += "($c).*"
        }
        return Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE)
    }

    interface HighlightListener<T> {
        val collections: Collection<T>

        fun getString(t: T): String

        fun update(map: Map<T, List<String>>, str: String)

        fun highlight(p: Pattern, str: String = ""): Map<T, List<String>> {
            val result = mutableMapOf<T, List<String>>()
            for (t in collections) {
                val matcher = p.matcher(getString(t))
                if (!matcher.matches()) continue
                //val index = collections.indexOf(t)
                val l = mutableListOf<String>()
                for (i in 0..matcher.groupCount() - 1) {
                    l.add(matcher.group(i))
                }
                result.put(t, l)
            }
            update(result, str)
            return result
        }
    }
}