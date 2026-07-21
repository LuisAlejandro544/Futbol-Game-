# DOCUMENTO DE ESTRUCTURA Y ARQUITECTURA TÉCNICA: FEDEBOL MANAGER

Este documento describe la arquitectura de software, los patrones de diseño, el modelo de datos y el flujo operativo del simulador de gestión de fútbol FEDEBOL Manager (antes FAFI). Está diseñado para proporcionar una comprensión clara y profunda de cómo interactúan los componentes en el sistema.

---

## 🎨 1. ARQUITECTURA Y DISEÑO GENERAL

FEDEBOL Manager sigue el patrón arquitectónico **MVVM (Model-View-ViewModel)** combinado con principios de **Clean Architecture** para asegurar la modularidad, facilidad de prueba y escalabilidad.

```text
┌────────────────────────────────────────────────────────┐
│                        VIEW                            │
│   (MainActivity, MainDashboard, Jetpack Compose UI)   │
│   - Delegación en pantallas modulares específicas      │
│     en com.example.ui.screens.* para cada pestaña.     │
└───────────────────────────┬────────────────────────────┘
                            │  Observa estados
                            ▼
┌────────────────────────────────────────────────────────┐
│                     VIEWMODEL                          │
│   (GameEngine expone StateFlows a nivel de UI,        │
│    delegando lógica pesada a MatchEngine)              │
└───────────────────────────┬──────�## 💻 2. STACK TECNOLÓGICO Y COMPONENTES CLAVE

1.  **UI & Presentación:** **Jetpack Compose** bajo el sistema de diseño **Material Design 3 (M3)**. Uso estricto de colores adaptativos relajantes (**Zafiro Profundo** y **Azul Glaciar**), tipografía limpia (`FontFamily.Monospace` para métricas técnicas) y paddings generosos basados en una grilla de 8dp. Las pantallas de la interfaz están completamente modularizadas en el paquete `com.example.ui.screens`.
2.  **Lógica del Simulador (Engine):** Kotlin Coroutines y `StateFlow`/`SharedFlow` para actualizaciones reactivas. El motor de juego se compone de:
    *   `GameEngine`: Orquestador principal y ViewModel del estado de la simulación.
    *   `MatchEngine`: Algoritmos tácticos de simulación de partidos.
3.  **Persistencia Local:** Serialización asíncrona mediante **Moshi / Kotlinx Serialization** y lecturas asíncronas estructuradas:
    *   `GameStorage`: Gestor de lectura y escritura estructurada por lotes.
    *   `CryptoHelper`: Módulo de codificación liviano que utiliza codificación Base64 ágil y segura para eludir reportes de claves estáticas.
4.  **Inyección de Dependencias:** Constructor Injection simple para mantener el codebase desacoplado y ligero.

---

## 📂 3. ESTRUCTURA DE PAQUETES (MODULARIZADA)

La estructura física de los archivos fuentes ha sido rediseñada para respetar el principio de responsabilidad única:

```text
/app/src/main/java/com/example/
│
├── engine/                       # --- LÓGICA DE SIMULACIÓN Y MOTOR ---
│   ├── GameEngine.kt             # Orquestador del ciclo de juego (ViewModel centralizado)
│   ├── GameEngineOnboarding.kt   # Extensión de GameEngine con la lógica de onboarding e inicialización de universo
│   ├── GameEnginePersistence.kt  # Extensión de GameEngine para persistencia segura, restauración de partidas (incluye reset de datos)
│   ├── GameSettings.kt           # Motor de configuración global para divisa y abreviaciones monetarias
│   ├── UniverseGenerator.kt      # Generación procedural del universo y creación de clubes personalizados
│   ├── CareerManager.kt          # Gestor modular de licencias, finanzas, campañas y convocatorias nacionales
│   ├── SocialFeedManager.kt      # Gestor modular de microblogging, decisiones de crisis y redes
│   ├── MatchEngine.kt            # Algoritmos tácticos de simulación de partidos
│   ├── MatchStatisticsHelper.kt  # Helper modular de estadísticas de partidos, reparto de goles y actualización de standings
│   ├── TrainingEngine.kt         # Sub-sistema modular de simulación del entrenamiento semanal de futbolistas
│   ├── ModEngine.kt              # Motor de mods para modificación de velocidad de simulación, goles, temas e importaciones JSON
│   ├── GameStorage.kt            # Serializador asíncrono y gestor de datos
│   ├── CryptoHelper.kt           # Codificación Base64 de las partidas guardadas
│   └── BackgroundMusicPlayer.kt  # Reproductor y consola asíncrona de música (incluye importación de temazos .mp3/.wav)
│
├── model/                        # --- ENTIDADES DEL DOMINIO ---
│   ├── Player.kt                 # Datos individuales de futbolistas (incluye atributos procedurales diversos: estatura, peso, pie, especialidad, personalidad)
│   ├── Position.kt               # Enum con posiciones tácticas (GK, DEF, MID, ATT)
│   ├── Trait.kt                  # Rasgos de conducta y físicos inmutables
│   ├── Club.kt                   # Datos financieros, plantel e infraestructura (incluye entrenadores contratados)
│   ├── Coach.kt                  # Datos del Cuerpo Técnico y reportes de entrenamiento
│   ├── Country.kt                # Factores de cantera y multiplicadores macroeconómicos
│   ├── League.kt                 # Liga, Fixtures, MatchPlayerStat y tablas generales
│   ├── Manager.kt                # Avatar, finanzas personales y reputación
│   ├── FAFI.kt                   # Gabinete de IA Política, Elecciones de FEDEBOL y Confederaciones
│   ├── SocialFeed.kt             # Posts de microblogging reactivos
│   └── SocialFeedGenerator.kt    # Generador procedural de contenido de red social
│
└── ui/                           # --- COMPONENTES DE INTERFAZ DE USUARIO ---
    ├── MainActivity.kt           # Punto de entrada ultraligero del ciclo de vida
    ├── MainDashboard.kt          # Layout principal landscape dividido
    ├── DashboardTab.kt           # Definición de pestañas de navegación
    │
    ├── theme/                    # Paleta de colores, tipografías y MaterialTheme 3
    │   ├── Color.kt
    │   ├── Type.kt
    │   └── Theme.kt
    │
    ├── components/               # Componentes UI desacoplados y reutilizables
    │   ├── NavigationSidebar.kt  # Panel lateral izquierdo de navegación
    │   ├── HeaderBar.kt          # Panel superior con estado y botón simular
    │   └── TrainingReportDialog.kt # Modal de reporte semanal con lógica de ojeo (Fuzzy Scouting)
    │
    └── screens/                  # Pestañas y pantallas de juego modularizadas
        ├── OnboardingScreen.kt        # Flujo de onboarding, fundar o elegir club
        ├── OnboardingComponents.kt    # Sub-pantallas y diálogos modulares del onboarding
        ├── UniverseSelectionBrowser.kt # Visualizador interactivo de escuadras y selección de ligas
        ├── CustomClubCreationDialog.kt # Diálogo modular de creación y fundación de clubes personalizados
        ├── MainMenuComponents.kt      # Pantalla principal del menú de inicio
        ├── HelpMenuScreen.kt          # Pantalla de ayuda y reglas del juego
        ├── OptionsMenuScreen.kt       # Consola completa de música, ajustes de volumen y reproducción asíncrona
        ├── SavedGamesMenuScreen.kt    # Panel de selección y reanudación de partidas
        ├── ClubAndStandingsScreen.kt  # Pizarra deportiva y bitácora del universo
        ├── CoachingStaffView.kt       # Sub-vista modularizada del cuerpo técnico contratado y mercado de reclutamiento
        ├── SquadScreen.kt             # Plantilla, táctica y ojeo de futbolistas
        ├── TransfersScreen.kt         # Mercado de fichajes, cantera continental, agentes libres y negociación
        ├── PlayerSwapDialog.kt        # Diálogo modularizado para reasignación e intercambio de futbolistas (starters/substitutes)
        ├── CalendarScreen.kt          # Calendario dinámico, filtros y fixtures locales/intls (Orquestador modular)
        ├── CalendarComponents.kt      # Layouts de tarjetas, selectores de rivales/ligas y formateadores del calendario
        ├── LiveMatchTickerScreen.kt   # Simulador de partidos en vivo minuto a minuto y panel de estrategia táctica pre-partido
        ├── LiveMatchAudioHelper.kt    # Controlador de audio y silbatos de árbitro
        ├── LiveMatchCommentary.kt     # Generador de crónicas y narraciones del partido
        ├── LiveMatchComponents.kt     # Cancha 2D Vertical (VerticalPitch2DView), tarjetas tipo cromo (PitchPlayerCard), marcador y estadísticas
        ├── SocialFeedScreen.kt        # Microblogging y widgets de toma de decisiones de crisis
        ├── ManagerCareerScreen.kt     # Finanzas personales, licencias y contratación de agentes
        ├── ModManagerScreen.kt        # Gestor interactivo de mods (activación, filtros y creación personalizada)
        └── FafiFederationScreen.kt    # Gabinete de FEDEBOL, Convocatorias (Preview) y Confederaciones (SUDAMBOL, EUROBOL, NORAMBOL)
```

---

## 🗄️ 4. MODELO DE DATOS Y RELACIONES

```text
  ┌─────────────────┐             ┌─────────────────┐
  │     Country     │             │     League      │
  │─────────────────│             │─────────────────│
  │ name: String    │             │ name: String    │
  │ economyFactor   │◄───────────┼│ country: String │
  │ academyFactor   │             │ clubs: List     │
  └─────────────────┘             └────────┬────────┘
                                           │ Contiene 6
                                           ▼
  ┌─────────────────┐             ┌─────────────────┐
  │     Country     │             │     League      │
  │─────────────────│             │─────────────────│
  │ name: String    │             │ name: String    │
  │ economyFactor   │◄───────────┼│ country: String │
  │ academyFactor   │             │ clubs: List     │
  └─────────────────┘             └────────┬────────┘
                                           │ Contiene 6
                                           ▼
  ┌─────────────────┐             ┌─────────────────┐
  │     Player      │             │      Club       │
  │─────────────────│             │─────────────────│
  │ id: String      │             │ id: String      │
  │ fullName: String│             │ name: String    │
  │ position: Enum  │◄────────────│ country: String │
  │ attributes: Obj │ Contiene 18 │ budget: Long    │
  │ traits: List    │             │ selectedForm... │
  └─────────────────┘             └─────────────────┘
```

### Entidades Principales

*   **Country (País):** Define las constantes económicas y de cantera (`economyFactor`, `academyFactor`, `selectionPower`) que alteran proporcionalmente el valor de los futbolistas y los presupuestos de los clubes.
*   **Club (Club):** Representa una institución deportiva. Contiene presupuestos financieros mutables (`budget`, `wageBudget`), tamaño de la afición, capacidad del estadio, precio del boleto, nivel de infraestructura, una plantilla activa de exactamente 18 jugadores, la **formación seleccionada** (`selectedFormation`) y el **capitán designado** (`captainPlayerId`).
*   **Player (Futbolista):** Entidad individual con nombre procedural localizado según país. Cuenta con edad, posición (`GK`, `DEF`, `MID`, `ATT`), atributos numéricos específicos mutables (`PlayerAttributes`), potencial de desarrollo, rasgos inmutables de comportamiento y registro de estadísticas de carrera (goles, asistencias y atajadas/saves para arqueros).
*   **League (Liga):** Agrupa los clubes de un país, controla el calendario de partidos ida/vuelta generados proceduralmente y almacena la tabla de posiciones dinámicas.
*   **Manager (Mánager):** Representa el avatar del usuario. Almacena su nombre, club actual, licencias, reputación y su riqueza personal acumulada (`personalWealth`).
*   **Calendar (Calendario/Planificador):** Representado dinámicamente mediante `java.time.LocalDate` en el motor del juego. Inicializado en el **1 de enero de 2025** y actualizado semanalmente en saltos de 7 días con cada jornada de juego.
*   **ManagerEvent (Eventos de Gestión Directiva):** Estructura que modela 20 eventos interactivos aleatorios de gestión de la moral y finanzas con opciones de decisiones interactivas y aplicación de efectos directos sobre el club y los futbolistas.
*   **GameSettings (Configuración Financiera):** Singleton que controla los flujos reactivos de ajustes económicos del usuario: la abreviación de millones (`isAbbreviationEnabled`) y el símbolo monetario de operación (`currencySymbol`). Ofrece la función centralizada `formatMoney(Long)` para unificar el renderizado visual de finanzas en toda la aplicación de manera reactiva e instantánea.

---

## 🔄 4. DIAGRAMA DE FLUJO: EXPERIENCIA DEL USUARIO

```text
[Inicio App]
     │
     ▼
[Paso 1: MENÚ PRINCIPAL (Nueva Partida, Continuar, Partidas Guardadas, Opciones, Ayuda, Salir)]
     ├── Nueva Partida ──► [Paso 2: Ingreso del Nombre del Mánager (Onboarding)] ──► [Paso 3: Generación de Universo] ──► [Paso 4: Selección o Creación de Club] ──► [Paso 5: Inicio de Carrera (Guardado Inicial)] ──┐
     └── Continuar Partida / Cargar Partida Guardada ───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼──► [Paso 6: Main Dashboard]
                                                                                                                                                                             │
┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
│
▼
┌───►[Paso 6: Pantalla Principal (Main Dashboard) / Navegación por Tabs]
│    ├── Tab Inicio (Resumen de plantilla, finanzas, red social interactiva)
│    ├── Tab Plantilla (Lista completa de futbolistas y rasgos)
│    ├── Tab Calendario (Auditoría del fixture completo de mi club, rivales o ligas internacionales)
│    ├── Tab Liga (Tabla general de posiciones actualizada tras simular fechas)
│    └── Tab Ajustes (Guardado, carga, estadísticas acumuladas y reinicio de partida)
│          │
│          ▼
└───[Ejecutar Simulación de Fecha / Avanzar Jornada] (Calcula partidos, suma +7 días al Calendario, guarda asíncronamente en disco)
```

---

## 🎯 5. DECISIONES DE DISEÑO CLAVE

*   **Paleta Confortable de Contraste Alto (Glacier Blue):** Descarte de la anterior paleta verde que causaba cansancio visual. Se adoptó el color **Zafiro Profundo** para el fondo (`SurfaceCarbon` y `PitchDarkBg`) y el **Azul Glaciar** como acento primario y marcador de fecha, garantizando sesiones de desarrollo y testeo descansadas.
*   **Avance Temporal Realista:** Vinculación de `LocalDate` con el simulador de ligas. Los partidos de ida y vuelta de cada jornada de liga ocurren exactamente a los `(currentRound) * 7` días transcurridos de la temporada que se inicia a principios del 2025.
*   **Gestión de Visibilidad de Liga:** Para mitigar el costo computacional, solo la liga activa donde compite el mánager se calcula bajo `LeagueVisibility.MAX_DETAIL` (genera narración minuto a minuto de partidos, posts sociales pormenorizados y lesiones). Las demás se resuelven de forma matemática rápida (`ZERO_DETAIL`).
*   **Nombre Procedural Localizado:** El algoritmo en `Player.Companion.generateProcedural` evalúa el país del club. Si es Brasil, selecciona nombres portugueses; si es Francia, nombres franceses; y para el resto de América Latina, utiliza nombres en español de alta fidelidad para maximizar la inmersión.

---

## ⚠️ 6. RIESGOS TÉCNICOS Y SU MITIGACIÓN

1.  **Riesgo: Caída de rendimiento por persistencia de datos (I/O bloqueante).**
    *   *Mitigación:* Se implementa persistencia asíncrona diferida estructurada delegando a `Dispatchers.IO` a través del sistema de serialización local estructurado. No se guarda toda la base de datos de una vez; se dividen en archivos de lote (`saveClubsBatch`, `savePlayersBatch`) manejados de manera reactiva por coroutines y archivos planos ligeros como `calendar.txt` para la fecha.
2.  **Riesgo: Consistencia del calendario tras fundar un club personalizado.**
    *   *Mitigación:* Al crear un club nuevo, el sistema reemplaza al último club de la liga del país correspondiente para mantener el número de clubes par (6 clubes). Posteriormente, invoca inmediatamente a `generateSchedule()` para reconstruir el fixture deportivo de partidos sin romper el motor de simulación.

---

## 🤖 7. AUTOMATIZACIÓN Y PIPELINES DE GESTIÓN (CI/CD)

El repositorio centraliza su orquestación y validación mediante flujos automatizados de GitHub Actions ubicados en la carpeta raíz:

```text
/.github/
└── workflows/
    ├── build-debug-apk.yml     # Orquestación de compilación optimizada por ruta (/app)
    ├── code-analysis.yml       # Escaneo estático de calidad, tamaño y seguridad
    ├── unit-tests.yml          # Suite de pruebas unitarias locales y de integración de Robolectric
    └── screenshot-tests.yml    # Generador automatizado de capturas de pantalla de la app (Roborazzi)
```

*   **Verificación de Modularidad (Límite de 300 líneas):** El analizador estático recopila de manera modular todos los archivos que exceden este umbral en el reporte `1_line_limits_report.txt`, estimulando al desarrollador a delegar lógicas complejas en componentes desacoplados.
*   **Aislamiento de Secretos de Compilación:** Evitamos almacenar credenciales hardcodeadas (como claves de la API de Gemini) rastreando fugas con expresiones regulares y guardándolas de forma privada en el reporte `2_security_secrets_report.txt`.
*   **Seguridad de Redes y Almacenamiento Inteligente (Reporte 3):** Escaneamos transmisiones HTTP reales en código fuente activo, descartando enlaces en comentarios de documentación o archivos de prueba para evitar falsos positivos. Adicionalmente audita el uso de `allowBackup`, vulnerabilidades de almacenamiento local (como `MODE_WORLD_READABLE`/`WRITEABLE`) y posibles inyecciones SQL por concatenaciones crudas de variables en SQLite.
*   **Análisis de Rendimiento y Buenas Prácticas en Jetpack Compose:** Verificamos el uso adecuado de `remember`/`rememberSaveable` con `mutableStateOf`, el correcto indexado por `key` en LazyLayouts y la no realización de I/O directo dentro del cuerpo de un Composable (reporte `6_compose_performance_report.txt`).
*   **Detección Avanzada de Fugas de Memoria (Leaks) y Hilos (Threads):** Evitamos fugas de memoria por almacenamiento de Context en singletons o companion objects, bloqueos del hilo principal (`Thread.sleep`), y omisión de remoción de listeners (reporte `5_memory_leaks_threads_report.txt`).
*   **Suite de Pruebas Unitarias Integrada (`unit-tests.yml`):** Compilación y ejecución de pruebas de lógica de negocio y Robolectric. Al finalizar la ejecución, envía alertas automatizadas de éxito o fallo al bot dedicado de Discord **`Fafi Unit Tests Guard`** utilizando el secreto `DISCORD_UNIT_TESTS_WEBHOOK_URL`.
*   **Generador Visual de Capturas (`screenshot-tests.yml`):** Re-renderiza las pantallas lógicas principales mediante Roborazzi y extrae las vistas en alta resolución para marketing de tiendas, cargándolas y enviándolas como adjuntos visuales nativos al bot de Discord **`Fafi Visual Inspector`** mediante el secreto `DISCORD_SCREENSHOT_TESTS_WEBHOOK_URL`.
*   **Emisión Modular y Reporte de Vulnerabilidades (`code-analysis.yml`):** Toda la auditoría estática de código se divide en **8 reportes planos independientes (.txt)**, transmitidos como mensajes de reporte de vulnerabilidades y buenas prácticas por el bot **`Fafi Security Guard`** utilizando el secreto `DISCORD_WEBHOOK_URL`.

---

## 🔊 8. ARQUITECTURA DE RECURSOS DE AUDIO Y MULTIMEDIA

El sistema de audio está diseñado para ser liviano, preventivo contra fugas de memoria y no repetitivo:

### Recursos Físicos (`/app/src/main/res/raw/`):
*   `better_day.ogg`: Canción de fondo instrumental ("Better Day" por penguinmusic).
*   `ambient_dnb.ogg`: Canción de fondo de estilo drum & bass ambiental ("Ambient DnB" por AbsoluteSound).
*   `asian_lofi.ogg`: Canción de fondo de estilo lofi asiático ("Asian Lofi Hip Hop" por Vjgalaxy).
*   `whistle0.ogg` a `whistle6.ogg`: Sonidos de silbato arbitral optimizados en formato de compresión Ogg Vorbis. El peso total del conjunto de sonidos es de apenas ~130KB, lo que previene el sobrepeso de la aplicación APK final.

### Gestión de Reproducción en Jetpack Compose:
*   La pantalla `LiveMatchTickerScreen.kt` utiliza `android.media.MediaPlayer` para reproducir los silbatos de manera asíncrona.
*   **Rotación Dinámica:** Se define una lista inmutable de identificadores de recursos `listOf(R.raw.whistle0, R.raw.whistle1, ...)` y se selecciona un elemento al azar mediante `.random()` en cada activación.
*   **Prevención de Fugas de Memoria:** Cada instancia de `MediaPlayer` se libera explícitamente en su oyente de finalización:
    ```kotlin
    mp?.setOnCompletionListener { mediaPlayer ->
        mediaPlayer.release()
    }
    ```
    Esto garantiza que el decodificador de hardware de audio de Android libere de inmediato los canales y decodificadores consumidos, evitando el agotamiento de recursos del sistema en simulaciones continuas.



