package com.lab6

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun Calculator() {
    val scrollState = rememberScrollState()

    val results = remember { mutableStateOf<List<OutputDistributionTire>>(emptyList()) }
    val totalDistributionTiresResult = remember { mutableStateOf<TotalDistributionTire?>(null) }
    val totalOutputResult = remember { mutableStateOf<OutputDistributionTireTotal?>(null) }
    val result1 = remember { mutableStateOf<OutputDistributionTire?>(null) }
    val result2 = remember { mutableStateOf<OutputDistributionTire?>(null) }
    val inputDistributionTires: List<InputDistributionTire> = listOf(
        InputDistributionTire("Шліфувальний верстат", 0.92, 0.9, 0.38, 4, 21, 0.15, 1.33),
        InputDistributionTire("Свердлильний верстат", 0.92, 0.9, 0.38, 2, 14, 0.12, 1.0),
        InputDistributionTire("Фугувальний верстат", 0.92, 0.9, 0.38, 4, 42, 0.15, 1.33),
        InputDistributionTire("Циркулярна пила", 0.92, 0.9, 0.38, 1, 36, 0.3, 1.56),
        InputDistributionTire("Прес", 0.92, 0.9, 0.38, 1, 20, 0.5, 0.75),
        InputDistributionTire("Полірувальний верстат", 0.92, 0.9, 0.38, 1, 40, 0.22, 1.0),
        InputDistributionTire("Фрезерний верстат", 0.92, 0.9, 0.38, 2, 32, 0.2, 1.0),
        InputDistributionTire("Вентилятор", 0.92, 0.9, 0.38, 1, 20, 0.65, 0.75)
    )
    val largeEP1 = InputDistributionTire("Зварювальний трансформатор", 0.92, 0.9, 0.38, 2, 100, 0.2, 3.0)
    val largeEP2 = InputDistributionTire("Сушильна шафа", 0.92, 0.9, 0.38, 2, 120, 0.8, 0.0)

    val inputDistributionTireTotal = InputDistributionTireTotal(81, 2330, 752, 657, 96399)

    Column(
        modifier = Modifier.padding(top = 64.dp, start = 16.dp)
    ) {
        Button(
            onClick = {
                val listOfOutputData = inputDistributionTires.map { calculateDistributionTire(it) }

                results.value = listOfOutputData
                totalDistributionTiresResult.value = calculateDistributionTireTotal(inputDistributionTires, listOfOutputData)
                totalOutputResult.value = calculateTotal(inputDistributionTireTotal)
                result1.value = calculateDistributionTire(largeEP1)
                result2.value = calculateDistributionTire(largeEP2)
            }
        ) {
            Text("Calculate")
        }

        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(scrollState)
        ) {
            results.value.forEach { result -> DistributionTireComponent(result) }

            totalDistributionTiresResult.value?.let { DistributionTireTotalComponent(it) }

            Text("\nКрупні ЕП, що живляться від ТП (трансформаторної підстанції)")
            result1.value?.let { DistributionTireComponent(it) }
            result2.value?.let { DistributionTireComponent(it) }

            Text("\nВсього, навантаження цеху")

            totalOutputResult.value?.let { TotalComponent(it) }
        }

    }
}

@Composable
fun DistributionTireComponent(shr: OutputDistributionTire) {
    Column {
        Text("Найменування ЕП: ${shr.name}")
        Text("n * Pv: ${shr.nP}")
        Text("n * Pv * Kv: ${shr.nPK}")
        Text("n * Pv * Kv * tg: ${shr.nPKtg}")
        Text("n * Pv^2: ${shr.nP2}")
        Text("Розрахунковий струм: ${shr.Ip}")
    }
}

@Composable
fun DistributionTireTotalComponent(totalShr: TotalDistributionTire) {
    Column {
        Text("")
        Text("Загальний результат ШР1, ШР2, ШР3.")
        Text("Кількість: ${totalShr.n}")
        Text("n * P: ${totalShr.nP}")
        Text("Коефіцієнт використання: ${totalShr.K}")
        Text("n * P * K: ${totalShr.nPK}")
        Text("n * P * K * tg: ${totalShr.nPKtg}")
        Text("n * P^2: ${totalShr.nP2}")
        Text("Ефективна кількість ЕП: ${totalShr.nEf}")
        Text("Розрахунковий коефіцієнт активної потужності: ${totalShr.Ka}")
        Text("Розрахункове активне навантаження: ${totalShr.Ra}")
        Text("Розрахункове реактивне навантаження: ${totalShr.Rr}")
        Text("Повна потужність: ${totalShr.Sr}")
        Text("Розрахунковий струм: ${totalShr.Ip}")
    }
}

@Composable
fun TotalComponent(total: OutputDistributionTireTotal) {
    Column {
        Text("Коефіцієнт використання: ${total.K}")
        Text("Ефективна кількість ЕП: ${total.nEf}")
        Text("Розрахунковий коефіцієнт активної потужності: ${total.Ka}")
        Text("Розрахункове активне навантаження: ${total.Ra}")
        Text("Розрахункове реактивне навантаження: ${total.Rr}")
        Text("Повна потужність: ${total.Sr}")
        Text("Розрахунковий струм: ${total.Ip}")
    }
}

fun calculateTotal(input: InputDistributionTireTotal): OutputDistributionTireTotal {
    val K = input.nPK.toDouble() / input.nP.toDouble()
    val nEf = (input.nP.toDouble()).pow(2) / input.nP2
    val Ka = 0.7
    val Ra = Ka * input.nPK
    val Rr = Ka * input.nPKtg
    val S = sqrt(Ra.pow(2) + Rr.pow(2))
    val I = Ra / 0.38

    return OutputDistributionTireTotal(round(K), round(nEf), Ka, Ra, Rr, round(S), round(I))
}

fun calculateDistributionTireTotal(inputs: List<InputDistributionTire>, outputs: List<OutputDistributionTire>): TotalDistributionTire {
    val n = inputs.sumOf {it.n}
    val nP = outputs.sumOf {it.nP}
    val nPK = outputs.sumOf {it.nPK}
    val nPKtg = outputs.sumOf {it.nPKtg}
    val nP2 = outputs.sumOf {it.nP2}
    val K = nPK / nP
    val nEf = (nP.toDouble().pow(2)) / nP2
    val Ra = 1.25 * nPK
    val Rr = nPKtg
    val S = sqrt(Ra.pow(2) + Rr.pow(2))
    val I = Ra / 0.38

    return TotalDistributionTire("ШР", n, nP, round(K), round(nPK), round(nPKtg), nP2, round(nEf), 1.25, round(Ra), round(Rr), round(S), round(I))
}

fun calculateDistributionTire(input: InputDistributionTire): OutputDistributionTire {
    val nP = input.n * input.Pn
    val nPK = nP * input.Kv
    val nPKtg = nPK * input.tg
    val nP2 = input.n * input.Pn * input.Pn
    val I = nP / (sqrt(3.0) * input.Un * input.cos * input.nomK)

    return OutputDistributionTire(input.name, nP, round(nPK), round(nPKtg), nP2, round(I))
}

@SuppressLint("DefaultLocale")
fun round(num: Double): Double {
    return String.format("%.2f", num).toDouble()
}

data class InputDistributionTire (
    val name: String, // Найменування ЕП
    val nomK: Double, // Номінальне значення коефіцієнта корисної дії ЕП
    val cos: Double, // Коефіцієнт потужності навантаження
    val Un: Double, // Напруга навантаження
    val n: Int, // Кількість ЕП
    val Pn: Int, // Номінальна потужність ЕП
    val Kv: Double, // Коефіцієнт використання
    val tg: Double, // Коефіцієнт реактивної потужності
)
data class InputDistributionTireTotal (
    val n: Int, // Кількість ЕП
    val nP: Int, // n * P
    val nPK: Int, // n * P * K
    val nPKtg: Int, // n * P * K * tg
    val nP2: Int, // n * P^2
)
data class OutputDistributionTire (
    val name: String, // Назва ЕП
    val nP: Int, // n * P
    val nPK: Double, // n * P * K
    val nPKtg: Double, // n * P * K * tg
    val nP2: Int, // n * P^2
    val Ip: Double, // Розрахунковий струм Ip
)
data class OutputDistributionTireTotal (
    val K: Double, // Коефіцієнт використання
    val nEf: Double, // Ефективна кількість ЕП
    val Ka: Double, // Розрахунковий коефіцієнт активної потужності
    val Ra: Double, // Розрахункове активне навантаження
    val Rr: Double, // Розрахункове реактивне навантаження
    val Sr: Double, // Повна потужність
    val Ip: Double, // Розрахунковий струм
)
data class TotalDistributionTire (
    val name: String, // Назва ЕП
    val n: Int, // Кількість ЕП
    val nP: Int, // n * P
    val K: Double, // Коефіцієнт використання
    val nPK: Double, // n * P * K
    val nPKtg: Double, // n * P * K * tg
    val nP2: Int, // n * P^2
    val nEf: Double, // Ефективна кількість ЕП
    val Ka: Double, // Розрахунковий коефіцієнт активної потужності
    val Ra: Double, // Розрахункове активне навантаження
    val Rr: Double, // Розрахункове реактивне навантаження
    val Sr: Double, // Повна потужність
    val Ip: Double, // Розрахунковий струм
)