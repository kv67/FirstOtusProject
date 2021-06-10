package kve.ru.firstproject.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kve.ru.firstproject.repositories.FilmRepository

class FilmViewModelFactory(private val filmRepository: FilmRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilmViewModel(filmRepository) as T
    }
}