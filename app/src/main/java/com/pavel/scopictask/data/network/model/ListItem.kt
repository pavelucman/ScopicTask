package com.pavel.scopictask.data.network.model

import java.io.Serializable

data class ListItem(
    val id: String = "",
    val name: String = "",
    val createdBy: User = User(),
    val done: Boolean = false
) : Serializable