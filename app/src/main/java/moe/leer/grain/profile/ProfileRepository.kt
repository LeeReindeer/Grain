package moe.leer.grain.profile

import android.content.Context
import moe.leer.grain.Constant
import moe.leer.grain.base.BaseUserRepository
import moe.leer.grain.db.ECardDatabase
import moe.leer.grain.getSP
import moe.leer.grain.getSPEdit

/**
 *
 * Created by leer on 1/24/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ProfileRepository(private val db: ECardDatabase, private val context: Context) : BaseUserRepository(context) {

    fun nukeData() {
        db.clearAllTables()

        val lastId = context.getSP(Constant.SP_NAME).getString(Constant.SP_LASTID, "")
        context.getSPEdit(Constant.SP_NAME) {
            clear()
            putString(Constant.SP_LASTID, lastId)
            apply()
        }
    }

    override fun refreshAndSaveUser() {
        super.refreshAndSaveUser()
    }
}