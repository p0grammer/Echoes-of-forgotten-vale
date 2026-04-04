## 2026-04-04 - [Android Accessibility Equivalents]
**Learning:** Translating web-based accessibility checks (like ARIA and alt text) into Android requires using `android:contentDescription`. For decorative elements that should be skipped by screen readers like TalkBack, setting `android:contentDescription="@null"` and `android:importantForAccessibility="no"` is critical to prevent a cluttered auditory experience.
**Action:** Use these attributes consistently on Android ImageViews to ensure both informative and decorative images are handled properly.
