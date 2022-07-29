package com.darekbx.riverstatus.stations

sealed class UIEvent {
    class Error(val message: String) : UIEvent()
    object ClearErrors : UIEvent()
}
