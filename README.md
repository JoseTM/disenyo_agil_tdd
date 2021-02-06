Se tiene que procesar un fichero csv de tal manera que cumpla como mínimo las siguientes reglas de negocio:

- Un fichero con una sola factura donde todo es correcto, debería producir como salida la misma
línea
- Un fichero con una sola factura donde IVA e IGIC están rellenos, debería eliminar la línea
- Un fichero con una sola factura donde el neto está mal calculado, debería ser eliminada
- Un fichero con una sola factura donde CIF y NIF están rellenos, debería eliminar la línea
- Un fichero de una sola línea es incorrecto porque no tiene cabecera
- Si el número de factura se repite en varias líneas, se eliminan todas ellas (sin dejar ninguna).
- Una lista vacía o nula producirá una lista vacía de salida
