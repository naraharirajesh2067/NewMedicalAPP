package com.example.medicalapp.ui.theme.screens

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ShareObjects{
  var locationStateObejct = MutableStateFlow("")
  val locationInfo : StateFlow<String> get() = locationStateObejct.asStateFlow()
  
  fun updateLocation(loca : String){
    locationStateObejct.value = loca
  }
}