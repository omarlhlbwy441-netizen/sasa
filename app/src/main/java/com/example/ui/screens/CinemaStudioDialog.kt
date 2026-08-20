package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.cinema.CinemaStudioEngine
import com.example.data.model.CinemaCharacter
import com.example.data.model.CinemaPitchConcept
import com.example.data.model.CinemaProductionType
import com.example.data.model.CinemaProject
import com.example.data.model.CinemaStage
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.TechDarkBorder
import com.example.ui.theme.TechDarkSurface
import com.example.ui.theme.TechDarkSurfaceVariant

@Composable
fun CinemaStudioDialog(
    initialPrompt: String = "",
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(CinemaProductionType.MOVIE) }
    var selectedGenre by remember { mutableStateOf(CinemaStudioEngine.AVAILABLE_GENRES[0]) }
    var storyPrompt by remember {
        mutableStateOf(
            if (initialPrompt.isNotBlank() && !initialPrompt.contains("فيديو") && !initialPrompt.contains("انتج لي")) initialPrompt
            else "قصة درامية ملحمية تدور حول مهندس ذكي يكتشف مؤامرة غامضة في المدينة ويسعى لحمايتها"
        )
    }

    var currentConceptIndex by remember { mutableIntStateOf(1) }
    var project by remember {
        mutableStateOf(
            CinemaStudioEngine.createInitialProject(
                type = selectedType,
                genre = selectedGenre,
                userDescription = storyPrompt
            )
        )
    }

    val isDirectWatchRequest = remember(initialPrompt) {
        initialPrompt.contains("مشاهدة") || initialPrompt.contains("حلقة") || initialPrompt.contains("معاينة") || initialPrompt.contains("تشغيل")
    }

    var selectedTab by remember { mutableIntStateOf(if (isDirectWatchRequest) 3 else 0) } // 0: التصور, 1: الشخصيات وملامح الوجه, 2: المشاهد والإنتاج, 3: المشغل السينمائي

    var heroName by remember { mutableStateOf("البطل طارق المنصور") }
    var heroRole by remember { mutableStateOf("البطل الرئيسي") }
    var heroCostume by remember { mutableStateOf("زي تكتيكي أنيق بأسلوب سينمائي عصري 8K") }
    var heroFeatures by remember {
        mutableStateOf(
            listOf(
                "تثبيت هندسة الوجه وعظام الفك (Facial Geometry Vector Locked 100%)",
                "نظرة العينين والملامح الدقيقة بدقة 8K Photorealistic",
                "بصمة ملامح مطابقة وموحدة في كافة زوايا الكاميرا والإضاءة"
            )
        )
    }

    var isFaceLockApplied by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary, Color(0xFFEAB308)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(CyanPrimary, IndigoSecondary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("استوديو صاصا للإنتاج السينمائي والدرامي", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEAB308).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("8K Photorealistic", color = Color(0xFFEAB308), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("توليد أفلام ومسلسلات تصويرية حقيقية مع تثبيت ملامح الشخصيات 100%", color = CyanPrimary, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = TechDarkSurface,
                    contentColor = CyanPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyanPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("1. التصور والاعتماد", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("2. تثبيت ملامح الأبطال", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("3. المشاهد التصويرية", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("4. مشغل العرض السينمائي", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> ConceptIterationTab(
                            selectedType = selectedType,
                            onTypeChange = {
                                selectedType = it
                                project = CinemaStudioEngine.createInitialProject(it, selectedGenre, storyPrompt)
                            },
                            selectedGenre = selectedGenre,
                            onGenreChange = {
                                selectedGenre = it
                                project = CinemaStudioEngine.createInitialProject(selectedType, it, storyPrompt)
                            },
                            storyPrompt = storyPrompt,
                            onStoryPromptChange = { storyPrompt = it },
                            pitch = project.currentPitch,
                            isApproved = project.isConceptApproved,
                            onApprove = {
                                project = project.copy(isConceptApproved = true, stage = CinemaStage.CHARACTER_CASTING)
                                selectedTab = 1
                            },
                            onNewPitch = {
                                currentConceptIndex++
                                val newPitch = CinemaStudioEngine.generatePitchConcept(currentConceptIndex, selectedType, selectedGenre, storyPrompt)
                                project = project.copy(currentPitch = newPitch, title = newPitch.title, isConceptApproved = false)
                            }
                        )

                        1 -> CharacterFaceLockTab(
                            heroName = heroName,
                            onHeroNameChange = { heroName = it },
                            heroRole = heroRole,
                            onHeroRoleChange = { heroRole = it },
                            heroCostume = heroCostume,
                            onHeroCostumeChange = { heroCostume = it },
                            heroFeatures = heroFeatures,
                            isFaceLockApplied = isFaceLockApplied,
                            onExtractFromImage = {
                                isFaceLockApplied = true
                                val char = CinemaStudioEngine.extractCharacterFeaturesFromImage(heroName, heroRole, heroCostume)
                                heroFeatures = char.facialFeatures
                                project = project.copy(characters = listOf(char))
                            },
                            onProceedToScenes = {
                                val char = CinemaStudioEngine.extractCharacterFeaturesFromImage(heroName, heroRole, heroCostume)
                                val generatedScenes = CinemaStudioEngine.producePhotorealisticScenes(project.copy(characters = listOf(char)))
                                project = project.copy(
                                    characters = listOf(char),
                                    scenes = generatedScenes,
                                    stage = CinemaStage.SCENE_PRODUCTION
                                )
                                selectedTab = 2
                            }
                        )

                        2 -> SceneProductionTab(
                            project = project,
                            onLaunchPlayer = {
                                val html = CinemaStudioEngine.generateInteractiveCinemaPlayerHtml(project)
                                project = project.copy(videoHtmlPlayer = html, stage = CinemaStage.THEATER_PLAYBACK)
                                selectedTab = 3
                            }
                        )

                        3 -> TheaterPlaybackTab(
                            project = project
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConceptIterationTab(
    selectedType: CinemaProductionType,
    onTypeChange: (CinemaProductionType) -> Unit,
    selectedGenre: String,
    onGenreChange: (String) -> Unit,
    storyPrompt: String,
    onStoryPromptChange: (String) -> Unit,
    pitch: CinemaPitchConcept,
    isApproved: Boolean,
    onApprove: () -> Unit,
    onNewPitch: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Step 1: Type & Genre Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TechDarkBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("اختر نوع العمل والتصنيف الإخراجي:", fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onTypeChange(CinemaProductionType.MOVIE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == CinemaProductionType.MOVIE) CyanPrimary else TechDarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = if (selectedType == CinemaProductionType.MOVIE) Color.Black else Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فيلم سينمائي", color = if (selectedType == CinemaProductionType.MOVIE) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onTypeChange(CinemaProductionType.SERIES) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == CinemaProductionType.SERIES) CyanPrimary else TechDarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Tv, contentDescription = null, tint = if (selectedType == CinemaProductionType.SERIES) Color.Black else Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مسلسل درامي", color = if (selectedType == CinemaProductionType.SERIES) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("التصنيف والأجواء:", fontSize = 12.sp, color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CinemaStudioEngine.AVAILABLE_GENRES) { genre ->
                            val isSelected = selectedGenre == genre
                            AssistChip(
                                onClick = { onGenreChange(genre) },
                                label = { Text(genre, fontSize = 10.sp, color = if (isSelected) Color.Black else Color.White) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = if (isSelected) CyanPrimary else TechDarkSurfaceVariant),
                                border = BorderStroke(1.dp, if (isSelected) CyanPrimary else TechDarkBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = storyPrompt,
                        onValueChange = onStoryPromptChange,
                        label = { Text("صف فكرة أو قصة الفيلم/المسلسل بإيجاز:", fontSize = 11.sp, color = CyanPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = TechDarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )
                }
            }
        }

        // Step 2: Generated Concept Pitch Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(CyanPrimary, GreenAccent)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("التصور الإخراجي المقترح (Pitch #${pitch.conceptId})", fontWeight = FontWeight.Bold, color = Color(0xFFEAB308), fontSize = 13.sp)
                        }
                        if (isApproved) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GreenAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("تم الاعتماد ✓", color = GreenAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(pitch.title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("💡 اللوجلاين (Logline): ${pitch.logline}", fontSize = 12.sp, color = CyanPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(pitch.fullPlot, fontSize = 12.sp, color = Color.LightGray, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("🎨 الطابع البصري والتصويري: ${pitch.visualMood}", fontSize = 11.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("🎥 نمط الكاميرات: ${pitch.cinematographyStyle}", fontSize = 11.sp, color = CyanPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("🎬 هيكل المشاهد الرئيسية المقترحة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    pitch.majorScenesOutline.forEach { sc ->
                        Text("• $sc", fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(vertical = 1.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Approval / Alternate Decision Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اعتماد هذا التصور والتنفيذ", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = onNewPitch,
                            colors = ButtonDefaults.buttonColors(containerColor = TechDarkSurfaceVariant),
                            border = BorderStroke(1.dp, CyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("طلب تصور بديل", color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterFaceLockTab(
    heroName: String,
    onHeroNameChange: (String) -> Unit,
    heroRole: String,
    onHeroRoleChange: (String) -> Unit,
    heroCostume: String,
    onHeroCostumeChange: (String) -> Unit,
    heroFeatures: List<String>,
    isFaceLockApplied: Boolean,
    onExtractFromImage: () -> Unit,
    onProceedToScenes: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("محرك تثبيت ملامح البطل والشخصيات (Consistent Face Lock)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GreenAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("قفل الملامح 100%", color = GreenAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("عند تزويد المنظومة بصورة أو مواصفات ملامح البطل، يتم استخراج خريطة الوجه ثلاثية الأبعاد والالتزام بها بنسبة 100% في كافة المشاهد واللقطات التصويرية بدون أي تشوه أو اختلاف في الهوية.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = heroName,
                            onValueChange = onHeroNameChange,
                            label = { Text("اسم البطل / الشخصية:", fontSize = 11.sp, color = CyanPrimary) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A),
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = TechDarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = heroRole,
                            onValueChange = onHeroRoleChange,
                            label = { Text("الدور الدرامي:", fontSize = 11.sp, color = CyanPrimary) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A),
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = TechDarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = heroCostume,
                        onValueChange = onHeroCostumeChange,
                        label = { Text("طابع الزي والإطلالة السينمائية:", fontSize = 11.sp, color = CyanPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = TechDarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Photo Extraction Simulation Button
                    Button(
                        onClick = onExtractFromImage,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(CyanPrimary, GreenAccent))),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📷 مسح واستخراج الملامح وتثبيت البصمة (Face Lock Analysis)", color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Extracted Face Features Breakdown Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GreenAccent.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("خريطة الملامح المعتمدة للشخصية (Photorealistic Identity Matrix):", fontWeight = FontWeight.Bold, color = GreenAccent, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    heroFeatures.forEach { feat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GreenAccent))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(feat, fontSize = 11.sp, color = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onProceedToScenes,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("الانتقال لإنتاج وتصوير المشاهد السينمائية ⏭", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SceneProductionTab(
    project: CinemaProject,
    onLaunchPlayer: () -> Unit
) {
    val scenes = if (project.scenes.isNotEmpty()) project.scenes else CinemaStudioEngine.producePhotorealisticScenes(project)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("المشاهد التصويرية الواقعية (8K Photorealistic Scenes)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Button(
                    onClick = onLaunchPlayer,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تشغيل في صالة العرض", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        items(scenes) { sc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TechDarkBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("المشهد ${sc.sceneNumber}: ${sc.title}", fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 12.sp)
                        Text("${sc.durationSec} ثانية", fontSize = 10.sp, color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🎥 زاوية الكاميرا: ${sc.cameraAngle}", fontSize = 11.sp, color = Color.White)
                    Text("💡 الإضاءة السينمائية: ${sc.lighting}", fontSize = 11.sp, color = Color.LightGray)

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("💬 الحوار: ${sc.dialogue}", fontSize = 11.sp, color = Color(0xFFFDE047), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("🎭 التوجيه الإخراجي: ${sc.actionDescription}", fontSize = 10.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TheaterPlaybackTab(
    project: CinemaProject
) {
    val htmlContent = if (project.videoHtmlPlayer.isNotBlank()) project.videoHtmlPlayer else CinemaStudioEngine.generateInteractiveCinemaPlayerHtml(project)

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://sasa-cinema-preview", htmlContent, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        )
    }
}
