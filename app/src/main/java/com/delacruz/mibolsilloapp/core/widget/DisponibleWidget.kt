package com.delacruz.mibolsilloapp.core.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.delacruz.mibolsilloapp.MainActivity
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

/**
 * Corre en el proceso del launcher (no en el de MainActivity), así que no hay hiltViewModel()
 * ni Activity — se resuelve la data una sola vez por refresco vía DisponibleWidgetEntryPoint
 * y se dibuja con la API de Glance (no es Jetpack Compose normal, es un subconjunto propio).
 */
class DisponibleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, DisponibleWidgetEntryPoint::class.java)
        val disponible = entryPoint.calcularDisponibleParaGastarUseCase()().first()
        val simbolo = entryPoint.monedaRepository().observarPredeterminada().first()?.simbolo ?: ""

        provideContent {
            ContenidoWidget(texto = disponible.formatearMonto(simbolo), context = context)
        }
    }

    @Composable
    private fun ContenidoWidget(texto: String, context: Context) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF1B1B1F)))
                .padding(12.dp)
                .clickable(actionStartActivity(ComponentName(context, MainActivity::class.java))),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Text(
                text = "Disponible para gastar",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFC9C5D0)),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                ),
            )
            Text(
                text = texto,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}
