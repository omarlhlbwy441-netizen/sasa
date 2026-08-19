package com.example.pipeline

import com.example.data.local.AgentLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InteractiveExecutionPipeline {

    val contextParser = ContextParser()
    val intentClassifier = IntentClassifier()
    val taskPlanner = TaskPlanner()
    val toolExecutor = ToolExecutor()
    val responseSynthesizer = ResponseSynthesizer()
    val outputFormatter = OutputFormatter()
    val codeEngine = NeamaCodeEngine()
    val swarmEngine = NeamaSwarmEngine()

    fun runPipelineStreaming(
        userMessage: String,
        logsHistory: List<AgentLogEntity>,
        userMemory: Map<String, String>,
        geminiResponseProvider: suspend (ClassificationResult, PipelineContext) -> Pair<String, Pair<String?, String?>>
    ): Flow<Pair<PipelineProgressUpdate, FormattedOutput?>> = flow {
        // 1. Context Parsing
        val context = contextParser.parse(userMessage, logsHistory, userMemory)

        // 2. Intent Classification
        val classification = intentClassifier.classify(userMessage, context)

        // 3. Pre-reasoning / Planning
        val plan = taskPlanner.createPlan(userMessage, classification, context)

        // 4. Interactive Execution (Streaming through 6 stages)
        var finalResults = emptyList<StepExecutionResult>()
        toolExecutor.executePlanStreaming(plan, context).collect { (update, currentResults) ->
            finalResults = currentResults

            if (!update.isCompleted) {
                // Emit progress update without final formatted output yet
                emit(update to null)
            } else {
                // Stage 5 & 6: Synthesis & Output Formatting
                val synthesisData = responseSynthesizer.synthesize(
                    userMessage = userMessage,
                    executionLog = finalResults,
                    context = context,
                    classification = classification
                )

                // Get AI & code responses
                val (aiText, snippets) = geminiResponseProvider(classification, context)
                val (htmlSnippet, codeSnippet) = snippets

                val finalOutput = outputFormatter.format(
                    synthesisData = synthesisData,
                    geminiAiResponse = aiText,
                    htmlSnippet = htmlSnippet,
                    codeSnippet = codeSnippet
                )

                emit(update to finalOutput)
            }
        }
    }
}
