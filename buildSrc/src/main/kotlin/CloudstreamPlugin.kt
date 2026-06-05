import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import java.io.File

class CloudstreamPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. Aplicamos los plugins esenciales de Android y Kotlin
        project.plugins.apply("com.android.library")
        project.plugins.apply("org.jetbrains.kotlin.android")

        // 2. Creamos la configuración especial que exige Jellyfin y otros scripts
        val cloudstreamConfig = project.configurations.maybeCreate("cloudstream")

        // 3. Registramos la extensión para que no fallen las variables como cloudstream { ... }
        project.extensions.create("cloudstream", CloudstreamExtension::class.java)

        // 4. Configuración común de Android para todos tus proveedores
        project.extensions.configure(LibraryExtension::class.java) {
            compileSdk = 34

            // Namespace automático basado en la carpeta del proveedor
            namespace = "com.stormext.${project.name.lowercase()}"

            defaultConfig {
                minSdk = 21
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }

        // 5. Forzamos de forma segura que Kotlin compile usando Java 17
        project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }

        // 6. INYECCIÓN AUTOMÁTICA COMPATIBLE: Buscamos el archivo físico para saber su nombre exacto
        val rootLibsDir = File(project.rootDir, "libs")
        val fallbackLibsDir = File(project.rootDir, "../libs")
        val targetLibsDir = if (rootLibsDir.exists()) rootLibsDir else fallbackLibsDir

        if (targetLibsDir.exists()) {
            val aarFile = targetLibsDir.listFiles { _, name -> name.contains("cloudstream") && name.endsWith(".aar") }?.firstOrNull()
            val baseName = aarFile?.name?.removeSuffix(".aar") ?: "cloudstream3"

            // Al estar el flatDir global en settings, pasar el string del nombre basta
            // para que Gradle propague las dependencias internas y firmas hacia los archivos .kt de forma nativa.
            val dependencyMap = mapOf("name" to baseName)

            project.dependencies.add("api", dependencyMap)
            project.dependencies.add("compileOnly", dependencyMap)
            project.dependencies.add("implementation", dependencyMap)
            project.dependencies.add(cloudstreamConfig.name, dependencyMap)
        }
    }
}

open class CloudstreamExtension {
    var language: String = ""
    var description: String = ""
    var authors: List<String> = listOf()
    var status: Int = 3
    var tvTypes: List<String> = listOf()
    var iconUrl: String = ""
    var isCrossPlatform: Boolean = false
    var requiresResources: Boolean = false
}