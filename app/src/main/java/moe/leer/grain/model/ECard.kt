package moe.leer.grain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 *
 * Created by leer on 1/18/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
@Entity(tableName = "card_items")
data class ECard(
    @PrimaryKey val id: Long,
    val name: String, val time: Date?, val money: Double
)