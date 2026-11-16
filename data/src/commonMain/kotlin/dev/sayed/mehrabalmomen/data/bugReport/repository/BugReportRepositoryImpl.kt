package dev.sayed.mehrabalmomen.data.bugReport.repository

import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import dev.sayed.mehrabalmomen.domain.repository.bugReport.BugReportRepository

class BugReportRepositoryImpl(
    private val remoteDataSource: BugReportRemoteDataSource
) : BugReportRepository {

    override suspend fun submitBugReport(request: BugReportRequest) {
        remoteDataSource.submit(request)
    }
}