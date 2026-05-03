package com.thisara.mypocket.data

import org.junit.Assert.assertFalse
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
}
