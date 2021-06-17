package kve.ru.firstproject.di


import dagger.Component
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.fragments.FavoriteListFragment
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.fragments.FilmListFragment
import kve.ru.firstproject.fragments.NotificationListFragment
import kve.ru.firstproject.model.FilmViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(filmListFragment: FilmListFragment)
    fun inject(favoriteListFragment: FavoriteListFragment)
    fun inject(filmDetailFragment: FilmDetailFragment)
    fun inject(notificationListFragment: NotificationListFragment)

    fun getFilmViewModel(): FilmViewModel
}