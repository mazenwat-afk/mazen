package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.PersonEntity
import com.example.data.local.UserSettings
import com.example.data.model.PersonSummary
import com.example.ui.components.PersonAccountCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun person_account_card_screenshot() {
    val samplePerson = PersonEntity(id = 1, name = "أحمد محمد", phone = "0912345678")
    val sampleSummary = PersonSummary(
        person = samplePerson,
        totalIncome = 50000.0,
        totalExpense = 20000.0,
        netBalance = 30000.0,
        transactionCount = 2,
        lastTimestamp = System.currentTimeMillis()
    )
    val settings = UserSettings()

    composeTestRule.setContent {
      MyApplicationTheme {
        PersonAccountCard(
            summary = sampleSummary,
            currency = settings.currency,
            onClick = {},
            onEditName = {},
            onDeletePerson = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/person_account_card.png")
  }
}
