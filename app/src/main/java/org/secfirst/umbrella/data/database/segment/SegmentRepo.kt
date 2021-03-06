package org.secfirst.umbrella.data.database.segment

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject

interface SegmentRepo {

    suspend fun loadChecklist(checklistId: String): Checklist?

    suspend fun loadMarkdown(markdownId: String): Markdown?

    suspend fun loadSubject(subjectId: String): Subject?

    suspend fun loadModule(moduleId: String): Module?

    suspend fun loadDifficulty(difficultyId: String): Difficulty?

    suspend fun saveChecklist(checklist: Checklist)

    suspend fun saveMarkdown(markdown: Markdown)

    suspend fun saveDifficultySelect(subjectId: String, difficulty: Difficulty)

    suspend fun loadDifficultyBySubjectId(subjectId: String): List<Difficulty>

    suspend fun loadSubjectByRootDir(rootDir: String): Subject?

    suspend fun loadModuleByRootdir(moduleName: String): Module?


}