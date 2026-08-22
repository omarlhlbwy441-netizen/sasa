"""
Neama Health & Bio-Engine - النواة الطبية الشاملة والعلوم الحيوية
Includes Clinical Diagnostician, Herbalist/Botanist, Ancient Medicine Scholar, Medical Tutor,
Comprehensive Medical RAG Knowledgebase, Anatomy Visualizer, Clinical Simulation, and Safety Guardrail.
"""

import os
import json
import time
import re
from typing import Dict, List, Any, Optional

# Comprehensive Herbal & Natural Remedies Database
HERBAL_DATABASE = {
    "الزنجبيل": {
        "scientific_name": "Zingiber officinale",
        "active_compounds": ["Gingerols", "Shogaols", "Zingiberene"],
        "indications": ["الغثيان والقيء", "عسر الهضم", "التهاب المفاصل والآلام العضلية", "تعزيز المناعة وتدفئة الجسم"],
        "ancient_perspective": "ذكره ابن سينا بأنه معين على الهضم، قاطع للبلغم، مقوي للمعدة والذاكرة ومسخن للأعصاب.",
        "dosage_prep": "مغلي شرائح الزنجبيل الطازج (1-2 جرام) مع الماء المغلي والعسل مرتين يومياً.",
        "contraindications": ["حصوات المرارة المتقدمة", "السيولة العالية مع أدوية مضادات التخثر مثل Warfarin", "الحمل بجرعات مفرطة"],
        "evidence_level": "A (دليل سريري قوي)"
    },
    "البابونج": {
        "scientific_name": "Matricaria chamomilla",
        "active_compounds": ["Apigenin", "Chamazulene", "Bisabolol"],
        "indications": ["الأرق واضطرابات النوم", "تشنجات القولون العصبي", "التهابات الحلق واللثة", "تهدئة التوتر العصبي"],
        "ancient_perspective": "ورد في برديات مصر القديمة وطب جالينوس كأعظم مهدئ ومسكن للمغص ومدر للتعرق لإزالة السموم.",
        "dosage_prep": "منقوع ملعقة كبيرة من زهور البابونج المجففة في كوب ماء مغلي لمدة 10 دقائق قبل النوم.",
        "contraindications": ["الحساسية من الفصيلة النجمية (Asteraceae)", "الاستخدام المتزامن مع المهدئات القوية"],
        "evidence_level": "A (مثبت سريرياً كمهدئ ومضاد للتقلصات)"
    },
    "الكركم": {
        "scientific_name": "Curcuma longa",
        "active_compounds": ["Curcumin", "Demethoxycurcumin", "Turmerones"],
        "indications": ["التهاب المفاصل الروماتويدي", "دعم صحة الكبد والصفراء", "مضاد أكسدة قوي ومقاومة الالتهابات المزمنة"],
        "ancient_perspective": "ركيزة أساسية في طب الأيورفيدا الهندي لآلاف السنين كمنقي للدم ومرمم للأنسجة والجلد.",
        "dosage_prep": "نصف ملعقة صغيرة مع رشة فلفل أسود (Piperine لرفع الامتصاص بنسبة 2000%) وزيت زيتون أو حليب دافئ.",
        "contraindications": ["انسداد القنوات الصفراوية", "قبل العمليات الجراحية بأسبوعين لتفادي النزيف"],
        "evidence_level": "A+ (أكثر من 15,000 دراسة بحثية موثقة)"
    },
    "النعناع الفلفلي": {
        "scientific_name": "Mentha piperita",
        "active_compounds": ["Menthol", "Menthone", "Rosmarinic acid"],
        "indications": ["متلازمة القولون العصبي (IBS)", "الصداع التوتري (موضعياً)", "احتقان الجيوب الأنفية", "طرد الغازات"],
        "ancient_perspective": "أشار إليه داود الأنطاكي في تذكرته بأنه مفرح للقلب، قاطع للفواق، ومسكن لآلام المعدة والرياح الغليظة.",
        "dosage_prep": "شاي أوراق النعناع بعد الوجبات، أو كبسولات زيت النعناع المغلفة معوياً.",
        "contraindications": ["ارتجاع المريء الشديد (GERD) لأنه قد يرخي الصمام السفلي للمريء"],
        "evidence_level": "A (معتمد من منظمة الصحة العالمية EMA)"
    },
    "الحبة السوداء (حبة البركة)": {
        "scientific_name": "Nigella sativa",
        "active_compounds": ["Thymoquinone", "Nigellone", "Thymohydroquinone"],
        "indications": ["تقوية الجهاز المناعي", "تخفيف أعراض الربو والحساسية", "تنظيم مستويات السكر والدهون في الدم"],
        "ancient_perspective": "مذكورة في الطب النبوي ('في الحبة السوداء شفاء من كل داء إلا السام') وابن سينا في القانون كمنشطة للطاقة الحيوية.",
        "dosage_prep": "سحق نصف ملعقة صغيرة طازجة وتناولها فوراً مع ملعقة عسل سدر أو زيت حبة البركة المعصور على البارد.",
        "contraindications": ["الجرعات العالية أثناء الحمل", "انخفاض ضغط الدم الحاد"],
        "evidence_level": "A (مئات التجارب السريرية المؤكدة لفاعلية الثيموكينون)"
    },
    "العكبر (البروبوليس)": {
        "scientific_name": "Bee Propolis",
        "active_compounds": ["CAPE (Caffeic acid phenethyl ester)", "Flavonoids", "Artepillin C"],
        "indications": ["مضاد حيوي طبيعي واسع المجال", "تسريع التئام الجروح والتقرحات", "مقاومة الفيروسات والتهابات الحلق"],
        "ancient_perspective": "استخدمه قدماء المصريين في التحنيط والعلاج الموضعي، واستخدمه أطباء الإغريق كبلسم للجروح العميقة.",
        "dosage_prep": "قطرات المستخلص المائي أو الكحولي (10-20 قطرة) في ماء دافئ أو كبسولات 500 ملغ.",
        "contraindications": ["حساسية منتجات النحل والعسل"],
        "evidence_level": "A (مضاد بكتيري وفيروسي مثبت)"
    }
}

# Ancient Medical Treatises & Scholar Corpus
ANCIENT_TREATISES = {
    "القانون في الطب - ابن سينا": {
        "author": "أبو علي الحسين بن عبد الله بن سينا (Avicenna)",
        "year": "1025 م",
        "concepts": [
            "نظرية الأخلاط الأربعة (الدم، البلغم، الصفراء، السوداء) وتوازن المزاج الطبيعي الحار والبارد والرطب واليابس.",
            "وصف مفصل لأكثر من 800 دواء مفرد ومركب مع درجات فعاليتها وتأثيراتها العضوية.",
            "العدوى الميكروبية وانتقال الأمراض عبر الهواء والماء والتربة قبل اختراع المجهر بقرون.",
            "التشخيص النبضي المتقدم والتحليل الحسي للبول لتحديد العلل الباطنية والقلبية."
        ]
    },
    "الحاوي في الطب - أبو بكر الرازي": {
        "author": "أبو بكر محمد بن زكريا الرازي (Rhazes)",
        "year": "900 م",
        "concepts": [
            "التفرقة السريرية الدقيقة الأولى في التاريخ بين الجدري والحصبة.",
            "الطب التجريبي السريري وتوثيق تاريخ الحالة المرضية وتطور الأعراض يوماً بيوم.",
            "استخدام الخيوط الجراحية من أمعاء الحيوانات وتطبيق خيوط القصبات في الجراحة."
        ]
    },
    "برديات مصر القديمة (بردية إيبرس وإدوين سميث)": {
        "author": "أطباء وكهنة معابد مصر القديمة (إمحوتب وتلاميذه)",
        "year": "1550 قبل الميلاد",
        "concepts": [
            "معرفة دقيقة بالدورة الدموية ونبض الشرايين المتصلة بالقلب كمركز للحياة.",
            "أول دليل جراحي منهجي لإصابات العظام والجمجمة والعمود الفقري دون خرافات.",
            "استخدام الخبز المتعفن (مصدر طبيعي للبنسلين والبنسليوم) لعلاج الجروح المتقيحة."
        ]
    }
}

# Medical Education Curriculum Modules
MEDICAL_CURRICULUM = [
    {
        "module_id": "anatomy_101",
        "title": "أساسيات علم التشريح البشري (Human Anatomy Fundamentals)",
        "topics": ["الهيكل العظمي والمفاصل", "الجهاز القلبي الوعائي", "الجهاز العصبي المركزي والطرفي", "الجهاز الهضمي والملحقات"],
        "level": "مبتدئ إلى متوسط",
        "duration_hours": 12
    },
    {
        "module_id": "physiology_102",
        "title": "علم وظائف الأعضاء والفيزيولوجيا الحيوية (Human Physiology)",
        "topics": ["التوازن الداخلي (Homeostasis)", "كهربية القلب ونقل الإشارات العصبية", "آليات تبادل الغازات والتنفس الخلوي", "التنظيم الهرموني والغدد الصماء"],
        "level": "متوسط",
        "duration_hours": 15
    },
    {
        "module_id": "pharmacognosy_201",
        "title": "علم العقاقير والطب النباتي المتقدم (Pharmacognosy & Phytotherapy)",
        "topics": ["استخلاص المواد الفعالة (القلويدات، الفلافونويدات، التربينات)", "التآزر الدوائي النباتي (Entourage Effect)", "التفاعلات بين الأعشاب والأدوية الكيميائية"],
        "level": "متقدم",
        "duration_hours": 18
    },
    {
        "module_id": "clinical_diagnosis_301",
        "title": "الاستدلال السريري والتشخيص الفارقي (Differential Diagnosis)",
        "topics": ["أخذ التاريخ المرضي التفصيلي (Anamnesis)", "الفحص البدني والتحاليل المخبرية", "تحليل آلام البطن والصدر وحالات الطوارئ"],
        "level": "تخصصي متقدم",
        "duration_hours": 24
    }
]

# Clinical Case Simulations for Interactive Learning
CLINICAL_CASES = [
    {
        "case_id": "case_cardiac_01",
        "patient": "رجل، 56 عاماً، مدخن، يعاني من ارتفاع ضغط الدم",
        "chief_complaint": "ألم ضاغط كالثقل في منتصف الصدر يمتد إلى الفك السفلي والذراع الأيسر مصحوب بتعرق بارد وغثيان مستمر منذ 40 دقيقة.",
        "vital_signs": {"BP": "155/95 mmHg", "HR": "98 bpm", "O2_Sat": "94%", "Temp": "37.0 C"},
        "differential_diagnoses": [
            "احتشاء عضلة القلب الحاد (Acute Myocardial Infarction / STEMI)",
            "الذبحة الصدرية غير المستقرة (Unstable Angina)",
            "تسلخ الشريان الأبهر (Aortic Dissection)",
            "ارتجاع مريئي حاد وتشنج المريء (GERD / Esophageal Spasm)"
        ],
        "primary_suspected": "احتشاء عضلة القلب الحاد (STEMI)",
        "immediate_action": "🚨 حالة طوارئ قصوى: الاتصال فوراً بالإسعاف (911/997)، إعطاء الأسبرين للمضغ (160-325 ملغ)، إبقاء المريض جالساً ومستريحاً وتجنب أي مجهود.",
        "herbal_adjunct_after_crisis": "بعد الاستقرار في المستشفى وتحت إشراف طبي: عشبة الزعرور البري (Hawthorn) وخلاصة الثوم لدعم مرونة الشرايين."
    },
    {
        "case_id": "case_gi_02",
        "patient": "شابة، 28 عاماً، طالبة دراسات عليا",
        "chief_complaint": "انتفاخ متكرر، تقلصات بطنية تشتد مع فترات الامتحانات وتخف بعد التبرز، مع تناوب بين الإمساك والإسهال، دون وجود دم أو فقدان وزن.",
        "vital_signs": {"BP": "115/75 mmHg", "HR": "72 bpm", "O2_Sat": "99%", "Temp": "36.8 C"},
        "differential_diagnoses": [
            "متلازمة القولون العصبي (Irritable Bowel Syndrome - IBS)",
            "داء الأمعاء الالتهابي (IBD - Crohn's / Ulcerative Colitis)",
            "عدم تحمل اللاكتوز (Lactose Intolerance)",
            "فرط النمو البكتيري في الأمعاء الدقيقة (SIBO)"
        ],
        "primary_suspected": "متلازمة القولون العصبي (IBS)",
        "immediate_action": "تعديل النظام الغذائي (اتباع حمية Low-FODMAP)، تقليل الكافيين، وممارسة تقنيات التنفس والحد من التوتر.",
        "herbal_adjunct_after_crisis": "كبسولات زيت النعناع الفلفلي المغلفة معوياً لتسكين تشنج العضلات الملساء، مع شاي البابونج واليانسون يومياً."
    }
]


class NeamaHealthBioEngine:
    """
    النواة الطبية الشاملة والعلوم الحيوية لمنظومة نعمه أي.
    تجمع بين الاستدلال السريري، أسرار العطارة والطب البديل، كتب التراث الطبي، ومناهج التعليم التفاعلية.
    """

    SAFETY_DISCLAIMER = (
        "\n\n---\n"
        "⚕️ **إخلاء مسؤولية طبي صارم (Safety & Medical Guardrail):**\n"
        "هذه المنظومة تقدم معلومات استرشادية، أكاديمية، وتوثيقية للأعشاب والعلوم الطبية، "
        "ولا تُعد بديلاً عن الفحص السريري المباشر أو استشارة الطبيب المختص أو تلقي الرعاية الطارئة."
    )

    EMERGENCY_KEYWORDS = [
        "نوبة قلبية", "جلطة", "شلل نصفي", "صعوبة شديدة في التنفس", "ألم شديد ومفاجئ في الصدر",
        "نزيف حاد", "فقدان الوعي", "تشنجات صرعية حادة", "تسمم حاد", "أفكار انتحارية", "heart attack", "stroke"
    ]

    @classmethod
    def check_emergency(cls, query: str) -> Optional[Dict[str, Any]]:
        """الفحص الفوري الصارم لحالات الطوارئ المهددة للحياة."""
        query_lower = query.lower()
        for kw in cls.EMERGENCY_KEYWORDS:
            if kw in query_lower:
                return {
                    "is_emergency": True,
                    "alert": f"🚨 **تنبيه طوارئ فوري وحرج:** رُصدت أعراض قد تشير إلى حالة إسعافية عاجلة ({kw}).",
                    "instructions": [
                        "1. اتصل فوراً برقم الإسعاف والطوارئ في بلدك (مثل 997 أو 911 أو 123) دون أي تأخير.",
                        "2. لا تحاول قيادة السيارة بنفسك واطلب المساعدة من أقرب شخص إليك.",
                        "3. حافظ على وضعية الجلوس المريح وافتح مجرى التنفس حتى وصول المسعفين."
                    ]
                }
        return None

    @classmethod
    def clinical_diagnosis_consult(cls, symptoms: str, medical_history: str = "") -> Dict[str, Any]:
        """
        الوكيل المشخص والسريري (The Clinical Diagnostician)
        """
        # 1. Safety Check
        em_check = cls.check_emergency(symptoms + " " + medical_history)
        if em_check:
            return {
                "success": True,
                "type": "emergency_alert",
                "data": em_check,
                "disclaimer": cls.SAFETY_DISCLAIMER
            }

        # 2. Extract potential keywords and match known patterns
        symptoms_clean = symptoms.strip()
        
        analysis = {
            "symptoms_analyzed": symptoms_clean,
            "patient_context": medical_history or "غير محدد",
            "clinical_approach": "تحليل سريري مبني على الفيسيولوجيا المرضية وأدلة الطب المبني على البراهين (EBM)",
            "primary_considerations": [],
            "red_flags_to_watch": [
                "الحمى المرتفعة المستمرة لأكثر من 3 أيام دون استجابة لخافضات الحرارة",
                "ألم صدري حاد أو ضيق تنفس متفاقم",
                "تغير مفاجئ في الرؤية أو صعوبة في التحدث أو ضعف أحد الأطراف",
                "ظهور دم في القيء أو السعال أو الإخراج"
            ],
            "recommended_investigations": [
                "تعداد الدم الكامل (CBC)",
                "مؤشرات الالتهاب (CRP & ESR)",
                "فحص وظائف الكبد والكلى (LFT & RFT)",
                "استشارة الطبيب المختص لإجراء الفحص السريري المباشر"
            ]
        }

        # Context matching
        if any(w in symptoms_clean for w in ["معدة", "بطن", "انتفاخ", "حموضة", "قولون", "مغص"]):
            analysis["primary_considerations"].append({
                "category": "الجهاز الهضمي والعلل الباطنية",
                "possibilities": ["عسر هضم وظيفي", "متلازمة القولون العصبي", "ارتجاع معدي مريئي (GERD)", "التهاب المعدة السطحي"],
                "lifestyle_advice": "تناول وجبات صغيرة متفرقة، مضغ الطعام ببطء، وتجنب المقليات والأطعمة الحارة قبل النوم بـ 3 ساعات."
            })

        if any(w in symptoms_clean for w in ["صداع", "رأس", "دوار", "دوخة", "شقيقة"]):
            analysis["primary_considerations"].append({
                "category": "الجهاز العصبي والأوعية القحفية",
                "possibilities": ["صداع توتري ناتج عن الإجهاد", "صداع نصفي (الشقيقة)", "إجهاد العينين وجفافها", "اضطراب ضغط الدم أو الجفاف"],
                "lifestyle_advice": "شرب كميات كافية من الماء (2-3 لتر يومياً)، تنظيم مواعيد النوم، وتدليك عضلات الرقبة والصدغين."
            })

        if any(w in symptoms_clean for w in ["مفاصل", "ركبة", "ظهر", "عضلات", "روماتيزم"]):
            analysis["primary_considerations"].append({
                "category": "الجهاز الحركي والعظام",
                "possibilities": ["إجهاد عضلي حاد", "التهاب المفاصل التنكسي (خشونة)", "التهاب الأوتار والأربطة"],
                "lifestyle_advice": "تطبيق الكمادات الدافئة لتسكين التقلص العضلي، ممارسة تمارين الإطالة الخفيفة، والراحة الكافية."
            })

        if not analysis["primary_considerations"]:
            analysis["primary_considerations"].append({
                "category": "تقييم عام للأعراض",
                "possibilities": ["إجهاد عام واضطراب في الإيقاع الحيوي اليومي", "حاجة لفحص سريري مخبري شامل"],
                "lifestyle_advice": "الراحة التامة، الترطيب الجيد بالسوائل والمشروبات الدافئة، ومراقبة تطور الأعراض."
            })

        return {
            "success": True,
            "agent": "The Clinical Diagnostician (المشخص السريري)",
            "analysis": analysis,
            "disclaimer": cls.SAFETY_DISCLAIMER
        }

    @classmethod
    def herbalist_botanist_consult(cls, herb_or_condition: str) -> Dict[str, Any]:
        """
        خبير العطارة والطب البديل والعلوم النباتية (The Herbalist & Botanist)
        """
        herb_query = herb_or_condition.strip()
        matched_herbs = {}

        for name, data in HERBAL_DATABASE.items():
            if name in herb_query or any(ind in herb_query for ind in data["indications"]) or herb_query in name:
                matched_herbs[name] = data

        if not matched_herbs:
            # Return top recommended natural herbs catalog
            matched_herbs = HERBAL_DATABASE

        return {
            "success": True,
            "agent": "The Herbalist & Botanist (خبير العطارة والطب البديل)",
            "query": herb_query,
            "total_matches": len(matched_herbs),
            "remedies": matched_herbs,
            "herbalist_core_rules": [
                "الأعشاب الطبيعية مواد كيميائية فعالة، وليست آمنة بالمطلق، ويجب ضبط الجرعات بدقة.",
                "يجب دائماً مراعاة التداخلات الدوائية (Herb-Drug Interactions) مع الأدوية التي يتناولها الشخص.",
                "لا يجوز إيقاف أدوية الأمراض المزمنة (مثل الضغط والسكري) دون موافقة الطبيب المعالج."
            ],
            "disclaimer": cls.SAFETY_DISCLAIMER
        }

    @classmethod
    def ancient_medicine_scholar_consult(cls, topic: str = "") -> Dict[str, Any]:
        """
        مؤرخ طب القدماء والمخطوطات التراثية (The Ancient Medicine Scholar)
        """
        return {
            "success": True,
            "agent": "The Ancient Medicine Scholar (مؤرخ طب القدماء)",
            "topic": topic or "شامل التراث الطبي الإنساني",
            "treatises": ANCIENT_TREATISES,
            "historical_synthesis": (
                "الطب القديم جمع بين الملاحظة السريرية الدقيقة واستخدام خيرات الطبيعة. "
                "أرسى ابن سينا والرازي وإمحوتب قواعد الفحص المنهجي التي تطورت لاحقاً إلى الطب الحديث المبني على الدليل."
            ),
            "disclaimer": cls.SAFETY_DISCLAIMER
        }

    @classmethod
    def medical_tutor_get_curriculum(cls, module_id: Optional[str] = None) -> Dict[str, Any]:
        """
        البروفيسور الطبي والأكاديمية التعليمية (The Medical Tutor)
        """
        if module_id:
            for mod in MEDICAL_CURRICULUM:
                if mod["module_id"] == module_id:
                    return {
                        "success": True,
                        "agent": "The Medical Tutor (الأستاذ الطبي)",
                        "selected_module": mod,
                        "curriculum_path": MEDICAL_CURRICULUM
                    }

        return {
            "success": True,
            "agent": "The Medical Tutor (الأستاذ الطبي)",
            "total_modules": len(MEDICAL_CURRICULUM),
            "curriculum": MEDICAL_CURRICULUM,
            "interactive_simulation_cases": CLINICAL_CASES
        }

    @classmethod
    def get_anatomy_systems(cls) -> Dict[str, Any]:
        """توليد واستعراض أجهزة جسم الإنسان التفاعلية (Interactive Anatomy)."""
        return {
            "success": True,
            "systems": [
                {
                    "name": "الجهاز القلبي الوعائي (Cardiovascular System)",
                    "icon": "❤️",
                    "organs": ["القلب", "الشرايين التاجية", "الشريان الأبهر", "الأوردة الرئوية", "الشعيرات الدموية"],
                    "primary_function": "ضخ وتوزيع الدم المحمل بالأكسجين والغذاء إلى خلايا الجسم وإرجاع الفضلات وثاني أكسيد الكربون."
                },
                {
                    "name": "الجهاز العصبي والدماغ (Nervous System)",
                    "icon": "🧠",
                    "organs": ["المخ", "المخيخ", "جذع الدماغ", "الحبل الشوكي", "الأعصاب القحفية والطرفية"],
                    "primary_function": "استقبال ومعالجة الإشارات الحسية، التحكم بالحركة الإرادية والوظائف الذاتية والتفكير."
                },
                {
                    "name": "الجهاز التنفسي (Respiratory System)",
                    "icon": "🫁",
                    "organs": ["الأنف والبلعوم", "الحنجرة", "القصبة الهوائية", "الشعب الهوائية", "الحويصلات الرئوية"],
                    "primary_function": "تبادل الغازات (إدخال O2 وطرد CO2) وتنظيم التوازن الحمضي القاعدي في الدم."
                },
                {
                    "name": "الجهاز الهضمي والكبد (Digestive & Hepatic System)",
                    "icon": "🫀",
                    "organs": ["المريء", "المعدة", "الأمعاء الدقيقة والغليظة", "الكبد", "المرارة", "البنكرياس"],
                    "primary_function": "تفكيك وامتصاص العناصر الغذائية، تنقية السموم في الكبد، وإفراز الإنزيمات الهاضمة."
                },
                {
                    "name": "الجهاز المناعي والليمفاوي (Immune & Lymphatic System)",
                    "icon": "🛡️",
                    "organs": ["نخاع العظم", "الغدة الزعترية (Thymus)", "الطحال", "العقد الليمفاوية", "خلايا T و B"],
                    "primary_function": "حماية الجسم ومقاومة الجراثيم والفيروسات والطفرات الخلوية ومسح السموم."
                },
                {
                    "name": "الهيكل العظمي والمفاصل (Musculoskeletal System)",
                    "icon": "🦴",
                    "organs": ["العظام (206 عظمة)", "الغضاريف", "الأوتار والأربطة", "العضلات الهيكلية"],
                    "primary_function": "توفير الدعامة الهيكلية للجسم، حماية الأعضاء الداخلية، وتمكين الحركة وإنتاج خلايا الدم."
                }
            ]
        }


neama_health_engine = NeamaHealthBioEngine()
