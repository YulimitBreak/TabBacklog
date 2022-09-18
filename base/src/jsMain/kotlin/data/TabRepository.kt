package data

import browser.tabs.CreateCreateProperties

class TabRepository {
    fun openManager() {
        browser.tabs.create(CreateCreateProperties {
            this.url = "manager.html"
        })
    }
}