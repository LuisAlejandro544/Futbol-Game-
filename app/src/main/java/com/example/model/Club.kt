package com.example.model

import com.squareup.moshi.JsonClass
import java.util.UUID
import kotlin.random.Random

@JsonClass(generateAdapter = true)
data class Club(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val country: String,
    var budget: Long,               // Transfer and structural budget
    var wageBudget: Long,           // Weekly wage allowance
    var fanBaseSize: Long,
    var stadiumCapacity: Int,
    var ticketPrice: Int,
    var trainingFacilities: Int = 1, // 1 - 5 stars
    var youthAcademy: Int = 1,       // 1 - 5 stars
    val squad: MutableList<Player> = mutableListOf(),
    
    // Standings / Stats
    var played: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var points: Int = 0,

    // Tactics & Formation
    var selectedFormation: String = "4-4-2",
    var selectedTactic: String = "Equilibrada",
    var captainPlayerId: String? = null,
    var coaches: List<Coach> = emptyList()
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst

    fun resetStats() {
        played = 0
        wins = 0
        draws = 0
        losses = 0
        goalsFor = 0
        goalsAgainst = 0
        points = 0
    }

    fun canAfford(fee: Long, salary: Long): Boolean {
        return budget >= fee && wageBudget >= salary
    }

    fun signPlayer(player: Player, fee: Long, salary: Long, contractYears: Int) {
        budget = (budget - fee).coerceAtLeast(0L)
        val updatedPlayer = player.copy(
            salary = salary,
            contractYears = contractYears,
            isFreeAgent = false,
            isStarter = squad.size < 11
        )
        squad.add(updatedPlayer)
    }

    fun sellPlayer(playerId: String, feeReceived: Long): Player? {
        val player = squad.firstOrNull { it.id == playerId }
        if (player != null) {
            squad.remove(player)
            budget += feeReceived
            player.isFreeAgent = true
        }
        return player
    }

    // Calculates overall defensive, midfield, and offensive ratings from squad starters
    fun getTeamRatings(): Triple<Int, Int, Int> {
        if (squad.isEmpty()) return Triple(30, 30, 30)
        
        // Find players designated as starters
        val starters = squad.filter { it.isStarter }
        val activeStarters = if (starters.isEmpty()) squad.take(11) else starters

        // Helper to scale player overall based on energy (100% = 1.0x, 50% = 0.8x, 0% = 0.6x)
        val getEffectiveRating = { p: Player ->
            val baseOvr = p.getOverallRating().toDouble()
            val energyMult = 0.6 + 0.4 * (p.energy.coerceIn(0, 100) / 100.0)
            baseOvr * energyMult
        }
        
        var defenseScore = activeStarters.filter { it.position == Position.DEF || it.position == Position.GK }
            .map { getEffectiveRating(it) }.average().let { if (it.isNaN()) 30.0 else it }
            
        var midfieldScore = activeStarters.filter { it.position == Position.MID }
            .map { getEffectiveRating(it) }.average().let { if (it.isNaN()) 30.0 else it }
            
        var attackScore = activeStarters.filter { it.position == Position.ATT }
            .map { getEffectiveRating(it) }.average().let { if (it.isNaN()) 30.0 else it }

        // Apply formation modifiers
        when (selectedFormation) {
            "4-3-3" -> {
                attackScore *= 1.10
                defenseScore *= 0.95
            }
            "3-5-2" -> {
                midfieldScore *= 1.15
                defenseScore *= 0.90
            }
            "5-3-2" -> {
                defenseScore *= 1.15
                attackScore *= 0.90
            }
            "4-2-3-1" -> {
                midfieldScore *= 1.05
                attackScore *= 1.05
            }
            // "4-4-2" is standard balanced
        }

        // Apply tactic modifiers
        when (selectedTactic) {
            "Agresiva", "Ofensiva" -> {
                attackScore *= 1.15
                defenseScore *= 0.90
            }
            "Defensiva" -> {
                defenseScore *= 1.20
                attackScore *= 0.85
            }
            "Contraataque" -> {
                attackScore *= 1.10
                defenseScore *= 1.05
            }
            "Posesión" -> {
                midfieldScore *= 1.20
            }
            "Presión Alta" -> {
                midfieldScore *= 1.10
                attackScore *= 1.10
                defenseScore *= 0.95
            }
        }

        return Triple(
            defenseScore.toInt().coerceIn(15, 99),
            midfieldScore.toInt().coerceIn(15, 99),
            attackScore.toInt().coerceIn(15, 99)
        )
    }

    fun getAverageRating(): Int {
        if (squad.isEmpty()) return 30
        return squad.map { it.getOverallRating() }.average().toInt()
    }

    companion object {
        private val countryClubTemplates = mapOf(
            "ARGENTINA" to listOf(
                "Boca de Buenos Aires", "River de Núñez", "Racing Club de Avellaneda", "Estudiantes de La Plata",
                "Independiente de Avellaneda", "San Lorenzo de Almagro", "Rosario Canalla", "Córdoba Talleres",
                "Lanús del Sur", "Vélez de Liniers", "Banfield de Peña", "Colón de Santa Fe"
            ),
            "BRASIL" to listOf(
                "Flamengo de Río", "Palmeiras de Sao Paulo", "Sao Paulo Tricolor", "Corinthians Alvinegro",
                "Santos Peixe", "Grêmio de Porto Alegre", "Cruzeiro de Belo Horizonte", "Atlético Mineiro",
                "Fluminense das Laranjeiras", "Botafogo da Estrela", "Vasco da Gama da Colina", "Internacional Colorado"
            ),
            "FRANCIA" to listOf(
                "Olympique de Paris", "Marseille Phocéen", "Lyon des Lions", "Monaco Princier",
                "Lille du Nord", "Rennes Rouge-Noir", "Lens Sang-et-Or", "Nice Azur",
                "Nantes Canari", "Strasbourg d'Alsace", "Reims Couronné", "Toulouse Violet"
            ),
            "MÉXICO" to listOf(
                "Club América de Coapa", "CD Guadalajara Chivas", "Cruz Azul de México", "Tigres de Nuevo León",
                "Rayados de Monterrey", "Pumas de la UNAM", "Atlas de Guadalajara", "León FC",
                "Pachuca de Hidalgo", "Toluca Escarlata", "Santos Laguna del Norte", "Necaxa del Rayo"
            ),
            "COLOMBIA" to listOf(
                "Millonarios de Bogotá", "Atlético Nacional Verde", "Junior de Barranquilla", "América de Cali",
                "Independiente Santa Fe", "Deportivo Cali Azucarero", "Independiente Medellín", "Once Caldas de Manizales",
                "Tolima Pijao", "La Equidad Seguros", "Atlético Bucaramanga", "Deportivo Pasto"
            ),
            "CHILE" to listOf(
                "Colo-Colo Albo", "Universidad de Chile Azul", "Universidad Católica Cruzada", "Cobreloa del Desierto",
                "Unión Española de Santiago", "Audax Italiano de La Florida", "Everton de Viña", "Santiago Wanderers"
            ),
            "URUGUAY" to listOf(
                "Peñarol de Montevideo", "Nacional Uruguayo", "Defensor de Montevideo", "Danubio de la Curva",
                "Montevideo City", "Liverpool de Belvedere", "River Plate Uruguayo", "Fénix del Capurro"
            ),
            "PARAGUAY" to listOf(
                "Olimpia Decano", "Cerro Porteño de Barrio Obrero", "Libertad Gumarelo", "Guaraní Aborigen",
                "Nacional Querido", "Sol de América de Villa Elisa", "Sportivo Luqueño", "Tacuary de Asunción"
            ),
            "ECUADOR" to listOf(
                "Barcelona de Guayaquil", "LDU de Quito", "Emelec Eléctrico", "Independiente del Valle",
                "El Nacional de Quito", "Delfín de Manta", "Aucas Oriental", "Deportivo Cuenca"
            ),
            "PERÚ" to listOf(
                "Alianza de Lima", "Universitario Crema", "Sporting Cristal Celeste", "Melgar de Arequipa",
                "Cienciano Imperial", "Cusco FC", "César Vallejo de Trujillo", "Sport Boys del Callao"
            ),
            "VENEZUELA" to listOf(
                "Caracas FC", "Deportivo Táchira Aurinegro", "Zamora del Llano", "Monagas Azulgrana",
                "Estudiantes de Mérida", "Metropolitanos de Caracas", "Deportivo La Guaira", "Academia Puerto Cabello"
            ),
            "BOLIVIA" to listOf(
                "Bolívar de La Paz", "The Strongest Atigrado", "Jorge Wilstermann Aviador", "Oriente Petrolero",
                "Blooming de Santa Cruz", "Always Ready de El Alto", "Royal Pari", "Real Tomayapo"
            ),
            "COSTA RICA" to listOf(
                "Deportivo Saprissa", "Alajuelense Manuda", "Herediano Florense", "Cartaginés de Cartago",
                "San Carlos del Norte", "Puntarenas Porteño", "Santos de Guápiles", "Liberia Aurinegra"
            ),
            "PANAMÁ" to listOf(
                "Tauro de Pedregal", "Plaza Amador", "Árabe Unido de Colón", "San Francisco de Chorrera",
                "Alianza de Panamá", "CAI de La Chorrera", "Sporting San Miguelito", "Herrera FC"
            ),
            "HONDURAS" to listOf(
                "Olimpia de Tegucigalpa", "Motagua de las Águilas", "Real España de San Pedro Sula", "Marathón de San Pedro",
                "Vida de La Ceiba", "Olancho FC", "Real Sociedad de Tocoa", "Victoria Ceibeño"
            ),
            "EL SALVADOR" to listOf(
                "Alianza de San Salvador", "FAS de Santa Ana", "Águila de San Miguel", "Luis Ángel Firpo",
                "Isidro Metapán", "Santa Tecla Colina", "Platense de Zacatecoluca", "Dragón Mitológico"
            ),
            "GUATEMALA" to listOf(
                "Comunicaciones Crema", "Municipal Escarlata", "Xelajú de Quetzaltenango", "Antigua de Sacatepéquez",
                "Cobán Imperial", "Guastatoya de El Progreso", "Malacateco de San Marcos", "Achuapa del Progreso"
            ),
            "NICARAGUA" to listOf(
                "Real Estelí del Tren", "Diriangén Cacique", "Managua FC", "Walter Ferretti",
                "Ocotal del Norte", "Matagalpa Indígena", "Jalapa de las Segovias", "UNAN Managua"
            ),
            "ESPAÑA" to listOf(
                "Real Madrid de la Capital", "FC Barcelona Blaugrana", "Atlético Colchonero", "Sevilla FC Hispalense",
                "Real Betis Verdiblanco", "Athletic Club Bilbao", "Real Sociedad Donostia", "Valencia CF Che"
            ),
            "ALEMANIA" to listOf(
                "Bayern de Múnich Baviera", "Borussia Dortmund Westfalia", "Bayer Leverkusen Renania", "RB Leipzig Sajonia",
                "Eintracht Frankfurt Main", "VfB Stuttgart Suabia", "Borussia Mönchengladbach", "Schalke Gelsenkirchen"
            ),
            "JAPÓN" to listOf(
                "Kawasaki Frontale", "Yokohama F. Marinos", "Urawa Red Diamonds", "Kashima Antlers",
                "Nagoya Grampus", "Gamba Osaka", "Vissel Kobe", "Sanfrecce Hiroshima"
            ),
            "JAPON" to listOf(
                "Kawasaki Frontale", "Yokohama F. Marinos", "Urawa Red Diamonds", "Kashima Antlers",
                "Nagoya Grampus", "Gamba Osaka", "Vissel Kobe", "Sanfrecce Hiroshima"
            ),
            "EE.UU." to listOf(
                "LA Galaxy de Los Ángeles", "Inter Miami Rosa", "New York Red Bulls", "Seattle Sounders Verde",
                "Atlanta United FC", "Columbus Crew Amarillo", "FC Cincinnati", "Philadelphia Union"
            ),
            "EE.UU" to listOf(
                "LA Galaxy de Los Ángeles", "Inter Miami Rosa", "New York Red Bulls", "Seattle Sounders Verde",
                "Atlanta United FC", "Columbus Crew Amarillo", "FC Cincinnati", "Philadelphia Union"
            ),
            "NORUEGA" to listOf(
                "Bodø/Glimt del Norte", "Molde FK de los Fiordos", "Rosenborg de Trondheim", "Vålerenga de Oslo",
                "Viking FK de Stavanger", "SK Brann de Bergen", "Lillestrøm SK", "Tromsø IL Ártico"
            )
        )

        private val fictionalPrefixes = listOf(
            "Deportivo", "Atlético", "Real", "Fútbol Club", "Unión", "Sporting", "Alianza", "Club", "Socio", "Juventud", "Rácing", "Academia", "Fénix", "Estrella"
        )

        private val fictionalRoots = mapOf(
            "ARGENTINA" to listOf("Aconcagua", "Cuyo", "La Pampa", "Patagonia", "Andino", "Serrano", "Platense", "Austral", "El Faro", "Río Negro", "Viento Norte", "Porteño"),
            "BRASIL" to listOf("Amazonas", "Ipanema", "Do Sol", "Verdeamarelo", "Carioca", "Nordeste", "Guanabara", "Paulista", "Do Norte", "Serra", "Planalto", "Bahiano"),
            "FRANCIA" to listOf("Riviera", "Alpino", "De la Seine", "Pyrénées", "Ardennes", "Bourgogne", "L'Etoile", "Azur", "Nouveau", "Bastille", "Lorraine", "Gironde"),
            "MÉXICO" to listOf("Anáhuac", "Chiapas", "Yucatán", "Sonora", "Volcán", "Del Norte", "Zacatecas", "Huasteca", "Guanajuato", "Sinaloa", "Azteca", "Guerreros"),
            "COLOMBIA" to listOf("Cafetero", "Andino", "Caribe", "Chocó", "Magdalena", "Orinoco", "Del Valle", "Sabana", "Nevado", "El Dorado", "Pacífico", "Cundinamarca"),
            "CHILE" to listOf("Atacama", "Valparaíso", "Bío Bío", "Del Sur", "Cordillera", "Araucanía", "Patagónico", "Los Lagos", "O'Higgins", "Antártico"),
            "URUGUAY" to listOf("Del Plata", "Charrúa", "Orientales", "Celeste", "Melo", "Rocha", "Punta del Este", "Durazno", "Salto", "Colonia"),
            "PARAGUAY" to listOf("Guaraní", "Chaco", "Paraná", "Del Este", "Pilcomayo", "Ipacaraí", "Ybytyruzú", "Itapúa", "Misiones", "Caaguazú"),
            "ECUADOR" to listOf("Galápagos", "Chimborazo", "Guayas", "Pichincha", "Andino", "Del Austro", "Imbabura", "Manabí", "Esmeraldas", "Amazonía"),
            "PERÚ" to listOf("Incaico", "Machu Picchu", "Titicaca", "Huánuco", "Amazonas", "Iquitos", "Arequipeño", "Serrano", "Misti", "Chimú"),
            "VENEZUELA" to listOf("Orinoco", "Caroní", "Andino", "El Avila", "Maracaibo", "Llaneros", "Guayana", "Margarita", "Cabriales", "Caribeño"),
            "BOLIVIA" to listOf("Illimani", "Altiplano", "Titicaca", "Sajama", "De la Sierra", "Trópico", "Valle Alto", "Chiquitanía", "Real Potosí", "Pilcomayo"),
            "COSTA RICA" to listOf("Pura Vida", "Arenal", "Talamanca", "Tortuguero", "Guanacaste", "Pacífico", "Caribe", "Del Valle", "Irazú", "Poás"),
            "PANAMÁ" to listOf("Canalero", "Darién", "Chiriquí", "Herrera", "Del Mar", "Transístmico", "Pacífico", "Soberanía", "Taboga", "Portobelo"),
            "HONDURAS" to listOf("Copán", "Roatán", "Lenca", "Comayagua", "Choluteca", "Ulúa", "Yoro", "La Mosquitia", "Catracho", "Sula"),
            "EL SALVADOR" to listOf("Cuscatlán", "Lempa", "Izalco", "San Vicente", "Ilopango", "Chinameca", "Del Golfo", "Pipil", "Volcán", "Bálsamo"),
            "GUATEMALA" to listOf("Quetzal", "Atitlán", "Petén", "Tikal", "Cuchumatanes", "Motagua", "Izabal", "Verapaz", "Antigüeño", "Tecún Umán"),
            "NICARAGUA" to listOf("Cocibolca", "Xolotlán", "Momotombo", "Mogotón", "Masaya", "Segovia", "San Juan", "Chinandega", "Miskito", "Pinolero"),
            "ESPAÑA" to listOf("Ibérico", "Castilla", "Andaluz", "Cantábrico", "Ebro", "Levante", "Pirineo", "Gallego", "Canario", "Baleares"),
            "ALEMANIA" to listOf("Baviera", "Rhenania", "Sajonia", "Selva Negra", "Berlín", "Hamburgo", "Baltico", "Baden", "Palatinado", "Turingia"),
            "JAPÓN" to listOf("Fuji", "Tokio", "Kioto", "Kansai", "Hokkaido", "Sakura", "Osaka", "Nagoya", "Okinawa", "Sendai"),
            "JAPON" to listOf("Fuji", "Tokio", "Kioto", "Kansai", "Hokkaido", "Sakura", "Osaka", "Nagoya", "Okinawa", "Sendai"),
            "EE.UU." to listOf("Liberty", "Pacific", "Atlantic", "Midwest", "Sunbelt", "Empire", "Lone Star", "Cascadia", "Rockies", "Evergreen"),
            "EE.UU" to listOf("Liberty", "Pacific", "Atlantic", "Midwest", "Sunbelt", "Empire", "Lone Star", "Cascadia", "Rockies", "Evergreen"),
            "NORUEGA" to listOf("Fiordo", "Vikingo", "Ártico", "Nórdico", "Aurora", "Trondheim", "Bergen", "Lofoten", "Oslo", "Svalbard")
        )

        fun generateProcedural(
            country: Country,
            index: Int,
            minStars: Int = 1,
            maxStars: Int = 5,
            useFictionalNames: Boolean = false
        ): Club {
            val random = Random
            
            // Extract core country key without flag emojis
            val countryKey = country.name.split(" ")[0].uppercase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")

            val name = if (useFictionalNames) {
                val roots = fictionalRoots[countryKey] ?: listOf("Fútbol", "Balón")
                val prefix = fictionalPrefixes.random(random)
                val root = if (index < roots.size) roots[index] else roots.random(random)
                if (random.nextBoolean()) {
                    "$prefix $root"
                } else {
                    "$prefix de $root"
                }
            } else {
                val templates = countryClubTemplates[countryKey] ?: listOf("Real Club", "Fútbol Club", "Atlético")
                if (index < templates.size) {
                    templates[index]
                } else {
                    "${templates.random(random)} $index"
                }
            }

            val starPower = random.nextInt(minStars, maxStars + 1).coerceIn(1, 5)
            
            // Economy factors scale financial metrics dynamically
            val baseBudget = (starPower * starPower * 1_500_000L * country.economyFactor).toLong()
            val baseWage = (starPower * 100_000L * country.economyFactor).toLong()
            val stadiumCap = starPower * starPower * 10_000 + random.nextInt(2000, 8000)
            val fanBase = (stadiumCap * random.nextDouble(1.5, 5.0)).toLong()
            val ticketPrice = starPower * 8 + random.nextInt(5, 15)

            val club = Club(
                name = name,
                country = country.name,
                budget = baseBudget,
                wageBudget = baseWage,
                stadiumCapacity = stadiumCap,
                fanBaseSize = fanBase,
                ticketPrice = ticketPrice,
                trainingFacilities = starPower,
                youthAcademy = starPower
            )

            // Generate squad of 18 players
            val squadSize = 18
            val minRating = 30 + starPower * 10
            val maxRating = 45 + starPower * 11

            // 2 GKs, 6 DEFs, 6 MIDs, 4 ATTs (11 starters, 7 bench)
            repeat(2) { idx ->
                val player = Player.generateProcedural(country.name, Position.GK, minRating, maxRating)
                player.isStarter = (idx == 0) // 1 starter, 1 bench
                club.squad.add(player)
            }
            repeat(6) { idx ->
                val player = Player.generateProcedural(country.name, Position.DEF, minRating, maxRating)
                player.isStarter = (idx < 4) // 4 starters, 2 bench
                club.squad.add(player)
            }
            repeat(6) { idx ->
                val player = Player.generateProcedural(country.name, Position.MID, minRating, maxRating)
                player.isStarter = (idx < 4) // 4 starters, 2 bench
                club.squad.add(player)
            }
            repeat(4) { idx ->
                val player = Player.generateProcedural(country.name, Position.ATT, minRating, maxRating)
                player.isStarter = (idx < 2) // 2 starters, 2 bench
                club.squad.add(player)
            }

            // Assign a random starting formation and designate a captain
            club.selectedFormation = listOf("4-4-2", "4-3-3", "3-5-2", "5-3-2", "4-2-3-1").random(random)
            club.captainPlayerId = club.squad.randomOrNull(random)?.id

            return club
        }
    }
}
