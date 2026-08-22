# دليل التشغيل والصيانة الدورية (System Maintenance Guide)

## 🕒 المهام الدورية وجداول الصيانة

### 1. الفحص اليومي (Daily Operations)
- **مراقبة مقاييس SLA**: مراجعة `/api/analytics/latency_sla` للتأكد من بقاء زمن الاستجابة أقل من 100ms والامتثال > 99.9%.
- **فحص تنبيهات الموارد**: التحقق من عدم وجود حاويات تحت ضغط الذاكرة عبر `/api/alerts/cluster`.

### 2. الفحص الأسبوعي (Weekly Operations)
- **تدقيق التبعيات والمكتبات (SCA)**: تشغيل فحص الثغرات عبر `/api/security/dependencies_scan`.
- **اختبار تدريب النوايا**: مراجعة سجلات تصنيف النوايا وتحديث الأوزان عبر `/api/intelligence/tune_weights`.
- **محاكاة اختبار الاختراق**: إجراء اختبار أمان دوري عبر `/api/security/pentest`.

### 3. الفحص الشهري (Monthly Operations)
- **تمرين استعادة النسخ الاحتياطي (Disaster Recovery Drill)**: تنفيذ محاكاة استعادة في بيئة معزولة عبر `/api/backup/test_restore`.
- **تدوير المفاتيح والشهادات**: فحص عدم وجود مفاتيح مكشوفة في البيئة والتحقق من سياسات الوصول.

## 🛠️ أوامر الصيانة السريعة
```bash
# فحص سلامة واختبارات الوحدات
python3 neama_module/tests/run_tests.py

# تشغيل الخادم محلياً
python3 app/server.py

# فحص المقاييس الحية
curl http://localhost:5000/api/metrics/prometheus
```
