package org.secfirst.umbrella.whitelabel.data.database.checklist

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

interface ChecklistRepo {

    suspend fun loadChecklist(sha1ID: String): Checklist?

    suspend fun delteChecklistContent(checklistContent: Content)

    suspend fun deleteChecklist(checklist: Checklist)

    suspend fun disableChecklistContent(checklistContent: Content)

    suspend fun insertChecklistContent(checklistContent: Content)

    suspend fun insertChecklist(checklist: Checklist)

    suspend fun loadChecklistProgressDone(): List<Checklist>

    suspend fun loadAllChecklistFavorite(): List<Checklist>

    suspend fun loadChecklistCount(): Long

    suspend fun loadCustomChecklistCount(): Long

    suspend fun loadAllChecklist(): List<Checklist>

    suspend fun loadSubjectById(subjectSha1ID: String): Subject?

    suspend fun loadDifficultyById(sha1ID: String): Difficulty

    suspend fun getAllChecklistInProgress(): List<Checklist>

    suspend fun getAllCustomChecklistInProgress(): List<Checklist>
}