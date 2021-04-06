package com.lee.oneweekonebook.ui.wish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.lee.oneweekonebook.R
import com.lee.oneweekonebook.database.BookDatabase
import com.lee.oneweekonebook.database.model.Book
import com.lee.oneweekonebook.databinding.FragmentWishBookBinding
import com.lee.oneweekonebook.ui.book.BookAdapter
import com.lee.oneweekonebook.ui.book.BookListener
import com.lee.oneweekonebook.ui.book.BookMoreListener
import com.lee.oneweekonebook.ui.history.HistoryFragmentDirections
import com.lee.oneweekonebook.ui.wish.viewmodel.WishBookViewModel
import com.lee.oneweekonebook.ui.wish.viewmodel.WishBookViewModelFactory

class WishBookFragment : Fragment() {

    var binding: FragmentWishBookBinding? = null
    private lateinit var viewModel: WishBookViewModel

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val application = requireNotNull(this.activity).application
        val bookDao = BookDatabase.getInstance(application).bookDatabaseDao

        val viewModelFactory = WishBookViewModelFactory(bookDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WishBookViewModel::class.java)

        binding = FragmentWishBookBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@WishBookFragment

            val bookAdapter = BookAdapter(
                BookListener { book ->
                    // 책 읽기 시작 TODO dialog 창으로 한번 더 확인
                    viewModel.addReadingBook(bookId = book.id)
                    Toast.makeText(requireContext(), getString(R.string.book_list_reading_add), Toast.LENGTH_SHORT).show()
                },
                BookMoreListener { view, bookId ->
                    val popupMenu = PopupMenu(requireContext(), view)
                    setPopupBookSelection(popupMenu, bookId)
                }
            )
            recyclerViewWishBook.apply {
                adapter = bookAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }

            viewModel.books.observe(viewLifecycleOwner, {
                (recyclerViewWishBook.adapter as BookAdapter).submitList(it)
            })

        }

        return binding?.root
    }

    private fun setPopupBookSelection(popupMenu: PopupMenu, bookId: Int) {
        popupMenu.menuInflater.inflate(R.menu.option_type, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit -> {
                    // 수정
                    findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToEditBookFragment(bookId = bookId))
                }
                R.id.menu_delete -> {
                    // 삭제
                    viewModel.deleteBook(bookId = bookId)
                    Toast.makeText(requireContext(), getString(R.string.book_list_delete), Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popupMenu.show()
    }
}