package com.lee.oneweekonebook.ui.done

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.lee.oneweekonebook.R
import com.lee.oneweekonebook.databinding.FragmentDoneBookDetailBinding
import com.lee.oneweekonebook.ui.MainActivity
import com.lee.oneweekonebook.ui.NoBottomNavigationToolbarIconFragment
import com.lee.oneweekonebook.ui.done.viewmodel.DoneBookDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoneBookDetailFragment : NoBottomNavigationToolbarIconFragment() {

    private val doneBookDetailViewModel by viewModels<DoneBookDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDoneBookDetailBinding.inflate(inflater, container, false)
            .apply {
                viewModel = doneBookDetailViewModel
                lifecycleOwner = this@DoneBookDetailFragment

                buttonSaveBook.setOnClickListener {
                    val contents = editTextContents.text.toString()
                    val review = editTextReview.text.toString()

                    doneBookDetailViewModel.saveReadingBook(contents = contents, review = review)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.reading_save),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                buttonContents.setOnClickListener {
                    doneBookDetailViewModel.setCurrentPage(isContentsPage = true)
                }

                buttonReview.setOnClickListener {
                    doneBookDetailViewModel.setCurrentPage(isContentsPage = false)
                }

                doneBookDetailViewModel.book.observe(viewLifecycleOwner, {
                    (activity as MainActivity).setToolbarTitle(it.title)

                    if (it.coverImage.isNotEmpty()) {
                        Glide.with(root.context).load(it.coverImage).into(layoutBook.imageViewBook)
                    } else {
                        Glide.with(root.context).load(R.drawable.ic_baseline_menu_book)
                            .into(layoutBook.imageViewBook)
                    }

                    editTextContents.setText(it.contents)
                    editTextContents.setSelection(it.contents.length)

                    editTextReview.setText(it.review)
                    editTextReview.setSelection(it.review.length)
                })

                doneBookDetailViewModel.isContentsPage.observe(viewLifecycleOwner, {
                    if (it) {
                        editTextContents.requestFocus()
                    } else {
                        editTextReview.requestFocus()
                    }
                })
            }

        return binding.root
    }

}