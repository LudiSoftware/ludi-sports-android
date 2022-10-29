package io.usys.report.ui.ysrviews
//
//@Composable
//fun DropDownList(
//    requestToOpen: Boolean = false,
//    list: List<String>,
//    request: (Boolean) -> Unit,
//    selectedString: (String) -> Unit
//) {
//    DropdownMenu(
//        dropdownModifier = Modifier.fillMaxWidth(),
//        toggle = {
//            // Implement your toggle
//        },
//        expanded = requestToOpen,
//        onDismissRequest = { request(false) },
//    ) {
//        list.forEach {
//            DropdownMenuItem(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = {
//                    request(false)
//                    selectedString(it)
//                }
//            ) {
//                Text(it, modifier = Modifier.wrapContentWidth().align(Alignment.Start))
//            }
//        }
//    }
//}
//@Composable
//fun CountrySelection() {
//    val countryList = listOf(
//        "United state",
//        "Australia",
//        "Japan",
//        "India",
//    )
//    val text = remember { mutableStateOf("") } // initial value
//    val isOpen = remember { mutableStateOf(false) } // initial value
//    val openCloseOfDropDownList: (Boolean) -> Unit = {
//        isOpen.value = it
//    }
//    val userSelectedString: (String) -> Unit = {
//        text.value = it
//    }
//    Box {
//        Column {
//            OutlinedTextField(
//                value = text.value,
//                onValueChange = { text.value = it },
//                label = { Text(text = "TextFieldTitle") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            DropDownList(
//                requestToOpen = isOpen.value,
//                list = countryList,
//                openCloseOfDropDownList,
//                userSelectedString
//            )
//        }
//        Spacer(
//            modifier = Modifier.matchParentSize().background(Color.Transparent).padding(10.dp)
//                .clickable(
//                    onClick = { isOpen.value = true }
//                )
//        )
//    }
//}