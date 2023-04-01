package com.example.swcertificatio

import com.google.gson.annotations.SerializedName

data class SWdata(
    @SerializedName("구분") val division: String,
    @SerializedName("학번") val schoolnumber: Int,
    @SerializedName("이름") val name: String,
    @SerializedName("점수") val score: Float
)
