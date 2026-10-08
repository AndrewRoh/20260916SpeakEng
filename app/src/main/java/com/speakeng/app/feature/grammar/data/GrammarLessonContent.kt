package com.speakeng.app.feature.grammar.data

import com.speakeng.app.feature.grammar.domain.model.GrammarExample
import com.speakeng.app.feature.grammar.domain.model.GrammarLesson

/**
 * The 25-lesson "말하기 영문법" track. Lesson titles follow a standard beginner English-grammar
 * syllabus (tense, be-verbs, modals, questions, etc.) — the same topics any grammar course covers
 * in roughly this order. The explanations and example sentences below are original, written for
 * this app; none of it is copied from any particular textbook.
 */
object GrammarLessonContent {

    private fun lesson(id: Int, title: String, explanation: String, examples: List<GrammarExample>) =
        GrammarLesson(id = id, dayNumber = (id - 1) / 2 + 1, title = title, explanation = explanation, examples = examples)

    val LESSONS: List<GrammarLesson> = listOf(
        lesson(
            1, "현재시제 긍정문",
            "현재의 습관이나 사실을 말할 때는 동사를 현재형 그대로 씁니다. 주어가 he/she/it처럼 3인칭 단수일 때는 동사에 -s를 붙입니다.",
            listOf(
                GrammarExample("I walk to school every day.", "나는 매일 학교까지 걸어가요."),
                GrammarExample("She drinks coffee in the morning.", "그녀는 아침에 커피를 마셔요."),
                GrammarExample("We live in Seoul.", "우리는 서울에 살아요."),
            ),
        ),
        lesson(
            2, "현재시제 부정문",
            "현재시제 부정문은 주어가 I/you/we/they일 때 don't, he/she/it일 때 doesn't를 동사 앞에 붙이고, 동사는 원형으로 돌아갑니다.",
            listOf(
                GrammarExample("I don't like spicy food.", "나는 매운 음식을 안 좋아해요."),
                GrammarExample("He doesn't drive to work.", "그는 차로 출근하지 않아요."),
                GrammarExample("They don't live near here.", "그들은 이 근처에 살지 않아요."),
            ),
        ),
        lesson(
            3, "과거시제 긍정문",
            "이미 끝난 일을 말할 때는 동사를 과거형으로 바꿉니다. 규칙 동사는 -ed를 붙이고, 불규칙 동사는 형태가 따로 바뀝니다(go→went, eat→ate).",
            listOf(
                GrammarExample("I watched a movie last night.", "나는 어젯밤에 영화를 봤어요."),
                GrammarExample("She visited her parents yesterday.", "그녀는 어제 부모님을 찾아뵀어요."),
                GrammarExample("We went to the beach last summer.", "우리는 지난여름에 바다에 갔어요."),
            ),
        ),
        lesson(
            4, "과거시제 부정문",
            "과거시제 부정문은 주어에 상관없이 didn't를 동사 앞에 붙이고, 동사는 원형으로 돌아갑니다.",
            listOf(
                GrammarExample("I didn't finish my homework.", "나는 숙제를 끝내지 못했어요."),
                GrammarExample("He didn't call me back.", "그는 저에게 다시 전화하지 않았어요."),
                GrammarExample("We didn't have time yesterday.", "우리는 어제 시간이 없었어요."),
            ),
        ),
        lesson(
            5, "미래시제 긍정문",
            "앞으로 일어날 일이나 즉흥적인 결정을 말할 때는 will + 동사원형을 씁니다.",
            listOf(
                GrammarExample("I will call you tomorrow.", "내일 전화할게요."),
                GrammarExample("She will join us for dinner.", "그녀는 우리와 저녁을 같이 먹을 거예요."),
                GrammarExample("It will rain this afternoon.", "오늘 오후에 비가 올 거예요."),
            ),
        ),
        lesson(
            6, "명령문",
            "명령문은 주어 없이 동사원형으로 바로 시작해서 지시나 요청을 나타냅니다. 부정 명령문은 앞에 Don't를 붙입니다.",
            listOf(
                GrammarExample("Open the window, please.", "창문 좀 열어주세요."),
                GrammarExample("Be quiet in the library.", "도서관에서는 조용히 해주세요."),
                GrammarExample("Don't touch that.", "그거 만지지 마세요."),
            ),
        ),
        lesson(
            7, "미래시제 부정문",
            "미래시제 부정문은 will 뒤에 not을 붙여 won't로 줄여서 많이 씁니다.",
            listOf(
                GrammarExample("I won't be late again.", "다시는 안 늦을게요."),
                GrammarExample("She won't forget your birthday.", "그녀는 당신 생일을 잊지 않을 거예요."),
                GrammarExample("They won't arrive before noon.", "그들은 정오 전에는 도착하지 않을 거예요."),
            ),
        ),
        lesson(
            8, "can (~할 수 있다)",
            "can은 능력이나 가능성을 나타내는 조동사로, 뒤에 동사원형이 옵니다. 부정형은 can't(또는 cannot)입니다.",
            listOf(
                GrammarExample("I can swim very well.", "나는 수영을 아주 잘할 수 있어요."),
                GrammarExample("She can speak three languages.", "그녀는 3개 언어를 할 수 있어요."),
                GrammarExample("I can't come to the party.", "저는 파티에 못 가요."),
            ),
        ),
        lesson(
            9, "and를 사용하여 can 문장 확장하기",
            "and로 두 개의 can 능력을 한 문장에 이어서 말하면 더 자연스러운 대화체 문장이 됩니다.",
            listOf(
                GrammarExample("I can cook and bake.", "나는 요리도 하고 빵도 구울 수 있어요."),
                GrammarExample("He can play the guitar and sing.", "그는 기타도 치고 노래도 할 수 있어요."),
                GrammarExample("We can walk there and save time.", "우리는 거기까지 걸어가서 시간도 아낄 수 있어요."),
            ),
        ),
        lesson(
            10, "have로 사물의 특징 표현하기",
            "have는 '가지고 있다'는 뜻으로, 사람·사물의 특징이나 소유를 표현할 때 씁니다.",
            listOf(
                GrammarExample("This house has a big garden.", "이 집은 큰 정원이 있어요."),
                GrammarExample("My phone has a great camera.", "제 휴대폰은 카메라가 좋아요."),
                GrammarExample("She has long black hair.", "그녀는 긴 검은 머리를 가지고 있어요."),
            ),
        ),
        lesson(
            11, "have 복습",
            "앞서 배운 have 문장을 다양한 주어로 바꿔 말하며 복습합니다. 3인칭 단수 주어에서는 has를 쓰는 것에 주의하세요.",
            listOf(
                GrammarExample("I have two brothers.", "저는 남자 형제가 둘 있어요."),
                GrammarExample("They have a new car.", "그들은 새 차가 있어요."),
                GrammarExample("Our office has a nice view.", "우리 사무실은 전망이 좋아요."),
            ),
        ),
        lesson(
            12, "if (~하면)",
            "if는 조건을 나타내는 접속사로, 'if + 주어 + 동사' 뒤에 결과를 이어 말합니다.",
            listOf(
                GrammarExample("If it rains, we will stay home.", "비가 오면 우리는 집에 있을 거예요."),
                GrammarExample("If you are tired, take a break.", "피곤하면 좀 쉬세요."),
                GrammarExample("If I have time, I will call you.", "시간이 있으면 전화할게요."),
            ),
        ),
        lesson(
            13, "should, must (~해야 한다)",
            "should는 '~하는 게 좋다'는 권유, must는 '반드시 ~해야 한다'는 강한 의무를 나타냅니다.",
            listOf(
                GrammarExample("You should see a doctor.", "병원에 가보는 게 좋겠어요."),
                GrammarExample("We must finish this today.", "우리는 오늘 이걸 꼭 끝내야 해요."),
                GrammarExample("You must wear a seatbelt.", "안전벨트를 꼭 매야 해요."),
            ),
        ),
        lesson(
            14, "인칭대명사",
            "인칭대명사는 사람이나 사물을 대신 가리키는 말로, 주격(I, you, he...)과 목적격(me, you, him...)의 형태가 다릅니다.",
            listOf(
                GrammarExample("He likes her very much.", "그는 그녀를 아주 좋아해요."),
                GrammarExample("They invited us to the party.", "그들은 우리를 파티에 초대했어요."),
                GrammarExample("I gave it to him.", "나는 그것을 그에게 줬어요."),
            ),
        ),
        lesson(
            15, "요일",
            "요일은 항상 대문자로 시작하며, 특정 요일에 하는 일을 말할 때는 전치사 on을 씁니다.",
            listOf(
                GrammarExample("I have English class on Monday.", "저는 월요일에 영어 수업이 있어요."),
                GrammarExample("We usually meet on Fridays.", "우리는 보통 금요일마다 만나요."),
                GrammarExample("The store is closed on Sunday.", "그 가게는 일요일에 문을 닫아요."),
            ),
        ),
        lesson(
            16, "be + 형용사 긍정문",
            "be동사(am/are/is) 뒤에 형용사를 쓰면 주어의 상태나 성질을 나타냅니다.",
            listOf(
                GrammarExample("I am happy today.", "저는 오늘 기분이 좋아요."),
                GrammarExample("She is very smart.", "그녀는 아주 똑똑해요."),
                GrammarExample("The weather is nice this week.", "이번 주 날씨가 좋네요."),
            ),
        ),
        lesson(
            17, "be + 형용사 부정문",
            "be동사 부정문은 be동사 뒤에 not을 붙입니다 (am not, aren't, isn't).",
            listOf(
                GrammarExample("I am not tired at all.", "저는 전혀 안 피곤해요."),
                GrammarExample("He isn't busy right now.", "그는 지금 안 바빠요."),
                GrammarExample("We aren't ready yet.", "우리는 아직 준비가 안 됐어요."),
            ),
        ),
        lesson(
            18, "be + 명사",
            "be동사 뒤에 명사를 쓰면 주어가 무엇인지(직업, 신분 등)를 나타냅니다.",
            listOf(
                GrammarExample("I am a nurse.", "저는 간호사예요."),
                GrammarExample("She is my best friend.", "그녀는 제 가장 친한 친구예요."),
                GrammarExample("They are students at this school.", "그들은 이 학교 학생이에요."),
            ),
        ),
        lesson(
            19, "be + 장소/위치",
            "be동사 뒤에 장소를 나타내는 말을 쓰면 '~에 있다'는 뜻이 됩니다.",
            listOf(
                GrammarExample("I am at the office.", "저는 사무실에 있어요."),
                GrammarExample("The keys are on the table.", "열쇠는 테이블 위에 있어요."),
                GrammarExample("We are near the station.", "우리는 역 근처에 있어요."),
            ),
        ),
        lesson(
            20, "be동사 의문문",
            "be동사 의문문은 be동사를 주어 앞으로 보내서 만듭니다 (Are you...? Is she...?).",
            listOf(
                GrammarExample("Are you free this weekend?", "이번 주말에 시간 있어요?"),
                GrammarExample("Is he your brother?", "그가 당신 남동생이에요?"),
                GrammarExample("Is the meeting at 3 o'clock?", "회의가 3시에 있나요?"),
            ),
        ),
        lesson(
            21, "일반동사 의문문",
            "일반동사 의문문은 문장 앞에 Do/Does를 붙이고, 동사는 원형으로 씁니다 (Does he like...?).",
            listOf(
                GrammarExample("Do you like Korean food?", "한국 음식 좋아하세요?"),
                GrammarExample("Does she work on weekends?", "그녀는 주말에 일하나요?"),
                GrammarExample("Do they live near you?", "그들은 당신 근처에 사나요?"),
            ),
        ),
        lesson(
            22, "be동사 과거시제",
            "be동사의 과거형은 was(I/he/she/it)와 were(you/we/they)입니다.",
            listOf(
                GrammarExample("I was at home yesterday.", "저는 어제 집에 있었어요."),
                GrammarExample("They were at the concert.", "그들은 콘서트에 있었어요."),
                GrammarExample("It was a great trip.", "정말 좋은 여행이었어요."),
            ),
        ),
        lesson(
            23, "과거시제 의문문",
            "일반동사 과거시제 의문문은 Did + 주어 + 동사원형 순서로 만듭니다. be동사 과거 의문문은 was/were를 주어 앞으로 보냅니다.",
            listOf(
                GrammarExample("Did you sleep well last night?", "어젯밤에 잘 주무셨어요?"),
                GrammarExample("Were you busy yesterday?", "어제 바쁘셨어요?"),
                GrammarExample("Did she call you back?", "그녀가 다시 전화했나요?"),
            ),
        ),
        lesson(
            24, "과거시제 복습",
            "일반동사와 be동사의 과거형 긍정문·부정문·의문문을 섞어서 복습합니다.",
            listOf(
                GrammarExample("I didn't see that movie, but I heard it was good.", "그 영화는 못 봤지만 좋다고 들었어요."),
                GrammarExample("Were you at work when she called?", "그녀가 전화했을 때 일하고 있었어요?"),
                GrammarExample("We went out, but it wasn't raining yet.", "우리는 나갔는데 아직 비는 안 오고 있었어요."),
            ),
        ),
        lesson(
            25, "be동사 미래시제",
            "be동사의 미래형은 will be입니다. 주어에 상관없이 형태가 바뀌지 않습니다.",
            listOf(
                GrammarExample("I will be at the office by nine.", "9시까지 사무실에 있을 거예요."),
                GrammarExample("She will be happy to see you.", "그녀는 당신을 보면 기뻐할 거예요."),
                GrammarExample("They will be ready in a minute.", "그들은 곧 준비될 거예요."),
            ),
        ),
    )
}
