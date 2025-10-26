package de.malteans.digishelf.core.data.network

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile

actual object ApiConfig {
    actual val googleApiToken: String
        get() = getStringResource(
            valueKey = "GOOGLE_API_KEY",
        ) ?: throw IllegalStateException("GOOGLE_API_KEY not found in Secrets.plist")
}

internal fun getStringResource(
    valueKey: String,
    filename: String = "Secrets",
    fileType: String = "plist",
): String? {
    val result = NSBundle.mainBundle.pathForResource(filename, fileType)?.let {
        val map = NSDictionary.dictionaryWithContentsOfFile(it)
        map?.get(valueKey) as? String
    }
    return result
}