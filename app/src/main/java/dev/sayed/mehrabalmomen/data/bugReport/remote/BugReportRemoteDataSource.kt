package dev.sayed.mehrabalmomen.data.bugReport.remote

import dev.sayed.mehrabalmomen.domain.model.BugReportRequest

interface BugReportRemoteDataSource {
    suspend fun submit(
        report: BugReportRequest
    )
}