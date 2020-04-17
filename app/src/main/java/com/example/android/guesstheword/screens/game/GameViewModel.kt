package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.view.animation.Transformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import timber.log.Timber

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    private var _buzzEvent = MutableLiveData<BuzzType>()
    val buzzEvent: LiveData<BuzzType>
        get() = _buzzEvent

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L

        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L

        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L

        const val COUNTDOWN_PANIC = 5000L
    }

    private val timer: CountDownTimer
    private val _time = MutableLiveData<Long>()
    val time: LiveData<Long>
        get() = _time

    val timeString = Transformations.map(time, { time ->
        DateUtils.formatElapsedTime(time)
    })

    // The current word
    private val _word = MutableLiveData<String>()
    val word = _word as LiveData<String>

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        Timber.i("GameViewModel created")

        _eventGameFinished.value = false
        resetList()
        nextWord()

        _score.value = 0

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < COUNTDOWN_PANIC) {
                    _buzzEvent.value = BuzzType.COUNTDOWN_PANIC
                }
                _time.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _buzzEvent.value = BuzzType.GAME_OVER
                _eventGameFinished.value = true
            }
        }

        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Timber.i("GameViewModel destroyed")
    }


    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
//            gameFinished()
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }


    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)
        nextWord()
        _buzzEvent.value = BuzzType.CORRECT
    }

    fun onGameFinishComplete() {
        _eventGameFinished.value = false
    }

    fun buzzCompleted() {
        _buzzEvent.value = BuzzType.NO_BUZZ
    }
}