package com.lee.oneweekonebook.ui.done.viewmodel

import androidx.lifecycle.*
import com.lee.oneweekonebook.repo.BookRepository
import com.lee.oneweekonebook.utils.DateUtils
import com.lee.oneweekonebook.utils.ioDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoneBookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository
) : ViewModel() {

    val book = bookRepository.getBookByIdAsync(savedStateHandle["bookId"] ?: 0)

    private val _isContentsPage = MutableLiveData(true)
    val isContentsPage: LiveData<Boolean>
        get() = _isContentsPage

    val bookPeriodFormat = Transformations.map(book) { book ->
        DateUtils().formatBookPeriod(book.startDate, book.endDate)
    }

    fun saveReadingBook(contents: String, review: String) {
        viewModelScope.launch(ioDispatcher) {
            book.value?.let { it ->
                it.contents = contents
                it.review = review
                bookRepository.updateBook(it)
            }
        }
    }

    fun setCurrentPage(isContentsPage: Boolean) = run {
        _isContentsPage.value = isContentsPage
    }

}