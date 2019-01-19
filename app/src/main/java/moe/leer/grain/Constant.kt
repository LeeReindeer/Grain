package moe.leer.grain

/**
 *
 * Created by leer on 1/19/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
object Constant {
    // const string for shared preference
    const val SP_NAME = "Repository"
    const val SP_TRANSCRIPT = "Repository"
    const val SP_ISLOGIN = "isLogin"
    const val SP_LASTID = "lastId"
    const val SP_FETCH_TRANSCRIPT_TIME = "ftt"

    // timeout in a day, means only **auto** fetch from server once a day
    const val FETCH_TIMEOUT = 1000 * 3600 * 24

}