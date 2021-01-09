package com.lee.oneweekonebook.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.oneweekonebook.database.BookDatabaseDao

class HomeViewModel(bookDao: BookDatabaseDao) : ViewModel() {

    val books = bookDao.getAllBooks()

}

class HomeViewModelFactory(
        private val bookDatabaseDao: BookDatabaseDao,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(bookDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}