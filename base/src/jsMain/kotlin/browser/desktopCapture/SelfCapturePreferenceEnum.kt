package browser.desktopCapture

import kotlin.String

/**
 * Mirrors <a
 * href="https://w3c.github.io/mediacapture-screen-share/#dom-selfcapturepreferenceenum">SelfCapturePreferenceEnum</a>.
 */
public enum class SelfCapturePreferenceEnum(
  private val `value`: String,
) {
  include("include"),
  exclude("exclude"),
  ;

  public override fun toString(): String = value
}
