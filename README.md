# SpeakEng

AI와 대화하며 배우는 영어 회화 학습 Android 앱입니다. Clean Architecture 기반으로 개발되고 있습니다.

## 📱 폰에 설치하기

`main`에 푸시될 때마다 GitHub Actions가 디버그 APK를 자동으로 빌드해서 릴리즈에 올려둡니다.

1. 폰 브라우저로 아래 링크를 열어 `app-debug.apk`를 다운로드합니다.

   **[⬇️ 최신 APK 다운로드](https://github.com/AndrewRoh/20260916SpeakEng/releases/tag/latest-debug)**

2. 다운로드한 apk 파일을 탭해서 설치합니다. "출처를 알 수 없는 앱" 설치를 허용하라는 안내가 나오면 허용해주세요 (Android 설정 → 앱 → 특별한 앱 접근 → 알 수 없는 앱 설치, 또는 설치 시 뜨는 팝업에서 바로 허용 가능).
3. 설치된 "SpeakEng" 앱을 실행합니다.

> AI 대화 연습 기능은 Gemini API 키가 설정되어 있어야 동작합니다 (아래 "AI 대화 기능 사용하려면" 참고). 리딩(TTS)과 발음 분석(STT) 기능은 키 없이도 바로 사용할 수 있습니다.

## 🎯 핵심 기능

- **홈 / 대시보드** — 학습 스트릭과 오늘의 목표 진행률
- **AI 대화 연습** — Gemini 기반 AI와 실시간 대화, AI 응답을 TTS로 듣기
- **영어 책읽기** — 번들/업로드한 책을 문장 단위로 TTS로 듣기, 배속·반복 지원
- **발음 분석** — 문장을 듣고 따라 말하면 STT로 인식해 단어 단위 정확도 채점
- **커리큘럼** — 난이도별 학습 콘텐츠 (스켈레톤)
- **통계 / 프로필** — 누적 대화 수, 연습 시간 등 (스켈레톤)

## 🔧 기술 스택

- Kotlin, Jetpack Compose + Material 3
- Clean Architecture (presentation / domain / data)
- Hilt (DI), Navigation-Compose, StateFlow
- Android `TextToSpeech` / `SpeechRecognizer` (음성 출력/입력)
- Gemini API (`com.google.ai.client.generativeai`) — AI 대화
- Room (Phase 4부터 본격 사용)
- JUnit5 + Turbine + MockK (테스트)

## 📐 개발 로드맵

| Phase | 내용 | 상태 |
|---|---|---|
| 1 | 프로젝트 셋업 + 기본 UI, 영어 책읽기(TTS) | ✅ 완료 |
| 2 | AI 대화 통합 (Gemini) + TTS 듣기 | ✅ 완료 |
| 3 | 발음 분석 (STT + 단어 매칭 채점) | ✅ 완료 |
| 4 | 커리큘럼 + 로컬 DB (Room) | 예정 |
| 5 | Firebase 동기화 | 예정 |

## 🏗️ 로컬에서 빌드하기

```bash
git clone https://github.com/AndrewRoh/20260916SpeakEng.git
cd 20260916SpeakEng
echo "GEMINI_API_KEY=your_key_here" >> local.properties   # AI 대화 기능을 쓸 경우
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

APK는 `app/build/outputs/apk/debug/app-debug.apk`에 생성됩니다.

## 🤖 AI 대화 기능 사용하려면

AI 대화 기능은 Gemini API 키가 필요합니다. **키는 절대 소스코드나 저장소에 커밋하지 마세요.**

- **CI로 빌드되는 APK에서 쓰려면**: 저장소 Settings → Secrets and variables → Actions에서 `GEMINI_API_KEY`라는 이름의 secret을 추가하세요. 다음 빌드부터 자동으로 반영됩니다.
- **로컬 빌드에서 쓰려면**: 위 "로컬에서 빌드하기"처럼 `local.properties`에 `GEMINI_API_KEY=...`를 추가하세요 (이 파일은 `.gitignore`에 포함되어 커밋되지 않습니다).

키가 없어도 앱은 정상적으로 빌드/설치되며, 리딩(TTS)·발음 분석(STT) 기능은 그대로 사용할 수 있습니다. AI 대화 화면에서만 오류가 표시됩니다.
