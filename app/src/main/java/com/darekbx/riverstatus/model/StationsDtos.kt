package com.darekbx.riverstatus.model

import com.google.gson.annotations.SerializedName

data class StationsWrapper(val byStations: List<Station>)

data class Station(
    @SerializedName("i")
    val id: Long,
    @SerializedName("n")
    val name: String,
    @SerializedName("lo")
    val longitude: Double,
    @SerializedName("la")
    val latitude: Double,
    @SerializedName("a")
    val a: String)