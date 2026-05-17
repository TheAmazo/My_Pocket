package com.thisara.mypocket.data

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.random.Random

object SavingsBoardGenerator {
    const val DEFAULT_CELL_COUNT = 30

    private val weightedAmounts = listOf(
        20 to 23,
        50 to 21,
        100 to 25,
        500 to 25,
        1000 to 6,
    )

    private val dailyOpenWeightedAmounts = listOf(
        20 to 34,
        50 to 30,
        100 to 23,
        500 to 10,
        1000 to 3,
    )

    val allowedAmounts: Set<Int> = weightedAmounts.map { it.first }.toSet()

    fun generate(
        monthKey: String,
        pocketId: String,
        round: Int = 0,
        count: Int = DEFAULT_CELL_COUNT,
    ): List<Int> {
        require(count > 0) { "Board must contain at least one cell." }

        val totalWeight = weightedAmounts.sumOf { it.second }
        val random = Random("$monthKey:$pocketId:$round".hashCode())

        return List(count) {
            val pick = random.nextInt(totalWeight)
            var running = 0
            weightedAmounts.first { (_, weight) ->
                running += weight
                pick < running
            }.first
        }
    }

    fun dailyOpenAmount(
        monthKey: String,
        pocketId: String,
        dayKey: String,
        cellIndex: Int,
        round: Int = cellIndex / DEFAULT_CELL_COUNT,
    ): Int {
        require(cellIndex >= 0) { "Cell index must be zero or greater." }

        val totalWeight = dailyOpenWeightedAmounts.sumOf { it.second }
        val random = Random("$monthKey:$pocketId:$dayKey:$round:$cellIndex".hashCode())
        val pick = random.nextInt(totalWeight)
        var running = 0
        return dailyOpenWeightedAmounts.first { (_, weight) ->
            running += weight
            pick < running
        }.first
    }

    fun dailyOpenAmountsForOpenCells(
        cells: List<SavingsCell>,
        monthKey: String,
        pocketId: String,
        dayKey: String,
    ): Map<String, Int> {
        return cells
            .filterNot { it.saved }
            .associate { cell ->
                cell.id to dailyOpenAmount(
                    monthKey = monthKey,
                    pocketId = pocketId,
                    dayKey = dayKey,
                    cellIndex = cell.index,
                )
            }
    }

    fun currentMonthKey(zoneId: ZoneId = ZoneId.systemDefault()): String {
        return YearMonth.now(zoneId).toString()
    }

    fun todayKey(zoneId: ZoneId = ZoneId.systemDefault()): String {
        return LocalDate.now(zoneId).toString()
    }

    fun currentYear(zoneId: ZoneId = ZoneId.systemDefault()): Int {
        return YearMonth.now(zoneId).year
    }

    fun isBeforeMonthEnd(monthKey: String, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        val month = YearMonth.parse(monthKey)
        return LocalDate.now(zoneId).dayOfMonth < month.lengthOfMonth()
    }
}
