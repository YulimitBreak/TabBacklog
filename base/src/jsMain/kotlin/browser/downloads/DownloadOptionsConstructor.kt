package browser.downloads


@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun DownloadOptions(block: DownloadOptions.() -> Unit) = (js("{}") as DownloadOptions).apply(block)