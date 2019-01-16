package moe.leer.grain.model

import com.google.gson.annotations.SerializedName

/**
 *
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
data class Transcript(
    @SerializedName("XN")
    val year: String,
    @SerializedName("XQ")
    val semester: Int, // 1/2
    @SerializedName("KCMC")
    val subjectName: String,
    @SerializedName("KCXZ")
    val subjectProperty: String, //选修课/必修课/公共选修课
    @SerializedName("XF")
    val subjectFullGrade: String,
    @SerializedName("CJ")
    val score: String,
    @SerializedName("CXCJ")
    val reScore: String? = null //重修成绩
)
