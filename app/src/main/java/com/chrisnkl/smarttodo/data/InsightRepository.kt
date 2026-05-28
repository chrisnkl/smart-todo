package com.chrisnkl.smarttodo.data

import com.chrisnkl.smarttodo.model.TaskAction
import com.chrisnkl.smarttodo.model.TaskActionType
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InsightRepository {
    private val entityExtractor: EntityExtractor by lazy {
        val options = EntityExtractorOptions.Builder(
            EntityExtractorOptions.ENGLISH
        ).build()

        EntityExtraction.getClient(options)
    }

    suspend fun analyzeTask(text: String): List<TaskAction> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext emptyList()

        try {
            entityExtractor.downloadModelIfNeeded().await()
            val annotations = entityExtractor.annotate(text).await()
            val actions = mutableListOf<TaskAction>()

            for (annotation in annotations) {
                val type = when {
                    annotation.entities.any { it.type == Entity.TYPE_PHONE } -> TaskActionType.PHONE
                    annotation.entities.any { it.type == Entity.TYPE_EMAIL } -> TaskActionType.EMAIL
                    annotation.entities.any { it.type == Entity.TYPE_URL } -> TaskActionType.URL
                    annotation.entities.any { it.type == Entity.TYPE_ADDRESS } -> TaskActionType.ADDRESS
                    else -> null
                }

                if (type != null) {
                    actions.add(
                        TaskAction(
                            start = annotation.start,
                            end = annotation.end,
                            value = annotation.annotatedText,
                            type = type
                        )
                    )
                }
            }

            actions
                .distinctBy { Triple(it.start, it.end, it.type) }
                .sortedBy { it.start }

        } catch (e: Exception) { emptyList() }

    }

    fun close() {
        entityExtractor.close()
    }

}