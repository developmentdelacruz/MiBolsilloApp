package com.delacruz.mibolsilloapp.core.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.delacruz.mibolsilloapp.domain.repository.PatrimonioSnapshotRepository
import com.delacruz.mibolsilloapp.domain.usecase.CalcularPatrimonioNetoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import kotlinx.coroutines.flow.first

@HiltWorker
class RegistrarSnapshotPatrimonioWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val calcularPatrimonioNetoUseCase: CalcularPatrimonioNetoUseCase,
    private val patrimonioSnapshotRepository: PatrimonioSnapshotRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val patrimonioNeto = calcularPatrimonioNetoUseCase().first()
        patrimonioSnapshotRepository.registrar(LocalDate.now(), patrimonioNeto)
        return Result.success()
    }
}
