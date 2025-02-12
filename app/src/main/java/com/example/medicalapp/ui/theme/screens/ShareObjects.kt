package com.example.medicalapp.ui.theme.screens

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ShareObjects{
  var locationStateObejct = MutableStateFlow("")
  val locationInfo : StateFlow<String> get() = locationStateObejct.asStateFlow()
  
  var sampleType = MutableStateFlow("")
  val sampletypeInfo : StateFlow<String> get() = sampleType.asStateFlow()
  fun updateLocation(loca : String){
    locationStateObejct.value = loca
  }
  fun sampleTypeUpdate(stype : String){
    sampleType.value = stype
  }
}