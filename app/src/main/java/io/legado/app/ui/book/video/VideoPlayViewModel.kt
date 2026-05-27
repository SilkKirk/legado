package io.legado.app.ui.book.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.legado.app.R
import io.legado.app.data.appDb
import io.legado.app.data.entities.Book
import io.legado.app.data.entities.BookSource
import io.legado.app.help.book.delete
import io.legado.app.help.book.getBookSource
import io.legado.app.help.book.removeType
import io.legado.app.help.source.saveBookSource
import io.legado.app.model.VideoPlay
import io.legado.app.utils.getString
import kotlinx.coroutines.launch

class VideoPlayViewModel : ViewModel() {

    private val _titleData = MutableLiveData<String>()
    val titleData: LiveData<String> = _titleData

    private val _coverData = MutableLiveData<String?>()
    val coverData: LiveData<String?> = _coverData

    fun initData(intent: android.content.Intent, onInit: () -> Unit) {
        viewModelScope.launch {
            val bookUrl = intent.getStringExtra("bookUrl") ?: return@launch
            val book = appDb.bookDao.getBook(bookUrl) ?: return@launch
            VideoPlay.book = book
            VideoPlay.bookSource = book.getBookSource()
            VideoPlay.upData(book)
            _titleData.value = book.name
            _coverData.value = book.getDisplayCover()
            VideoPlay.resetData(book)
            onInit()
        }
    }

    fun upSource() {
        VideoPlay.book?.getBookSource()?.let {
            VideoPlay.bookSource = it
            _titleData.value = VideoPlay.book?.name
            _coverData.value = VideoPlay.book?.getDisplayCover()
        }
    }

    fun changeTo(source: BookSource, book: Book, toc: List<io.legado.app.data.entities.BookChapter>) {
        viewModelScope.launch {
            source.saveBookSource()
            VideoPlay.book?.delete()
            VideoPlay.book = book
            VideoPlay.bookSource = source
            VideoPlay.upData(book)
            _titleData.value = book.name
            _coverData.value = book.getDisplayCover()
        }
    }

    fun removeFromBookshelf(onSuccess: () -> Unit) {
        viewModelScope.launch {
            VideoPlay.book?.let { book ->
                book.removeType(io.legado.app.constant.BookType.notShelf)
                appDb.bookDao.delete(book)
            }
            onSuccess()
        }
    }
}
