# خطة الطوارئ وإجراءات التراجع السريع (Disaster Recovery & Rollback Guide)

## 🔄 إجراءات التراجع الفوري (Instant Rollback)
في حال حدوث أي خلل أثناء النشر التلقائي أو التشغيل:
1. **التراجع عبر Git**:
   ```bash
   git revert HEAD --no-edit
   git push origin main
   ```
2. **استعادة النسخة الاحتياطية (Database & Embeddings Restore)**:
   - طلب الاستعادة عبر مسار الـ API المعتمد: `POST /api/backup/test_restore` مع تحديد `backup_id`.
3. **إعادة توازن الحاويات (Pod Self-Healing)**:
   - سياسة التصعيد `AlertEscalationPolicy` تقوم تلقائياً بإعادة تشغيل الحاويات المتعثرة وتوسيع الذاكرة بنسبة +50%.

## ⏱️ مؤشرات التعافي المعتمدة
- **RTO (Recovery Time Objective)**: أقل من 1.2 دقيقة.
- **RPO (Recovery Point Objective)**: 0.0 دقيقة (بدون أي فقدان للبيانات).
