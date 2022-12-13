@file:JsModule("webextension-polyfill")
@file:JsQualifier("downloads")

package browser.downloads

import kotlin.js.Promise

public external fun download(downloadOptions: DownloadOptions? = definedExternally): Promise<Unit>