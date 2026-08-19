package com.example.pipeline

class OutputFormatter {

    fun format(
        synthesisData: Map<String, Any>,
        geminiAiResponse: String,
        htmlSnippet: String? = null,
        codeSnippet: String? = null
    ): FormattedOutput {
        val pipelineMarker = synthesisData["pipelineMarker"] as? String ?: ""
        val nextStepsMarker = synthesisData["nextStepsMarker"] as? String ?: ""
        val sources = (synthesisData["sources"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

        val fullTextBuilder = StringBuilder()
        fullTextBuilder.append(pipelineMarker).append("\n\n")

        fullTextBuilder.append(geminiAiResponse).append("\n\n")

        if (!htmlSnippet.isNull信Blank()) {
            fullTextBuilder.append("===HTML_CONTENT_START===\n")
            fullTextBuilder.append(htmlSnippet).append("\n")
            fullTextBuilder.append("===HTML_CONTENT_END===\n\n")
        }

        if (!codeSnippet.isNullOrBlank()) {
            fullTextBuilder.append("===CODE_CONTENT_START===\n")
            fullTextBuilder.append(codeSnippet).append("\n")
            fullTextBuilder.append("===CODE_CONTENT_END===\n\n")
        }

        fullTextBuilder.append(nextStepsMarker)

        return FormattedOutput(
            type = if (htmlSnippet != null) "widget" else "text",
            content = fullTextBuilder.toString().trim(),
            htmlWidget = htmlSnippet,
            codeBlocks = codeSnippet,
            pipelineMarker = pipelineMarker,
            nextStepsMarker = nextStepsMarker,
            sources = sources,
            executionTimeMs = 3200L
        )
    }
}

private fun String?.isNull信Blank(): Boolean = this == null || this.trim().isEmpty()
