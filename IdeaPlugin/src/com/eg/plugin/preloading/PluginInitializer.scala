package com.eg.plugin.preloading

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator

class PluginInitializer extends PreloadingActivity {
  override def preload(progressIndicator: ProgressIndicator): Unit = {
    // It's necessary to initialize the class loader.
    // SubmitExecutorFactory
  }
}
