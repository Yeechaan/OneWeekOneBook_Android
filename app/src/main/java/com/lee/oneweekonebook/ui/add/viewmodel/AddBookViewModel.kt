package com.lee.oneweekonebook.ui.add.viewmodel

import androidx.lifecycle.*
import com.lee.oneweekonebook.database.BookDatabaseDao
import com.lee.oneweekonebook.database.model.Book
import com.lee.oneweekonebook.utils.ioDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddBookViewModel(private val bookDao: BookDatabaseDao) : ViewModel() {

    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book>
        get() = _book

    fun getBook(bookId: Int) {
        viewModelScope.launch(ioDispatcher) {
            val currentBook = bookDao.getBook(bookId)
            _book.postValue(currentBook)
        }
    }

    fun saveBook(book: Book) {
        viewModelScope.launch(ioDispatcher) {
                bookDao.insert(book)
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch(ioDispatcher) {
            bookDao.update(book)
        }
    }

}

class AddBookViewModelFactory(
    private val bookDatabaseDao: BookDatabaseDao,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddBookViewModel::class.java)) {
            return AddBookViewModel(bookDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}