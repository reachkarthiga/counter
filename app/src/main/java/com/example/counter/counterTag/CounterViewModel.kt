package com.example.counter.counterTag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.counter.models.Tags

class CounterViewModel : ViewModel() {

    var count = MutableLiveData<Int>()
    private val steps = 1

    val tagList = MutableLiveData<MutableList<Tags>>()

    init {
        count.value = 0
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

    fun saveTag(tagName:String, count:Int, counterName:String) {
        tagList.value?.add(Tags(tagName, count, counterName))
    }

}