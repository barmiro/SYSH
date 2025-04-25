package com.github.barmiro.syshclient.presentation.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.data.stats.HourlyStatsDTO
import com.github.barmiro.syshclient.data.stats.StatsSeriesChunkDTO
import com.github.barmiro.syshclient.util.averageStreamLengthInterpolator
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.PointConnector
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun StreamingSumChart(statsSeries: List<StatsSeriesChunkDTO>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var filteredStats = statsSeries.filter { it.start_date?.isBefore(OffsetDateTime.now()) ?: false }
    LaunchedEffect(statsSeries) {
        filteredStats = statsSeries.filter { it.start_date?.isBefore(OffsetDateTime.now()) ?: false }
        if (filteredStats.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(
                        y = listOf(0) + filteredStats.let { list ->
                            list.map { sample ->
                                var sum: Int = 0
                                for (i in 0..list.indexOf(sample)) {
                                    sum += list[i].minutes_streamed
                                }
                                sum
                            }
                        }
                    )
                    series(
                        y = listOf(0) + filteredStats.let { list ->
                            list.map { sample ->
                                var sum: Int = 0
                                for (i in 0..list.indexOf(sample)) {
                                    sum += list[i].stream_count
                                }
                                sum
                            }
                        }
                    )
                }
            }
        }
    }
    StreamingStatsChartScaffold(modelProducer, filteredStats, "Streamed in total")
}


@Composable
fun StreamingValuesChart(statsSeries: List<StatsSeriesChunkDTO>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var filteredStats = statsSeries.filter { it.start_date?.isBefore(OffsetDateTime.now()) ?: false }
    LaunchedEffect(statsSeries) {
        filteredStats = statsSeries.filter { it.start_date?.isBefore(OffsetDateTime.now()) ?: false }
        if (filteredStats.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(
                        y = filteredStats.let { list ->
                            list.map { sample ->
                                sample.minutes_streamed
                            }
                        }
                    )
                    series(
                        y = filteredStats.let { list ->
                            list.map { sample ->
                                sample.stream_count
                            }
                        }
                    )
                }
            }
        }
    }
    StreamingStatsChartScaffold(modelProducer, filteredStats, "Streamed by period")
}


@Composable
fun StreamingStatsChartScaffold(
    modelProducer: CartesianChartModelProducer,
    statsSeries: List<StatsSeriesChunkDTO>,
    topText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Column() {
                Text(
                    text = topText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )

            }
        }
        Row {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider =  LineCartesianLayer.LineProvider
                            .series(vicoTheme.lineCartesianLayerColors.map { color ->
                                LineCartesianLayer.rememberLine(
                                    areaFill = LineCartesianLayer.AreaFill.single(
                                        fill(color.copy(alpha = 0.2f))
                                    ),
                                    fill = LineCartesianLayer.LineFill.single(fill(color)),
                                    pointConnector = PointConnector.Sharp
                                )
                            })
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        itemPlacer = remember { VerticalAxis.ItemPlacer.count(count = { 6 }) }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = {
                            statsSeries.size / 6 + 1
                        }, addExtremeLabelPadding = false),
                        valueFormatter = { _, value, _ ->
                            val index = value.toInt() - 1
                            if (index == -1) {
                                statsSeries[0].start_date?.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                                    ?: "."
                            } else if (statsSeries.isNotEmpty() && index in statsSeries.indices) {
                                statsSeries[value.toInt() - 1].end_date?.format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy-MM"
                                    )
                                ) ?: "."
                            } else {
                                "."
                            }
                        }
                    ),
                ),
                modelProducer = modelProducer,
                scrollState = rememberVicoScrollState(scrollEnabled = false)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Icon(
                    painter = painterResource(id = R.drawable.horizontal_rule_16dp),
                    tint = vicoTheme.lineCartesianLayerColors[0],
                    contentDescription = "Line"
                )
            }
            Column {
                Text(
                    text = "Minutes",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.width(24.dp))
            Column {
                Icon(
                    painter = painterResource(id = R.drawable.horizontal_rule_16dp),
                    tint = vicoTheme.lineCartesianLayerColors[1],
                    contentDescription = "Line"
                )
            }
            Column {
                Text(
                    text = "Streams",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HourlyStatsChart(hourlyStats: List<HourlyStatsDTO>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(hourlyStats) {
        if (hourlyStats.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(
                        y = hourlyStats.map { item -> item.minutes_streamed }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Column() {
                Text(
                    text = "Minutes streamed by time of day",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )

            }
        }
        Row {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider
                            .series(vicoTheme.lineCartesianLayerColors.map { color ->
                                LineCartesianLayer.rememberLine(
                                    areaFill = LineCartesianLayer.AreaFill.single(
                                        fill(color.copy(alpha = 0.2f))
                                    ),
                                    fill = LineCartesianLayer.LineFill.single(fill(color)),
                                    pointConnector = PointConnector.Sharp
                                )
                            })
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        itemPlacer = remember { VerticalAxis.ItemPlacer.count(count = { 6 }) }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                            spacing = { 12 },
                            addExtremeLabelPadding = false),
                        valueFormatter = { _, value, _ -> (value / 4).toInt().toString() + ":00" }
//                        valueFormatter = { _, value, _ ->
//                            val index = value.toInt() - 1
//                            if (index == -1) {
//                                statsSeries[0].start_date?.format(DateTimeFormatter.ofPattern("yyyy-MM"))
//                                    ?: "."
//                            } else if (statsSeries.isNotEmpty() && index in statsSeries.indices) {
//                                statsSeries[value.toInt() - 1].end_date?.format(
//                                    DateTimeFormatter.ofPattern(
//                                        "yyyy-MM"
//                                    )
//                                ) ?: "."
//                            } else {
//                                "."
//                            }
//                        }
                    ),
                ),
                modelProducer = modelProducer,
                scrollState = rememberVicoScrollState(scrollEnabled = false)
            )
        }
    }
}

@Composable
fun AverageStreamLengthChart(statsSeries: List<StatsSeriesChunkDTO>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var filteredStats = averageStreamLengthInterpolator(
        statsSeries.filter {
            it.start_date?.isBefore(OffsetDateTime.now()) ?: false
        }
    )

    LaunchedEffect(statsSeries) {
        filteredStats = averageStreamLengthInterpolator(
            statsSeries.filter {
                it.start_date?.isBefore(OffsetDateTime.now()) ?: false
            }
        )
        if (filteredStats.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(
                        y = filteredStats.let { list ->
                            list.map { sample ->
                                if (sample.stream_count > 0) {
                                    1f * sample.minutes_streamed / sample.stream_count
                                } else 0
                            }
                        }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Column() {
                Text(
                    text = "Average stream duration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )

            }
        }

        val color = vicoTheme.lineCartesianLayerColors[2]

        val validValues = filteredStats.mapNotNull { sample ->
            if (sample.stream_count > 0) {
                1.0 * sample.minutes_streamed / sample.stream_count
            } else null
        }.filter { it > 0 }

        var minY: Double? = null
        var maxY: Double? = null

        validValues.takeIf {
            it.isNotEmpty()
        }?.let { list ->
            minY = list.minOrNull()
                ?.let { floor(it * 0.9 * 2) / 2 }
            maxY = list.maxOrNull()
                ?.let { ceil(it * 1.1 * 2) / 2 }
        }

        Row {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider
                            .series(
                                LineCartesianLayer.rememberLine(
                                    areaFill = LineCartesianLayer.AreaFill.single(
                                        fill(color.copy(alpha = 0.2f))
                                    ),
                                    fill = LineCartesianLayer.LineFill.single(fill(color)),
                                    pointConnector = PointConnector.Sharp
                                )
                            ),
                        rangeProvider = CartesianLayerRangeProvider.fixed(
                            minY = minY,
                            maxY = maxY
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        itemPlacer = remember {
                            VerticalAxis.ItemPlacer
                                .count(count = { 6 })

                        }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = {
                            filteredStats.size / 6 + 1
                        }, addExtremeLabelPadding = false),
                        valueFormatter = { _, value, _ ->
                            val index = value.toInt() - 1
                            if (index == -1) {
                                filteredStats[0].start_date?.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                                    ?: "."
                            } else if (filteredStats.isNotEmpty() && index in filteredStats.indices) {
                                filteredStats[value.toInt() - 1].end_date?.format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy-MM"
                                    )
                                ) ?: "."
                            } else {
                                "."
                            }
                        }
                    ),
                ),
                modelProducer = modelProducer,
                scrollState = rememberVicoScrollState(scrollEnabled = false)
            )
        }
    }
}