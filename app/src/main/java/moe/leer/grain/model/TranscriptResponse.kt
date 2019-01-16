package moe.leer.grain.model

import com.google.gson.annotations.SerializedName

data class TranscriptResponse(

    @SerializedName("cj")
    val transcriptList: MutableList<Transcript>? = null
)