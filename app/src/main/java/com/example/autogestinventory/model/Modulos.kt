package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Modulos(
    val id: Int? = null,
    val nombre: String,
    val check: Boolean = false
)

@Serializable
data class PermisoModulo(
    val id_modulo: Int? = null,
    val modulos: Modulos? = null
)

@Serializable
data class UsuarioConModulos(
    val permisos: List<PermisoModulo> = emptyList()
)
