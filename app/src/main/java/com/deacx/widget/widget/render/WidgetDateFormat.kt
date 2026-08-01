package com.deacx.widget.widget.render

import java.time.format.DateTimeFormatter
import java.util.Locale

object WidgetDateFormat {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
}
