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

    @Query("UPDATE film SET comment = :comment where id = :id")
    fun setCommentById(id: Long, comment: String)

    @Query("UPDATE film SET is_ok = :ok where id = :id")
    fun setOkById(id: Long, ok: Int)

    @Query("UPDATE film SET is_favorite = :isFavorite where id = :id")
    fun setFavoriteById(id: Long, isFavorite: Int)

    @Query("UPDATE film SET film_name = :name, description = :dsc, poster_path = :path, big_poster_path = :bigPath, popularity = :popularity where id = :id")
    fun updateDataById(
        id: Int,
        name: String?,
        dsc: String?,
        path: String?,
        bigPath: String?,
        popularity: Int?
    )
}