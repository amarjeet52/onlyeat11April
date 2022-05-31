package com.codebrew.clikat.base

data class PagingResult<out T>(
        val isFirstPage: Boolean,
        val result: T? = null)