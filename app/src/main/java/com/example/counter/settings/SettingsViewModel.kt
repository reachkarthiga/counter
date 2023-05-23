package com.example.counter.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.counter.dataBase.Database
import kotlinx.coroutines.launch

const val SETTINGS = "settings"
const val TAP = "tap"
const val STEP_VALUE = "stepValue"
const val SOUND = "sound"


class SettingsViewModel(val application: Application) :ViewModel() {

    var step_value = MutableLiveData<Int>()

    val sharedPreferences = application.getSharedPreferences(SETTINGS , Context.MODE_PRIVATE)

    var tap_setting = MutableLiveData<Boolean>()
    var playSound_setting = MutableLiveData<Boolean>()
    var useButtons_setting = MutableLiveData<Boolean>()

    private val database = Database.getInstance(application.baseContext)

    fun clearAll() {
        viewModelScope.launch {
            database.counterDao.deleteAllCounters()
            database.tagDao.deleteAllTags()
        }
    }

    init {
        step_value.value = sharedPreferences.getInt(STEP_VALUE, 1)
        tap_setting.value = sharedPreferences.getBoolean(TAP, false)
        playSound_setting.value = sharedPreferences.getBoolean(SOUND, false)

    }

    fun increaseStepValue() {

        if ((step_value.value ?: 0) < 10) {
            step_value.value = step_value.value?.plus(1)
        }

        sharedPreferences.edit().apply {
            this.putInt(STEP_VALUE , step_value.value.toString().toInt())
        } .apply()

    }

    fun decreaseStepValue() {

        if ((step_value.value ?: 0) > 1) {
            step_value.value = step_value.value?.minus(1)
        }

        sharedPreferences.edit().apply {
            this.putInt(STEP_VALUE , step_value.value.toString().toInt())
        } .apply()


    }

    fun setTap(tapValue :Boolean) {
        sharedPreferences.edit().apply {
            this.putBoolean(TAP , tapValue)
        } .apply()

        tap_setting.value = tapValue
    }

    fun setPlayingSound(playSound: Boolean) {
        sharedPreferences.edit().apply {
            this.putBoolean(SOUND , playSound)
        } .apply()

        playSound_setting.value =playSound
    }

}