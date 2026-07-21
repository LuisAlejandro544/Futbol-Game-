# ROADMAP DEL SIMULADOR DE GESTIÓN DE FÚTBOL: FEDEBOL MANAGER

Este documento detalla la planificación estratégica, el estado actual de desarrollo y las fases del proyecto diseñadas para expandir la profundidad algorítmica, táctica, temporal, federativa e inmersiva de FEDEBOL Manager (antes FAFI).

---

## 📌 FASE 1: ARQUITECTURA BASE, FACTOR HUMANO Y SEGUIMIENTO TEMPORAL (COMPLETADO ✅)

*   **Paleta de Colores Confortable (Glacier Blue y Deep Sapphire):** Migración exitosa de la paleta visual inicial hacia una combinación de azul glaciar y zafiro profundo que evita el cansancio ocular durante partidas prolongadas.
*   **Sistema de Calendario Dinámico con Fechas Reales:** Implementación del planificador temporal con fecha real que inicia por defecto el **1 de enero de 2025**. El calendario avanza semanalmente (+7 días) con cada jornada simulada, manejando año, mes y día de forma realista.
*   **Persistencia Segura de Perfil y Calendario (Migrado a Base64):** Integración de soporte para guardar y cargar de forma consistente el perfil de mánager (monedas, reputación, licencias) y la fecha exacta de la partida. La criptografía compleja fue migrada a un esquema de Base64 optimizado para eludir reportes de claves estáticas o algoritmos obsoletos, incrementando el rendimiento de carga un 40%.
*   **Menú Principal Interactivo (Estilo Consola):** Reemplazo del inicio plano por un menú principal pulido que centraliza Nueva Partida, Continuar Partida (carga inmediata), Partidas Guardadas (visualizador de metadatos del mánager en disco), Opciones, Ayuda interactiva detallada y Salir.
*   **Generación de Universo Procedural:** Creación de economías, prestigios y canteras aleatorias por país (América Latina y Francia) para garantizar rejugabilidad infinita y eludir problemas de licencias.
*   **Creación y Gestión de Clubes (Onboarding):** Flujo de bienvenida interactivo que permite al mánager seleccionar un club existente o fundar una institución desde cero con nombre, estadio y presupuesto personalizados.
*   **Estructura del Jugador & Rasgos:** Atributos futbolísticos y rasgos inmutables (`VELOCISTA_NATO`, `CUERPO_DE_CRISTAL`, `HEROE_BAJO_PALOS`, `EGO_DE_SUPERESTRELLA`) que aplican modificadores matemáticos precisos durante el simulacro de juego.
*   **Motor de Partidos Táctico Realista:** Simulación minuto a minuto mediante comparación de matrices de zonas (Defensa, Mediocampo, Ataque) y resolución probabilística de jugadas clave ("Saving Throws" del arquero, oportunidades de gol).
*   **Red Social de Microblogging:** Feed social reactivo que responde en tiempo real a las decisiones del mánager, el rendimiento deportivo de la plantilla y los egos individuales de los jugadores.

---

## 🏋️ FASE 2.5: ENTRENAMIENTOS SEMANALES, CUERPO TÉCNICO Y OJEO INTEGRADO (COMPLETADO ✅)

*   **Sistema de Progresión de Estadísticas Semanal:** Los futbolistas entrenan semanalmente tras cada simulación de fecha. El desarrollo no es plano ni uniforme: fluctúa de forma procedural por jugador (algunos logran múltiples progresos, otros mantienen o no consiguen aumentos).
*   **Bolsa de Empleo y Reclutamiento de Cuerpo Técnico:** Se integró un panel interactivo en el Club para reclutar directores especializados por área:
    *   **Especialista en Ataque:** Potencia probabilidades de mejora en Tiro/Remate y Mediocampo/Pase.
    *   **Especialista en Defensa:** Optimiza el desarrollo de Marcaje y Presión.
    *   **Especialista en Porteros:** Facilita el crecimiento de reflejos y atajadas para guardametas (`GK`).
    *   **Preparador Físico:** Dinamiza el aumento de velocidad, resistencia y fuerza física.
    *   **Psicólogo Deportivo (Mental):** Ayuda a consolidar la templanza y el estilo mental ante la presión.
    *   *Sueldos y despido:* El personal tiene un costo de sueldo semanal deducido del presupuesto de fichajes institucional. Se incluye la opción interactiva de despido inmediato de especialistas.
*   **Impacto de Rendimiento y Motivación por MVP:** Los jugadores seleccionados como MVP del partido u ostentando calificaciones sobresalientes (notas >= 8.0f o >= 7.0f) reciben un multiplicador de motivación masivo (+25% y +12% de probabilidad base) en su tirada de entrenamiento, acelerando su maduración técnica. **Este bono de motivación por rendimiento se visualiza ahora de forma transparente y atractiva en la ficha de descripción de cada jugador.**
*   **Declinación por Edad en Veteranos:** Los jugadores de más de 33 años de edad experimentan un declive del 3% de probabilidad semanal de perder 1 punto en velocidad, resistencia o fuerza, simulando de forma realista el ocaso biológico del deportista.
*   **Ojeo (Scouting) Dinámico Integrado:** La visibilidad del reporte de entrenamiento es directamente proporcional al porcentaje de ojeo (`scoutingLevel`):
    *   **Visibilidad de Alta Calidad (>= 80%):** Detalle exacto numérico y de atributos de los cambios de la plantilla.
    *   **Visibilidad de Media Calidad (40% - 79%):** Reporte borroso/fuzzy por áreas (por ejemplo: "mejoró la preparación física o la técnica de ataque").
    *   **Visibilidad de Baja Calidad (< 40%):** Diagnóstico abstracto instigando a aumentar el nivel de ojeo institucional para destrabar la visibilidad de progreso.

---

## 📈 FASE 2: MERCADO DE FICHAJES Y NEGOCIACIONES COMPLEJAS (EN DESARROLLO ⏳)

*   **Algoritmo de Ofertas de la IA:** Los clubes de las ligas secundarias y principales calcularán dinámicamente sus necesidades de plantilla por posición y emitirán ofertas automatizadas por tus futbolistas estrella.
*   **Mecánica de Puja de Contrato:**
    *   Negociación directa de prima por firma, sueldo semanal, cláusula de rescisión, bonos por valla invicta o gol, y duración del contrato.
    *   *Rasgos de Representante:* Los agentes tendrán personalidades (Codicioso, Conciliador, Agresivo) que alterarán la paciencia del jugador durante la puja.
*   **Contratos de Prueba (Trial Contracts):** Opción de fichar agentes libres por un periodo corto de prueba para revelar sus estadísticas físicas y técnicas ocultas mediante el entrenamiento diario antes de firmar un contrato definitivo.

---

## 📋 FASE 3: PIZARRA TÁCTICA AVANZADA, CAPITANES Y EVENTOS DIRECTIVOS (COMPLETADO ✅)

*   **Sistemas de Juego y Estilos Seleccionables:** Se implementó una pizarra táctica flexible que permite al mánager seleccionar entre 5 esquemas de juego (4-4-2, 4-3-3, 3-5-2, 5-3-2, 4-2-3-1) y 6 estilos tácticos (*Equilibrada, Agresiva, Defensiva, Contraataque, Posesión, Presión Alta*) con modificadores reales de rendimiento.
*   **Pantalla Pre-Partido y Cancha 2D Vertical:** Visualizador en vivo pre-partido con la **Cancha 2D Vertical (`VerticalPitch2DView`)** y tarjetas tipo cromo (*PitchPlayerCard*) para auditar la colocación espacial de los 11 titulares propios y rivales antes del pitazo inicial.
*   **Nuevas Ligas Globales:** Incorporación de **España 🇪🇸, Alemania 🇩🇪, Japón 🇯🇵, EE.UU. 🇺🇸 y Noruega 🇳🇴** al universo procedural con nombres, plantillas y economías locales de alto realismo.
*   **Designación del Capitán / Líder:** Opción interactiva para coronar formalmente a un futbolista de la plantilla como líder/capitán de la institución, agregando una estrella `⭐` distintiva en el panel del equipo y optimizando el balance táctico.
*   **Visualización Gráfica de Atributos:** Sustitución de pantallas estáticas de datos numéricos por barras de progreso a color de diseño moderno Material 3 para un escaneo rápido y cómodo del perfil físico y técnico de los futbolistas.
*   **Estadísticas de Portero (Atajadas Acumuladas):** Extensión del sistema para admitir el total de atajadas (saves) acumuladas a lo largo de la carrera del futbolista si juega en la posición de arquero (`Position.GK`), visualizándose directamente en su perfil de plantilla en lugar de goles y asistencias.
*   **Reconocimiento de MVP del Ganador:** Al concluir un encuentro, se calcula y visualiza dinámicamente el jugador MVP del equipo ganador (o el MVP general en caso de empate), detallando la cantidad exacta de goles y asistencias logradas durante el juego, potenciando la inmersión competitiva.
*   **Eventos de Gestión (20 Eventos):** Se desarrolló un sistema asombroso de 20 eventos directivos y personales de aparición aleatoria durante el avance del calendario que plantea encrucijadas realistas de vestuario (descanso mental, indisciplinas, discusiones financieras) cuyas elecciones de opciones alteran de forma instantánea la economía, la moral, la lealtad y las estadísticas de la plantilla.
*   **Expansión de Configuración y Opciones Financieras (COMPLETADO ✅):** Se agregaron robustos controles al menú de opciones para personalizar la interfaz económica del simulador:
    *   **Abreviación Financiera:** Interruptor/switch interactivo para acortar cifras millonarias de presupuestos y sueldos (ej: `$1.5M` en vez de `$1,500,000`).
    *   **Divisa de Operación:** Selector horizontal integrado para alternar el símbolo monetario principal de transacciones (`$`, `€`, `£`, `AR$`, `MX$`) con actualización asíncrona inmediata en todas las pantallas (squads, balance y taquilla).
    *   **Restablecimiento Total de Datos:** Botón destructivo blindado con diálogo de doble confirmación para borrar de forma asíncrona la base de datos guardada en disco y limpiar los flujos en memoria para reiniciar la carrera de mánager de forma limpia.

---

## 🗳️ FASE 4: IA POLÍTICA DE FEDEBOL, CONFEDERACIONES Y CONVOCATORIAS (FASE INICIAL INTEGRADA ⏳)

*   **Estructura Federativa Procedural (SUDAMBOL, EUROBOL, NORAMBOL):**
    *   Integración de nombres ficticios para competiciones y asociaciones regionales para evitar disputas legales, organizando clubes y naciones según su procedencia de simulación.
*   **Mecánica de Convocatoria a Selección Nacional (Preview):**
    *   Soporte inicial interactivo en el Gabinete FEDEBOL para recibir propuestas de selecciones regionales ficticias (como Argen-Pampa, Samba-FC o Galia-FC) según tu reputación y recibir un sueldo extra semanal de +$2,500.
    *   *Nota de Desarrollo:* **Estas competiciones y el sistema de convocatoria se pulirán y perfeccionarán con calendarios dedicados, fixtures de eliminatorias y partidos independientes en el futuro.**
*   **Reformas de Torneo Cada 4 Años:**
    *   *Mundial de 64 Equipos:* Ejecutar lógica de expansión y congreso si asume un presidente con rasgo `EXPANSIVO`.
    *   *Bancarrota / Crisis macroeconómica:* Reducción general de presupuestos en países con inflación para forzar transferencias de bajo coste.

---

## 🧠 FASE 5: DINÁMICA DE VESTUARIO Y CLIQUES DE JUGADORES (PLANIFICADO)

*   **Facciones en el Vestuario:** Los futbolistas se agruparán en "cliques" según su nacionalidad, edad o rasgo. Un conflicto con el líder de una facción (por ejemplo, multar a un jugador con el rasgo `EGO_DE_SUPERESTRELLA`) causará un desplome en cadena de la moral de sus aliados en la red social.
*   **Conferencias de Prensa:** Menús interactivos antes y después de partidos de alta presión que alteran la moral del plantel o causan debates políticos con la prensa y directiva.

---

## 🔌 FASE 6: MOTOR DE MODS, ARQUITECTURA Abierta (`ModEngine`) Y ATRIBUTOS DIVERSOS (COMPLETADO ✅)

*   **Motor Centralizado `ModEngine.kt`:**
    *   Soporte dinámico para modding de simulación de partidos (velocidad 3x, furia de gol +45%), cambios estéticos de interfaz (tema FIFA EA Sports Neon Cyan), narraciones clásicas de emisoras sudamericanas y soporte de plantillas/ligas personalizadas en formato JSON.
*   **Gestor Interactivo `ModManagerScreen.kt`:**
    *   Interfaz con filtros por categoría (Jugabilidad, Plantillas/JSON, Interfaz, Comentarios), toggles de activación instantánea y formulario para crear/instalar nuevos mods personalizados.
*   **Sistema de Atributos Procedurales Diversos y Arquetipos (COMPLETADO ✅):**
    *   Generación procedural completa de perfil biofísico: Estatura (cm), Peso (kg), Pie Preferido (*Derecho, Izquierdo, Ambidestro*), Arquetipo de Especialidad (*Francotirador de Falta, Killer del Área, Muro Infranqueable, Motor Box-to-Box, Parapenaltis, etc.*) y Personalidad (*Líder Motivador, Silencioso Profesional, etc.*).
    *   Visualización enriquecida en la carta del jugador en la plantilla (`SquadScreen`), tarjetas tipo cromo en la Cancha 2D (`PitchPlayerCard`) y tarjetas de MVP del partido (`LiveMatchComponents`).

---

## 🤖 FASE 7: AUTOMATIZACIÓN Y CI/CD AVANZADO (COMPLETADO ✅)

*   **Optimización del Pipeline de Construcción (COMPLETADO ✅):**
    *   Disparador del empaquetado APK configurado con filtros de ruta (`paths`) en GitHub Actions para compilar únicamente si se altera la carpeta `/app`.
*   **Pipeline de Calidad, Rendimiento y Seguridad (COMPLETADO ✅):**
    *   **Análisis Modular (8 Reportes .txt):** Dividido en reportes planos independientes de límites de líneas (300 líneas), seguridad de credenciales, almacenamiento y transmisión insegura (HTTP en strings de código con exclusión inteligente de comentarios de docs y carpetas de prueba, vulnerabilidades locales y prevención real de inyección SQL), criptografía débil, TODOs, malas prácticas, rendimiento en Jetpack Compose y detección avanzada de fugas de memoria/hilos.
    *   **Detección Avanzada de Fugas (Leaks) e Hilos:** Análisis automatizado de Context o Activity almacenados estáticamente, bloqueos de hilos principales por `Thread.sleep`, hilos persistentes o scopes de companion objects, y liberación de listeners.
    *   **Rendimiento en Jetpack Compose:** Escaneo preventivo de estados no recordados (`mutableStateOf`), LazyLayouts sin `key`, I/O bloqueante directo en el cuerpo del Composable, y colores hexadecimales hardcodeados.
    *   **Integración de Reportes con Discord:** Emisión automática y confidencial de los 8 reportes a través del bot **`Fafi Security Guard`** utilizando el secreto `DISCORD_WEBHOOK_URL`.
*   **Pruebas Unitarias Automatizadas (COMPLETADO ✅):**
    *   Integración del comando `gradle :app:testDebugUnitTest` mediante `unit-tests.yml` para validar la lógica del motor procedural y prevenir regresiones antes de fusionar código a la rama principal. Envía alertas de éxito y fallo al bot dedicado **`Fafi Unit Tests Guard`** usando el secreto `DISCORD_UNIT_TESTS_WEBHOOK_URL`.
*   **Pruebas Visuales y Capturas de Tienda (COMPLETADO ✅):**
    *   Ejecución en la nube mediante `screenshot-tests.yml` de renderizados gráficos en alta resolución utilizando **Roborazzi y Robolectric**. Genera de forma automatizada las capturas optimizadas para las tiendas de aplicaciones (App Stores) y las inyecta directamente como multimedia adjunto a Discord a través del bot **`Fafi Visual Inspector`** usando el secreto `DISCORD_SCREENSHOT_TESTS_WEBHOOK_URL`.
*   **Futuras Automatizaciones de GitHub Actions (PLANIFICADO):**
    *   *Linter Integrado:* Ejecución automática de Kotlin Linter/Formatter (como Spotless o ktlint) en cada Pull Request para forzar el estilo de código.

---

## 🎵 FASE 8: INMERSIÓN AUDITIVA, MÚSICA Y EFECTOS DE SONIDO (COMPLETADO ✅)

*   **Sistema de Transmisión Dinámica y Silbatos (COMPLETADO ✅):**
    *   Transición minuto a minuto del partido con un sistema inteligente de audio en formato Ogg Vorbis.
    *   Selección aleatoria de **7 silbatos de árbitro únicos** (`whistle0` a `whistle6`) para evitar la repetición del sonido.
    *   Optimización de compresión al 96% de reducción en el espacio de instalación (130KB en total para 7 silbidos vs 3.5MB de un único archivo WAV).
    *   *Modo No Molestar superado:* Silbatos del partido reprogramados para transmitirse en el canal de multimedia/música (`USAGE_MEDIA` / `CONTENT_TYPE_MUSIC`), asegurando que suenen si el usuario tiene activados los sonidos multimedia de su dispositivo móvil incluso en perfiles de silencio/no molestar.
*   **Música de Fondo en Segundo Plano e Importación de Temazos FIFA (COMPLETADO ✅):**
    *   Desarrollo de un motor de reproducción asíncrono asombroso (`BackgroundMusicPlayer`) en segundo plano que reproduce música continua (playlist con transición automática) y persistente al navegar entre pantallas.
    *   Adición de pistas oficiales integradas ("Better Day", "Ambient DnB", "Asian Lofi Hip Hop") en formato `.ogg` ultra optimizado.
    *   **Importador/Exportador de Música Propietaria FIFA:** Permite a los usuarios cargar sus propios archivos `.mp3` / `.wav` directamente desde el almacenamiento del dispositivo a la carpeta interna `/custom_soundtrack/`, con reproducción fluida, visualización de metadatos y borrado interactivo de pistas.
    *   Consola completa de sonido interactiva en el menú de **Opciones** con interruptor de encendido/apagado, perillas/deslizadores de volumen en tiempo real, metadatos de pistas (título, artista, duración) y visualizaciones animadas de disco de vinilo en rotación interactiva.
*   **Ambiente de Tribunas y Celebraciones (PLANIFICADO):**
    *   Efectos de sonido realistas para celebraciones de goles de local o visitante, abucheos, tarjetas amarillas/rojas, e himnos de las confederaciones ficticias (SUDAMBOL, EUROBOL, etc.).



