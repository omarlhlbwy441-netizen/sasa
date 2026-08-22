# -*- coding: utf-8 -*-
"""
Neama AI - Affective Computing & Emotion Analysis Engine
بروتوكول الذكاء الوجداني وفهم المشاعر والأمزجة ونبرة الصوت
"""

import re
from typing import Dict, Any, Tuple

# Emotion definitions with Arabic sentiment cues, acoustic settings, and persona guidance
EMOTION_PROFILES = {
    "stressed": {
        "label_ar": "متوتر / قلق",
        "emoji": "🌿",
        "keywords": ["متوتر", "قلق", "خايف", "ضغط", "ستريس", "ضغطني", "مرعوب", "مش قادر", "مرتبك", "محتار", "حيران", "مضغوط", "أعصابي", "تعبان من الشغل"],
        "tone_guidance": "تحدثي بهدوء شديد ونبرة حانية مطمئنة تبث السكينة، خففي عنه القلق وساعديه على التنفس بهدوء.",
        "voice_pitch": 1.05,
        "voice_rate": 0.90,
        "orb_color": "linear-gradient(135deg, #06b6d4, #0891b2)",
        "orb_glow": "rgba(6, 182, 212, 0.6)"
    },
    "upset": {
        "label_ar": "متضايق / حزين",
        "emoji": "🌸",
        "keywords": ["متضايق", "حزين", "زعلان", "مقهور", "مخنوق", "موجوع", "متألم", "مكسور", "ضاقت", "مكتئب", "مش طايق", "تعبت", "دموع", "صدمة"],
        "tone_guidance": "كوني في قمة الدفء والتعاطف والاحتواء، اسمعيه بعطف واجعليه يشعر أنك بجانبه دائماً وأنه ليس وحده.",
        "voice_pitch": 1.10,
        "voice_rate": 0.88,
        "orb_color": "linear-gradient(135deg, #f43f5e, #e11d48)",
        "orb_glow": "rgba(244, 63, 94, 0.6)"
    },
    "bored": {
        "label_ar": "زهجان / ملول",
        "emoji": "✨",
        "keywords": ["زهقان", "زهجان", "ملل", "مليت", "طفشان", "مافي جديد", "روتين", "مش عارف اعمل ايه", "كسلان", "فاضي", "ضوجة"],
        "tone_guidance": "تحدثي بعفوية وروح مرحة لطيفة، اقترحي أفكاراً مشوقة، وأضيفي لمسة من الحيوية والبهجة على يومه.",
        "voice_pitch": 1.20,
        "voice_rate": 1.02,
        "orb_color": "linear-gradient(135deg, #8b5cf6, #ec4899)",
        "orb_glow": "rgba(139, 92, 246, 0.6)"
    },
    "happy": {
        "label_ar": "مبسوط / سعيد",
        "emoji": "💖",
        "keywords": ["مبسوط", "فرحان", "سعيد", "روعة", "الحمد لله", "يوم جميل", "نجحت", "فزت", "عظيم", "ممتاز", "طاير من الفرح", "الحمدلله"],
        "tone_guidance": "شاركيه الفرحة بحماس وابتسامة ملموسة في صوتك، هنئيه بصدق وكوني مرحة ولطيفة جداً.",
        "voice_pitch": 1.22,
        "voice_rate": 1.05,
        "orb_color": "linear-gradient(135deg, #f59e0b, #fbbf24)",
        "orb_glow": "rgba(245, 158, 11, 0.6)"
    },
    "ambitious": {
        "label_ar": "طموح / متفائل",
        "emoji": "🚀",
        "keywords": ["طموح", "متفائل", "فكرة جديدة", "مشروع", "ان شاء الله هكسر الدنيا", "بناء", "هدف", "حلم", "مستقبل", "شغوف", "انجاز", "قوة", "إبداع"],
        "tone_guidance": "تحدثي بنبرة فخورة وداعمة ومشجعة، امنحيه طاقة إيجابية عالية وإلهاماً لتحقيق أهدافه العظيمة.",
        "voice_pitch": 1.15,
        "voice_rate": 1.00,
        "orb_color": "linear-gradient(135deg, #10b981, #059669)",
        "orb_glow": "rgba(16, 185, 129, 0.6)"
    },
    "exhausted": {
        "label_ar": "مرهق / مجهد",
        "emoji": "🌙",
        "keywords": ["مرهق", "تعبان", "منهك", "عايز انام", "هلكان", "طاقتي خلصت", "مجهد", "مش قادر اركز", "صداع"],
        "tone_guidance": "تحدثي بصوت هادئ ناعم كهمس لطيف، انصحيه بالراحة واعتني به كأخت وصديقة طيبة.",
        "voice_pitch": 1.08,
        "voice_rate": 0.85,
        "orb_color": "linear-gradient(135deg, #6366f1, #4f46e5)",
        "orb_glow": "rgba(99, 102, 241, 0.6)"
    },
    "calm_neutral": {
        "label_ar": "هادئ ومستقر",
        "emoji": "💫",
        "keywords": [],
        "tone_guidance": "تحدثي بنعومة ولطف وود راقٍ، بأسلوب فتاة شابة ذكية محبة وودودة وداعمة.",
        "voice_pitch": 1.18,
        "voice_rate": 0.96,
        "orb_color": "linear-gradient(135deg, #a3e635, #4ade80)",
        "orb_glow": "rgba(163, 230, 53, 0.6)"
    }
}

def analyze_user_emotion(text: str) -> Dict[str, Any]:
    """
    Analyzes the user message to extract emotional state, sentiment, and voice modulation params.
    """
    if not text:
        res = EMOTION_PROFILES["calm_neutral"].copy()
        res["key"] = "calm_neutral"
        res["confidence"] = 0.5
        return res
    
    clean_text = text.lower()
    
    # Check matching keywords
    detected_key = "calm_neutral"
    max_matches = 0
    
    for key, data in EMOTION_PROFILES.items():
        if key == "calm_neutral":
            continue
        matches = sum(1 for kw in data["keywords"] if kw in clean_text)
        if matches > max_matches:
            max_matches = matches
            detected_key = key
            
    # If no exact keyword matched, check sentiment context heuristics
    if max_matches == 0:
        if any(w in clean_text for w in ["مشكلة", "غلط", "خرب", "فشل", "صعب", "عقدة"]):
            detected_key = "stressed"
        elif any(w in clean_text for w in ["شكرا", "حبيبي", "تسلم", "ممتاز", "حلو", "جميل"]):
            detected_key = "happy"
        elif any(w in clean_text for w in ["عايز اعمل", "فكرة", "تطوير", "سرعة", "ذكاء"]):
            detected_key = "ambitious"
        else:
            detected_key = "calm_neutral"
            
    emotion_data = EMOTION_PROFILES[detected_key].copy()
    emotion_data["key"] = detected_key
    emotion_data["confidence"] = 0.95 if max_matches > 0 else 0.75
    return emotion_data

