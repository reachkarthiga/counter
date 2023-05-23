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

    private val database = Database.getInstance(application.applicationContext)

    private val sharedPreferences = application.getSharedPreferences(SETTINGS , Context.MODE_PRIVATE)

     var steps: Int = sharedPreferences.getInt(STEP_VALUE, 1)
     var tapToIncrease :Boolean = sharedPreferences.getBoolean(TAP, false)
     var playSound :Boolean = sharedPreferences.getBoolean(SOUND, false)

    val tagList: LiveData<List<Tags>>
        get() = Transformations.switchMap(counterName) {
            database.tagDao.getTagsByName(it)
        }

    val counterList: LiveData<List<Counter>>
        get() = database.counterDao.getCounters()

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

            val counter :Counter = database.counterDao.getCounterByName(TEMPORARY_COUNTER)

            if (counter == null) {
                database.counterDao.insertCounter(
                    Counter(
                        TEMPORARY_COUNTER,
                        0,
                        0
                    )
                )

                count.value = 0

            } else {
                count.value = counter.count
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

            if ((count.value ?: 0) < 0) {
                count.value = 0
            }

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

                val counter = database.counterDao.getCounterByName(TEMPORARY_COUNTER)

                database.counterDao.deleteCounter(counter)

            }

            viewModelScope.launch {
                database.tagDao.updateTags(_counterName.value.toString(), counterName)
            }

            _counterName.value = counterName

        }

    }

    override fun onCleared() {

        viewModelScope.launch {
            database.tagDao.clearTags(TEMPORARY_COUNTER)
            val counter = database.counterDao.getCounterByName(TEMPORARY_COUNTER)
            database.counterDao.deleteCounter(counter)
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

    fun setNewCounterOnScreen(counterName: String) {
        _counterName.value = counterName
        count.value = 0
    }

    fun setCounterValuesOnScreen(counterName: String) {
        viewModelScope.launch {
            _counterName.value = counterName
            count.value = database.counterDao.getCountValue(counterName)
        }
    }


    fun setCounterCount(countValue:Int) {

        viewModelScope.launch {
            database.counterDao.setCountValue(countValue, counterName.value.toString())
            count.value = countValue
        }

    }

    fun clearTags() {

        viewModelScope.launch {
            database.tagDao.clearTags(counterName.value.toString())

            val counter = database.counterDao.getCounterByName(counterName.value.toString())
            database.counterDao.updateCounter(Counter(counter.name, counter.count, 0, counter.id))

        }

    }

}


