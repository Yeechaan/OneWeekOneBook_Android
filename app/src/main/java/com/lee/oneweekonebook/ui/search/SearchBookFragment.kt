package com.lee.oneweekonebook.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.lee.oneweekonebook.R
import com.lee.oneweekonebook.databinding.FragmentSearchBookBinding
import com.lee.oneweekonebook.ui.BOTTOM_MENU_SEARCH
import com.lee.oneweekonebook.ui.MainActivity
import com.lee.oneweekonebook.ui.home.PREVIOUS_ADD
import com.lee.oneweekonebook.ui.search.viewmodel.SearchBookViewModel
import com.lee.oneweekonebook.utils.isNetworkConnected
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchBookFragment : Fragment() {

    private val args: SearchBookFragmentArgs by navArgs()
    var binding: FragmentSearchBookBinding? = null
    lateinit var inputMethodManager: InputMethodManager
    private val searchBookViewModel by viewModels<SearchBookViewModel>()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).setBottomNavigationStatus(BOTTOM_MENU_SEARCH)

        binding = FragmentSearchBookBinding.inflate(inflater, container, false)
            .apply {
                inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                lifecycleOwner = this@SearchBookFragment
                viewModel = searchBookViewModel

                if (args.previous == PREVIOUS_ADD) {
                    showKeyboard()
                }

                editTextSearchBook.setOnEditorActionListener { textView, action, _ ->
                    when (action) {
                        EditorInfo.IME_ACTION_SEARCH -> {
                            val isTitleEmpty = textView.text.isNullOrEmpty()
                            val isNetworkConnected = isNetworkConnected(requireContext())

                            when {
                                isNetworkConnected && !isTitleEmpty -> {
                                    searchBookViewModel.searchBook(editTextSearchBook.text.toString())
                                }
                                isTitleEmpty -> {
                                    Toast.makeText(requireContext(), getString(R.string.search_book_title_empty), Toast.LENGTH_SHORT).show()
                                }
                                !isNetworkConnected -> {
                                    Toast.makeText(requireContext(), getString(R.string.error_network_not_connected), Toast.LENGTH_SHORT).show()
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }

                val searchBookAdapter = SearchBookAdapter(SearchBookListener { book ->
                    findNavController().navigate(SearchBookFragmentDirections.actionSearchBookFragmentToBookDetailFragment(book = book))
                })
                recyclerViewSearchBook.apply {
                    adapter = searchBookAdapter
                    addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                }

                searchBookViewModel.books.observe(viewLifecycleOwner, {
                    searchBookAdapter.data = it

                    if (it.isEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.search_book_result_empty), Toast.LENGTH_SHORT).show()
                    }
                })
            }

        return binding?.root
    }

    private fun showKeyboard() {
        binding?.editTextSearchBook?.requestFocus()
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

}