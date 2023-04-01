package com.example.swcertificatio

import com.google.gson.annotations.SerializedName

data class SWdataDto(
    @SerializedName("score") val Swscore: List<SWdata>
)
