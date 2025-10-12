package com.example.myapplication.entity

data class DetalleLista (
    val id : Int = 0,
    val producto : String,
    val unidadMedida : String = "",
    val cantidad : Int = 0,
    val precioUnitario : Double = 0.0,
    val precioPagado : Double = 0.0,
    val idListaCompras : Int
)

