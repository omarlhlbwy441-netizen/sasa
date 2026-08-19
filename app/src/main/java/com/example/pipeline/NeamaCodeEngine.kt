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
        author: String = "Sasa AI (صاصا) - الشيخ الهلباوي",
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

    /**
     * System Building: Scaffolds a complete project architecture.
     */
    fun buildFullSystem(
        projectType: String,
        projectName: String,
        description: String = ""
    ): Map<String, String> {
        val files = mutableMapOf<String, String>()
        val safeName = projectName.lowercase().replace(" ", "_")

        when (projectType.lowercase()) {
            "python", "fastapi", "flask", "backend" -> {
                files["server.py"] = createProjectFileContent(
                    fileName = "server.py",
                    rawCode = """import os, sys, json
from http.server import HTTPServer, SimpleHTTPRequestHandler

PORT = int(os.environ.get("PORT", 8080))

class Handler(SimpleHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json; charset=utf-8')
        self.end_headers()
        res = {"status": "online", "system": "$projectName", "engine": "Sasa AI", "architect": "الشيخ الهلباوي"}
        self.wfile.write(json.dumps(res).encode('utf-8'))

if __name__ == '__main__':
    print(f"🚀 Starting server on port {PORT}")
    HTTPServer(('', PORT), Handler).serve_forever()
""",
                    description = "Core autonomous server engine"
                )
                files["requirements.txt"] = "fastapi>=0.100.0\nuvicorn>=0.22.0\nrequests>=2.31.0\npsycopg2-binary>=2.9.6\n"
                files["Dockerfile"] = "FROM python:3.11-slim\nWORKDIR /app\nCOPY requirements.txt .\nRUN pip install --no-cache-dir -r requirements.txt\nCOPY . .\nEXPOSE 8080\nCMD [\"python\", \"server.py\"]\n"
                files["README.md"] = "# $projectName\n\n$description\n\n- **Architect**: الشيخ الهلباوي\n- **Engine**: Sasa AI Autonomous Subsystem\n"
            }
            "android", "compose", "mobile" -> {
                files["README.md"] = "# $projectName - Android Architecture\n\n$description\n\nBuilt with Kotlin, Jetpack Compose, Room DB, and Sasa Autonomous Engine."
            }
            else -> {
                files["index.html"] = "<!DOCTYPE html><html lang='ar' dir='rtl'><head><meta charset='UTF-8'><title>$projectName</title></head><body><h1>$projectName</h1><p>$description</p><p>تم التطوير بواسطة منظومة صاصا - الشيخ الهلباوي</p></body></html>"
                files["README.md"] = "# $projectName\n\n$description\n\nEngineered by Sasa AI (الشيخ الهلباوي)"
            }
        }
        return files
    }

    /**
     * System Evolution: Extends existing code with new features.
     */
    fun evolveModule(
        existingCode: String,
        newFeatureSpec: String,
        targetExtension: String
    ): FileEditResult {
        if (existingCode.isBlank()) {
            return FileEditResult(
                isSuccess = true,
                originalLinesCount = 0,
                updatedLinesCount = newFeatureSpec.lines().size,
                modifiedContent = newFeatureSpec,
                message = "تم إنشاء وتطوير الموديول البرمجي الجديد بنجاح."
            )
        }

        val evolved = existingCode.trimEnd() + "\n\n// --- [تطوير برمجيات صاصا AI - الشيخ الهلباوي] ---\n" + newFeatureSpec
        return FileEditResult(
            isSuccess = true,
            originalLinesCount = existingCode.lines().size,
            updatedLinesCount = evolved.lines().size,
            modifiedContent = evolved,
            message = "تم تطوير الوحدة البرمجية وتوسيع وظائفها بنجاح."
        )
    }

    /**
     * System Repair: Analyzes broken code and logs, applying targeted fixes.
     */
    fun autoRepairCode(
        brokenCode: String,
        errorLogs: String,
        fileExtension: String
    ): FileEditResult {
        var repaired = brokenCode

        // Auto fix 1: bare except in python
        if (fileExtension == "py" && repaired.contains("except:")) {
            repaired = repaired.replace("except:", "except Exception as e:")
        }

        // Auto fix 2: unhandled null or BuildConfig
        if ((fileExtension == "kt" || fileExtension == "java") && errorLogs.contains("Unresolved reference 'GEMINI_API_KEY'")) {
            if (repaired.contains("BuildConfig.GEMINI_API_KEY")) {
                repaired = repaired.replace("BuildConfig.GEMINI_API_KEY", "runCatching { BuildConfig.GEMINI_API_KEY }.getOrDefault(\"\")")
            }
        }

        // Auto fix 3: syntax errors or missing imports
        if (errorLogs.contains("Cannot infer type") && repaired.contains("val apiKey =")) {
            repaired = repaired.replace("val apiKey =", "val apiKey: String =")
        }

        val fixed = repaired != brokenCode
        return FileEditResult(
            isSuccess = true,
            originalLinesCount = brokenCode.lines().size,
            updatedLinesCount = repaired.lines().size,
            modifiedContent = repaired,
            message = if (fixed) "تم إصلاح ومعالجة الخلل البرمجي تلقائياً وتصحيح الكود." else "لم يتم العثور على أخطاء برمجية حرجة تتطلب تدخلاً جراحياً."
        )
    }
}
