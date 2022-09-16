package browser.accessibilityPrivate

import kotlin.String

/**
 * Types of accessibility-specific DLCs.
 */
public enum class DlcType(
  private val `value`: String,
) {
  ttsEsUs("ttsEsUs"),
  ;

  public override fun toString(): String = value
}
