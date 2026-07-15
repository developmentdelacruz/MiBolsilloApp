package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    private val repository: CategoriaRepository,
) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crear(nombre: String, icono: String, tipo: TipoCategoria) {
        viewModelScope.launch { repository.crear(Categoria(nombre = nombre, icono = icono, tipo = tipo)) }
    }

    fun eliminar(categoria: Categoria) {
        viewModelScope.launch { repository.eliminar(categoria) }
    }
}
