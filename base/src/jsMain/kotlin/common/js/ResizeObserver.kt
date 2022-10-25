package common.js

import org.w3c.dom.Element

open external class ResizeObserver(callback: (dynamic) -> Unit) {
    open fun observe(element: Element)

    open fun unobserve(element: Element)

    open fun disconnect()

    open fun foo(): Boolean
}