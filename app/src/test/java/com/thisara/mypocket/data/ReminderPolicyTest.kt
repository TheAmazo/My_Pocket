package com.thisara.mypocket.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderPolicyTest {
    @Test
    fun reminderIsSkippedWhenMoneyWasSavedToday() {
        assertFalse(ReminderPolicy.shouldNotify("2026-05-03", "2026-05-03"))
    }

    @Test
    fun reminderIsShownWhenNothingWasSavedToday() {
        assertTrue(ReminderPolicy.shouldNotify("2026-05-02", "2026-05-03"))
        assertTrue(ReminderPolicy.shouldNotify(null, "2026-05-03"))
    }
}
