package dev.sayed.mehrabalmomen.data.bugReport.remote

import dev.sayed.mehrabalmomen.domain.model.BugReportRequest

class IosBugReportRemoteDataSourceImpl : BugReportRemoteDataSource {
    override suspend fun submit(report: BugReportRequest) {}
}
