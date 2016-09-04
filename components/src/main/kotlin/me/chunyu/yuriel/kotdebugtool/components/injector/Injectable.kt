package me.chunyu.yuriel.kotdebugtool.components.injector

import java.util.*

/**
 * Created by yuriel on 8/15/16.
 */
internal interface Injectable<T> {
    val model: LinkedHashMap<String, T>
    fun put(name: String, t: T)
    operator fun get(name: String): T?
    fun run(t: T?)
    fun run(name: String)
}