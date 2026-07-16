package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NegociosViewModel @Inject constructor(
    private val repository: NegocioRepository,
) : ViewModel() {

    val negocios: StateFlow<List<Negocio>> = repository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crear(nombre: String) {
        viewModelScope.launch { repository.crear(Negocio(nombre = nombre)) }
    }
}
