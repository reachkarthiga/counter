package com.example.counter.counterTag

import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.*
import com.example.counter.dataBase.Database
import com.example.counter.models.Counter
import com.example.counter.models.Tags
import kotlinx.coroutines.launch

const val TEMPORARY_COUNTER = "Untitled"

class CounterViewModel(val database: Database) : ViewModel() {

    var count = MutableLiveData<Int>()
    private val steps = 1

    val tagList: LiveData<List<Tags>>
        get() = Transformations.switchMap(counterName) {
            database.tagDao.getTagsByName(it)
        }

    private val _counterName = MutableLiveData<String>()
    val counterName: LiveData<String>
        get() = _counterName

    val showCounterName: LiveData<Int>
        get() = _counterName.map {
            if (_counterName.value == TEMPORARY_COUNTER) {
                INVISIBLE
            } else {
                VISIBLE
            }
        }

    init {

        _counterName.value = TEMPORARY_COUNTER

        viewModelScope.launch {
            count.value = database.counterDao.getCountValue(TEMPORARY_COUNTER)
        }

        if (count.value == 0) {
            viewModelScope.launch {
                database.counterDao.insertCounter(
                    Counter(
                        TEMPORARY_COUNTER,
                        count.value.toString().toInt(),
                        0
                    )
                )
            }
        }

    }

    fun increaseCountValue() {
        count.value = count.value?.plus(steps)

        viewModelScope.launch {
            database.counterDao.updateCount(
                count.value.toString().toInt(),
                counterName.value.toString()
            )
        }

    }

    fun decreaseCountValue() {
        if ((count.value ?: 0) > steps) {
            count.value = count.value?.minus(steps)
        } else {
            count.value = 0
        }

        viewModelScope.launch {
            database.counterDao.updateCount(
                count.value.toString().toInt(),
                counterName.value.toString()
            )
        }

    }

    fun saveTag(tagName: String, count: Int, counter: String) {

        if (tagName.isNotEmpty()) {

            val counterName = counter.ifEmpty { TEMPORARY_COUNTER }

            viewModelScope.launch {
                database.tagDao.insertTag(Tags(tagName, count, counterName))
            }

            viewModelScope.launch {
                database.counterDao.increaseTagCount(counterName)
            }

        }

    }

    fun saveCounter(counterName: String) {

        if (counterName.isNotEmpty()) {

            viewModelScope.launch {
                val tagsCount = database.tagDao.getTagsCount(_counterName.value.toString())
                database.counterDao.insertCounter(
                    Counter(
                        counterName,
                        count.value.toString().toInt(),
                        tagsCount
                    )
                )
            }

            viewModelScope.launch {
                database.tagDao.updateTags(_counterName.value.toString(), counterName)
            }

            _counterName.value = counterName

        }

    }

    override fun onCleared() {

        viewModelScope.launch {
            database.tagDao.clearTempTags(TEMPORARY_COUNTER)
            database.counterDao.clearTempCounter(TEMPORARY_COUNTER)
        }

        super.onCleared()

    }

    fun checkTagAvailability(): Boolean {

        var isAlreadyTagged = false

        viewModelScope.launch {
            isAlreadyTagged = database.tagDao.checkAvailability(
                count.value.toString().toInt(),
                counterName.value.toString()
            )
        }

        return isAlreadyTagged
    }

}