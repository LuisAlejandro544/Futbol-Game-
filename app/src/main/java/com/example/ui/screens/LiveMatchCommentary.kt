package com.example.ui.screens

// Generate procedurally sound football commentaries to make the tick feel alive
fun getDynamicCommentary(minute: Int, homeTeam: String, awayTeam: String): String? {
    val random = java.util.Random(minute.toLong() * 31 + homeTeam.hashCode())
    val commentaries = listOf(
        "El mediocampo de $homeTeam intenta hilvanar juego paciente con pases cortos y precisos.",
        "Presión alta de $awayTeam complicando seriamente la salida defensiva local.",
        "Se calientan los ánimos tras una disputada barrida de balón en la línea de banda.",
        "Balón largo buscando la velocidad por los extremos, pero se pierde por banda.",
        "Falta táctica inteligente en mitad de cancha para frenar un contragolpe rival.",
        "La hinchada de $homeTeam canta con fervor en las tribunas para animar a los suyos.",
        "El director técnico de $awayTeam grita indicaciones enérgicas al borde de su área técnica.",
        "Pase filtrado peligroso en la frontal, interceptado con maestría por la saga defensiva.",
        "Balón dividido en tres cuartos de cancha, juego físico de contacto y mucha fricción.",
        "Excelente cruce defensivo en el área grande para despejar un balón sumamente venenoso.",
        "Disparo lejano con poca dirección que termina cómodamente en los guantes del arquero.",
        "El partido reduce la intensidad mientras ambas escuadras reordenan sus líneas tácticas."
    )
    return if (minute > 0 && minute % 6 == 0) {
        commentaries[random.nextInt(commentaries.size)]
    } else {
        null
    }
}
