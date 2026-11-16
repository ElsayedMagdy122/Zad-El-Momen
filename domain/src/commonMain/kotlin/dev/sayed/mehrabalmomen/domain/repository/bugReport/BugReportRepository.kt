package dev.sayed.mehrabalmomen.domain.repository.bugReport

import dev.sayed.mehrabalmomen.domain.model.BugReportRequest

interface BugReportRepository {
    suspend fun submitBugReport(request: BugReportRequest)
}