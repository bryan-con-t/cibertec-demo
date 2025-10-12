package com.example.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context : Context) : SQLiteOpenHelper(context, "compras.db", null, 1) {

    override fun onCreate(db : SQLiteDatabase) {
        // Tabla USUARIO
        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dni TEXT,
                apellido_paterno TEXT,
                apellido_materno TEXT,
                nombres TEXT,
                celular TEXT,
                sexo TEXT,
                correo TEXT,
                clave TEXT
            )
        """.trimIndent()) // Elimina espacios y saltos innecesarios

        // Tabla LISTA_COMPRAS
        db.execSQL("""
            CREATE TABLE lista_compras(
                id_lista_compras INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                fecha TEXT,
                id_usuario INTEGER,
                FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
            )
        """.trimIndent())

        // Tabla DETALLE_LISTA
        db.execSQL("""
            CREATE TABLE detalle_lista(
                id_detalle_lista INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                producto TEXT,
                unidad_medida TEXT,
                cantidad INTEGER,
                precio_unitario REAL,
                precio_pagado REAL,
                id_lista_compras INTEGER,
                FOREIGN KEY (id_lista_compras) REFERENCES lista_compras (id_lista_compras)
            )
        """.trimIndent())
    }

    /**
     * Se utiliza cuando se cambia la versión de la base de datos, para reiniciar todos los datos y la estructura
     */
    override fun onUpgrade(db : SQLiteDatabase, oldVersion : Int,newVersion : Int) {
        db.execSQL("DROP TABLE IF EXISTS detalle_lista")
        db.execSQL("DROP TABLE IF EXISTS lista_compras")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)

        // En entornos de producción
//        if (oldVersion < 2) {
//            db.execSQL("ALTER TABLE usuario ADD COLUMN direccion TEXT")
//        }
    }
}