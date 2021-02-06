package csvFilter

import java.math.BigDecimal

class CsvFilter {
    private val ivaFieldIndex = 4
    private val igicFieldIndex = 5
    private val netoFieldIndex = 3
    private val brutoFieldIndex = 2
    private val cifFieldIndex = 7
    private val nifFieldIndex = 8

    fun filter (lines: List<String>) : List<String> {
        if (lines.size < 2){
            return listOf()
        }

        val result = mutableListOf<String>()
        result.add(lines[0])
        val invoiceLine = lines[1]
        val fields = invoiceLine.split(',')

        val regexDecimal = "\\D+".toRegex()

        val isImpuestosCorrectos = (fields[ivaFieldIndex].isNullOrEmpty() xor fields[igicFieldIndex].isNullOrEmpty()) &&
                (!(fields[ivaFieldIndex].contains(regexDecimal)) && !(fields[igicFieldIndex].contains(regexDecimal)))
        val isTotalesCorrectos = (!fields[netoFieldIndex].isNullOrEmpty() && !fields[brutoFieldIndex].isNullOrEmpty()) &&
                (!(fields[netoFieldIndex].contains(regexDecimal)) && !(fields[brutoFieldIndex].contains(regexDecimal)))
        val isCifNifCorrecto = (fields[cifFieldIndex].isNullOrEmpty() xor fields[nifFieldIndex].isNullOrEmpty())

        if (isCifNifCorrecto && isImpuestosCorrectos && isTotalesCorrectos && calculateNetoCorrecto(fields)) {
            result.add(lines[1])
        }

        return result.toList()
    }

    private fun calculateNetoCorrecto(fields: List<String> ):Boolean {
        val impuesto = if (fields[ivaFieldIndex].isNullOrEmpty()) fields[igicFieldIndex].toBigDecimal()
        else fields[ivaFieldIndex].toBigDecimal()
        val neto = fields[netoFieldIndex].toBigDecimal().setScale(2)
        val bruto = fields[brutoFieldIndex].toBigDecimal()
        val netoCorrecto = bruto.multiply(impuesto.divide(BigDecimal.valueOf(100))) + bruto
        return  neto.equals(netoCorrecto)
    }
}