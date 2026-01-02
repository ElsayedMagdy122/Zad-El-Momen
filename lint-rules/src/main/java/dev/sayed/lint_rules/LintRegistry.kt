package dev.sayed.lint_rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class LintRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(ForbiddenMaterial3ComponentsDetector.ISSUE)

    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
}