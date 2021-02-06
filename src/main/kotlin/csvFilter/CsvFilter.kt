package csvFilter

import java.math.BigDecimal

class CsvFilter {
    private val headerLineIndex = 0
    private val codFieldIndex = 0
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

        val numeroFacturaList = mutableListOf<String>()

        lines.forEach{  numeroFacturaList.add(it.split(',')[codFieldIndex])  }

        val lineasSinDuplicados = mutableListOf<String>()
        lines.forEach {  if (notDuplicated(it, numeroFacturaList)) lineasSinDuplicados.add(it)   }

        val result = mutableListOf<String>()
        result.add(lines[headerLineIndex])
        lineasSinDuplicados.forEach { if (isLineValid(it)) result.add(it) }

        return result.toList()
    }

    private fun isLineValid(invoiceLine: String):Boolean {


        val fields = invoiceLine.split(',')

        val regexDecimal = "\\D+".toRegex()

        val isImpuestosCorrectos =
            (fields[ivaFieldIndex].isEmpty() xor fields[igicFieldIndex].isEmpty()) &&
                    (!(fields[ivaFieldIndex].contains(regexDecimal)) && !(fields[igicFieldIndex].contains(
                        regexDecimal
                    )))
        val isTotalesCorrectos =
            (!fields[netoFieldIndex].isEmpty() && !fields[brutoFieldIndex].isEmpty()) &&
                    (!(fields[netoFieldIndex].contains(regexDecimal)) && !(fields[brutoFieldIndex].contains(
                        regexDecimal
                    )))
        val isCifNifCorrecto = (fields[cifFieldIndex].isEmpty() xor fields[nifFieldIndex].isEmpty())

        return (isCifNifCorrecto && isImpuestosCorrectos && isTotalesCorrectos && isNetoCorrect(fields))

    }

    private fun isNetoCorrect(fields: List<String> ):Boolean {
        val impuesto = if (fields[ivaFieldIndex].isEmpty()) fields[igicFieldIndex].toBigDecimal()
        else fields[ivaFieldIndex].toBigDecimal()
        val neto = fields[netoFieldIndex].toBigDecimal().setScale(2)
        val bruto = fields[brutoFieldIndex].toBigDecimal()
        val netoCorrecto = bruto.multiply(impuesto.divide(BigDecimal.valueOf(100))) + bruto
        return  neto.equals(netoCorrecto)
    }

    private fun notDuplicated(it: String, duplicadosList: MutableList<String> ):Boolean {
        val codigoFactura = it.split(',')[codFieldIndex]
        return (duplicadosList.count { it == codigoFactura } == 1)
    }


}