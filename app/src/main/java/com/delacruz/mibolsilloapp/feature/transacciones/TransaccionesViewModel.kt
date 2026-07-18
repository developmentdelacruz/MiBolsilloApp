package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.GastoCompartido
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.PatrimonioSnapshot
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.SugerenciaRecurrencia
import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import com.delacruz.mibolsilloapp.domain.repository.GastoCompartidoRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import com.delacruz.mibolsilloapp.domain.repository.PatrimonioSnapshotRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import com.delacruz.mibolsilloapp.domain.usecase.CalcularDisponibleParaGastarUseCase
import com.delacruz.mibolsilloapp.domain.usecase.CalcularPatrimonioNetoUseCase
import com.delacruz.mibolsilloapp.domain.usecase.DetectarTransaccionesRecurrentesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TransaccionesViewModel @Inject constructor(
    private val repository: TransaccionRepository,
    categoriaRepository: CategoriaRepository,
    negocioRepository: NegocioRepository,
    proyectoRepository: ProyectoRepository,
    cuentaRepository: CuentaRepository,
    monedaRepository: MonedaRepository,
    private val suscripcionRepository: SuscripcionRepository,
    patrimonioSnapshotRepository: PatrimonioSnapshotRepository,
    presupuestoRepository: PresupuestoRepository,
    private val gastoCompartidoRepository: GastoCompartidoRepository,
    calcularDisponibleParaGastarUseCase: CalcularDisponibleParaGastarUseCase,
    detectarRecurrenciaUseCase: DetectarTransaccionesRecurrentesUseCase,
    calcularPatrimonioNetoUseCase: CalcularPatrimonioNetoUseCase,
) : ViewModel() {

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val transacciones: StateFlow<List<Transaccion>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categorias: StateFlow<List<Categoria>> = categoriaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val negocios: StateFlow<List<Negocio>> = negocioRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val proyectos: StateFlow<List<Proyecto>> = proyectoRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cuentas: StateFlow<List<Cuenta>> = cuentaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val balanceTotal: StateFlow<BigDecimal> = cuentaRepository.observarSaldoTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BigDecimal.ZERO)

    val disponibleParaGastar: StateFlow<BigDecimal> = calcularDisponibleParaGastarUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BigDecimal.ZERO)

    val patrimonioNeto: StateFlow<BigDecimal> = calcularPatrimonioNetoUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BigDecimal.ZERO)

    val historialPatrimonio: StateFlow<List<PatrimonioSnapshot>> = patrimonioSnapshotRepository
        .observarUltimos(30)
        .map { it.sortedBy { snapshot -> snapshot.fecha } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val presupuestosActivos: StateFlow<List<PresupuestoConConsumo>> = presupuestoRepository.observarTodosConConsumo()
        .map { lista -> lista.filter { it.presupuesto.activo } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Descarte en memoria, no persistido: si se reinicia la app, la sugerencia puede reaparecer. */
    private val sugerenciasDescartadas = MutableStateFlow<Set<Pair<Long, Long>>>(emptySet())

    val sugerenciasRecurrencia: StateFlow<List<SugerenciaRecurrencia>> = combine(
        repository.observarTodas().map { detectarRecurrenciaUseCase() },
        sugerenciasDescartadas,
    ) { sugerencias, descartadas ->
        sugerencias.filter { (it.negocioId to it.categoriaId) !in descartadas }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun descartarSugerencia(sugerencia: SugerenciaRecurrencia) {
        sugerenciasDescartadas.update { it + (sugerencia.negocioId to sugerencia.categoriaId) }
    }

    fun convertirEnSuscripcion(sugerencia: SugerenciaRecurrencia) {
        viewModelScope.launch {
            val nombre = negocios.value.firstOrNull { it.id == sugerencia.negocioId }?.nombre
                ?: "Suscripción"
            suscripcionRepository.crear(
                Suscripcion(
                    nombre = nombre,
                    montoMensual = sugerencia.montoPromedio,
                    diaCobro = sugerencia.ultimaFecha.dayOfMonth,
                    categoriaId = sugerencia.categoriaId,
                ),
            )
            descartarSugerencia(sugerencia)
        }
    }

    fun crear(
        descripcion: String,
        monto: BigDecimal,
        fecha: LocalDate,
        tipo: TipoTransaccion,
        categoriaId: Long,
        cuentaId: Long,
        negocioId: Long?,
        proyectoId: Long?,
        participantes: List<Triple<String, String, BigDecimal>> = emptyList(),
    ) {
        viewModelScope.launch {
            val id = repository.crear(
                Transaccion(
                    descripcion = descripcion,
                    monto = monto,
                    fecha = fecha,
                    tipo = tipo,
                    categoriaId = categoriaId,
                    cuentaId = cuentaId,
                    negocioId = negocioId,
                    proyectoId = proyectoId,
                ),
            )
            guardarParticipantes(id, participantes)
        }
    }

    fun actualizar(transaccion: Transaccion, participantes: List<Triple<String, String, BigDecimal>>? = null) {
        viewModelScope.launch {
            repository.actualizar(transaccion)
            if (participantes != null) {
                // Se borran y reinsertan en vez de diffear contra lo existente: el volumen de
                // participantes por transacción es chico (típicamente 1-3) y esto evita lidiar
                // con altas/bajas/ediciones parciales para un dato de bajo riesgo si se rehace.
                gastoCompartidoRepository.observarPorTransaccion(transaccion.id).first()
                    .forEach { gastoCompartidoRepository.eliminar(it) }
                guardarParticipantes(transaccion.id, participantes)
            }
        }
    }

    fun eliminar(transaccion: Transaccion) {
        viewModelScope.launch { repository.eliminar(transaccion) }
    }

    fun participantesDe(transaccionId: Long): Flow<List<GastoCompartido>> =
        gastoCompartidoRepository.observarPorTransaccion(transaccionId)

    private suspend fun guardarParticipantes(transaccionId: Long, participantes: List<Triple<String, String, BigDecimal>>) {
        participantes.forEach { (nombreContacto, telefono, montoAPagar) ->
            gastoCompartidoRepository.agregar(
                GastoCompartido(
                    transaccionId = transaccionId,
                    nombreContacto = nombreContacto,
                    telefono = telefono,
                    montoAPagar = montoAPagar,
                ),
            )
        }
    }
}
