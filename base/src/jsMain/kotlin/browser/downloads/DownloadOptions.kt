@file:JsModule("webextension-polyfill")
@file:JsQualifier("downloads")

package browser.downloads

// https://developer.mozilla.org/en-US/docs/Mozilla/Add-ons/WebExtensions/API/downloads/download
public external interface DownloadOptions {
    var url: String
    var filename: String
}