package com.example.counter.counterTag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.counter.dataBase.Database
import com.example.counter.models.Tags
import kotlinx.coroutines.launch

const val TEMPORARY_COUNTER = "Untitled"

class CounterViewModel(val database: Database) : ViewModel() {

    var count = MutableLiveData<Int>()
    private val steps = 1

    val tagList :LiveData<List<Tags>>
        get() = database.tagDao.getTagsByName(counterName.value.toString())

    private val _counterName = MutableLiveData<String>()
    val counterName :LiveData<String>
        get() = _counterName

    init {
        count.value = 0
        _counterName.value = TEMPORARY_COUNTER
    }

    fun increaseCountValue() {
        count.value = count.value?.plus(steps)
    }

    fun decreaseCountValue() {
        if ((count.value ?: 0) > steps) {
            count.value = count.value?.minus(steps)
        } else {
            count.value = 0
        }
    }

    fun saveTag(tagName:String, count:Int, counter:String) {

        if (tagName.isNotEmpty()) {
            val counterName = counter.ifEmpty { TEMPORARY_COUNTER }
            viewModelScope.launch {
                database.tagDao.insertTag(Tags(tagName, count, counterName))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}