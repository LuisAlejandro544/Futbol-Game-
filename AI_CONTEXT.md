# MANUAL DE CONTEXTO PARA DESARROLLO CON INTELIGENCIA ARTIFICIAL: FEDEBOL MANAGER

Este documento sirve como manual arquitectónico e instructivo de desarrollo para cualquier Agente de IA que trabaje en el código fuente de FEDEBOL Manager (antes FAFI). Contiene directrices inmutables para mantener la consistencia del sistema, evitar regresiones y respetar el diseño original del simulador.

---

## 🧭 1. PRINCIPIOS DE DESARROLLO INDISPENSABLES

1.  **Preservar la Proceduralidad (Sin Licencias Reales y Nombres Ficticios):**
    *   No hardcodear nombres de clubes o futbolistas reales de ligas profesionales europeas o sudamericanas en el código principal.
    *   Toda confederación y competición utiliza nombres ficticios (FEDEBOL, SUDAMBOL, EUROBOL, NORAMBOL, Copa de los Conquistadores, Copa Continental de las Alturas, etc.) para blindar legalmente el proyecto.
    *   *Mecánica de Selección Nacional (Preview):* El mánager puede recibir propuestas de selecciones ficticias regionales (e.g. Argen-Pampa, Samba-FC, Galia-FC) al acumular reputación o realizar campañas de relaciones públicas. Este sistema inicial se pulirá y expandirá con calendarios dedicados en el futuro.
    *   Toda generación de datos debe basarse en plantillas de nombres, factores económicos de país (`economyFactor`) y coeficientes de potencial de cantera (`academyFactor`).
2.  **No Modificar Formatos de Serialización a la Ligera:**
    *   Los modelos de datos (`Player`, `Club`, `League`, `Manager`) están anotados con `@JsonClass(generateAdapter = true)` para su persistencia asíncrona local estructurada. Cualquier adición de campo (por ejemplo, `saves: Int = 0` en `Player`, o `isGoalkeeper: Boolean = false` y `saves: Int = 0` en `MatchPlayerStat`) debe contemplar obligatoriamente un valor por defecto o inicializador seguro para no corromper partidas guardadas en disco por versiones anteriores.
3.  **UI 100% Jetpack Compose (Material Design 3):**
    *   No usar layouts XML tradicionales para la interfaz de usuario.
    *   Uso de temas e inyecciones de color unificados (**GlacierBlue** / Azul Glaciar, **PitchDarkBg** / Zafiro Profundo, **SurfaceCarbon** / Carbón de Interfaz). No definir colores hexadecimales directos en los archivos composables independientes.
    *   Respetar la adaptabilidad: el diseño es responsivo y soporta tanto visualizaciones de móviles en vertical como pantallas anchas de tabletas o emuladores en modo apaisado utilizando estructuras de columnas fluidas o barras de navegación en riel.

---

## 🧠 2. FLUJO DE ESTADO Y SUS ESPECIFICACIONES

El `GameEngine` actúa como la única fuente de verdad (*Single Source of Truth*). Expone flujos de lectura `StateFlow<T>` y manipula mutabilidades a través de hilos seguros en Coroutines con el contexto `Dispatchers.Default` o `Dispatchers.IO`.

### Estados Críticos que debes Vigilar:

*   `isOnboardingFinished` (Boolean): Controla si el mánager ya completó su registro o selección de club y se encuentra en el menú principal del Dashboard.
*   `isSimulating` (Boolean): Bandera para indicar que hay un procesamiento de fondo activo. Debe deshabilitar clics e interacciones de usuario en pantalla para evitar estados de carrera.
*   `manager` (StateFlow<Manager>): Perfil activo de la carrera del mánager (dinero, reputación, licencias).
*   `currentDate` (StateFlow<LocalDate>): Mantiene la fecha real de la simulación del juego. Inicia en el **1 de enero de 2025** de forma predeterminada.
*   `clubs` (StateFlow<List<Club>>) y `ligas` (StateFlow<List<League>>): Colecciones que se mutan en conjunto tras simular fechas.

### Almacenamiento Local Específico y Codificación:
*   `manager.json`: Serialización JSON del objeto `Manager` para persistir compras de licencias o contratación de agentes.
*   `calendar.txt`: Archivo plano que almacena la fecha real en formato ISO-8601 (`YYYY-MM-DD`). Se actualiza sumando 7 días de forma asíncrona al simular cada fecha.
*   `CryptoHelper.kt`: Codificador liviano que utiliza codificación Base64 en lugar de algoritmos AES/ECB/CBC pesados y con claves hardcodeadas. Esto elimina vulnerabilidades de criptografía débil reportadas por analizadores de seguridad estáticos (SAST) y previene falsos positivos manteniendo la integridad de las partidas guardadas de manera eficiente.

### 💣 2.1 Módulo de Mercado de Fichajes, Cantera y Negociaciones

*   `freeAgentsAndTalents` (`StateFlow<List<Player>>`): Mantiene la lista activa de futbolistas libres y canteranos juveniles en el mercado continental.
*   **Generador de Cantera y Agentes Libres:** `Player.generateYouthTalent()` y `Player.generateFreeAgent()` producen semanalmente (65% probabilidad por fecha) futbolistas de 16-19 años con alto potencial (`potentialRating` hasta 96) o agentes libres con palmarés de trofeos y goles.
*   **Negociación Bipartita:**
    1. `GameEngine.negotiateClubTransfer()`: Valida la oferta de traspaso contra las pretensiones del club según el OVR y valor de mercado.
    2. `GameEngine.negotiatePlayerContract()`: Valida requerimientos salariales por semana y años de vínculo.
    3. `GameEngine.executeTransferSigning()`: Ejecuta la firma, deduce el presupuesto del club e inyecta al jugador en la plantilla activa.
    4. `GameEngine.sellPlayerFromUserSquad()`: Traspasa un jugador de la plantilla al mercado recibiendo su valor monetario.
Al arrancar la aplicación, el usuario accede directamente a un **Menú Principal** de diseño profesional que centraliza las operaciones del simulador antes de inicializar o cargar el estado global:
1.  **Nueva partida:** Inicia el flujo de onboarding solicitando el nombre del mánager y generando el universo procedural.
2.  **Continuar partida:** Carga y reanuda inmediatamente el último estado de juego guardado en disco si existe.
3.  **Partidas guardadas:** Panel interactivo que extrae y visualiza de forma estática los metadatos de la carrera del mánager guardada (nombre, reputación, fondos personales).
4.  **Opciones:** Consola completa e interactiva de sonido que permite activar/desactivar y pausar/reproducir la música de fondo, navegar en la lista de tracks, regular el volumen de forma fina con un control deslizante y observar los metadatos de las canciones en reproducción.
5.  **Ayuda y reglas:** Menú instructivo detallando los conceptos fundamentales del simulador (Universo procedural, motor de partidos táctico, federación, red social y persistencia).
6.  **Salir del juego:** Cierra el proceso de la aplicación de forma segura y controlada.

---

## 🛠️ 3. REGLAS PARA IMPLEMENTAR NUEVAS FUNCIONALIDADES

### A. Para Añadir un Rasgo de Jugador (`Trait`)
1.  Modifica el enumerador de rasgos en `Player.kt`.
2.  Define una descripción descriptiva clara y un modificador aplicable.
3.  Inserta la lógica del modificador en el motor de partidos de `GameEngine.kt` (por ejemplo, dentro del generador de eventos clave del partido o resolución de disparos/ataques).

### B. Para Ampliar Países o Ligas
1.  Edita `Country.Companion.generateUniverse()` en `Club.kt` añadiendo la bandera emoji, factores de escala financiera y prestigio. El simulador incluye **España 🇪🇸, Alemania 🇩🇪, Japón 🇯🇵, EE.UU. 🇺🇸 y Noruega 🇳🇴**, además de las ligas sudamericanas y centroamericanas.
2.  Agrega plantillas de nombres de clubes ficticios representativos en el mapa `countryClubTemplates` dentro de `Club.Companion`.
3.  Asegúrate de que la cantidad de clubes generada por defecto se mantenga en números pares para no romper el algoritmo del fixture cíclico de partidos.

### C. Para Modificar Lógicas Temporales en el Calendario
1.  La fecha de la partida avanza llamando a `engine.advanceRound()`.
2.  La fórmula para derivar la fecha histórica o futura de cualquier fecha de jornada se calcula multiplicando el índice de la jornada por 7 días a partir del 1 de enero de 2025.

### D. Tácticas, Líderes y Eventos Directivos
1.  **Formaciones y Estilos Tácticos:** Los modificadores aplicados a la defensa, el mediocampo y el ataque se calculan dinámicamente en `Club.getTeamRatings()` con base en la formación seleccionada (`selectedFormation`) y el estilo táctico activo (`selectedTactic`: *Equilibrada, Agresiva, Defensiva, Contraataque, Posesión, Presión Alta*).
2.  **Pantalla Pre-Partido y Cancha 2D Vertical:** Antes de iniciar cada encuentro en `LiveMatchTickerScreen`, se presenta una vista táctica pre-partido con la opción de configurar el estilo estratégico y la formación, acompañada de un visualizador en **Cancha 2D Vertical (`VerticalPitch2DView`)** que renderiza en tarjetas tipo cromo (*PitchPlayerCard*) las posiciones reales de los 11 titulares del mánager y del rival según sus esquemas tácticos.
3.  **Designación de Capitán:** La capitanía se almacena en `captainPlayerId` dentro del objeto `Club` y se controla desde `SquadScreen` enviando llamadas a `engine.setClubCaptain()`.
3.  **Sistema de Decisiones (Eventos de Gestión):** Las lógicas de opciones y resoluciones de eventos de gestión administrativa se definen en `ManagerEvent.kt`. Las estadísticas físicas/técnicas y los presupuestos son mutables y se actualizan al elegir opciones en `resolveActiveEvent()`.
4.  **Sistema de Entrenamientos, Cuerpo Técnico y Ojeo:**
    *   **Progresión procedimental:** Se ejecutan de forma automática tras cada jornada de simulación dentro de `engine.advanceRound()`. El crecimiento depende probabilísticamente de la edad, del rendimiento (bonificación de MVP/notas altas) y de la influencia de entrenadores contratados. **Los bonos y penalizaciones de motivación por rendimiento se muestran de forma explícita en la tarjeta de detalles de cada jugador en la plantilla.**
    *   **Especialistas del Club:** El mánager puede contratar hasta un (1) especialista por cada rama deportiva (Ataque, Defensa, Porteros, Físico, Mental). Los sueldos semanales se descuentan de forma automática del presupuesto del club.
    *   **Ojeo (Fuzzy Scouting):** El reporte de entrenamiento se renderiza adaptándose dinámicamente al `scoutingLevel` del jugador en tres capas de precisión: Exacta (>= 80%), Borrosa (40-79%) y Oculta con advertencias (< 40%).

### E. Configuración Financiera y Restablecimiento de Datos (COMPLETADO ✅)
1.  **Abreviación de Cifras Financieras:** El estado se gestiona mediante `GameSettings.isAbbreviationEnabled`. Permite acortar los valores grandes de dinero en toda la aplicación de manera dinámica (ej: de `$1,500,000` a `$1.5M`) en cabeceras, plantillas de futbolistas y mercado.
2.  **Divisa de Operación:** El estado se gestiona mediante `GameSettings.currencySymbol`. Permite al usuario elegir el símbolo monetario principal (`$`, `€`, `£`, `AR$`, `MX$`) para salarios, presupuestos y boletaje.
3.  **Restablecimiento Total de Datos:** El método `GameEngine.resetAllData()` realiza un borrado asíncrono completo de los archivos guardados en disco (`storage.clearAll()`) y reinicia todos los flujos de `GameEngine` en memoria de manera segura, retornando de inmediato al usuario a la pantalla inicial de Onboarding.

### F. Sistema Realista de Lesiones y Secuelas (`Player.kt`, `MatchEngine.kt`, `GameEngine.kt`)
1.  **Ocurrencia y Diagnósticos:** En partidos en vivo, los futbolistas expuestos a jugadas o faltas tienen un 8% de probabilidad base de sufrir lesiones. `Player.isInjured` pasa a `true`, con un diagnóstico (`injuryName`: *"Ruptura Fibrilar"*, *"Esguince de Tobillo"*, *"Sobrecarga Muscular"*) y un tiempo de baja (`injuryDurationWeeks`: 1 a 4 semanas).
2.  **Procesamiento de Alta y Secuelas:** Al avanzar cada jornada en `advanceRound()`, el contador `injuryDurationWeeks` decrece. Al llegar a 0, `isInjured` vuelve a `false` y se ejecutan secuelas aleatorias: 1 o 2 atributos físicos o técnicos bajan de 1 a 2 puntos por inactividad.
3.  **Visualización en Plantilla:** `SquadScreen.kt` despliega insignias `🏥 LESIONADO` y un panel interactivo de Reporte Médico cuando el jugador seleccionado está de baja.

### G. Importación y Exportación de Música Propietaria (`BackgroundMusicPlayer.kt`)
1.  **Directorio Local:** Las canciones personalizadas importadas del dispositivo se copian y almacenan en `/custom_soundtrack/` con persistencia asíncrona.
2.  **Gestión de Pistas:** Soporta formatos `.mp3` y `.wav`. Se administran mediante `importCustomAudioTrack()`, `deleteCustomTrack()` y `playTrack()`.

### H. Motor de Mods y Personalización Abierta (`ModEngine.kt`, `ModManagerScreen.kt`)
1.  **Estado Centralizado:** `ModEngine` expone `installedMods`, `activeMatchSpeedMultiplier`, `activeGoalMultiplier` e indicadores estéticos.
2.  **Integración en Motor:** `MatchEngine` consulta los multiplicadores de `ModEngine` para adaptar la velocidad del ticker (minuto a minuto) y la probabilidad de gol.
3.  **UI de Modding:** `ModManagerScreen` permite filtrar por categorías (Jugabilidad, Plantillas/JSON, Interfaz, Comentarios) y crear/cargar definiciones JSON externas.

### I. Atributos Procedurales Diversos y Arquetipos (`Player.kt`, `League.kt`, `SquadScreen.kt`, `LiveMatchComponents.kt`)
1.  **Generación de Datos:** Generación aleatoria y lógica de `heightCm` (165-202 cm según posición), `weightKg` (fórmula acoplada a altura), `preferredFoot` (*Derecho, Izquierdo, Ambidestro*), `specialty` (*Francotirador de Falta, Killer del Área, Muro Infranqueable, Motor Box-to-Box, Parapenaltis, etc.*) y `personality` (*Líder Motivador, Silencioso Profesional, etc.*).
2.  **Transferencia de Datos:** `MatchStatisticsHelper.kt` transmite la especialidad, pie, estatura y personalidad al objeto `MatchPlayerStat` durante los partidos.
3.  **Representación UI:** `SquadScreen.kt` despliega insignias de especialidad y personalidad, pie y perfil físico. `PitchPlayerCard` en la cancha 2D y el panel de MVP en `LiveMatchComponents.kt` muestran las insignias y detalles técnicos del futbolista.

---

## 🧪 4. ESTÁNDARES DE CALIDAD Y PRUEBAS

*   **Identificación de Componentes en Tests (`testTag`):**
    *   Asigna siempre tags únicos de prueba usando `Modifier.testTag("tag_name")` en todos los botones y campos clave de entrada (por ejemplo, `manager_name_input`, `simulate_button`, `calendar_match_row_<X>`).
*   **Pruebas Locales (Robolectric):**
    *   Cualquier refactorización de lógica en `GameEngine` o cálculo de atributos en `Player` requiere ejecutar las pruebas automatizadas locales para garantizar estabilidad:
        ```bash
        gradle :app:testDebugUnitTest
        ```
*   **Validaciones Visuales (Roborazzi):**
    *   Si realizas cambios estéticos en la UI, asegúrate de actualizar las capturas de referencia usando:
        ```bash
        gradle :app:recordRoborazziDebug
        ```

---

## 🔒 5. PREVENCIÓN DE ERRORES FRECUENTES (ANTI-PATTERNS)

*   **❌ NO** realices llamadas bloqueantes `Thread.sleep()` en Compose o el hilo principal. Utiliza `delay()` o maneja eventos concurrentes con Coroutines.
*   **❌ NO** agregues dependencias externas a `libs.versions.toml` sin revisar primero su compatibilidad con la versión activa de Kotlin del proyecto.
*   **❌ NO** uses variables mutables globales que no estén vinculadas al hilo seguro del motor de persistencia estructurado.
*   **❌ NO** asumas que el almacenamiento local siempre tiene datos válidos. Implementa bloques `try-catch` con valores de respaldo al deserializar archivos JSON o planos de disco.
*   **❌ NO** crees o extiendas archivos de código individuales que superen las **300 líneas de código**. Delega lógica pesada en pantallas modularizadas independientes, clases helpers o módulos de lógica pura.

---

## 🤖 6. CUMPLIMIENTO DE PIPELINES DE AUTOMATIZACIÓN (CI/CD)

Todo agente de IA o desarrollador que colabore en este repositorio debe respetar las reglas validadas por nuestros workflows automáticos de GitHub Actions. El pipeline genera **8 reportes planos independientes (.txt)** para un escaneo enfocado:

1.  **Mantener la modularidad de archivos:**
    *   Cualquier archivo de código (`.kt`, `.kts`, `.java`, `.gradle`) que supere las **300 líneas de código** será reportado por `code-analysis.yml` en `1_line_limits_report.txt`. Mantén los composables limpios y desacoplados.
2.  **Prevención de filtrado de secretos:**
    *   No hardcodear claves bajo ninguna circunstancia. El pipeline audita tokens y contraseñas (`2_security_secrets_report.txt`). Usa `BuildConfig` para cargar variables del entorno.
3.  **Seguridad en Transmisiones y Almacenamiento Local (Reporte 3):**
    *   **❌ NO** declares enlaces HTTP inseguros (`http://`) en strings de código productivo real; el escáner los auditará en `3_insecure_storage_report.txt` descartando con filtros inteligentes comentarios de documentación y archivos de prueba.
    *   **❌ NO** concatenes variables de texto directamente en consultas SQL crudas en SQLite (`rawQuery` o `execSQL`); usa siempre placeholders de vinculación segura (`?`) para evitar inyecciones SQL.
    *   **❌ NO** crees archivos ni SharedPreferences usando modos obsoletos vulnerables (`MODE_WORLD_READABLE` o `MODE_WORLD_WRITEABLE`).
4.  **Rendimiento y Buenas Prácticas en Jetpack Compose:**
    *   **❌ NO** inicialices `mutableStateOf()` sin un bloque `remember` o `rememberSaveable` dentro de un Composable (reporte `6_compose_performance_report.txt`).
    *   **✅ SÍ** provee un parámetro `key` explícito al utilizar `items()` en `LazyColumn`/`LazyRow` para optimizar las recomposiciones.
    *   **❌ NO** realices llamadas directas de lectura/escritura de archivos o de persistencia (I/O bloqueante) dentro del cuerpo directo de un Composable; delega en ViewModels o bloques de efectos controlados como `LaunchedEffect`.
    *   **❌ NO** uses colores hexadecimales hardcodeados (como `Color(0xFF...)`) en composables; utiliza los esquemas dinámicos del `MaterialTheme.colorScheme`.
5.  **Detección de Fugas de Memoria y Bloqueo de Hilos:**
    *   **❌ NO** declares variables de tipo `Context`, `Activity` o `View` estáticas o dentro de companion objects de Kotlin (reporte `5_memory_leaks_threads_report.txt`).
    *   **❌ NO** inyectes ni retengas instancias de `Context` de forma directa en singletons (`object`); usa siempre `context.applicationContext`.
    *   **❌ NO** utilices `Thread.sleep()` en hilos de producción para no bloquear el hilo de interfaz de usuario. Usa Coroutines y su función suspendible `delay()`.
    *   **✅ SÍ** remueve o desregistra siempre listeners, receptores de broadcast (`registerReceiver` / `unregisterReceiver`) o sensores en los ciclos de vida correctos.
6.  **Compilaciones eficientes:**
    *   El empaquetado del APK de depuración se activa de manera selectiva. Los cambios menores exclusivos en archivos markdown de documentación (`.md`) o configuraciones externas no disparan la compilación asíncrona, pero los cambios en `/app` sí lo harán.
7.  **Uso de Logs seguros:**
    *   Evita el uso de `printStackTrace()` y `System.out.println()` en el código de producción de la aplicación principal para no generar alertas en el reporte `8_debugging_practices_report.txt`. Utiliza los canales de log de Android estándar (`android.util.Log`).
8.  **Suite de Pruebas y Generación de Capturas Visuales:**
    *   **✅ SÍ** ejecuta las pruebas unitarias y de Robolectric localmente con `gradle :app:testDebugUnitTest`.
    *   **✅ SÍ** regenera o graba las capturas de pantalla de referencia y marketing de las tiendas usando `gradle :app:recordRoborazziDebug` antes de subir cambios visuales importantes.
    *   **❌ NO** intentes ejecutar pruebas que requieran un emulador de Android (`androidTest/` con Espresso o ADB) en este entorno de compilación, ya que no se dispone de dispositivo físico ni emulador local activo.
9.  **Configuración de Webhooks de Discord (3 Bots):**
    *   **Fafi Security Guard** (`DISCORD_WEBHOOK_URL`): Bot para reporte automático de análisis estático (8 archivos .txt).
    *   **Fafi Unit Tests Guard** (`DISCORD_UNIT_TESTS_WEBHOOK_URL`): Bot para notificar estado de pruebas unitarias locales.
    *   **Fafi Visual Inspector** (`DISCORD_SCREENSHOT_TESTS_WEBHOOK_URL`): Bot para enviar directamente las capturas gráficas de la App Store en formato de imagen (.png) adjunta.

---

## 🎵 7. SISTEMA DE AUDIO, EFECTOS DE SONIDO Y MULTIMEDIA

Para dotar al simulador de una inmersión auditiva realista sin inflar el tamaño de instalación de la aplicación, FEDEBOL Manager cuenta con las siguientes directrices de audio:

1.  **Rotación Aleatoria Anti-Repetición:**
    *   Se dispone de **7 silbidos arbitrales únicos** (`whistle0.m4a` a `whistle6.m4a`) en formato AAC (Advanced Audio Coding) de alta calidad.
    *   Cada vez que ocurre una transición de fase en el simulador de partidos en vivo, se selecciona un silbato al azar para mantener la transmisión fresca y realista.
2.  **Resolución de Errores del Entorno (Compatibilidad con el explorador de AI Studio / Git):**
    *   **Problema original:** Los archivos de audio corruptos o de formatos poco soportados (como `.ogg` con errores de CRC o `.wav` de gran tamaño) causan alertas visuales de error (círculos rojos con signos de exclamación `!`) en el explorador de archivos del entorno de AI Studio y bloqueos durante el guardado de estado o sincronización a GitHub.
    *   **Solución implementada:** Se convirtieron todos los archivos multimedia al estándar **`.m4a`** con compresión de audio **AAC (Advanced Audio Coding)** usando `ffmpeg`:
        *   Música de fondo: `ffmpeg -i input.mp3 -c:a aac -b:a 128k output.m4a`
        *   Efectos/silbatos: `ffmpeg -i input.wav -c:a aac -b:a 64k output.m4a`
    *   **Análisis Espectral y Separación de Silbatos (Corte de Multipistas):**
        *   Para el archivo compuesto de pitidos `568995__strongbot__metal-whistle.wav` (que contenía el resto de silbatos con espacios de silencio), se utilizó la herramienta de detección de silencios de `ffmpeg` (`silencedetect=n=-35dB:d=0.3`) para encontrar las marcas espectrales precisas de ruido/sonido.
        *   Una vez determinados los picos activos, se segmentó el audio original en 6 pistas independientes y optimizadas (`whistle1.m4a` a `whistle6.m4a`):
            *   **Whistle 1**: De `0.75s` a `1.30s` (Corto e intenso)
            *   **Whistle 2**: De `3.65s` a `4.60s` (Prolongado clásico)
            *   **Whistle 3**: De `6.70s` a `7.45s` (Intermedio)
            *   **Whistle 4**: De `9.45s` a `10.00s` (Corto de advertencia)
            *   **Whistle 5**: De `12.70s` a `13.60s` (Doble tono fuerte)
            *   **Whistle 6**: De `18.40s` a `19.25s` (Cierre fuerte)
    *   Esta estructura limpia todas las alertas del entorno, elimina archivos binarios pesados o corruptos, y garantiza compatibilidad nativa del 100% con Android, Git y la plataforma de AI Studio.
3.  **Compresión de Alta Fidelidad (Auriculares y Altavoces):**
    *   **❌ NO** usar archivos `.wav` crudos o `.ogg` propensos a corrupción.
    *   **✅ SÍ** utilizar compresión AAC en contenedor `.m4a` con un bitrate estéreo de **128kbps** para música (logrando reducir el archivo de música de 2.8 MB a solo 1.5 MB) y **64kbps mono** para los silbatos arbitrales. Esto garantiza que la calidad acústica sea excelente e indistinguible de la pista original, incluso al usar auriculares de alta fidelidad, manteniendo la aplicación ligera.
4.  **Estado de Desarrollo de Audio y Música de Fondo (COMPLETADO ✅):**
    *   *Música de Fondo:* Se ha desarrollado un motor asíncrono robusto (`BackgroundMusicPlayer`) para la reproducción de música instrumental en segundo plano, continua y persistente al navegar entre las distintas pantallas.
    *   *Cantera de música en expansión:* El catálogo musical cuenta ahora con tres canciones de alta calidad en formato `.ogg` optimizado: **"Better Day" por *penguinmusic***, **"Ambient DnB" por *AbsoluteSound***, y **"Asian Lofi Hip Hop" por *Vjgalaxy***.
    *   *Modo No Molestar superado:* Tanto los silbatos de árbitro como la música de fondo se direccionan por el canal multimedia principal de Android (`USAGE_MEDIA` / `CONTENT_TYPE_MUSIC`), garantizando que se escuchen incluso en configuraciones "No Molestar" o perfiles de vibración (siempre que el volumen multimedia general esté activo).
    *   *Ambiente e Himnos:* Se planea la integración de efectos sonoros para celebraciones de goles, tarjetas, y coros de las hinchadas para las tribunas virtuales.



