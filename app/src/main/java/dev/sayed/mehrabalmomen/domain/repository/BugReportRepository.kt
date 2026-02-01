package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.model.BugReportRequest

interface BugReportRepository {
    suspend fun submitBugReport(request: BugReportRequest)
}