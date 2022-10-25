package browser.virtualKeyboardPrivate

/**
 * The display format associated with this item.
 */
public enum class DisplayFormat(
  private val `value`: String,
) {
  text("text"),
  png("png"),
  html("common/js"),
  `file`("file"),
  ;

  public override fun toString(): String = value
}
