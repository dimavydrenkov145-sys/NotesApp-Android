import java.util.Properties
import java.io.FileInputStream

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
plugins {
    id("com.android.application") version "9.4.0" apply false
    // Для Room в новых версиях используется встроенный kapt
    id("com.android.legacy-kapt") version "9.4.0" apply false
}