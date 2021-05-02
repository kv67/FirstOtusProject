package kve.ru.firstproject.db

import androidx.room.*

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: Film)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilms(films: List<Film>)

    @Update
    fun updateFilm(film: Film)

    @Query("SELECT * FROM film ORDER BY popularity DESC")
    fun getAll(): List<Film>?

    @Query("SELECT * FROM film WHERE is_favorite = 1 ORDER BY popularity DESC")
    fun getFavorites(): List<Film>?

    @Query("SELECT * FROM film WHERE id = :id")
    fun getById(id: Int): Film?

    @Query("DELETE FROM film")
    fun deleteAll()

    @Query("UPDATE film SET is_favorite = 1 WHERE id = :id")
    fun addToFavorite(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)

    @Update
    fun updateNotification(notification: Notification)

    @Query("SELECT * FROM notification ORDER BY notification_dt")
    fun getAllNotifications(): List<Notification>?

    @Query("SELECT * FROM notification WHERE id = :id")
    fun getNotificationById(id: Int): Notification?

    @Query("DELETE FROM notification WHERE id = :id")
    fun deleteNotificationById(id: Int)
}