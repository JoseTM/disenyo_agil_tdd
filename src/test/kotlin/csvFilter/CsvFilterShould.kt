package csvFilter

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CsvFilterShould {
    val headerLine = "Num_factura, Fecha, Bruto, Neto, IVA, IGIC, Concepto, CIF_cliente, NIF_cliente"
    lateinit var filter : CsvFilter
    private val emptyDataFile = listOf(headerLine)
    private val emptyField = ""

    @Before
    fun Setup(){
        filter = CsvFilter()
    }

    @Test
    fun allow_for_correct_lines_only(){
        val lines = fileWithOneInvoiceLineHaving()
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(lines)
    }

    @Test
    fun exclude_lines_with_both_tax_fields_populated_as_they_are_exclusive(){
        val lines = fileWithOneInvoiceLineHaving(ivaTax = "19")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_both_tax_fields_empty_as_one_is_required(){
        val lines = fileWithOneInvoiceLineHaving(igicTax = emptyField)
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_iva_tax_field_filled_with_alphabetic_as_number_is_required(){

        val lines = fileWithOneInvoiceLineHaving(igicTax = emptyField, ivaTax = "2B3c")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_igic_tax_field_filled_with_alphabetic_as_number_is_required(){

        val lines = fileWithOneInvoiceLineHaving(igicTax = "3ñ4", ivaTax = emptyField)
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_both_tax_fields_populated_even_if_non_decimal(){
        val lines = fileWithOneInvoiceLineHaving(igicTax = "12", ivaTax = "XYZ")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_neto_or_bruto_empty_as_both_are_required(){
        val lines = fileWithOneInvoiceLineHaving(gross = emptyField, neto = emptyField)
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_alphabetic_characters_in_neto_as_number_is_required(){
        val lines = fileWithOneInvoiceLineHaving(gross = "1000", neto = "8ñ10")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_alphabetic_characters_in_bruto_as_number_is_required(){
        val lines = fileWithOneInvoiceLineHaving(gross = "10ñ00", neto = "1080")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_neto_misscalculated(){
        val lines = fileWithOneInvoiceLineHaving(gross = "10ñ00", neto = "810")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_lines_with_cif_and_nif_filled_as_just_one_is_required(){
        val lines = fileWithOneInvoiceLineHaving(numCIF = "B76430134", numNIF = "84988292A")
        val result = filter.apply(lines)
        assertThat(result).isEqualTo(emptyDataFile)
    }

    @Test
    fun exclude_files_without_header(){
        val invoiceLine = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,84988292A"
        val result = filter.apply(listOf(invoiceLine))
        assertThat(result).isEmpty()
    }

    @Test
    fun return_empty_when_the_file_is_empty(){
        val result = filter.apply(listOf())
        assertThat(result).isEmpty()
    }

    @Test
    fun exclude_lines_with_same_number_facture_as_number_facture_must_be_unique(){
        val invoiceLine = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,84988292A"
        val invoiceLine2 = "2,02/05/2019,1000,1080,,8,ACER Laptop,,84988292A"
        val invoiceLine3 = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"
        val invoiceLine4 = "3,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"
        val invoiceLine5 = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"

        val result = filter.apply(listOf(headerLine,invoiceLine, invoiceLine2, invoiceLine3, invoiceLine4, invoiceLine5))

        assertThat(result).isEqualTo(listOf(headerLine,invoiceLine2, invoiceLine4))
    }

    private fun fileWithOneInvoiceLineHaving(gross:String = "1000", neto:String = "1080", ivaTax: String = emptyField,
                                             igicTax: String = "8", concept: String = "irrelevant", numCIF:String = "B76430134",
                                             numNIF:String = emptyField): List<String> {

        val invoiceId = "1"
        val invoiceDate = "02/05/2019"
        val grossAmount = gross
        val netAmount = neto
        val cif = numCIF
        val nif = numNIF
        val formattedLine = listOf(invoiceId, invoiceDate, grossAmount, netAmount, ivaTax, igicTax, concept, cif, nif
        ).joinToString(",")
        return listOf(headerLine, formattedLine)
    }

}