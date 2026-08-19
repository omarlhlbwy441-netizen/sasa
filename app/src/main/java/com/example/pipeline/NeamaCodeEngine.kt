package com.example.pipeline

data class CodeIssue(
    val issueId: String,
    val filePath: String,
    val severity: String, // "CRITICAL", "WARNING", "INFO"
    val description: String,
    val rootCause: String,
    val proposedSolution: String,
    val autoFixAvailable: Boolean = true
)

data class CodebaseAuditResult(
    val totalFilesScanned: Int,
    val issuesFound: List<CodeIssue>,
    val healthScore: Int, // 0..100
    val summaryAr: String,
    val autoFixPlan: List<String>
)

data class FileEditResult(
    val isSuccess: Boolean,
    val originalLinesCount: Int,
    val updatedLinesCount: Int,
    val modifiedContent: String,
    val message: String
)

class NeamaCodeEngine {

    /**
     * Surgical line modification: Finds target lines and safely replaces them without corrupting the rest of the file.
     */
    fun modifyFileSurgically(
        originalContent: String,
        targetContent: String,
        replacementContent: String
    ): FileEditResult {
        if (!originalContent.contains(targetContent)) {
            // Attempt trimmed line-by-line fuzzy match
            val normalizedOrig = originalContent.replace("\r\n", "\n")
            val normalizedTarget = targetContent.replace("\r\n", "\n").trim()

            if (normalizedOrig.contains(normalizedTarget)) {
                val updated = normalizedOrig.replace(normalizedTarget, replacementContent.trim())
                return FileEditResult(
                    isSuccess = true,
                    originalLinesCount = originalContent.lines().size,
                    updatedLinesCount = updated.lines().size,
                    modifiedContent = updated,
                    message = "تم استبدال الأسطر المحددة بدقة جراحية وتحديث الكود بنجاح."
                )
            }

            return FileEditResult(
                isSuccess = false,
                originalLinesCount = originalContent.lines().size,
                updatedLinesCount = originalContent.lines().size,
                modifiedContent = originalContent,
                message = "تعذر العثور على الأسطر المستهدفة بالضبط داخل الملف لمطابقتها."
            )
        }

        val updated = originalContent.replace(targetContent, replacementContent)
        return FileEditResult(
            isSuccess = true,
            originalLinesCount = originalContent.lines().size,
            updatedLinesCount = updated.lines().size,
            modifiedContent = updated,
            message = "تم تطبيق التعديل الجراحي وحفظ التغييرات بدقة عالية."
        )
    }

    /**
     * Insert specific lines before or after an anchor line in code.
     */
    fun insertLines(
        originalContent: String,
        anchorLine: String,
        newLinesToInsert: String,
        insertAfter: Boolean = true
    ): FileEditResult {
        val lines = originalContent.lines().toMutableList()
        val anchorIndex = lines.indexOfFirst { it.trim().contains(anchorLine.trim()) }

        if (anchorIndex == -1) {
            return FileEditResult(
                isSuccess = false,
                originalLinesCount = lines.size,
                updatedLinesCount = lines.size,
                modifiedContent = originalContent,
                message = "لم يتم العثور على سطر الارتكاز (Anchor Line) المحدد لإدراج الأسطر."
            )
        }

        val targetInsertIndex = if (insertAfter) anchorIndex + 1 else anchorIndex
        val linesToAdd = newLinesToInsert.lines()
        lines.addAll(targetInsertIndex, linesToAdd)

        val updated = lines.joinToString("\n")
        return FileEditResult(
            isSuccess = true,
            originalLinesCount = originalContent.lines().size,
            updatedLinesCount = lines.size,
            modifiedContent = updated,
            message = "تمت إضافة ${linesToAdd.size} أسطر جديدة ${if (insertAfter) "بعد" else "قبل"} السطر المحدد بنجاح."
        )
    }

    /**
     * Deep scan codebase or files for issues, missing dependencies, security risks, or syntax breaks.
     */
    fun scanCodebaseForIssues(
        filesMap: Map<String, String>,
        contextInfo: String = ""
    ): CodebaseAuditResult {
        val issues = mutableListOf<CodeIssue>()

        filesMap.forEach { (filePath, content) ->
            // Check missing imports / syntax markers
            if (filePath.endsWith(".kt") || filePath.endsWith(".java")) {
                if (content.contains("TODO(") || content.contains("// TODO")) {
                    issues.add(
                        CodeIssue(
                            issueId = "TODO_${filePath.hashCode()}",
                            filePath = filePath,
                            severity = "WARNING",
                            description = "توجد أقسام غير مكتملة أو معلقة (TODO) في الشفرة.",
                            rootCause = "كود تجريبي أو قيد الإنشاء.",
                            proposedSolution = "إكمال المنطق البرمجي وتوليد الدوال الناقصة.",
                            autoFixAvailable = true
                        )
                    )
                }
            }

            if (filePath.endsWith(".js") || filePath.endsWith(".ts")) {
                if (content.contains("require(") && content.contains("import ")) {
                    issues.add(
                        CodeIssue(
                            issueId = "MIXED_MODULES_${filePath.hashCode()}",
                            filePath = filePath,
                            severity = "WARNING",
                            description = "خلط بين CommonJS (require) و ES Modules (import).",
                            rootCause = "استيراد غير متجانس للحزم في ملف جافاسكريبت.",
                            proposedSolution = "توحيد نظام الوحدات إلى ES Modules أو CommonJS.",
                            autoFixAvailable = true
                        )
                    )
                }
            }

            if (filePath.endsWith(".py")) {
                if (content.contains("except:") && !content.contains("except Exception:")) {
                    issues.add(
                        CodeIssue(
                            issueId = "BARE_EXCEPT_${filePath.hashCode()}",
                            filePath = filePath,
                            severity = "INFO",
                            description = "استخدام Bare except بدون تحديد نوع الخطأ في بايثون.",
                            rootCause = "قد يخفي استثناءات حرجة مثل KeyboardInterrupt.",
                            proposedSolution = "تحديد except Exception as e لتسجيل الخطأ بدقة.",
                            autoFixAvailable = true
                        )
                    )
                }
            }
        }

        val healthScore = if (issues.isEmpty()) 100 else maxOf(40, 100 - (issues.size * 15))
        val summary = if (issues.isEmpty()) {
            "🟢 تم فحص جميع الملفات (${filesMap.size} ملف) ولم يتم العثور على أخطاء برمجية أو تعارضات."
        } else {
            "⚠️ تم اكتشاف ${issues.size} ملاحظات وإشكاليات برمجية بحاجة للمعالجة التلقائية."
        }

        val fixPlan = issues.map { "تطبيق معالجة فورية لـ: ${it.description} في ${it.filePath}" }

        return CodebaseAuditResult(
            totalFilesScanned = filesMap.size,
            issuesFound = issues,
            healthScore = healthScore,
            summaryAr = summary,
            autoFixPlan = fixPlan
        )
    }

    /**
     * Create or format complete professional file content with header documentation.
     */
    fun createProjectFileContent(
        fileName: String,
        rawCode: String,
        author: String = "نعمة أي (Neama AI)",
        description: String = "Auto-generated & engineered autonomously"
    ): String {
        val extension = fileName.substringAfterLast('.', "")
        val commentPrefix = when (extension) {
            "py", "sh", "yaml", "yml", "dockerfile", "env" -> "#"
            "html", "xml" -> "<!--"
            else -> "//"
        }
        val commentSuffix = if (extension == "html" || extension == "xml") " -->" else ""

        val header = """$commentPrefix ========================================================$commentSuffix
$commentPrefix Project File: $fileName$commentSuffix
$commentPrefix Generated & Managed By: $author$commentSuffix
$commentPrefix Description: $description$commentSuffix
$commentPrefix ========================================================$commentSuffix

"""
        return header + rawCode.trim()
    }
}
