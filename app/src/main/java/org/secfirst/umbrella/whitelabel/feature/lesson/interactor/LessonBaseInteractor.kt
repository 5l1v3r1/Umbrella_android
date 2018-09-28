package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchCategories(): List<Module>

    suspend fun fetchCategoryBy(id: Long): Module?

    suspend fun fetchSubcategoryBy(id: Long): Subject?

    suspend fun fetchChildBy(id: Long): Difficulty?

    suspend fun fetchTopicPreferredBy(subcategory: Long): TopicPreferred?
}