package com.lee.oneweekonebook.ui.reading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.oneweekonebook.database.model.BOOK_TYPE_READING
import com.lee.oneweekonebook.repo.BookRepository
import com.lee.oneweekonebook.utils.ioDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    val books = bookRepository.getAllBookByTypeAsync(BOOK_TYPE_READING)

    fun deleteBook(bookId: Int) {
        viewModelScope.launch(ioDispatcher) {
            bookRepository.deleteBookById(bookId)
        }
    }
}