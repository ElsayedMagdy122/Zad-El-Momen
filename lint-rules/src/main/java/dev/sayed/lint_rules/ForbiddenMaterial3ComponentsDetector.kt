package dev.sayed.lint_rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement
import org.jetbrains.uast.getContainingUFile


class ForbiddenMaterial3ComponentsDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitImportStatement(node: UImportStatement) {
                val importReference = node.importReference ?: return
                val importedFqName = importReference.asSourceString().trim()

                if (importedFqName.startsWith("androidx.compose.material3.")) {
                    val componentName = importedFqName.substringAfterLast('.')

                    if (componentName == "*") {
                        reportForbidden(context, importReference, "wildcard import")
                        return
                    }

                    if (componentName in FORBIDDEN_COMPONENTS) {
                        val uFile = node.getContainingUFile() ?: return
                        val packageName = uFile.packageName.orEmpty()

                        if (packageName.startsWith("dev.sayed.mehrabalmomen.design_system")) {
                            return
                        }

                        if (packageName.startsWith("dev.sayed.mehrabalmomen.presentation")) {
                            reportForbidden(context, importReference, componentName)
                        }
                    }
                }
            }
        }
    }

    private fun reportForbidden(context: JavaContext, locationElement: UElement, componentName: String) {
        context.report(
            ISSUE,
            context.getLocation(locationElement),
            "Direct use of Material3 $componentName is forbidden in screen package. Use the Design System component instead."
        )
    }

    companion object {
        private val FORBIDDEN_COMPONENTS = setOf(
            "Button",
            "Text",
            "OutlinedButton",
            "FilledTonalButton",
            "ElevatedButton",
            "TextButton"
        )

        val ISSUE = Issue.create(
            id = "ForbiddenMaterial3Components",
            briefDescription = "Forbidden Material3 components",
            explanation = """
                The following Material3 components are not allowed in the presentation layer:
                ${FORBIDDEN_COMPONENTS.joinToString(", ")}
                
                Use equivalent components from the Design System instead.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                ForbiddenMaterial3ComponentsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}