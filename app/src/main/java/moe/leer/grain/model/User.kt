package moe.leer.grain.model

/**
 *
 * Created by leer on 1/14/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
data class User(
    val id: Int, val password: String,
    val name: String = "", val className: String = "",
    var moneyRem: Double = 0.0,
    var bookRentNum: Int = 0, var bookRentOutDataNum: Int = 0
)