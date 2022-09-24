package data

import browser.tabs.CreateCreateProperties

class TabsRepository {
    fun openManager() {
        browser.tabs.create(CreateCreateProperties {
            this.url = "manager.html"
        })
    }
}