package com.darekbx.riverstatus.waterlevel

sealed class UIEvent {
    class Error(val message: String) : UIEvent()
    object ClearErrors : UIEvent()
}
