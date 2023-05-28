package com.example.counter.counterTag

import android.app.Application
import android.content.Context
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.*
import com.example.counter.dataBase.Database
import com.example.counter.models.Counter
import com.example.counter.models.Tags
import com.example.counter.settings.*
import kotlinx.coroutines.launch

const val TEMPORARY_COUNTER = "Untitled"

class CounterViewModel(application: Application) : ViewModel() {

    var count = MutableLiveData<Int>()

    var tempTagList = mutableListOf<Tags>()

    private val database = Database.getInstance(application.applicationContext)

    private val sharedPreferences = application.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)

    var steps: Int = sharedPreferences.getInt(STEP_VALUE, 1)
    var tapToIncrease: Boolean = sharedPreferences.getBoolean(TAP, false)
    var playSound: Boolean = sharedPreferences.getBoolean(SOUND, false)

    val tagList: LiveData<List<Tags>>
        get() = Transformations.switchMap(counterNameSelected) {
            database.tagDao.getTagsByName(it)
        }

    val counterList: LiveData<List<Counter>>
        get() = database.counterDao.getCounters()

    val counterNameSelected = MutableLiveData<String>()

    val showCounterName: LiveData<Int>
        get() = counterNameSelected.map {
            if (counterNameSelected.value == TEMPORARY_COUNTER) {
                INVISIBLE
            } else {
                VISIBLE
            }
        }

    val showToastOnMaxiCount = MutableLiveData<Boolean>()

    init {

        counterNameSelected.value = TEMPORARY_COUNTER
        count.value = 0

    }

    fun increaseCountValue() {

        if ((count.value?.toInt() ?: 0) < 9999) {
            count.value = count.value?.plus(steps)
            showToastOnMaxiCount.value = false
        } else {
            showToastOnMaxiCount.value = true
        }

        if (counterNameSelected.value != TEMPORARY_COUNTER) {
            viewModelScope.launch {
                database.counterDao.updateCount(
                    count.value.toString().toInt(),
                    counterNameSelected.value.toString()
                )
            }
        }

    }

    fun decreaseCountValue() {

        if ((count.value ?: 0) >= steps) {
            count.value = count.value?.minus(steps)
        } else {
            count.value = 0
        }

        if (counterNameSelected.value != TEMPORARY_COUNTER) {
            viewModelScope.launch {
                database.counterDao.updateCount(
                    count.value.toString().toInt(),
                    counterNameSelected.value.toString()
                )
            }
        }

    }


    fun saveTag(tagName: String, count: Int, counterName: String) {

        if (tagName.isNotEmpty() && counterName != TEMPORARY_COUNTER) {
            viewModelScope.launch {
                database.tagDao.insertTag(Tags(tagName, count, counterName))
            }
        }

    }

    fun increaseTagCount(counterName: String) {
        viewModelScope.launch {
            database.counterDao.increaseTagCount(counterName)
        }

    }

    fun saveCounter(counterName: String) {

        if (counterName.isNotEmpty()) {

            viewModelScope.launch {
                val tagsCount = database.tagDao.getTagsCount(counterName)
                database.counterDao.insertCounter(
                    Counter(
                        counterName,
                        count.value.toString().toInt(),
                        tagsCount
                    )
                )
            }

            counterNameSelected.value = counterName

        }

    }

    fun setNewCounterOnScreen(counterName: String) {
        counterNameSelected.value = counterName
        count.value = 0
    }

    fun setCounterValuesOnScreen(counterName: String) {
        viewModelScope.launch {
            counterNameSelected.value = counterName
            count.value = database.counterDao.getCountValue(counterName)
        }
    }


    fun setCounterCount(countValue: Int) {

        viewModelScope.launch {
            database.counterDao.setCountValue(countValue, counterNameSelected.value.toString())
            count.value = countValue
        }

    }

    fun clearTags() {

        viewModelScope.launch {
            database.tagDao.clearTags(counterNameSelected.value.toString())
            val counter = database.counterDao.getCounterByName(counterNameSelected.value.toString())
            database.counterDao.updateCounter(Counter(counter.name, counter.count, 0, counter.id))
        }

    }

    fun deleteCounter(counter: Counter) {

        viewModelScope.launch {
            database.counterDao.deleteCounter(counter)
        }

        viewModelScope.launch {
            database.tagDao.deleteTagByCounter(counter.name)
        }

        if (counter.name == counterNameSelected.value.toString()) {
            counterNameSelected.value = TEMPORARY_COUNTER
            count.value = 0
        }

    }

    fun updateCounter(oldCounter: Counter, newCounter: Counter) {

        viewModelScope.launch {
            database.counterDao.updateCounter(newCounter)
        }

        viewModelScope.launch {
            database.tagDao.updateAllTagsByCounter(oldCounter.name, newCounter.name)
        }

        if (oldCounter.name == counterNameSelected.value) {
            counterNameSelected.value = newCounter.name
        }

    }

    fun deleteTag(tag: Tags) {
        viewModelScope.launch {
            database.tagDao.deleteTag(tag)
        }
    }

    fun updateTag(tag: Tags) {
        viewModelScope.launch {
            database.tagDao.updateTag(tag)
        }
    }

    fun checkCounterAvailability(newCounterName: String): Boolean {
        return database.counterDao.getCounterByName(newCounterName)?.toString().isNullOrBlank()
    }

}


