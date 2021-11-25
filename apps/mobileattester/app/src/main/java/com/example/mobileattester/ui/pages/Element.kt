package com.example.mobileattester.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.mobileattester.data.model.Element
import com.example.mobileattester.data.model.ElementResult
import com.example.mobileattester.ui.components.TagRow
import com.example.mobileattester.ui.components.anim.FadeInWithDelay
import com.example.mobileattester.ui.components.common.DecorText
import com.example.mobileattester.ui.components.common.HeaderRoundedBottom
import com.example.mobileattester.ui.components.common.OutlinedIconButton
import com.example.mobileattester.ui.components.common.TextWithIcon
import com.example.mobileattester.ui.theme.*
import com.example.mobileattester.ui.util.*
import com.example.mobileattester.ui.viewmodel.AttestationViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*


const val ARG_ITEM_ID = "item_id"

@Composable
fun Element(navController: NavController, viewModel: AttestationViewModel) {
    val clickedElementId =
        navController.currentBackStackEntry?.arguments?.getString(ARG_ITEM_ID)
            ?: run {
                Text(text = "Data for the item id could not be found.")
                return
            }

    val element = viewModel.getElementFromCache(clickedElementId) ?: run {
        ElementNull()
        return
    }

    fun onAttestClick() {
        navController.navigate(Screen.Attest.route, bundleOf(Pair(ARG_ITEM_ID, element.itemid)))
    }

    fun onLocationClick() {
        viewModel.useUpdateUtil().updateElement(element)
    }

    // Content
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Element header
        HeaderRoundedBottom {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                Text(
                    text = element.name,
                    fontSize = FONTSIZE_XXL,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = element.endpoint,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                )
            }
        }
        // Content
        Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            TagRow(element.types)
            Spacer(modifier = Modifier.size(26.dp))
            ElementActions(onAttestClick = ::onAttestClick, onLocationClick = ::onLocationClick)
            Spacer(modifier = Modifier.size(26.dp))
            Text(
                text = element.description ?: "",
                color = DarkGrey,
            )
            Spacer(modifier = Modifier.size(26.dp))
            Divider(color = LightGrey, thickness = 1.dp)
            Spacer(modifier = Modifier.size(26.dp))
            ElementResult(navController, element)

        }
    }
}

@Composable
private fun ElementNull() {
    FadeInWithDelay(1000) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "This element was not found, please ensure that the server contains this element",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun ElementResult(navController: NavController, element: Element) {
    val resultHoursShown = remember { mutableStateOf(24) }
    val latestResults = element.results.latestResults(resultHoursShown.value)


    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Results", fontSize = 24.sp)
        TextWithIcon(
            text = resultHoursShown.value.shownHoursToString(),
            icon = TablerIcons.ListCheck,
            color = PrimaryDark
        )
    }
    Spacer(Modifier.size(32.dp))

    ElementResultSummary(latestResults)

    Spacer(Modifier.size(24.dp))

    ElementResultFull(
        latestResults,
        latestResults.size == element.results.size,
        onResultClicked = {
            navController.navigate(Screen.Result.route, bundleOf(Pair(ARG_RESULT_ID, it.itemid)))
        },
    ) {
        // On more items requested fetch a batch of items by time.
        resultHoursShown.value =
            Timestamp.fromSecondsString(element.results.getOrNull(latestResults.size)?.verifiedAt.toString())
                ?.timeSince()
                ?.toHours()?.hoursHWMYRounded() ?: resultHoursShown.value
    }
}

@Composable
private fun ElementResultSummary(results: Collection<ElementResult>) {
    val passed = results.count { it.result == 0 }
    val failed = results.size - passed

    @Composable
    fun LocalComp(num: Int, text: String, color: Color) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedIconButton(
                text = num.toString(),
                rounded = true,
                width = 20.dp,
                height = 20.dp,
                color = color,
                filled = true
            ) // TODO: Use AspectRatio
            DecorText(txt = text, color = color)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        LocalComp(num = results.size, text = "Attest.", Primary)
        LocalComp(num = passed, text = "Passed", Ok)
        LocalComp(num = failed, text = "Failed", Error)
    }
}

@Composable
private fun ElementResultFull(
    results: Collection<ElementResult>,
    allShown: Boolean,
    onResultClicked: (ElementResult) -> Unit,
    onMoreRequested: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        results.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onResultClicked(it) }
                    .padding(vertical = 6.dp),
                verticalAlignment = CenterVertically,
            ) {
                OutlinedIconButton(
                    icon = getResultIcon(it),
                    width = 24.dp,
                    height = 24.dp,
                    border = null,
                    color = getCodeColor(it.result),
                    rounded = true
                )
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = it.ruleName,
                    maxLines = 1,
                )
                Text(
                    text = getTimeFormatted(it.verifiedAt, DatePattern.DateOnly),
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (!allShown) {
            TextButton(modifier = Modifier.padding(16.dp), onClick = { onMoreRequested() }) {
                Text(
                    text = "More results (+24h)",
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            if (results.isEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(24.dp),
                    text = "No results available",
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    modifier = Modifier
                        .padding(24.dp),
                    text = "All results listed.",
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ElementActions(onAttestClick: () -> Unit, onLocationClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        OutlinedIconButton(TablerIcons.Checkbox, rounded = true, color = Ok) {
            onAttestClick()
        }
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedIconButton(TablerIcons.CurrentLocation, rounded = true) {
            onLocationClick()
        }
    }
}
