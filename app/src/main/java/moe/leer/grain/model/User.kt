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
    val bookRentNum: Int = 0, val bookRentOutDataNum: Int = 0
)