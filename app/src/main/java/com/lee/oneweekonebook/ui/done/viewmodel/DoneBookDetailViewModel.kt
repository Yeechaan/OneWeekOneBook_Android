package com.lee.oneweekonebook.ui.done.viewmodel

import androidx.lifecycle.*
import com.lee.oneweekonebook.database.BookDatabaseDao
import com.lee.oneweekonebook.database.model.Book
import com.lee.oneweekonebook.utils.DateUtils
import com.lee.oneweekonebook.utils.ioDispatcher
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoneBookDetailViewModel(val bookDao: BookDatabaseDao, val bookId: Int) : ViewModel() {

    val book = bookDao.getBookAsync(bookId)

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
                bookDao.update(it)
            }
        }
    }

    fun setCurrentPage(isContentsPage: Boolean) = run {
        _isContentsPage.value = isContentsPage
    }

}

class DoneBookDetailViewModelFactory(
    private val bookDatabaseDao: BookDatabaseDao,
    private val bookId: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoneBookDetailViewModel::class.java)) {
            return DoneBookDetailViewModel(bookDatabaseDao, bookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}