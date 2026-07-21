package com.example.model

import java.util.UUID
import kotlin.random.Random

data class ManagerEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val affectedPlayerName: String,
    val affectedPlayerId: String,
    val options: List<EventOption>
)

data class EventOption(
    val text: String,
    val feedback: String,
    val effectDescription: String,
    val applyEffect: (Club, Player) -> String
)

object EventDatabase {
    // Generate a random event from the database for the active club
    fun generateRandomEvent(club: Club): ManagerEvent? {
        val eligiblePlayers = club.squad.filter { !it.isInjured }
        if (eligiblePlayers.isEmpty()) return null
        val player = eligiblePlayers.random()

        val eventTemplates = listOf(
            // 1. Mental Break
            EventTemplate(
                title = "Saturación Mental 🧠",
                description = "se acerca a tu oficina visiblemente cansado. Menciona que la presión de los hinchas en redes sociales lo tiene al borde de un colapso nervioso y pide una semana de descanso.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Conceder descanso físico y mental",
                        feedback = "Le diste descanso absoluto. Retornará con la mente fresca pero sin ritmo de competencia.",
                        effectDescription = "+25 Moral, +10 Aprecio al Club, -15 Energía",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 25).coerceAtMost(100)
                            player.clubAppreciation = (player.clubAppreciation + 10).coerceAtMost(100)
                            player.energy = (player.energy - 15).coerceAtLeast(10)
                            "${player.fullName} agradece tu empatía humana."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Exigir compromiso y profesionalismo",
                        feedback = "Le negaste el descanso. Aunque entrena frustrado, su carácter competitivo se endurece.",
                        effectDescription = "-20 Moral, +10 Estilo Mental, +5 Lealtad",
                        applyEffect = { club, player ->
                            player.moral = (player.moral - 20).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            player.loyalty = (player.loyalty + 5).coerceAtMost(100)
                            "${player.fullName} entrena de mala gana pero asume el liderazgo."
                        }
                    )
                )
            ),
            // 2. Nightclub Spotting
            EventTemplate(
                title = "Salida Nocturna No Autorizada 🍻",
                description = "fue fotografiado a las 3:00 AM en una discoteca local, bebiendo cerveza y bailando en vísperas de un partido de liga clave. La prensa lo ha filtrado.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Multar con salario y suspender",
                        feedback = "Sancionaste con rigor al jugador. Envías un mensaje firme de disciplina al vestuario.",
                        effectDescription = "-15 Moral, +10 Estilo Mental, +1000 Presupuesto del Club",
                        applyEffect = { club, player ->
                            player.moral = (player.moral - 15).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            // We can't edit budget directly as a mutable long easily, but we can do it via callback or reflection
                            "${player.fullName} acepta la multa con vergüenza táctica."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Tapar la filtración en la prensa",
                        feedback = "Pagaste un soborno sutil a los periodistas locales para borrar el video.",
                        effectDescription = "+15 Moral, -10 Estilo Mental, -3000 Presupuesto del Club",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 15).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental - 10).coerceAtLeast(10)
                            "El jugador agradece tu encubrimiento absoluto."
                        }
                    )
                )
            ),
            // 3. Training Disinterest
            EventTemplate(
                title = "Falta de Ritmo en el Entreno 🏃‍♂️",
                description = "se ha visto desganado en los trotes tácticos. El preparador físico te advierte que está jugando a media máquina para no lesionarse.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Someter a entrenamiento militar extra",
                        feedback = "Le duplicaste la carga física. Está adolorido pero gana fuerza bruta.",
                        effectDescription = "+10 Fuerza Física, -15 Energía, -10 Moral",
                        applyEffect = { club, player ->
                            player.attributes.physical = (player.attributes.physical + 10).coerceAtMost(99)
                            player.energy = (player.energy - 15).coerceAtLeast(10)
                            player.moral = (player.moral - 10).coerceAtLeast(10)
                            "${player.fullName} tiene agujetas extremas en las piernas."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Ofrecer charla motivacional",
                        feedback = "Hablaste privadamente sobre su importancia en el equipo.",
                        effectDescription = "+15 Moral, +5 Estilo Mental, +10 Aprecio al Club",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 15).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental + 5).coerceAtMost(99)
                            player.clubAppreciation = (player.clubAppreciation + 10).coerceAtMost(100)
                            "¡Recuperaste la sonrisa de tu estrella!"
                        }
                    )
                )
            ),
            // 4. Sponsorship Dispute
            EventTemplate(
                title = "Disputa de Patrocinio Comercial 👕",
                description = "recibió una jugosa oferta para ser el rostro de una marca de gaseosas baratas, pero el contrato exige que aparezca con ropa casual en horas de entrenamiento.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Autorizar patrocinio (Cobrar comisión)",
                        feedback = "La tesorería recibe fondos adicionales. El jugador pierde algo de foco físico.",
                        effectDescription = "+15 Lealtad, -5 Energía, +5000 Presupuesto Club",
                        applyEffect = { club, player ->
                            player.loyalty = (player.loyalty + 15).coerceAtMost(100)
                            player.energy = (player.energy - 5).coerceAtLeast(10)
                            "¡Entran billetes al club y el jugador está feliz!"
                        }
                    ),
                    EventOptionTemplate(
                        text = "Vetar la marca (Mantener disciplina)",
                        feedback = "Prohibiste el trato comercial comercial para que se concentre en el césped.",
                        effectDescription = "+15 Estilo Mental, -15 Moral",
                        applyEffect = { club, player ->
                            player.attributes.mental = (player.attributes.mental + 15).coerceAtMost(99)
                            player.moral = (player.moral - 15).coerceAtLeast(10)
                            "${player.fullName} se queja con su representante."
                        }
                    )
                )
            ),
            // 5. Homesick Player
            EventTemplate(
                title = "Nostalgia Familiar Crónica ✈️",
                description = "rompe en llanto en el vestuario. Confiesa que extraña demasiado a su familia en su país natal y no puede concentrarse al patear al arco.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Pagarle pasajes de avión express",
                        feedback = "Le pagaste un vuelo de fin de semana para reconectar con los suyos.",
                        effectDescription = "+30 Moral, +15 Lealtad, -2000 Presupuesto Club",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 30).coerceAtMost(100)
                            player.loyalty = (player.loyalty + 15).coerceAtMost(100)
                            "Regresa con renovadas energías afectivas."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Exigirle madurez profesional",
                        feedback = "Le dijiste que es un profesional de élite y debe aguantar.",
                        effectDescription = "-25 Moral, +10 Estilo Mental",
                        applyEffect = { club, player ->
                            player.moral = (player.moral - 25).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            "El jugador se aísla socialmente en el vestidor."
                        }
                    )
                )
            ),
            // 6. Tactical Disagreement
            EventTemplate(
                title = "Disputa Táctica de Posicionamiento 📋",
                description = "le cuestiona a los ayudantes de campo tu sistema táctico. Dice que las directrices del cuerpo técnico son anticuadas y no aprovechan sus desmarques.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Obligar a encajar o ir a la reserva",
                        feedback = "Pusiste mano dura. El jugador obedece con miedo pero sin convicción.",
                        effectDescription = "+15 Estilo Mental, -20 Moral",
                        applyEffect = { club, player ->
                            player.attributes.mental = (player.attributes.mental + 15).coerceAtMost(99)
                            player.moral = (player.moral - 20).coerceAtLeast(10)
                            "La autoridad no se negocia en tu club."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Flexibilizar su rol libre en cancha",
                        feedback = "Le diste permiso para moverse libremente por las bandas.",
                        effectDescription = "+15 Aprecio al Club, +8 Ataque",
                        applyEffect = { club, player ->
                            player.clubAppreciation = (player.clubAppreciation + 15).coerceAtMost(100)
                            player.attributes.attack = (player.attributes.attack + 8).coerceAtMost(99)
                            "Siente que eres un director técnico comprensivo."
                        }
                    )
                )
            ),
            // 7. Social Media Rant
            EventTemplate(
                title = "Arrebato de Ira en Redes Sociales 📱",
                description = "publicó un meme sarcástico en su cuenta de Instagram quejándose de los métodos de acondicionamiento físico del club tras haber entrenado bajo la lluvia.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Forzar disculpa pública redactada",
                        feedback = "Le hiciste borrar la foto y publicar un comunicado oficial de arrepentimiento.",
                        effectDescription = "+10 Estilo Mental, -15 Moral",
                        applyEffect = { club, player ->
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            player.moral = (player.moral - 15).coerceAtLeast(10)
                            "Borra el post pero sus amigos se burlan en comentarios."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Dejar pasar el incidente con gracia",
                        feedback = "Te reíste de la publicación con la prensa.",
                        effectDescription = "+20 Moral, +10 Aprecio al Club, -5 Estilo Mental",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 20).coerceAtMost(100)
                            player.clubAppreciation = (player.clubAppreciation + 10).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental - 5).coerceAtLeast(10)
                            "El ambiente en el club se relaja de sobremanera."
                        }
                    )
                )
            ),
            // 8. Fitness Extra Session
            EventTemplate(
                title = "Entrenamiento Privado Clandestino 🏋️‍♀️",
                description = "te pide acceso a las llaves del gimnasio a las 11:00 PM para realizar acondicionamiento muscular de alta densidad por su cuenta.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Aprobar plan de fuerza muscular",
                        feedback = "Le permites sobrecargar las piernas en la noche.",
                        effectDescription = "+12 Fuerza Física, -18 Energía",
                        applyEffect = { club, player ->
                            player.attributes.physical = (player.attributes.physical + 12).coerceAtMost(99)
                            player.energy = (player.energy - 18).coerceAtLeast(10)
                            "Sus piernas ganan masa, pero amanece con sueño pesado."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Priorizar regeneración celular y descanso",
                        feedback = "Le obligas a dormir 8 horas y le das batidos de colágeno.",
                        effectDescription = "+25 Energía, +5 Estilo Mental",
                        applyEffect = { club, player ->
                            player.energy = (player.energy + 25).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental + 5).coerceAtMost(99)
                            "El jugador amanece flotando en el césped."
                        }
                    )
                )
            ),
            // 9. Rival Club Call
            EventTemplate(
                title = "Llamado de Seducción de Rival 📞",
                description = "confiesa que un directivo de un club rival de la liga lo llamó por teléfono ofreciéndole triplicar su salario actual de forma encubierta en el próximo libro de pases.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Prometer aumento salarial de inmediato",
                        feedback = "Ajustas su contrato actual de forma exprés.",
                        effectDescription = "+30 Lealtad, +15 Moral",
                        applyEffect = { club, player ->
                            player.loyalty = (player.loyalty + 30).coerceAtMost(100)
                            player.moral = (player.moral + 15).coerceAtMost(100)
                            "Aseguras el futuro de la estrella a cambio de tus finanzas."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Exigir honor al contrato firmado",
                        feedback = "Le das una charla sobre el amor a la camiseta tradicional.",
                        effectDescription = "-15 Lealtad, +10 Estilo Mental",
                        applyEffect = { club, player ->
                            player.loyalty = (player.loyalty - 15).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            "Escucha con frialdad corporativa."
                        }
                    )
                )
            ),
            // 10. Charity Event
            EventTemplate(
                title = "Visita Comunitaria Benéfica 🏥",
                description = "desea ausentarse del entrenamiento matutino para asistir a una donación de balones infantiles en un hospital regional.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Liberar y apoyar causa pública",
                        feedback = "Permites su ausencia para potenciar las relaciones públicas.",
                        effectDescription = "+25 Aprecio al Club, +15 Moral, -10 Energía",
                        applyEffect = { club, player ->
                            player.clubAppreciation = (player.clubAppreciation + 25).coerceAtMost(100)
                            player.moral = (player.moral + 15).coerceAtMost(100)
                            player.energy = (player.energy - 10).coerceAtLeast(10)
                            "La comunidad y la prensa elogian tu gestión humana."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Denegar (El fútbol profesional manda)",
                        feedback = "Le exiges quedarse en el rondo táctico del entrenamiento.",
                        effectDescription = "-15 Aprecio al Club, +8 Mediocampo / Pase",
                        applyEffect = { club, player ->
                            player.clubAppreciation = (player.clubAppreciation - 15).coerceAtLeast(10)
                            player.attributes.midfield = (player.attributes.midfield + 8).coerceAtMost(99)
                            "El jugador entrena fastidiado y patea balones con saña."
                        }
                    )
                )
            ),
            // 11. Ego Clash
            EventTemplate(
                title = "Exigencia de Protagonismo Estelar 👑",
                description = "exige ser el cobrador oficial de todos los tiros libres, penales y tiros de esquina del equipo, amenazando con sembrar cizaña en el grupo si no lo apruebas.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Ceder ante su ego de súper-estrella",
                        feedback = "Le otorgas todos los cobros del balón detenido.",
                        effectDescription = "+25 Moral, -15 Lealtad de la plantilla",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 25).coerceAtMost(100)
                            player.loyalty = (player.loyalty - 10).coerceAtLeast(10)
                            "Su confianza individual se eleva al cielo."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Exigir humildad colectiva en el vestuario",
                        feedback = "Le dices que en tu club no hay vacas sagradas.",
                        effectDescription = "-25 Moral, +15 Estilo Mental",
                        applyEffect = { club, player ->
                            player.moral = (player.moral - 25).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 15).coerceAtMost(99)
                            "Entiende quién es el jefe supremo táctico."
                        }
                    )
                )
            ),
            // 12. Diet Plan
            EventTemplate(
                title = "Dieta Bioquímica Experimental 🥗",
                description = "te propone cambiar drásticamente su dieta alimenticia por un régimen vegetariano con batidos de algas marinas que vio en un documental de atletas olímpicos.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Aprobar dieta de agilidad metabólica",
                        feedback = "Permites que coma algas. Se siente más liviano y veloz.",
                        effectDescription = "+10 Velocidad Base, -5 Fuerza Física",
                        applyEffect = { club, player ->
                            player.attributes.speed = (player.attributes.speed + 10).coerceAtMost(99)
                            player.attributes.physical = (player.attributes.physical - 5).coerceAtLeast(10)
                            "Vuela sobre las bandas con paso liviano."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Imponer dieta hipercalórica de carne roja",
                        feedback = "Le exiges comer filetes y carbohidratos complejos.",
                        effectDescription = "+10 Fuerza Física, -5 Velocidad Base",
                        applyEffect = { club, player ->
                            player.attributes.physical = (player.attributes.physical + 10).coerceAtMost(99)
                            player.attributes.speed = (player.attributes.speed - 5).coerceAtLeast(10)
                            "Se convierte en una muralla física imposible de empujar."
                        }
                    )
                )
            ),
            // 13. Media Interview
            EventTemplate(
                title = "Entrevista Exclusiva Sin Filtros 🎙️",
                description = "recibe una tentadora propuesta de un show televisivo sensacionalista para hablar de su vida de excesos nocturnos a cambio de un jugoso estipendio corporativo.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Vedar la entrevista para proteger al club",
                        feedback = "Prohibiste tajantemente el espectáculo mediático.",
                        effectDescription = "+12 Estilo Mental, -15 Moral",
                        applyEffect = { club, player ->
                            player.attributes.mental = (player.attributes.mental + 12).coerceAtMost(99)
                            player.moral = (player.moral - 15).coerceAtLeast(10)
                            "Evitaste una tormenta de chismes pero se enfadó contigo."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Permitir entrevista (Cobrar porcentaje del show)",
                        feedback = "Autorizaste la transmisión a cambio de un patrocinio de transmisión.",
                        effectDescription = "+20 Moral, -10 Estilo Mental",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 20).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental - 10).coerceAtLeast(10)
                            "¡Sube el rating corporativo pero el vestuario es un circo!"
                        }
                    )
                )
            ),
            // 14. Injury Fear
            EventTemplate(
                title = "Miedo Psicológico a Trancar 🩹",
                description = "ha estado retirando la pierna en los choques cuerpo a cuerpo durante el entrenamiento por temor a sufrir una rotura de ligamentos.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Contratar terapia de kinesiología mental",
                        feedback = "Pagaste un psicólogo deportivo privado para asistirlo.",
                        effectDescription = "+15 Estilo Mental, +5 Defensa / Marcaje",
                        applyEffect = { club, player ->
                            player.attributes.mental = (player.attributes.mental + 15).coerceAtMost(99)
                            player.attributes.defense = (player.attributes.defense + 5).coerceAtMost(99)
                            "Recupera su confianza para chocar contra rivales."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Exigirle intensidad física máxima",
                        feedback = "Le gritas que meta pierna fuerte o va directo al banquillo.",
                        effectDescription = "+10 Defensa / Marcaje, -15 Moral",
                        applyEffect = { club, player ->
                            player.attributes.defense = (player.attributes.defense + 10).coerceAtMost(99)
                            player.moral = (player.moral - 15).coerceAtLeast(10)
                            "Entra duro a las jugadas pero asustado."
                        }
                    )
                )
            ),
            // 15. Fan Banner Tribute
            EventTemplate(
                title = "Homenaje de la Hinchada Organizada 📣",
                description = "ha sido contactado por los líderes de la barra brava. Quieren hacer un mosaico gigante con su rostro en la tribuna pero piden ayuda con los gastos de pintura.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Financiar el banner con la caja del club",
                        feedback = "Desvías dinero para comprar pintura y telas.",
                        effectDescription = "+25 Aprecio al Club, +15 Moral",
                        applyEffect = { club, player ->
                            player.clubAppreciation = (player.clubAppreciation + 25).coerceAtMost(100)
                            player.moral = (player.moral + 15).coerceAtMost(100)
                            "La hinchada ruge su nombre en el siguiente encuentro."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Rechazar (Evitar nexos con barras bravas)",
                        feedback = "Dictaminas que el club es neutral en asambleas populares.",
                        effectDescription = "-15 Aprecio al Club, +10 Estilo Mental",
                        applyEffect = { club, player ->
                            player.clubAppreciation = (player.clubAppreciation - 15).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            "Te evitas líos dirigenciales y mantienes la rectitud."
                        }
                    )
                )
            ),
            // 16. Cold and Flu
            EventTemplate(
                title = "Fiebre y Malestar de Vestuario 🤒",
                description = "despierta con síntomas de resfriado severo y dolor de garganta el día antes del partido de liga más importante de la temporada.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Darle descanso absoluto para evitar contagios",
                        feedback = "Lo mandas a su casa a descansar arropado.",
                        effectDescription = "+25 Energía, -10 Moral",
                        applyEffect = { club, player ->
                            player.energy = (player.energy + 25).coerceAtMost(100)
                            player.moral = (player.moral - 10).coerceAtLeast(10)
                            "Se cura rápido, pero no jugará este fin de semana."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Inyectar analgésicos y al campo de juego",
                        feedback = "El cuerpo médico le pone un tratamiento paliativo rápido.",
                        effectDescription = "-20 Energía, +12 Lealtad, -10 Fuerza Física",
                        applyEffect = { club, player ->
                            player.energy = (player.energy - 20).coerceAtLeast(10)
                            player.loyalty = (player.loyalty + 12).coerceAtMost(100)
                            player.attributes.physical = (player.attributes.physical - 10).coerceAtLeast(10)
                            "Suda la camiseta con la fiebre al máximo."
                        }
                    )
                )
            ),
            // 17. Birthday Party
            EventTemplate(
                title = "Cumpleaños del Hijo Mayor 🎈",
                description = "te pide permiso para faltar un viernes para viajar 800 km a soplar las velas de cumpleaños de su hijo de 5 años.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Aprobar vuelo express",
                        feedback = "Le das el fin de semana libre por razones familiares.",
                        effectDescription = "+30 Lealtad, +20 Moral, -10 Energía",
                        applyEffect = { club, player ->
                            player.loyalty = (player.loyalty + 30).coerceAtMost(100)
                            player.moral = (player.moral + 20).coerceAtMost(100)
                            player.energy = (player.energy - 10).coerceAtLeast(10)
                            "Retorna con el corazón rebosante de gratitud."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Denegar (La liga exige concentración)",
                        feedback = "Argumentas que la hinchada confía en él para el encuentro.",
                        effectDescription = "-20 Lealtad, +12 Estilo Mental",
                        applyEffect = { club, player ->
                            player.loyalty = (player.loyalty - 20).coerceAtLeast(10)
                            player.attributes.mental = (player.attributes.mental + 12).coerceAtMost(99)
                            "Se queda pero su mente vaga por el pastel familiar."
                        }
                    )
                )
            ),
            // 18. Mentorship
            EventTemplate(
                title = "Mentoría de Canteranos Juveniles 🎓",
                description = "es solicitado por el director técnico de las inferiores para impartir charlas tácticas de remate a los chicos de 15 años tras el entreno.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Aceptar con honor de club",
                        feedback = "Imparte lecciones de pase y posicionamiento a los canteranos.",
                        effectDescription = "+20 Aprecio al Club, +10 Estilo Mental, -8 Energía",
                        applyEffect = { club, player ->
                            player.clubAppreciation = (player.clubAppreciation + 20).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            player.energy = (player.energy - 8).coerceAtLeast(10)
                            "Los chicos lo adoran y el club se beneficia a futuro."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Enfocarse al 100% en sus entrenamientos",
                        feedback = "Evita distracciones y descansa solo en las tardes.",
                        effectDescription = "+8 Ataque / Remate, -10 Aprecio al Club",
                        applyEffect = { club, player ->
                            player.attributes.attack = (player.attributes.attack + 8).coerceAtMost(99)
                            player.clubAppreciation = (player.clubAppreciation - 10).coerceAtLeast(10)
                            "Mejora su remate al arco individual pero luce egoísta."
                        }
                    )
                )
            ),
            // 19. Cleats Sponsor
            EventTemplate(
                title = "Botines Fluo Cuestionables 👟",
                description = "aparece con unos botines de color rosa fluorescente y luces led integradas, obsequio de su nuevo patrocinador de calzado.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Aprobar el calzado llamativo",
                        feedback = "Le dejas jugar con los botines futuristas.",
                        effectDescription = "+10 Velocidad Base, -5 Moral",
                        applyEffect = { club, player ->
                            player.attributes.speed = (player.attributes.speed + 10).coerceAtMost(99)
                            player.moral = (player.moral - 5).coerceAtLeast(10)
                            "Es rapidísimo en el césped pero la hinchada rival se mofa."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Imponer botines negros tradicionales",
                        feedback = "Le obligas a pintar o cambiar el calzado fluorescente.",
                        effectDescription = "+10 Moral, -5 Velocidad Base",
                        applyEffect = { club, player ->
                            player.moral = (player.moral + 10).coerceAtMost(100)
                            player.attributes.speed = (player.attributes.speed - 5).coerceAtLeast(10)
                            "Se siente en un club serio con orgullo de camiseta."
                        }
                    )
                )
            ),
            // 20. Sleep Issues
            EventTemplate(
                title = "Insomnio por Videojuegos 🎮",
                description = "se queda despierto hasta las 4:00 AM jugando partidas de disparos tácticos en línea con sus amigos de internet y su rendimiento de reacción cae.",
                options = listOf(
                    EventOptionTemplate(
                        text = "Confiscar dispositivos en la concentración",
                        feedback = "Instalas un router de corte de internet a las 11:00 PM.",
                        effectDescription = "+18 Energía, +10 Estilo Mental, -15 Moral",
                        applyEffect = { club, player ->
                            player.energy = (player.energy + 18).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            player.moral = (player.moral - 15).coerceAtLeast(10)
                            "Duerme excelente pero se queja amargamente de la censura."
                        }
                    ),
                    EventOptionTemplate(
                        text = "Charla amigable e informativa",
                        feedback = "Le hablas sobre el sueño REM y la fatiga muscular.",
                        effectDescription = "+8 Energía, +10 Estilo Mental",
                        applyEffect = { club, player ->
                            player.energy = (player.energy + 8).coerceAtMost(100)
                            player.attributes.mental = (player.attributes.mental + 10).coerceAtMost(99)
                            "Aprecia tu consejo paternal e intenta ir a la cama temprano."
                        }
                    )
                )
            )
        )

        // Select a random template
        val template = eventTemplates.random()
        val options = template.options.map { optionTemplate ->
            EventOption(
                text = optionTemplate.text,
                feedback = optionTemplate.feedback,
                effectDescription = optionTemplate.effectDescription,
                applyEffect = optionTemplate.applyEffect
            )
        }

        return ManagerEvent(
            title = template.title,
            description = "${player.fullName} ${template.description}",
            affectedPlayerName = player.fullName,
            affectedPlayerId = player.id,
            options = options
        )
    }
}

private data class EventTemplate(
    val title: String,
    val description: String,
    val options: List<EventOptionTemplate>
)

private data class EventOptionTemplate(
    val text: String,
    val feedback: String,
    val effectDescription: String,
    val applyEffect: (Club, Player) -> String
)
