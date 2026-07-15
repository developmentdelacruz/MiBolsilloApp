package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.CategoriaEntity
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(categoria: CategoriaEntity): Long

    @Update
    suspend fun update(categoria: CategoriaEntity)

    @Delete
    suspend fun delete(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias WHERE id = :id")
    fun observeById(id: Long): Flow<CategoriaEntity?>

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun observeAll(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE tipo = :tipo ORDER BY nombre ASC")
    fun observeByTipo(tipo: TipoCategoria): Flow<List<CategoriaEntity>>
}
