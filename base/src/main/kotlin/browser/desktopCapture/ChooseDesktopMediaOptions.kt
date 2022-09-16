@file:JsModule("webextension-polyfill")
@file:JsQualifier("desktopCapture")

package browser.desktopCapture

/**
 * Mirrors members of <a
 * href="https://w3c.github.io/mediacapture-screen-share/#dom-displaymediastreamconstraints">DisplayMediaStreamConstraints</a>
 * which need to be applied before the user makes their selection, and must therefore be provided to
 * chooseDesktopMedia() rather than be deferred to getUserMedia().
 */
public external interface ChooseDesktopMediaOptions {
  /**
   * Mirrors <a
   * href="https://w3c.github.io/mediacapture-screen-share/#dom-displaymediastreamconstraints-systemaudio">systemAudio</a>.
   */
  public var systemAudio: SystemAudioPreferenceEnum?

  /**
   * Mirrors <a
   * href="https://w3c.github.io/mediacapture-screen-share/#dom-displaymediastreamconstraints-selfbrowsersurface">selfBrowserSurface</a>.
   */
  public var selfBrowserSurface: SelfCapturePreferenceEnum?

  /**
   * Indicates that the caller intends to perform local audio suppression, and that the media picker
   * shown to the user should therefore reflect that with the appropriate warnings, as it does when
   * getDisplayMedia() is invoked.
   */
  public var suppressLocalAudioPlaybackIntended: Boolean?
}
