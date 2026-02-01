package dev.sayed.mehrabalmomen.data.repository

import dev.sayed.mehrabalmomen.data.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import dev.sayed.mehrabalmomen.domain.repository.BugReportRepository

class BugReportRepositoryImpl(
    private val remoteDataSource: BugReportRemoteDataSource
) : BugReportRepository {

    override suspend fun submitBugReport(request: BugReportRequest) {
        remoteDataSource.submit(request)
    }
}