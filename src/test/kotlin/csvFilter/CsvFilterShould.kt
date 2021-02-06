package csvFilter

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CsvFilterShould {
    val headerLine = "Num_factura, Fecha, Bruto, Neto, IVA, IGIC, Concepto, CIF_cliente, NIF_cliente"

    @Test
    fun allow_for_correct_lines_only(){
        val invoiceLine = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine, invoiceLine))
    }

    @Test
    fun exclude_lines_with_both_tax_fields_populated_as_they_are_exclusive(){
        val invoiceLine = "1,02/05/2019,1000,810,19,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_both_tax_fields_empty_as_one_is_required(){
        val invoiceLine = "1,02/05/2019,1000,810,,,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_iva_tax_field_filled_with_alphabetic_as_number_is_required(){
        val invoiceLine = "1,02/05/2019,1000,810,2B3c,,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_igic_tax_field_filled_with_alphabetic_as_number_is_required(){
        val invoiceLine = "1,02/05/2019,1000,810,,3ñ4,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_both_tax_fields_populated_even_if_non_decimal(){
        val invoiceLine = "1,02/05/2019,,,XYZ,12,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_neto_or_bruto_empty_as_both_are_required(){
        val invoiceLine = "1,02/05/2019,1000,,,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_alphabetic_characters_in_neto_as_number_is_required(){
        val invoiceLine = "1,02/05/2019,1000,8ñ10,,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_alphabetic_characters_in_bruto_as_number_is_required(){
        val invoiceLine = "1,02/05/2019,10ñ00,810,,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_neto_misscalculated(){
        val invoiceLine = "1,02/05/2019,1000,810,,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_lines_with_cif_and_nif_filled_as_just_one_is_required(){
        val invoiceLine = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,78561783Q"
        val result = CsvFilter().filter(listOf(headerLine, invoiceLine))
        assertThat(result).isEqualTo(listOf(headerLine))
    }

    @Test
    fun exclude_file_without_header(){
        val invoiceLine = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,78561783Q"
        val result = CsvFilter().filter(listOf(invoiceLine))
        assertThat(result).isEmpty()
    }

    @Test
    fun empty_file_produce_empty_return(){
        val result = CsvFilter().filter(listOf())
        assertThat(result).isEmpty()
    }

    @Test
    fun exclude_lines_with_same_number_facture_as_number_facture_must_be_unique(){
        val invoiceLine = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,78561783Q"
        val invoiceLine2 = "2,02/05/2019,1000,1080,,8,ACER Laptop,,78561783Q"
        val invoiceLine3 = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"
        val invoiceLine4 = "3,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"
        val invoiceLine5 = "1,02/05/2019,1000,1080,,8,ACER Laptop,B76430134,"
        val result = CsvFilter().filter(listOf(headerLine,invoiceLine, invoiceLine2, invoiceLine3, invoiceLine4, invoiceLine5))
        assertThat(result).isEqualTo(listOf(headerLine,invoiceLine2, invoiceLine4))
    }

}