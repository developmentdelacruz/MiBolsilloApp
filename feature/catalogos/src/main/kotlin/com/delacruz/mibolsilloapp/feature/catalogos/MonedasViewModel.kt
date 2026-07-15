package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MonedasViewModel @Inject constructor(
    private val repository: MonedaRepository,
) : ViewModel() {

    val monedas: StateFlow<List<Moneda>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crear(codigo: String, nombre: String, simbolo: String, esPredeterminada: Boolean) {
        viewModelScope.launch {
            repository.crear(
                Moneda(codigo = codigo, nombre = nombre, simbolo = simbolo, esPredeterminada = esPredeterminada),
            )
        }
    }

    fun eliminar(moneda: Moneda) {
        viewModelScope.launch { repository.eliminar(moneda) }
    }
}
