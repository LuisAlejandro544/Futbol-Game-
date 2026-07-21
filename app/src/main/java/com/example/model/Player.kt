package com.example.model

import com.squareup.moshi.JsonClass
import java.util.UUID
import kotlin.random.Random

@JsonClass(generateAdapter = true)
data class PlayerAttributes(
    var attack: Int,      // Technical offensive
    var defense: Int,     // Technical defensive
    var midfield: Int,    // Tactical / passing
    var speed: Int,       // Speed
    var stamina: Int,     // Stamina
    var goalkeeper: Int,  // GK skill
    var mental: Int,      // Mental / pressure resistance
    var physical: Int     // Strength / physicality
)

@JsonClass(generateAdapter = true)
data class Player(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val age: Int,
    val country: String,
    val position: Position,
    val traits: List<Trait>,
    val attributes: PlayerAttributes,
    val marketValue: Long,
    val salary: Long,
    val contractYears: Int,
    var scoutingLevel: Int = 0, // 0 = Unknown, 100 = Fully Scouted
    var energy: Int = 100,      // 0 - 100%
    var isStarter: Boolean = true, // true = starter, false = bench/substitute
    var moral: Int = 80,        // 0 - 100%
    var matchPerformanceLast: Float = 6.0f,
    var isInjured: Boolean = false,
    var injuryDurationWeeks: Int = 0,
    var injuryName: String = "",
    var loyalty: Int = 50,              // 0 - 100
    var clubAppreciation: Int = 50,     // 0 - 100
    var goals: Int = 0,                 // Total career goals
    var assists: Int = 0,               // Total career assists
    var saves: Int = 0,                  // Total career goalkeeper saves
    val heightCm: Int = 178,            // Height in cm
    val weightKg: Int = 74,             // Weight in kg
    val preferredFoot: String = "Derecho", // Preferred foot ("Derecho", "Izquierdo", "Ambidestro")
    val specialty: String = "Todoterreno",  // Distinct specialty / archetype badge
    val personality: String = "Silencioso Profesional" // Personality archetype
) {
    val fullName: String get() = "$firstName $lastName"

    // Calculates overall rating based on position, applying traits mathematically
    fun getOverallRating(): Int {
        val raw = when (position) {
            Position.GK -> (attributes.goalkeeper * 0.6 + attributes.defense * 0.2 + attributes.mental * 0.2)
            Position.DEF -> (attributes.defense * 0.6 + attributes.physical * 0.2 + attributes.speed * 0.2)
            Position.MID -> (attributes.midfield * 0.5 + attributes.attack * 0.2 + attributes.defense * 0.1 + attributes.stamina * 0.2)
            Position.ATT -> (attributes.attack * 0.6 + attributes.speed * 0.2 + attributes.mental * 0.2)
        }

        // Apply Trait Multipliers
        var multiplier = 1.0f
        traits.forEach { trait ->
            if (position == Position.ATT && trait == Trait.VELOCISTA_NATO) multiplier *= trait.speedMultiplier
            if (position == Position.GK && trait == Trait.HEROE_BAJO_PALOS) multiplier *= trait.pressureMultiplier
            if (trait == Trait.PULMON_INFINITO) multiplier *= 1.05f
            if (trait == Trait.PECHO_FRIO) multiplier *= 0.95f
        }

        return (raw * multiplier).coerceIn(10.0, 99.0).toInt()
    }

    // Returns a representation of an attribute. If scouting is low, returns a fuzzy range or "?"
    fun getScoutedAttributeString(attributeName: String, value: Int): String {
        if (scoutingLevel >= 80) return value.toString()
        if (scoutingLevel >= 40) {
            val delta = (100 - scoutingLevel) / 5
            val min = (value - delta).coerceIn(10, 99)
            val max = (value + delta).coerceIn(10, 99)
            return "$min-$max"
        }
        return "?? - ??"
    }

    fun applyInjury(weeks: Int, name: String) {
        isInjured = true
        injuryDurationWeeks = weeks
        injuryName = name
        isStarter = false // Move injured player to bench/reserves
    }

    fun processInjuryRecovery(): String? {
        if (!isInjured) return null

        injuryDurationWeeks--
        if (injuryDurationWeeks <= 0) {
            isInjured = false
            val recoveredFrom = if (injuryName.isNotEmpty()) injuryName else "Lesión Muscular"
            injuryName = ""

            // Random stat drop post-injury (1 or 2 random attributes decrease by 1 to 2 points)
            val random = Random
            val attributesToModify = mutableListOf<String>()
            val countToModify = random.nextInt(1, 3) // 1 or 2 stats
            val possibleStats = listOf("attack", "defense", "midfield", "speed", "stamina", "goalkeeper", "mental", "physical").shuffled(random)
            val selectedStats = possibleStats.take(countToModify)

            selectedStats.forEach { stat ->
                val drop = random.nextInt(1, 3) // -1 or -2
                when (stat) {
                    "attack" -> {
                        attributes.attack = (attributes.attack - drop).coerceAtLeast(10)
                        attributesToModify.add("Ataque (-$drop)")
                    }
                    "defense" -> {
                        attributes.defense = (attributes.defense - drop).coerceAtLeast(10)
                        attributesToModify.add("Defensa (-$drop)")
                    }
                    "midfield" -> {
                        attributes.midfield = (attributes.midfield - drop).coerceAtLeast(10)
                        attributesToModify.add("Mediocampo (-$drop)")
                    }
                    "speed" -> {
                        attributes.speed = (attributes.speed - drop).coerceAtLeast(10)
                        attributesToModify.add("Velocidad (-$drop)")
                    }
                    "stamina" -> {
                        attributes.stamina = (attributes.stamina - drop).coerceAtLeast(10)
                        attributesToModify.add("Resistencia (-$drop)")
                    }
                    "goalkeeper" -> {
                        if (position == Position.GK) {
                            attributes.goalkeeper = (attributes.goalkeeper - drop).coerceAtLeast(10)
                            attributesToModify.add("Portería (-$drop)")
                        }
                    }
                    "mental" -> {
                        attributes.mental = (attributes.mental - drop).coerceAtLeast(10)
                        attributesToModify.add("Mental (-$drop)")
                    }
                    "physical" -> {
                        attributes.physical = (attributes.physical - drop).coerceAtLeast(10)
                        attributesToModify.add("Físico (-$drop)")
                    }
                }
            }

            val statChangesStr = if (attributesToModify.isNotEmpty()) {
                "Secuelas físicas tras el alta: ${attributesToModify.joinToString(", ")}."
            } else "Recuperación completa sin secuelas."

            return "🏥 ¡ALTA MÉDICA! $fullName se ha recuperado de '$recoveredFrom'. $statChangesStr"
        }
        return null
    }

    companion object {
        private val frenchFirstNames = listOf(
            "Hugo", "Lucas", "Antoine", "Julien", "Nicolas", "Mathieu", "Pierre", "Jean", "Arthur", "Bastien",
            "Mael", "Raphael", "Enzo", "Louis", "Alexandre", "Thomas", "Paul", "Maxime", "Clement", "Adrien"
        )
        private val frenchLastNames = listOf(
            "Dubois", "Dupont", "Martin", "Lefebvre", "Moreau", "Laurent", "Simon", "Michel", "Leroy", "Roux",
            "David", "Bertrand", "Garnier", "Faure", "Lambert", "Aubry", "Gautier", "Morin", "Girard", "Fournier"
        )

        private val brazilianFirstNames = listOf(
            "Gabriel", "Lucas", "Matheus", "Pedro", "Thiago", "Vinicius", "Arthur", "Felipe", "Gustavo", "Rodrigo",
            "Douglas", "Ronaldo", "Adriano", "Neymar", "Bruno", "Marcelo", "Diego", "Alisson", "Richarlison", "Igor"
        )
        private val brazilianLastNames = listOf(
            "Silva", "Santos", "Souza", "Oliveira", "Pereira", "Lima", "Carvalho", "Ferreira", "Ribeiro", "Almeida",
            "Costa", "Gomes", "Alves", "Rocha", "Cardoso", "Rodrigues", "Martins", "Teixeira", "Barbosa", "Moreira"
        )

        private val spanishFirstNames = listOf(
            "Santiago", "Mateo", "Juan", "Lucas", "Matías", "Nicolás", "Alejandro", "Diego", "Felipe", "Carlos",
            "Emiliano", "Gabriel", "Lautaro", "Enzo", "Federico", "Luis", "Jorge", "Francisco", "Andrés", "Sebastián",
            "Tomás", "Facundo", "Ignacio", "Ezequiel", "Gonzalo", "Álvaro", "Rodrigo", "Manuel", "Agustín", "Joaquín"
        )
        private val spanishLastNames = listOf(
            "González", "Rodríguez", "Gómez", "Fernández", "López", "Díaz", "Martínez", "Pérez", "Romero", "Sánchez",
            "Álvarez", "Cardozo", "Silva", "Medina", "Torres", "Suárez", "Gutiérrez", "Vidal", "Mendoza", "Castillo",
            "Ortiz", "Paz", "Rojas", "Herrera", "Castro", "Cáceres", "Bustos", "Vargas", "Benítez", "Morales"
        )

        private val germanFirstNames = listOf(
            "Thomas", "Lukas", "Leon", "Florian", "Joshua", "Kai", "Julian", "Timo", "Manuel", "Marco",
            "Mario", "Sami", "Mats", "Bastian", "Maximilian", "Niklas", "Felix", "Jonas", "Lars", "Sven"
        )
        private val germanLastNames = listOf(
            "Müller", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann", "Schäfer",
            "Koch", "Bauer", "Richter", "Klein", "Wolf", "Schröder", "Neumann", "Schwarz", "Zimmermann", "Braun"
        )

        private val japaneseFirstNames = listOf(
            "Takumi", "Kaoru", "Takefusa", "Wataru", "Ritsu", "Daichi", "Junya", "Kyogo", "Maya", "Yuto",
            "Shinji", "Keisuke", "Makoto", "Hidemasa", "Aose", "Koki", "Reo", "Mao", "Hiroki", "Ko"
        )
        private val japaneseLastNames = listOf(
            "Mitoma", "Kubo", "Endo", "Doan", "Kamada", "Ito", "Minamino", "Furuhashi", "Yoshida", "Nagatomo",
            "Kagawa", "Honda", "Hasebe", "Morita", "Tanaka", "Itakura", "Tomiyasu", "Suga", "Maeda", "Ueda"
        )

        private val americanFirstNames = listOf(
            "Christian", "Weston", "Tyler", "Gio", "Folarin", "Sergiño", "Brenden", "Antonee", "Matt", "Walker",
            "Tim", "Miles", "Kellyn", "DeAndre", "Jordan", "Paxton", "Jesus", "Ricardo", "Gabriel", "Brandon"
        )
        private val americanLastNames = listOf(
            "Pulisic", "McKennie", "Adams", "Reyna", "Balogun", "Dest", "Aaronson", "Robinson", "Turner", "Zimmerman",
            "Weah", "Robinson", "Acosta", "Yedlin", "Morris", "Pomykal", "Ferreira", "Pepi", "Slonina", "Vazquez"
        )

        private val norwegianFirstNames = listOf(
            "Erling", "Martin", "Alexander", "Sander", "Kristoffer", "Oscar", "Jørgen", "Leo", "Patrick", "Morthen",
            "Stian", "Fredrik", "Markus", "Jonas", "Tobias", "Sindre", "Aron", "Magnus", "Henrik", "Emil"
        )
        private val norwegianLastNames = listOf(
            "Haaland", "Ødegaard", "Sørloth", "Berge", "Ajer", "Bobb", "Larsen", "Ostigard", "Berg", "Thorsby",
            "Pedersen", "Meling", "Nyland", "Solbakken", "Aursnes", "Hauge", "Ryerson", "Gregersen", "Wolfe", "Nusa"
        )

        fun generateProcedural(
            country: String,
            pos: Position? = null,
            minRating: Int = 50,
            maxRating: Int = 85
        ): Player {
            val random = Random
            
            // Localized name resolution
            val countryUpper = country.uppercase()
            val (fName, lName) = when {
                countryUpper.contains("FRANCIA") || countryUpper.contains("FRANCE") -> {
                    frenchFirstNames.random(random) to frenchLastNames.random(random)
                }
                countryUpper.contains("BRASIL") || countryUpper.contains("BRAZIL") -> {
                    brazilianFirstNames.random(random) to brazilianLastNames.random(random)
                }
                countryUpper.contains("ALEMANIA") || countryUpper.contains("GERMANY") -> {
                    germanFirstNames.random(random) to germanLastNames.random(random)
                }
                countryUpper.contains("JAPÓN") || countryUpper.contains("JAPON") || countryUpper.contains("JAPAN") -> {
                    japaneseFirstNames.random(random) to japaneseLastNames.random(random)
                }
                countryUpper.contains("EE.UU") || countryUpper.contains("USA") || countryUpper.contains("UNITED STATES") -> {
                    americanFirstNames.random(random) to americanLastNames.random(random)
                }
                countryUpper.contains("NORUEGA") || countryUpper.contains("NORWAY") -> {
                    norwegianFirstNames.random(random) to norwegianLastNames.random(random)
                }
                else -> {
                    spanishFirstNames.random(random) to spanishLastNames.random(random)
                }
            }

            val age = random.nextInt(16, 38)
            val position = pos ?: Position.values().random(random)

            // Random traits assignment
            val traits = mutableListOf<Trait>()
            if (random.nextFloat() < 0.25f) {
                traits.add(Trait.values().random(random))
            }
            if (random.nextFloat() < 0.05f && traits.isNotEmpty()) { // Rare second trait
                val second = Trait.values().random(random)
                if (second != traits[0]) traits.add(second)
            }

            // Generate stats based on target ratings
            val targetBase = random.nextInt(minRating, maxRating).coerceIn(20, 95)
            val attack = if (position == Position.ATT) targetBase + random.nextInt(5, 15) else targetBase - random.nextInt(10, 25)
            val defense = if (position == Position.DEF) targetBase + random.nextInt(5, 15) else targetBase - random.nextInt(10, 25)
            val midfield = if (position == Position.MID) targetBase + random.nextInt(5, 15) else targetBase - random.nextInt(10, 20)
            val goalkeeper = if (position == Position.GK) targetBase + random.nextInt(10, 25) else random.nextInt(5, 15)
            
            val speed = targetBase + random.nextInt(-10, 15)
            val stamina = targetBase + random.nextInt(-10, 15)
            val mental = targetBase + random.nextInt(-15, 15)
            val physical = targetBase + random.nextInt(-10, 15)

            val attrs = PlayerAttributes(
                attack = attack.coerceIn(10, 99),
                defense = defense.coerceIn(10, 99),
                midfield = midfield.coerceIn(10, 99),
                speed = speed.coerceIn(10, 99),
                stamina = stamina.coerceIn(10, 99),
                goalkeeper = goalkeeper.coerceIn(10, 99),
                mental = mental.coerceIn(10, 99),
                physical = physical.coerceIn(10, 99)
            )

            // Value & Salary calculation
            val overall = (targetBase + 5).coerceIn(30, 99)
            val valueFactor = Math.pow(overall.toDouble() / 50.0, 4.0)
            val marketValue = (valueFactor * 500_000 * random.nextDouble(0.8, 1.2)).toLong().coerceAtLeast(10_000L)
            val salary = (marketValue / 52 * random.nextDouble(0.9, 1.1)).toLong().coerceAtLeast(500L) // Weekly salary
            val contractYears = random.nextInt(1, 5)

            // Dynamic loyalty and club appreciation based on traits
            var loyaltyVal = random.nextInt(40, 81)
            var appreciationVal = random.nextInt(40, 81)

            traits.forEach { trait ->
                when (trait) {
                    Trait.AMOR_A_LA_CAMISETA -> {
                        loyaltyVal = random.nextInt(85, 101)
                        appreciationVal = random.nextInt(80, 101)
                    }
                    Trait.MERCENARIO_DECLARADO -> {
                        loyaltyVal = random.nextInt(10, 36)
                        appreciationVal = random.nextInt(10, 41)
                    }
                    Trait.INCONFORMISTA -> {
                        loyaltyVal = random.nextInt(30, 61)
                        appreciationVal = random.nextInt(20, 56)
                    }
                    Trait.SENSATO -> {
                        loyaltyVal = random.nextInt(70, 91)
                        appreciationVal = random.nextInt(70, 91)
                    }
                    else -> {}
                }
            }

            // Procedural Diverse Attributes
            val heightCm = when (position) {
                Position.GK -> random.nextInt(185, 202)
                Position.DEF -> random.nextInt(180, 198)
                Position.MID -> random.nextInt(168, 188)
                Position.ATT -> random.nextInt(165, 192)
            }
            val weightKg = (heightCm - 105) + random.nextInt(-5, 9)

            val preferredFoot = when (random.nextFloat()) {
                in 0.0f..0.68f -> "Derecho"
                in 0.68f..0.92f -> "Izquierdo"
                else -> "Ambidestro"
            }

            val specialties = when (position) {
                Position.ATT -> listOf("🎯 Francotirador de Falta", "⚡ Killer del Área", "🌀 Regateador Eléctrico", "🚀 Velocista de Ruptura", "💥 Cañonero Lejano")
                Position.MID -> listOf("⚙️ Motor Box-to-Box", "👁️ Visión Láser", "🎼 Director de Orquesta", "🛡️ Recuperador Feroz", "🧠 Creador Táctico")
                Position.DEF -> listOf("🧱 Muro Infranqueable", "✂️ Especialista en Tacle", "👑 Central Mariscal", "🏎️ Carrilero Infatigable", "🛡️ Escudo Aéreo")
                Position.GK -> listOf("🧤 Parapenaltis", "🐆 Reflejos Felinos", "🏰 Líder bajo Palos", "⚽ Líbero Moderno", "✋ Domador de Corner")
            }
            val specialty = specialties.random(random)

            val personalities = listOf(
                "🌟 Líder Motivador",
                "💼 Silencioso Profesional",
                "🔥 Competitivo Ambicioso",
                "💎 Talento Indisciplinado",
                "🛡️ Leal y Pasional"
            )
            val personality = personalities.random(random)

            return Player(
                firstName = fName,
                lastName = lName,
                age = age,
                country = country,
                position = position,
                traits = traits,
                attributes = attrs,
                marketValue = (marketValue / 1000) * 1000, // Round to nearest thousand
                salary = (salary / 10) * 10,
                contractYears = contractYears,
                scoutingLevel = random.nextInt(0, 101), // Fully random scouting for initial gen
                loyalty = loyaltyVal,
                clubAppreciation = appreciationVal,
                heightCm = heightCm,
                weightKg = weightKg,
                preferredFoot = preferredFoot,
                specialty = specialty,
                personality = personality
            )
        }
    }
}
