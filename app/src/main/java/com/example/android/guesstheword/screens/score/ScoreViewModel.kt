package com.example.android.guesstheword.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class ScoreViewModel(finalScore: Int) : ViewModel() {
    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    init {
        Timber.i("Final score is $finalScore")
        _eventPlayAgain.value = false
        _score.value = finalScore
    }

    fun onTryAgain() {
        _eventPlayAgain.value = true
    }

    fun onScoreObservingComplete() {
        _eventPlayAgain.value = false
    }
}