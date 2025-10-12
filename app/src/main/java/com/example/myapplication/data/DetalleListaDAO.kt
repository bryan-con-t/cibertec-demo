package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.myapplication.entity.DetalleLista

class DetalleListaDAO(context : Context) {
    private val dbHelper = AppDatabaseHelper(context)

    /**
     * Función: Insertar nuevo detalle de lista de compras
     */
    fun insertar(detalle : DetalleLista) : Long { // Tipo Long por la respuesta que recibe
        val db = dbHelper.writableDatabase // Define la variable de BBDD con privilegios de escritura
        val valores = ContentValues().apply { // Asigna los valores a una variable Array de clave-valor
            put("producto", detalle.producto)
            put("unidad_medida", detalle.unidadMedida)
            put("cantidad", detalle.cantidad)
            put("precio_unitario", detalle.precioUnitario)
            put("precio_pagado", detalle.precioPagado)
            put("id_lista_compras", detalle.idListaCompras)
        }
        return db.insert("detalle_lista", null, valores) // Inserta los valores en la BBDD
    }

    /**
     * Función: Obtener los detalles de la lista
     *
     * @idLista = ID de la lista a consultar
     */
    fun obtenerPorLista(idLista : Int) : List<DetalleLista> { // Tipo List<> por la respuesta que recibe
        val db = dbHelper.readableDatabase // Define la variable de BBDD con privilegios de lectura
        val lista = mutableListOf<DetalleLista>() // Inicializamos una lista vacía
        val cursor : Cursor = db.rawQuery( // Realiza la consulta a la BBDD
            "SELECT * FROM detalle_lista WHERE id_lista_compras = ?", // Define la consulta SQL
            arrayOf(idLista.toString()) // Le envía el parámetro a la consulta SQL
        )
        while (cursor.moveToNext()) { // Mientras existan registros en el resultado obtenido
            lista.add( // Agregamos un registro a la lista vacía
                DetalleLista(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_detalle_lista")),
                    producto = cursor.getString(cursor.getColumnIndexOrThrow("producto")),
                    unidadMedida =  cursor.getString(cursor.getColumnIndexOrThrow("unidad_medida")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_unitario")),
                    precioPagado = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_pagado")),
                    idListaCompras = cursor.getInt(cursor.getColumnIndexOrThrow("id_lista_compras"))
                )
            )
        }
        cursor.close() // Cerramos el cursor de la consulta
        db.close() // Cerramos la consulta de la BBDD
        return lista // Devuelve la lista llena con resultados de la BBDD
    }
}