# FEDEBOL MANAGER - SIMULADOR DE GESTIÓN DE FÚTBOL PROCEDURAL

FEDEBOL Manager (antes FAFI) es un simulador táctico y político de gestión futbolística para dispositivos Android. A través de un universo procedural realista y blindado legalmente mediante nombres ficticios, el juego sitúa al usuario en el rol de mánager y director deportivo para liderar clubes o fundar nuevas instituciones. El simulador integra un sistema de gobierno global (FEDEBOL), confederaciones de fantasía (SUDAMBOL, EUROBOL, NORAMBOL), algoritmos de partidos minuto a minuto, microblogging en red social y una oficina de convocatoria a selección nacional que se pulirá y profundizará en futuras actualizaciones.

---

## 🛠️ REQUISITOS PREVIOS Y VERSIONES

*   **Sistema Operativo:** Android 12.0+ (API Level 31+) recomendado (Soporta API Level 26+).
*   **Java Development Kit (JDK):** JDK 17 o superior.
*   **Gradle Build System:** Gradle 8.2+ con Kotlin DSL (`build.gradle.kts`).
*   **Android Gradle Plugin (AGP):** 8.2.x o superior.
*   **Kotlin Compiler:** 1.9.x o superior.
*   **Jetpack Compose:** Última versión estable bajo Material Design 3 (M3).

---

## 🚀 INSTALACIÓN Y CONFIGURACIÓN PASO A PASO

Sigue estos sencillos pasos para compilar, ensamblar y ejecutar el proyecto de forma local o en tu entorno de desarrollo:

1. **Clonar el Repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/fedebol-manager.git
   cd fedebol-manager
   ```

2. **Verificar Configuración del Entorno:**
   Asegúrate de tener configurada la variable `ANDROID_SDK_ROOT` o `ANDROID_HOME` apuntando al SDK de Android de tu sistema.

3. **Compilar el Proyecto con Gradle:**
   Utiliza Gradle para compilar todas las dependencias y validar la sintaxis:
   ```bash
   gradle compileDebugSources
   ```

4. **Generar el Archivo APK de Depuración:**
   ```bash
   gradle assembleDebug
   ```
   El APK resultante estará disponible en la ruta:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📋 CARACTERÍSTICAS TÁCTICAS Y EVENTOS DE GESTIÓN

El juego incorpora un simulador deportivo y administrativo con un control directivo sin precedentes:

*   **Estrategia Táctica y Estilos de Juego Flexibles:** Elige entre 5 formaciones modernas y 6 estilos tácticos (*Equilibrada, Agresiva / Ofensiva, Defensiva, Contraataque, Posesión, Presión Alta*) tanto en la plantilla como en la previa del partido:
    *   **4-4-2:** Balance estándar táctico sin modificadores.
    *   **4-3-3:** Presión ofensiva (+10% Ataque, -5% Defensa).
    *   **3-5-2:** Dominio de posesión (+15% Medio, -10% Defensa).
    *   **5-3-2:** Cerrojo defensivo (+15% Defensa, -10% Ataque).
    *   **4-2-3-1:** Transición equilibrada moderna (+5% Medio, +5% Ataque).
*   **Atributos Procedurales Diversos y Arquetipos:** Cada futbolista generado proceduralmente cuenta con perfil biofísico y técnico completo: **Estatura** (165cm - 202cm), **Peso** (60kg - 95kg), **Pie Preferido** (*Derecho, Izquierdo, Ambidestro*), **Arquetipo de Especialidad** (*🎯 Francotirador de Falta, ⚡ Killer del Área, 🧱 Muro Infranqueable, ⚙️ Motor Box-to-Box, 🧤 Parapenaltis, etc.*) y **Personalidad** (*🌟 Líder Motivador, 💼 Silencioso Profesional, 🔥 Competitivo Ambicioso, etc.*). Estos atributos diversos se muestran de forma destacada en la carta del jugador en la plantilla, en la cancha 2D y en los paneles de MVP de cada encuentro.
*   **Sistema Realista de Lesiones y Secuelas de Rendimiento:** Durante las simulaciones de partidos en vivo, los futbolistas pueden sufrir accidentes o choques físicos con un 8% de probabilidad base, generando diagnósticos médicos realistas (*"Ruptura Fibrilar", "Esguince de Tobillo", "Sobrecarga Muscular"*) y tiempos de baja en semanas. Los jugadores lesionados muestran insignias `🏥 LESIONADO` y un reporte médico interactivo en la plantilla. **Mecánica de Secuelas:** Al recuperarse, 1 o 2 atributos físicos o técnicos del futbolista pueden descender de 1 a 2 puntos aleatoriamente debido a la inactividad, forzando al mánager a gestionar la rehabilitación.
*   **Importación y Exportación de Música Propietaria (Banda Sonora FIFA):** Los usuarios pueden importar sus propias canciones `.mp3` / `.wav` directamente desde el almacenamiento de su dispositivo móvil para escuchar "temazos de la FIFA" mientras gestionan su club. Incluye gestión de lista de reproducción personalizada, guardado en almacenamiento local y opción de eliminación de pistas.
*   **Motor de Mods y Personalización Abierta (`ModEngine`):** Habilita un ecosistema de modding para transformar la jugabilidad y la interfaz del simulador. Incluye mods de velocidad de simulación (3x), mayor frecuencia de goles espectaculares (+45%), interfaz estática estilo FIFA EA Sports Neon Cyan, comentarios clásicos sudamericanos y soporte de importación de plantillas legendarias en formato JSON.
*   **Visualización Pre-Partido en Cancha 2D Vertical:** Antes de iniciar la simulación, el mánager puede configurar su estrategia y observar a los 11 titulares propios y del rival dispuestos tácticamente en una **Cancha 2D Vertical** con tarjetas tipo cromo (*PitchPlayerCard*), mostrando insignias de posición, valoraciones OVR y estrellas de capitán.
*   **Expansión Global de Ligas:** Bienvenido a **España 🇪🇸, Alemania 🇩🇪, Japón 🇯🇵, EE.UU. 🇺🇸 y Noruega 🇳🇴**, con nombres procedurales, factores económicos y plantillas de futbolistas nativos ajustados por país.
*   **Designación del Líder / Capitán:** Corona a tu jugador más influyente como el líder del equipo (con indicador de estrella `⭐` en la plantilla). La presencia de un capitán potencia el equilibrio táctico y el comportamiento competitivo general de la plantilla.
*   **Barras de Atributos Visuales:** Las estadísticas numéricas aburridas de velocidad, fuerza, ataque, defensa y mediocampo ahora se muestran con barras de progreso a color Material 3 de alta visibilidad para un escaneo visual instantáneo de los futbolistas de la plantilla.
*   **Estadísticas de Portero (Atajadas):** En lugar de goles y asistencias, se hace un seguimiento detallado y acumulativo de las atajadas/saves de tu portero en su carrera deportiva, visualizadas elegantemente en su descripción de plantilla.
*   **Reporte de MVP del Ganador:** Al concluir cada encuentro en vivo, la pantalla muestra de forma destacada al jugador MVP de la escuadra ganadora, detallando su nombre, nota de rendimiento, goles y asistencias logradas durante el partido.
*   **Eventos de Gestión de Mánager (20 Eventos):** Se han implementado 20 situaciones reales complejas (descansos mentales, salidas nocturnas no autorizadas, disputas de primas, crisis de disciplina, etc.) que ocurren aleatoriamente durante el avance de jornadas, ofreciendo decisiones interactivas de múltiples opciones que alteran de forma instantánea la moral, la lealtad profesional, el aprecio al club y las estadísticas de tus futbolistas.
*   **Entrenamientos Semanales Procedurales:** Los jugadores progresan técnicamente de forma individual tras cada simulación de fecha, con mejoras asimétricas basadas en su edad y veterana maduración. Los jugadores sobresalientes (calificaciones >= 8.0 o MVP) reciben un masivo bono de motivación del +25% de probabilidad, y notas >= 7.0 un +12%, para consolidar más rápido sus estadísticas. **Esta bonificación de motivación por rendimiento de juego se muestra de forma explícita y animada con colores en el panel de detalles de cada jugador.**
*   **Panel de Cuerpo Técnico y Contratación:** Recluta especialistas en el Club para potenciar exponencialmente las probabilidades de entrenamiento por áreas deportivas (Ataque, Defensa, Porteros, Preparación Física y Psicología Mental). El cuerpo técnico percibe un sueldo semanal deducido del presupuesto del club, con soporte de despido interactivo.
*   **Ojeo Adaptativo (Fuzzy Scouting):** El reporte visual de entrenamiento del plantel cambia según la calidad de ojeo institucional. Muestra datos exactos si el nivel es excelente (>= 80%), descripciones borrosas/fuzzy de áreas si es medio (40-79%), o alertas de baja visibilidad si es pobre (< 40%).

---

## 📅 SISTEMA DE CALENDARIO Y FECHA REAL

El juego cuenta con un planificador temporal dinámico con las siguientes características:
*   **Inicio Realista:** Comienza de forma predeterminada el **1 de enero de 2025**.
*   **Avance Semanal:** Cada jornada deportiva avanzada incrementa la fecha actual en exactamente 7 días.
*   **Pantalla de Fixture Detallada:** Permite auditar el calendario de partidos del propio club mánager, el de rivales locales e inclusive el de clubes de ligas internacionales de otros países.
*   **Persistencia Completa:** La fecha actual y el perfil del mánager se guardan de forma segura de manera asíncrona, de modo que las partidas continúen exactamente donde se dejaron.

---

## 🧭 MENÚ PRINCIPAL E INICIO DEL JUEGO

El simulador incorpora un **Menú Principal** de diseño profesional, inspirado en videojuegos de consola de primer nivel, que optimiza el flujo de juego inicial:
*   **Nueva partida:** Permite ingresar tu nombre de mánager y generar un nuevo universo procedural desde cero (limpiando registros previos).
*   **Continuar partida:** Carga y reanuda al instante la partida más reciente guardada localmente de manera asíncrona.
*   **Partidas guardadas:** Visualiza en un slot dedicado los metadatos de tu carrera (nombre, reputación, fondos personales).
*   **Opciones:** Panel de configuración completo para personalizar la experiencia de juego:
    *   **Consola de Audio:** Controla la música en segundo plano (Play, Pausa, Siguiente, Anterior), activa/desactiva la música de fondo, ajusta el volumen y explora el repertorio de pistas.
    *   **Abreviación de Cifras Financieras:** Alterna entre ver cifras extendidas o compactadas para presupuestos y salarios (por ejemplo, `$1.5M` en lugar de `$1,500,000`).
    *   **Divisa de Operación:** Selecciona tu símbolo de moneda preferido (`$`, `€`, `£`, `AR$`, `MX$`) para que se aplique de forma dinámica en toda la aplicación.
    *   **Restablecimiento Total de Datos:** Limpia de forma permanente y segura todos los datos guardados en disco y restablece los estados en memoria para empezar una carrera de mánager desde cero con un universo completamente renovado.
*   **Ayuda y reglas:** Guía interactiva que enseña los pilares del juego (universo procedural ficticio, motor de simulación de partidos de tres zonas, confederaciones FEDEBOL, microblogging y guardado local).
*   **Salir del juego:** Cierra la aplicación de forma segura en dispositivos Android.

---

## 🔊 AUDIO, EFECTOS DE SONIDO Y SIMULACIÓN EN VIVO

El simulador cuenta con una experiencia de audio y partido en vivo inmersiva en todas sus pantallas:
*   **Música en Segundo Plano con Cantera de Tracks:** Motor de reproducción de música asíncrono e integrado que reproduce tracks continuos y automáticos. El repertorio incluye **"Better Day" de *penguinmusic***, **"Ambient DnB" de *AbsoluteSound*** y **"Asian Lofi Hip Hop" de *Vjgalaxy***. Todos los archivos han sido optimizados a formato ultra liviano **`.ogg`**, garantizando una inmersión fluida y reduciendo de manera masiva el consumo de memoria.
*   **Modo "No Molestar" (DND) superado:** Tanto la música de fondo como los silbatos del partido están configurados con atributos de audio dedicados (`USAGE_MEDIA` / `CONTENT_TYPE_MUSIC`). Esto permite que los sonidos se reproduzcan incluso si el teléfono está configurado en modo "No Molestar" o "Silencio" (siempre que el volumen multimedia de tu dispositivo esté activo).
*   **Ajustes de Sonido:** El panel de **Opciones** cuenta con un deslizador de volumen de alta precisión, botón de encendido/apagado global para la música y un diseño visual animado con un disco de vinilo procedural que rota de acuerdo con el ritmo del partido.
*   **Simulación Dinámica de Partido:** El cotejo corre de manera realista minuto a minuto dividido en primer tiempo, entretiempo de descanso, segundo tiempo y fin del partido, con controles de pausa/reproducción y aceleración del tiempo de 1x a 10x.
*   **Cronología de Juego Realista:** Se generan eventos dinámicos y comentarios tácticos minuciosos minuto a minuto adaptados a la marcha del cotejo.
*   **Rotación Inteligente de Sonidos de Silbato:** Integración de un sistema que rota de forma aleatoria entre **7 silbidos arbitrales únicos** de metal y plástico (optimizados en formato ligero `.ogg` para ahorrar espacio en disco, logrando un ahorro de más del 96%) para evitar la monotonía auditiva.

---

## 🎨 DISEÑO ESTÉTICO: AZUL GLACIAR Y ZAFIRO PROFUNDO

Para optimizar la experiencia durante jornadas de juego largas, se ha diseñado una interfaz de usuario cómoda para la vista basada en:
*   **Zafiro Profundo:** Fondo y áreas de lectura oscuras para reducir la fatiga visual.
*   **Azul Glaciar:** Color de acento moderno que resalta elementos interactivos clave, botones principales y la fecha actual.
*   **Métricas Clarificadas:** Diseño limpio de Material Design 3 con bordes suaves de acero y espaciado de cuadrícula estricto.

---

## 🗺️ ESTRUCTURA DEL PROYECTO

La organización de carpetas y módulos principales del código fuente de FEDEBOL Manager se divide de la siguiente manera:

```text
/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/          # Código fuente principal en Kotlin
│   │   │   │   ├── engine/                # Motor de juego, simulación y estados globales
│   │   │   │   │   ├── GameEngine.kt      # Núcleo del simulador y avance de jornadas
│   │   │   │   │   └── GameStorage.kt     # Serialización y persistencia local asíncrona
│   │   │   │   ├── model/                 # Modelos de dominio y generadores procedurales
│   │   │   │   │   ├── Player.kt          # Futbolistas, rasgos, cálculos de calificación
│   │   │   │   │   ├── Club.kt            # Entidades de clubes deportivos y ligas
│   │   │   │   │   └── Manager.kt         # Perfil del mánager, finanzas y licencias
│   │   │   │   ├── storage/               # Utilidades de guardado y cifrado local
│   │   │   │   ├── ui/                    # Componentes visuales bajo Jetpack Compose
│   │   │   │   │   ├── theme/             # Colores (Glacier Blue/Sapphire), tipografía y formas
│   │   │   │   │   └── MainDashboard.kt   # Interfaz principal, pantallas de tabs (incluyendo Calendario)
│   │   │   │   └── MainActivity.kt        # Actividad raíz y flujo de Onboarding
│   │   │   └── res/                       # Recursos estáticos (strings, drawables, XML)
│   │   └── test/                          # Unit Testing y Robolectric local JVM
│   └── build.gradle.kts                   # Configuración del módulo de la aplicación
├── gradle/                                # Archivos de envoltura de Gradle
├── build.gradle.kts                       # Script de construcción a nivel de proyecto
├── settings.gradle.kts                    # Definición de submódulos y dependencias
├── ROADMAP.md                             # Planificación y fases del proyecto
├── STRUCTURE.md                           # Detalle minucioso de la arquitectura técnica
└── AI_CONTEXT.md                          # Manual para asistentes de desarrollo de IA
```

---

## 🧪 CÓMO EJECUTAR LOS TESTS

El proyecto incluye soporte para pruebas unitarias rápidas y pruebas integrales basadas en Robolectric en la Máquina Virtual de Java (JVM):

*   **Ejecutar todas las pruebas locales:**
    ```bash
    gradle :app:testDebugUnitTest
    ```

*   **Grabar imágenes de referencia de captura (Screenshot Testing con Roborazzi):**
    ```bash
    gradle :app:recordRoborazziDebug
    ```

*   **Validar la fidelidad del diseño visual mediante comparativas de pantalla:**
    ```bash
    gradle :app:verifyRoborazziDebug
    ```

---

## 🤖 INTEGRACIÓN CONTINUA (GITHUB ACTIONS)

El proyecto cuenta con pipelines de integración continua (CI) configurados mediante **GitHub Actions** para automatizar la verificación del código, seguridad y compilación. En el futuro, se podrán incorporar workflows adicionales de pruebas instrumentadas o despliegue automatizado:

1. **Build Debug APK (`build-debug-apk.yml`):**
   - Compila el proyecto de forma automática.
   - **Optimización de Disparador (Path Filtering):** Solo se activa cuando se detectan modificaciones reales dentro del subdirectorio `/app` (código fuente y recursos directos de la aplicación), evitando compilaciones redundantes por cambios en documentación o scripts de soporte.
   - Genera y emite un artefacto descargable con el archivo APK depurado (`app-debug.apk`) con retención de 7 días.

2. **Code Quality & Security Scan (`code-analysis.yml`):**
   - Ejecuta un análisis estático profundo del repositorio estructurado de manera modular en **8 reportes planos independientes (.txt)** para evitar la sobrecarga de un solo archivo masivo y facilitar el análisis enfocado:
     1. **`1_line_limits_report.txt`:** Control de modularidad y alerta de archivos de código que superan las **300 líneas**.
     2. **`2_security_secrets_report.txt`:** Escaneo de llaves API, secretos y tokens hardcodeados en el código.
     3. **`3_insecure_storage_report.txt`:** Detección de transmisiones en texto plano en strings de código (HTTP), filtrado inteligente de URLs en comentarios o archivos de prueba (evitando falsos positivos), configuraciones vulnerables (allowBackup, MODE_WORLD) y riesgos reales de inyección SQL por concatenación en consultas SQLite crudas.
     4. **`4_cryptography_report.txt`:** Alertas sobre uso de algoritmos criptográficos débiles o inseguros (MD5, SHA-1, ECB).
     5. **`5_memory_leaks_threads_report.txt`:** **Detección Avanzada de Fugas de Memoria (Leaks) y Hilos (Threads)**. Analiza referencias estáticas peligrosas a Context/Activity, singletons con retención de Context, Coroutines companion scopes sin liberar, bloqueo del hilo principal con Thread.sleep, y registros de listeners (Broadcast/Sensor) sin cierre.
     6. **`6_compose_performance_report.txt`:** **Análisis de Rendimiento y Buenas Prácticas en Jetpack Compose**. Escanea inicializaciones de mutableStateOf sin remember, colores hexadecimales hardcodeados en composables, lecturas bloqueantes de archivos o SharedPreferences en el cuerpo del Composable, y uso de items() en LazyLayouts sin parámetros 'key' explícitos.
     7. **`7_todos_fixmes_report.txt`:** Centraliza todos los comentarios TODO y FIXME pendientes del proyecto.
     8. **`8_debugging_practices_report.txt`:** Identifica depuraciones crudas (System.out.println, printStackTrace) promoviendo el estándar de logs en Android.
   - **Notificaciones Seguras y Privadas a Discord:** Envía los 8 reportes de forma simultánea y 100% privada directamente a tu canal de Discord configurando `DISCORD_WEBHOOK_URL` en los secretos de GitHub, garantizando máxima seguridad en repositorios abiertos.
   - Genera un archivo comprimido descargable con los 8 reportes modulares (`modular-code-analysis-reports`) con retención de 14 días.

3. **Android Unit Tests (`unit-tests.yml`):**
   - Compila y ejecuta la suite completa de pruebas unitarias locales y Robolectric de la aplicación.
   - Envía notificaciones de estado automáticas a tu canal de Discord (con estado Exitoso/Fallido, detalles del commit y enlace directo a la ejecución en GitHub) configurando la variable `DISCORD_UNIT_TESTS_WEBHOOK_URL`.

4. **Android Screenshot & App Store Generator (`screenshot-tests.yml`):**
   - Utiliza **Roborazzi y Robolectric** para renderizar y capturar las vistas clave de la aplicación en alta resolución (Onboarding, Squad de Jugadores, Gabinete Federal, etc.).
   - Genera y transmite las imágenes directamente a tu Discord como archivos adjuntos mediante `multipart/form-data` utilizando el secreto `DISCORD_SCREENSHOT_TESTS_WEBHOOK_URL`, permitiéndote previsualizar los assets de marketing o App Store directamente en el feed de chat.

---

## 🤖 CONFIGURACIÓN DE LOS 3 BOTS DE DISCORD

Para automatizar la entrega de reportes y archivos visuales sin necesidad de descargar artefactos manuales desde GitHub, configura estos **3 Webhooks dedicados** en tu servidor de Discord. A continuación tienes la guía exacta de qué nombres ponerles y qué secretos configurar en GitHub:

### 1. 🛡️ Bot 1: **Fafi Security Guard**
*   **Nombre del Bot recomendado:** `Fafi Security Guard`
*   **Canal sugerido en Discord:** `#auditorias-codigo` o `#seguridad`
*   **Secreto en GitHub:** `DISCORD_WEBHOOK_URL`
*   **Qué hace:** Envía simultáneamente los **8 reportes planos modulares (.txt)**. Audita modularidad (límite de 300 líneas), contraseñas/secretos filtrados, transmisiones inseguras HTTP y SQL Injections, criptografía débil, TODOs/FIXMEs, rendimiento en Jetpack Compose, fugas de memoria y hilos, e indicadores de depuración sucios.

### 2. 🧪 Bot 2: **Fafi Unit Tests Guard**
*   **Nombre del Bot recomendado:** `Fafi Unit Tests Guard`
*   **Canal sugerido en Discord:** `#pruebas-unitarias` o `#ci-cd-builds`
*   **Secreto en GitHub:** `DISCORD_UNIT_TESTS_WEBHOOK_URL`
*   **Qué hace:** Notifica inmediatamente con cada push o Pull Request si las pruebas lógicas unitarias pasaron exitosamente (100% de éxito) o si ocurrieron regresiones y fallaron, incluyendo detalles del autor, rama y enlace directo a los logs en GitHub.

### 3. 📸 Bot 3: **Fafi Visual Inspector**
*   **Nombre del Bot recomendado:** `Fafi Visual Inspector`
*   **Canal sugerido en Discord:** `#capturas-de-pantalla` o `#marketing-assets`
*   **Secreto en GitHub:** `DISCORD_SCREENSHOT_TESTS_WEBHOOK_URL`
*   **Qué hace:** Genera capturas de pantalla de alta resolución de las pantallas de la aplicación utilizando datos reales (Onboarding, Plantilla de Jugadores, Panel de la Federación). Transmite los archivos gráficos en vivo directamente como archivos adjuntos (`.png`) al chat de Discord listos para marketing y la App Store.

---

## 🔒 VARIABLES DE ENTORNO Y SECRETOS

Para cualquier integración con servicios de red externos, inteligencia artificial (como la API de Gemini) o entrega automatizada de alertas de calidad y testeo a Discord, configure sus variables en el panel de **Secrets** de su repositorio en GitHub:

| Variable | Tipo | Descripción | Obligatoria | Ejemplo |
| :--- | :--- | :--- | :---: | :--- |
| `GEMINI_API_KEY` | String | Clave de acceso para la generación de resúmenes de prensa mediante IA | No (Opcional) | `AIzaSyB4x...` |
| `DISCORD_WEBHOOK_URL` | String | Webhook para **Fafi Security Guard** (8 Reportes de análisis estático) | No | `https://discord.com/api/webhooks/...` |
| `DISCORD_UNIT_TESTS_WEBHOOK_URL` | String | Webhook para **Fafi Unit Tests Guard** (Éxito o fallo de pruebas lógicas) | No | `https://discord.com/api/webhooks/...` |
| `DISCORD_SCREENSHOT_TESTS_WEBHOOK_URL` | String | Webhook para **Fafi Visual Inspector** (Galería de capturas y assets de marketing) | No | `https://discord.com/api/webhooks/...` |

---

## 🤝 CÓMO CONTRIBUIR

1. Crea una rama para tu característica: `git checkout -b feature/nueva-tactica`
2. Asegúrate de compilar y correr las pruebas antes de enviar: `gradle :app:testDebugUnitTest`
3. Haz un commit de tus cambios: `git commit -m "Añadir rasgo de jugador: LÍDER_NATO"`
4. Realiza un Push a tu rama: `git push origin feature/nueva-tactica`
5. Abre un Pull Request describiendo detalladamente tu implementación.
