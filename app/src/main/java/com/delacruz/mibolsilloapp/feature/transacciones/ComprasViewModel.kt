package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Compra
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.CompraRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import com.delacruz.mibolsilloapp.domain.usecase.EliminarCompraUseCase
import com.delacruz.mibolsilloapp.domain.usecase.GenerarCuotasCompraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ComprasViewModel @Inject constructor(
    private val repository: CompraRepository,
    categoriaRepository: CategoriaRepository,
    cuentaRepository: CuentaRepository,
    negocioRepository: NegocioRepository,
    monedaRepository: MonedaRepository,
    private val generarCuotasUseCase: GenerarCuotasCompraUseCase,
    private val eliminarCompraUseCase: EliminarCompraUseCase,
) : ViewModel() {

    val compras: StateFlow<List<Compra>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categoriasDeGasto: StateFlow<List<Categoria>> = categoriaRepository.observarTodas()
        .map { categorias -> categorias.filter { it.tipo == TipoCategoria.GASTO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cuentas: StateFlow<List<Cuenta>> = cuentaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val negocios: StateFlow<List<Negocio>> = negocioRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun crear(
        descripcion: String,
        montoTotal: BigDecimal,
        cuotasTotales: Int,
        categoriaId: Long,
        cuentaId: Long,
        negocioId: Long?,
        fechaPrimeraCuota: LocalDate,
    ) {
        viewModelScope.launch {
            generarCuotasUseCase(
                Compra(
                    descripcion = descripcion,
                    montoTotal = montoTotal,
                    cuotasTotales = cuotasTotales,
                    categoriaId = categoriaId,
                    cuentaId = cuentaId,
                    negocioId = negocioId,
                    fechaPrimeraCuota = fechaPrimeraCuota,
                ),
            )
        }
    }

    fun actualizar(compra: Compra) {
        viewModelScope.launch { repository.actualizar(compra) }
    }

    fun eliminar(compra: Compra) {
        viewModelScope.launch { eliminarCompraUseCase(compra) }
    }
}
