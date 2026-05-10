package com.chrisnkl.smarttodo.data

import com.chrisnkl.smarttodo.model.TaskAction
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsightRepository {


    private val entityExtractor: EntityExtractor by lazy {
        val options = EntityExtractorOptions.Builder(
            EntityExtractorOptions.ENGLISH
        ).build()

        EntityExtraction.getClient(options)
    }

    suspend fun analyzeTask(text: String): List<TaskAction> = withContext(Dispatchers.IO)

    fun close() {
        entityExtractor.close()
    }

}