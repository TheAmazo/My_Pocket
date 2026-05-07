package com.thisara.mypocket.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SavingsCellRulesTest {
    @Test
    fun unsavedCellsStayClickable() {
        val cell = SavingsCell(id = "00", index = 0, amount = 100)

        assertTrue(cell.canToggle("2026-05-03"))
    }

    @Test
    fun cellsSavedTodayCanStillBeEditedToday() {
        val cell = SavingsCell(
            id = "00",
            index = 0,
            amount = 100,
            saved = true,
            savedDayKey = "2026-05-03",
        )

        assertTrue(cell.canToggle("2026-05-03"))
    }

    @Test
    fun cellsSavedBeforeTodayAreLocked() {
        val cell = SavingsCell(
            id = "00",
            index = 0,
            amount = 100,
            saved = true,
            savedDayKey = "2026-05-02",
        )

        assertFalse(cell.canToggle("2026-05-03"))
    }

    @Test
    fun boardSortRankKeepsOpenCellsBeforeSavedCells() {
        val todayKey = "2026-05-03"
        val openCell = SavingsCell(id = "open", index = 2, amount = 20)
        val savedTodayCell = SavingsCell(
            id = "saved-today",
            index = 0,
            amount = 100,
            saved = true,
            savedDayKey = todayKey,
        )
        val lockedCell = SavingsCell(
            id = "locked",
            index = 1,
            amount = 50,
            saved = true,
            savedDayKey = "2026-05-02",
        )

        val sorted = listOf(savedTodayCell, lockedCell, openCell)
            .sortedWith(compareBy<SavingsCell> { it.sortRank(todayKey) }.thenBy { it.index })

        assertEquals(listOf("open", "saved-today", "locked"), sorted.map { it.id })
    }
}
