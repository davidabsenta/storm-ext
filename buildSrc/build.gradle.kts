subprojects {
    // Al definir buildSrc, Gradle buscará este ID localmente
    apply(plugin = "com.lagradost.cloudstream3.extensions")

    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")

        // AGREGAMOS ESTO AQUÍ: Le da acceso a la carpeta libs a todos los proveedores
        flatDir {
            dir(rootProject.file("libs"))
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}