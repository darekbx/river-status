package com.darekbx.riverstatus.model

data class StationWrapper(
    val name: String,
    val state: String,
    val waterStateRecords: List<WaterStateRecord>) {

    var newRows = 0
}

data class WaterStateRecord(
    val state: String,
    val value: Int,
    val date: String)