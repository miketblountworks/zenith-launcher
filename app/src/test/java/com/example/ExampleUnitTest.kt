package com.example

import org.junit.Test
import java.io.File
import java.net.URL

class ExampleUnitTest {
  @Test
  fun downloadDoc() {
    try {
      val url = URL("https://docs.google.com/document/d/1qVhXE72xDzMqvA27STG2n2tnp1kb0GgILnmxoIVIpwk/export?format=txt")
      val text = url.readText()
      File("app/src/test/java/com/example/blueprint.txt").writeText(text)
      println("DOWNLOAD_SUCCESS")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
