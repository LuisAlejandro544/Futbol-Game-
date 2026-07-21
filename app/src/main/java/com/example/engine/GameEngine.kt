package com.example.engine

import android.content.Context
import android.util.Log
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random

class GameEngine(internal val context: Context) {

    internal val storage = GameStorage(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Calendar State
    internal val _currentDate = MutableStateFlow(LocalDate.of(2025, 1, 1))
    val currentDate: StateFlow<LocalDate> = _currentDate

    // Game state observables
    internal val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    internal val _ligas = MutableStateFlow<List<League>>(emptyList())
    val ligas: StateFlow<List<League>> = _ligas

    internal val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs: StateFlow<List<Club>> = _clubs

    internal val _manager = MutableStateFlow(Manager("Mánager Anónimo"))
    val manager: StateFlow<Manager> = _manager

    internal val _fafi = MutableStateFlow(FAFI.createDefault())
    val fafi: StateFlow<FAFI> = _fafi

    internal val _socialFeed = MutableStateFlow<List<SocialPost>>(emptyList())
    val socialFeed: StateFlow<List<SocialPost>> = _socialFeed

    internal val _currentLiveMatch = MutableStateFlow<Match?>(null)
    val currentLiveMatch: StateFlow<Match?> = _currentLiveMatch

    internal val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating

    internal val _newsLog = MutableStateFlow<List<String>>(emptyList())
    val newsLog: StateFlow<List<String>> = _newsLog

    internal val _isOnboardingFinished = MutableStateFlow(false)
    val isOnboardingFinished: StateFlow<Boolean> = _isOnboardingFinished

    // Diary State (Manager plain-text journal logs)
    internal val _diaryLines = MutableStateFlow<List<String>>(emptyList())
    val diaryLines: StateFlow<List<String>> = _diaryLines

    // Training & Coaching staff State
    internal val _currentTrainingReport = MutableStateFlow<WeeklyTrainingReport?>(null)
    val currentTrainingReport: StateFlow<WeeklyTrainingReport?> = _currentTrainingReport

    internal val _availableCoachesToHire = MutableStateFlow<List<Coach>>(emptyList())
    val availableCoachesToHire: StateFlow<List<Coach>> = _availableCoachesToHire

    fun generateCandidateCoaches(): List<Coach> {
        val specialities = listOf("Ataque", "Defensa", "Porteros", "Físico", "Mental")
        val firstNames = listOf("Carlos", "Gastón", "Mauricio", "Jorge", "Guillermo", "Marcelo", "Hernán", "Gustavo", "Eduardo", "Claudio")
        val lastNames = listOf("Bianchi", "Gallardo", "Pochettino", "Simeone", "Almada", "Scaloni", "Crespo", "Sampaoli", "Bielsa", "Pizzi")
        val random = Random
        
        return specialities.map { spec ->
            val name = "${firstNames.random(random)} ${lastNames.random(random)}"
            val level = random.nextInt(1, 6) // 1 to 5 stars
            val salary = (level * 400L + random.nextInt(100, 400)).coerceIn(300L, 3000L)
            Coach(name = name, speciality = spec, level = level, salary = salary)
        }
    }

    init {
        _availableCoachesToHire.value = generateCandidateCoaches()
    }

    // Active Managerial Event State (Choice-based random events)
    internal val _currentActiveEvent = MutableStateFlow<ManagerEvent?>(null)
    val currentActiveEvent: StateFlow<ManagerEvent?> = _currentActiveEvent

    // Sub-systems modular managers
    private val careerManager = CareerManager(
        managerFlow = _manager,
        clubsFlow = _clubs,
        storage = storage,
        scope = scope,
        addNews = { addNews(it) }
    )

    private val socialFeedManager = SocialFeedManager(
        socialFeedFlow = _socialFeed,
        managerFlow = _manager,
        clubsFlow = _clubs,
        addNews = { addNews(it) }
    )

    // Advance 1 Round (Main logic loop executed strictly outside UI Thread)
    suspend fun advanceRound() = withContext(Dispatchers.Default) {
        if (_isSimulating.value) return@withContext
        _isSimulating.value = true
        addNews("Iniciando simulación de la jornada...")

        val activeLeagues = _ligas.value
        val activeClubs = _clubs.value.associateBy { it.id }

        // Process Match Simulation per League depending on visibility levels
        activeLeagues.forEach { league ->
            val fixtures = league.fixtures
            val roundIdx = league.currentRound

            if (roundIdx < fixtures.size) {
                val round = fixtures[roundIdx]
                addNews("Simulando fecha ${round.roundNumber} de la ${league.name}...")

                round.matches.forEach { match ->
                    val homeClub = activeClubs[match.homeClubId]
                    val awayClub = activeClubs[match.awayClubId]

                    if (homeClub != null && awayClub != null && !match.played) {
                        MatchEngine.simulateMatch(match, homeClub, awayClub, league.visibility)

                        // If it's the manager's club, log the match result to the diary!
                        val userClubId = _manager.value.currentClubId
                        if (homeClub.id == userClubId || awayClub.id == userClubId) {
                            val goalsUs = if (homeClub.id == userClubId) match.homeGoals else match.awayGoals
                            val goalsThem = if (homeClub.id == userClubId) match.awayGoals else match.homeGoals
                            val opponentName = if (homeClub.id == userClubId) awayClub.name else homeClub.name
                            val isHome = homeClub.id == userClubId

                            val prefix = if (goalsUs > goalsThem) "🟢 [Victoria]" else if (goalsUs < goalsThem) "🔴 [Derrota]" else "🟡 [Empate]"
                            val location = if (isHome) "en casa" else "como visitante"

                            logToDiary("$prefix [${_currentDate.value}] Jugamos contra $opponentName $location. Resultado: $goalsUs - $goalsThem.")
                        }
                    }
                }

                league.currentRound++
            } else {
                addNews("La ${league.name} ha concluido la temporada. Reseteando tabla...")
                league.clubs.forEach { it.resetStats() }
                league.generateSchedule()
            }
        }

        // Process Manager Salary & Career Finances
        val currentMgr = _manager.value
        val activeClub = _clubs.value.firstOrNull { it.id == currentMgr.currentClubId }
        if (activeClub != null) {
            // Manager weekly payout based on reputation and club tier
            var salaryEarned = (currentMgr.reputation * 30 + activeClub.stadiumCapacity / 1000 * 50).toLong().coerceIn(1000L, 10000L)
            if (currentMgr.isSummoned) {
                salaryEarned += 2500L // Extra $2,500 for national team duties
            }
            currentMgr.personalWealth += salaryEarned
            addNews("Recibiste tu salario semanal de $${salaryEarned} como mánager del ${activeClub.name}${if (currentMgr.isSummoned) " y tu labor de Selección Nacional" else ""}.")

            // Deduct staff weekly salaries from club budget
            val totalStaffSalaries = activeClub.coaches.sumOf { it.salary }
            if (totalStaffSalaries > 0) {
                activeClub.budget = (activeClub.budget - totalStaffSalaries).coerceAtLeast(0L)
                addNews("💼 SEGUIMIENTO FINANCIERO: Se pagaron $${totalStaffSalaries} en salarios de tu cuerpo técnico desde el presupuesto del club.")
            }

            // Run weekly training session
            runWeeklyTraining(activeClub)

            // Refresh available coaches in job market
            _availableCoachesToHire.value = generateCandidateCoaches()
        }

        // Auto-increment national team summon progress slowly if not summoned
        if (!currentMgr.isSummoned && currentMgr.nationalSummonProgress < 100) {
            val randomIncrement = Random.nextInt(2, 6) + (currentMgr.reputation / 25)
            currentMgr.nationalSummonProgress = (currentMgr.nationalSummonProgress + randomIncrement).coerceAtMost(100)
            if (currentMgr.nationalSummonProgress >= 100) {
                addNews("🦁 CONVOCATORIA DISPONIBLE: ¡Tu reputación continental es excelente! Tienes una oferta de Selección Nacional en el Gabinete.")
            }
        }

        // Process FEDEBOL President Election cycle
        val currentFafi = _fafi.value
        currentFafi.yearsUntilElection--
        if (currentFafi.yearsUntilElection <= 0) {
            val announcement = currentFafi.triggerElectionEvent()
            addNews("🗳️ ELECCIONES FEDEBOL: $announcement")
            _newsLog.value = (_newsLog.value + "FEDEBOL: ${currentFafi.currentRuleSet}")
        }
        _fafi.value = currentFafi

        // Trigger dynamic player events & Social Feed conflicts
        generateRandomSocialCrisis()

        // Process injury recoveries across all clubs
        _clubs.value.forEach { club ->
            club.squad.forEach { player ->
                val recoveryMsg = player.processInjuryRecovery()
                if (recoveryMsg != null) {
                    addNews(recoveryMsg)
                    if (club.id == _manager.value.currentClubId) {
                        scope.launch { logToDiary(recoveryMsg) }
                    }
                }
            }
        }

        // Trigger random managerial event (choice-based)
        triggerRandomManagementEvent()

        // Advance date by 7 days (weekly matches)
        _currentDate.value = _currentDate.value.plusDays(7)

        // Batch save to slow storage asynchronously (preventing UI bottleneck)
        addNews("Guardando estado del universo de forma asíncrona en lotes...")
        storage.saveManager(_manager.value)
        storage.saveCalendarDate(_currentDate.value.toString())
        storage.saveClubsBatch(_clubs.value)
        storage.saveLigasBatch(_ligas.value)

        _isSimulating.value = false
        addNews("Jornada simulada exitosamente. Revisa el feed social y las tablas.")
    }

    // Configures active tactical formation for the manager's club
    fun updateClubFormation(formation: String) {
        val managerClub = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId }
        if (managerClub != null) {
            managerClub.selectedFormation = formation
            _clubs.value = _clubs.value.toList()
            scope.launch {
                logToDiary("📋 Cambiamos la táctica a la formación: $formation.")
            }
        }
    }

    // Designates a specific player as team leader/captain
    fun setClubCaptain(playerId: String) {
        val managerClub = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId }
        if (managerClub != null) {
            managerClub.captainPlayerId = playerId
            _clubs.value = _clubs.value.toList()
            val player = managerClub.squad.firstOrNull { it.id == playerId }
            if (player != null) {
                scope.launch {
                    logToDiary("👑 Designamos a ${player.fullName} como nuevo líder y capitán del equipo.")
                }
            }
        }
    }

    // Swaps a starting player and a substitute player in the roster
    fun swapStarterAndSubstitute(starterPlayerId: String, substitutePlayerId: String) {
        val club = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId } ?: return
        val starter = club.squad.firstOrNull { it.id == starterPlayerId }
        val substitute = club.squad.firstOrNull { it.id == substitutePlayerId }
        if (starter != null && substitute != null) {
            starter.isStarter = false
            substitute.isStarter = true
            _clubs.value = _clubs.value.toList() // trigger Compose state update
            scope.launch {
                logToDiary("🔄 Alineación: Cambiamos a ${starter.fullName} por ${substitute.fullName} en la pizarra táctica.")
                storage.saveClubsBatch(_clubs.value)
            }
        }
    }

    // Re-simulate second half of user match (e.g. after mid-game substitutions)
    fun reSimulateUserMatchSecondHalf() {
        val userClubId = _manager.value.currentClubId
        _ligas.value.forEach { league ->
            val fixtures = league.fixtures
            val roundIdx = (league.currentRound - 1).coerceAtLeast(0)
            if (roundIdx < fixtures.size) {
                val round = fixtures[roundIdx]
                val match = round.matches.firstOrNull { it.homeClubId == userClubId || it.awayClubId == userClubId }
                if (match != null && match.played) {
                    val homeClub = _clubs.value.firstOrNull { it.id == match.homeClubId }
                    val awayClub = _clubs.value.firstOrNull { it.id == match.awayClubId }
                    if (homeClub != null && awayClub != null) {
                        scope.launch {
                            MatchEngine.reSimulateSecondHalf(match, homeClub, awayClub)
                            _ligas.value = _ligas.value.toList() // trigger state update
                            _clubs.value = _clubs.value.toList() // trigger state update
                            storage.saveLigasBatch(_ligas.value)
                            storage.saveClubsBatch(_clubs.value)
                        }
                    }
                }
            }
        }
    }

    // Dynamic choice-based event trigger
    fun triggerRandomManagementEvent() {
        val managerClub = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId }
        if (managerClub != null && Random.nextFloat() < 0.35f) { // 35% chance
            val randomEvent = EventDatabase.generateRandomEvent(managerClub)
            if (randomEvent != null) {
                _currentActiveEvent.value = randomEvent
                addNews("⚠️ EVENTO DE GESTIÓN: Un acontecimiento requiere tu atención inmediata.")
            }
        }
    }

    // Resolves active event option
    fun resolveActiveEvent(optionIndex: Int) {
        val event = _currentActiveEvent.value ?: return
        val managerClub = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId }
        if (managerClub != null) {
            val player = managerClub.squad.firstOrNull { it.id == event.affectedPlayerId }
            if (player != null) {
                val option = event.options.getOrNull(optionIndex)
                if (option != null) {
                    val resultText = option.applyEffect(managerClub, player)
                    addNews("📢 DECISIÓN: ${option.feedback} $resultText")
                    
                    // Specific budget effects
                    if (option.effectDescription.contains("+5000 Presupuesto")) {
                        managerClub.budget += 5000L
                    } else if (option.effectDescription.contains("-3000 Presupuesto")) {
                        managerClub.budget = (managerClub.budget - 3000L).coerceAtLeast(0L)
                    } else if (option.effectDescription.contains("-2000 Presupuesto")) {
                        managerClub.budget = (managerClub.budget - 2000L).coerceAtLeast(0L)
                    } else if (option.effectDescription.contains("-4000 Presupuesto")) {
                        managerClub.budget = (managerClub.budget - 4000L).coerceAtLeast(0L)
                    } else if (option.effectDescription.contains("+1000 Presupuesto")) {
                        managerClub.budget += 1000L
                    }
                    
                    scope.launch {
                        logToDiary("📋 [Decisión] Evento '${event.title}' de ${player.fullName}. Tomamos la opción: ${option.text}.")
                    }
                }
            }
        }
        _currentActiveEvent.value = null
    }

    // Handles Manager social network feedback decisions
    fun handleSocialFeedDecision(postId: String, decisionIndex: Int) {
        socialFeedManager.handleSocialFeedDecision(postId, decisionIndex)
    }

    // Dynamic Microblogging generator for player egos & disciplines
    fun generateRandomSocialCrisis() {
        socialFeedManager.generateRandomSocialCrisis()
    }

    suspend fun logToDiary(entry: String) {
        storage.appendToDiary(entry)
        _diaryLines.value = storage.loadDiary()
    }

    internal fun addNews(news: String) {
        val log = _newsLog.value.toMutableList()
        log.add(0, news)
        if (log.size > 40) {
            _newsLog.value = log.take(40)
        } else {
            _newsLog.value = log
        }
        Log.i("GameEngine", news)
    }

    // Recover player energies and decrease injury timers
    fun advanceWeekRecovery() {
        val currentClubs = _clubs.value
        currentClubs.forEach { club ->
            club.squad.forEach { player ->
                // Infinite lungs recovery boost
                val staminaRefill = if (player.traits.contains(Trait.PULMON_INFINITO)) 18 else 12
                player.energy = (player.energy + staminaRefill).coerceAtMost(100)

                if (player.isInjured) {
                    player.injuryDurationWeeks--
                    if (player.injuryDurationWeeks <= 0) {
                        player.isInjured = false
                        player.injuryDurationWeeks = 0
                        addNews("🏥 MÉDICO: ${player.fullName} se ha recuperado de su lesión y está disponible.")
                    }
                }
            }
        }
    }

    fun purchaseLicense(type: String): Boolean {
        return careerManager.purchaseLicense(type)
    }

    fun hirePrivateAgent(): Boolean {
        return careerManager.hirePrivateAgent()
    }

    fun investInPRCampaign(): Boolean {
        return careerManager.investInPRCampaign()
    }

    fun acceptNationalSummon(): Boolean {
        return careerManager.acceptNationalSummon()
    }

    fun resignNationalSummon(): Boolean {
        return careerManager.resignNationalSummon()
    }

    // Fired/Hired coaches logic
    fun hireCoach(speciality: String, level: Int, salary: Long, name: String): Boolean {
        val club = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId } ?: return false
        if (club.budget < salary) {
            return false
        }
        
        if (club.coaches.any { it.speciality == speciality }) {
            return false
        }
        
        val newCoach = Coach(name = name, speciality = speciality, level = level, salary = salary)
        club.coaches = club.coaches + newCoach
        _clubs.value = _clubs.value.toList() // trigger state update
        
        addNews("💼 CONTRATACIÓN: Contrataste a $name como ${newCoach.specialityLabel} (Nivel ${level}★) por $${salary}/semana.")
        scope.launch {
            logToDiary("💼 Contratamos a $name como ${newCoach.specialityLabel} (Nivel ${level}★).")
        }
        return true
    }

    fun fireCoach(coachId: String) {
        val club = _clubs.value.firstOrNull { it.id == _manager.value.currentClubId } ?: return
        val coach = club.coaches.firstOrNull { it.id == coachId } ?: return
        club.coaches = club.coaches.filter { it.id != coachId }
        _clubs.value = _clubs.value.toList() // trigger state update
        
        addNews("💼 DESPIDO: Despediste al ${coach.specialityLabel} ${coach.name}.")
        scope.launch {
            logToDiary("💼 Despedimos a ${coach.name} de nuestro cuerpo técnico.")
        }
    }

    fun runWeeklyTraining(club: Club) {
        _currentTrainingReport.value = TrainingEngine.runWeeklyTraining(
            club = club,
            currentDateString = _currentDate.value.toString(),
            addNews = ::addNews
        )
    }
}
