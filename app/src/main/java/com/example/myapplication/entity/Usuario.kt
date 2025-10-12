package com.example.myapplication.entity

data class Usuario (
    val id : Int,
    val dni : String,
    val apellidoPaterno : String,
    val apellidoMaterno : String,
    val nombres : String,
    val celular : String,
    val sexo : String,
    val correo : String,
    val clave : String
)